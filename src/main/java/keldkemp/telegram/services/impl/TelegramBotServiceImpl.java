package keldkemp.telegram.services.impl;


import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramButtons;
import keldkemp.telegram.models.TelegramKeyboardRows;
import keldkemp.telegram.models.TelegramKeyboardTypes;
import keldkemp.telegram.models.TelegramKeyboards;
import keldkemp.telegram.models.TelegramMessages;
import keldkemp.telegram.models.TelegramStages;
import keldkemp.telegram.repositories.TelegramBotsRepository;
import keldkemp.telegram.repositories.TelegramButtonsRepository;
import keldkemp.telegram.repositories.TelegramKeyboardRowsRepository;
import keldkemp.telegram.repositories.TelegramKeyboardTypesRepository;
import keldkemp.telegram.repositories.TelegramKeyboardsRepository;
import keldkemp.telegram.repositories.TelegramMessagesRepository;
import keldkemp.telegram.repositories.TelegramStagesRepository;
import keldkemp.telegram.rest.dto.telegram.TelegramBotDto;
import keldkemp.telegram.rest.dto.telegram.TelegramButtonDto;
import keldkemp.telegram.rest.dto.telegram.TelegramKeyboardDto;
import keldkemp.telegram.rest.dto.telegram.TelegramKeyboardRowDto;
import keldkemp.telegram.rest.dto.telegram.TelegramMessageDto;
import keldkemp.telegram.rest.dto.telegram.TelegramStageDto;
import keldkemp.telegram.rest.dto.telegram.TelegramStageTransferDto;
import keldkemp.telegram.rest.mappers.TelegramMapper;
import keldkemp.telegram.services.BeanFactoryService;
import keldkemp.telegram.services.TelegramBotService;
import keldkemp.telegram.services.TransactionService;
import keldkemp.telegram.services.UserService;
import keldkemp.telegram.telegram.service.SchedulerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class TelegramBotServiceImpl implements TelegramBotService {

    Logger logger = LoggerFactory.getLogger(TelegramBotServiceImpl.class);

    @Autowired
    private TelegramBotsRepository tBotsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramStagesRepository tStagesRepository;

    @Autowired
    private TelegramMessagesRepository tMessagesRepository;

    @Autowired
    private TelegramKeyboardsRepository tKeyboardsRepository;

    @Autowired
    private TelegramKeyboardRowsRepository tKeyboardRowsRepository;

    @Autowired
    private TelegramButtonsRepository tButtonsRepository;

    @Autowired
    private TelegramKeyboardTypesRepository tKeyboardTypesRepository;

    @Autowired
    private TelegramMapper telegramMapper;

    @Autowired
    @Qualifier("telegramBotBeanService")
    private BeanFactoryService telegramBeanFactory;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SchedulerService schedulerService;

    @Override
    public List<TelegramKeyboardTypes> getKeyboardTypes() {
        return tKeyboardTypesRepository.findAll();
    }

    @Override
    @Transactional
    public List<TelegramBots> getBots() {
        return tBotsRepository.getTelegramBotsByUser(userService.getCurrentUser());
    }

    @Override
    @Transactional
    public TelegramBots getBot(Long id) {
        TelegramBots bot = tBotsRepository.getById(id);
        checkUser(bot);
        return bot;
    }

    @Override
    @Transactional
    public TelegramBots save(TelegramBots bot) {
        bot.setUser(userService.getCurrentUser());
        validate(bot);
        TelegramBots saveBot;

        if (!isNewBot(bot)) {
            bot.setFrontOptions(getBot(bot.getId()).getFrontOptions());
        }

        //Check Token
        if (isNewBot(bot)) {
            telegramBeanFactory.deleteBean(bot);
        }

        try {
            saveBot = transactionService.doInTransactionAnnotation(() -> tBotsRepository.save(bot));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Бот с таким токеном уже создан!", e);
        }

        telegramBeanFactory.deleteBean(saveBot);
        if (Boolean.TRUE == saveBot.getIsActive()) {
            telegramBeanFactory.getBean(saveBot.getBotToken());
        }

        return saveBot;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TelegramBots bot = tBotsRepository.getById(id);
        checkUser(bot);

        deleteStages(id);
        tBotsRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<TelegramStages> getStages(Long botId) {
        TelegramBots bot = tBotsRepository.getById(botId);
        checkUser(bot);

        return tStagesRepository.getTelegramStagesByTelegramBot(bot);
    }

    //TODO: Порядок сохранения Этапов надо учесть.
    // Валидацию всего и вся сделать надо.
    @Override
    @Transactional
    public TelegramStageTransferDto saveStages(TelegramStageTransferDto telegramTransferDto, Long botId) {
        HashMap<Long, TelegramStages> stagesHashMap = new HashMap<>();
        TelegramBots bot = tBotsRepository.getById(botId);
        TelegramBotDto botDto = new TelegramBotDto();
        botDto.setId(botId);
        checkUser(bot);

        telegramTransferDto.getTelegramStages().forEach(stage -> {
            //Don't use Mapper
            TelegramStages telegramStage = new TelegramStages();
            telegramStage.setId(stage.getId());
            telegramStage.setName(stage.getName());
            if (stage.getPreviousStage() != null) {
                telegramStage.setPreviousStage(getStageByFictiveId(telegramTransferDto, stage.getPreviousStage()).getId());
            }
            telegramStage.setTelegramBot(bot);
            telegramStage.setFrontNodeId(stage.getFrontNodeId());
            telegramStage.setIsScheduleActive(Boolean.TRUE == stage.getIsScheduleActive());
            telegramStage.setScheduleCron(stage.getScheduleCron());
            telegramStage.setScheduleDateTime(stage.getScheduleDateTime());

            telegramStage = tStagesRepository.saveAndFlush(telegramStage);

            if (stage.getId() == null) {
                String frontOptions = bot.getFrontOptions();
                if (frontOptions != null) {
                    frontOptions = frontOptions.replace(stage.getFrontPrefixReplace(), telegramStage.getId().toString());
                }
                bot.setFrontOptions(frontOptions);
            }

            stagesHashMap.put(telegramStage.getId(), telegramStage);

            stage.setId(telegramStage.getId());
            stage.setTelegramBot(botDto);
            stage.setPreviousStage(telegramStage.getPreviousStage());
            stage.setIsScheduleActive(telegramStage.getIsScheduleActive());
        });

        telegramTransferDto.getTelegramStages().forEach(stageDto -> {
            //save TelegramMessages
            List<TelegramMessageDto> messagesDto = stageDto.getTelegramMessages();
            List<TelegramMessages> messages = telegramMapper.toTelegramMessagesPoFromDto(messagesDto);
            messages.forEach(m -> m.setTelegramStage(stagesHashMap.get(stageDto.getId())));
            messages = saveMessages(messages, bot);
            for (int i = 0; messagesDto.size() > i; i++) {
                messagesDto.get(i).setId(messages.get(i).getId());
            }

            //save TelegramKeyboards
            List<TelegramKeyboardDto> keyboardsDto = stageDto.getTelegramKeyboards();
            if (keyboardsDto != null) {
                List<TelegramKeyboards> keyboards = telegramMapper.toTelegramKeyboardsPoFromDto(keyboardsDto);
                keyboards.forEach(k -> {
                    k.setTelegramStage(stagesHashMap.get(stageDto.getId()));
                    k.setTelegramKeyboardRows(null);
                });
                keyboards = saveKeyboards(keyboards, bot);
                for (int i = 0; keyboardsDto.size() > i; i++) {
                    keyboardsDto.get(i).setId(keyboards.get(i).getId());
                }

                keyboardsDto.forEach(k -> {
                    //save TelegramKeyboardRows
                    List<TelegramKeyboardRowDto> rowsDto = k.getTelegramKeyboardRows();
                    List<TelegramKeyboardRows> rows = telegramMapper.toTelegramRowsPoFromDto(rowsDto);
                    rows.forEach(r -> {
                        r.setTelegramKeyboard(tKeyboardsRepository.getById(k.getId()));
                        r.setTelegramButtons(null);
                    });
                    rows = saveKeyboardRows(rows, bot);
                    for (int i = 0; rowsDto.size() > i; i++) {
                        rowsDto.get(i).setId(rows.get(i).getId());
                    }

                    rowsDto.forEach(r -> {
                        //save TelegramButtons
                        List<TelegramButtonDto> buttonsDto = r.getTelegramButtons();
                        buttonsDto.forEach(bDto -> {
                            if (bDto.getCallbackData() != null
                                    && (bDto.getCallbackData().getId() != null || bDto.getCallbackData().getFictiveId() != null)) {
                                bDto.getCallbackData().setId(getStageByFictiveId(telegramTransferDto,
                                                bDto.getCallbackData().getFictiveId() == null ?
                                                        bDto.getCallbackData().getId() :
                                                        bDto.getCallbackData().getFictiveId()
                                        ).getId()
                                );
                            }
                        });

                        List<TelegramButtons> buttons = telegramMapper.toTelegramButtonsPoFromDto(buttonsDto);
                        buttons.forEach(b -> {
                            b.setTelegramKeyboardRow(tKeyboardRowsRepository.getById(r.getId()));
                            if (b.getCallbackData() != null && b.getCallbackData().getId() != null) {
                                b.setCallbackData(stagesHashMap.get(b.getCallbackData().getId()));
                            } else {
                                b.setCallbackData(null);
                            }
                        });
                        buttons = saveButtons(buttons, bot);
                        for (int i = 0; buttonsDto.size() > i; i++) {
                            buttonsDto.get(i).setId(buttons.get(i).getId());
                        }
                    });
                });
            }
        });

        bot.setFrontOptions(telegramTransferDto.getFrontOptions());
        tBotsRepository.saveAndFlush(bot);

        List<TelegramStages> stages = telegramMapper.toTelegramStagesPoFromDto(telegramTransferDto.getTelegramStages());
        deleteAnotherStages(stages, bot);
        setSchedules(stages, bot);

        TelegramStageTransferDto stageTransferDto = new TelegramStageTransferDto();
        stageTransferDto.setTelegramStages(telegramMapper.toTelegramStagesDtoFromPo(stages));
        stageTransferDto.setFrontOptions(bot.getFrontOptions());
        return stageTransferDto;
    }

    @Override
    @Transactional
    public void deleteStages(Long botId) {
        TelegramBots bot = tBotsRepository.getById(botId);
        checkUser(bot);

        tStagesRepository.deleteAllByTelegramBot(bot);
    }

    private List<TelegramStages> saveStages(List<TelegramStages> stages, TelegramBots bot) {
        return tStagesRepository.saveAllAndFlush(stages);
    }

    private List<TelegramKeyboards> saveKeyboards(List<TelegramKeyboards> keyboards, TelegramBots bot) {
        //Validation
        keyboards.forEach(keyboard -> {
            TelegramKeyboardTypes type = keyboard.getTelegramKeyboardType();
            Asserts.check(type != null && type.getId() != null, "Keyboard type is null");
            Asserts.check(bot.getId().equals(keyboard.getTelegramStage().getTelegramBot().getId()), "" +
                    "Error save keyboards. Stage link to another bot");
        });
        return tKeyboardsRepository.saveAllAndFlush(keyboards);
    }

    private List<TelegramKeyboardRows> saveKeyboardRows(List<TelegramKeyboardRows> keyboardRows, TelegramBots bot) {
        //Validation
        List<Long> ordList = new ArrayList<>();
        keyboardRows.forEach(row -> {
            Asserts.notNull(row.getOrd(), "KeyboardRow attr Ord");
            Asserts.notNull(row.getTelegramKeyboard(), "KeyboardRow link to Keyboard");
            Asserts.notNull(row.getTelegramKeyboard().getId(), "KeyboardRow link to Keyboard");

            Asserts.check(bot.getId().equals(tKeyboardsRepository.getTelegramBotByKeyboard(row.getTelegramKeyboard().getId()).getId()),
                    "Error save rows. Keyboard link to another bot");
            ordList.add(row.getOrd());
        });
        Set<Long> ordSet = new HashSet<>(ordList);

        Asserts.check(ordList.size() == ordSet.size(), "KeyboardRow attr Ord is not unique");
        return tKeyboardRowsRepository.saveAllAndFlush(keyboardRows);
    }

    private List<TelegramButtons> saveButtons(List<TelegramButtons> buttons, TelegramBots bot) {
        //Validation
        List<Long> ordList = new ArrayList<>();
        buttons.forEach(button -> {
            TelegramStages stage = button.getCallbackData();
            if (stage != null && stage.getId() != null) {
                Asserts.check(bot.getId().equals(tStagesRepository.getById(stage.getId()).getTelegramBot().getId()),
                        "Error save buttons. callback data link to another bot");
                Asserts.check(StringUtils.isEmpty(button.getButtonLink()),
                        "ButtonLink should be null when three is callback data");
            }
            TelegramKeyboardRows row = button.getTelegramKeyboardRow();
            Asserts.notNull(row, "Keyboard row");
            Asserts.notNull(row.getId(), "Keyboard row");

            Asserts.check(bot.getId().equals(tKeyboardRowsRepository.getTelegramBotByRow(row.getId()).getId()),
                    "Error save buttons. KeyboardRow link to another bot");
            Asserts.notNull(button.getButtonOrd(), "Button attr Ord");

            ordList.add(button.getButtonOrd());
        });
        Set<Long> ordSet = new HashSet<>(ordList);

        Asserts.check(ordList.size() == ordSet.size(), "Buttons attr Ord is not unique");
        return tButtonsRepository.saveAllAndFlush(buttons);
    }

    private List<TelegramMessages> saveMessages(List<TelegramMessages> messages, TelegramBots bot) {
        //Validation
        messages.forEach(message -> {
            Asserts.notNull(message.getMessageText(), "Text message");
            Asserts.check(bot.getId().equals(tStagesRepository.getById(message.getTelegramStage().getId()).getTelegramBot().getId()),
                    "Error save messages. Message link to another bot");
        });
        return tMessagesRepository.saveAllAndFlush(messages);
    }

    private void deleteAnotherStages(List<TelegramStages> stages, TelegramBots bot) {
        tStagesRepository.getAllByIdNotInAndTelegramBot(stages.stream().map(TelegramStages::getId).toList(), bot)
                .forEach(stage -> schedulerService.cancelStageSchedule(stage));
        List<Long> messageIds = new ArrayList<>();
        List<Long> keyboardIds = new ArrayList<>();
        List<Long> rowIds = new ArrayList<>();
        List<Long> buttonIds = new ArrayList<>();

        stages.forEach(stage -> {
            messageIds.addAll(stage.getTelegramMessages().stream().map(TelegramMessages::getId).toList());

            if (stage.getTelegramKeyboards() != null) {
                stage.getTelegramKeyboards().forEach(keyboard -> {
                    keyboard.getTelegramKeyboardRows().forEach(row -> buttonIds.addAll(row.getTelegramButtons().stream().map(TelegramButtons::getId).toList()));
                    rowIds.addAll(keyboard.getTelegramKeyboardRows().stream().map(TelegramKeyboardRows::getId).toList());
                });
                keyboardIds.addAll(stage.getTelegramKeyboards().stream().map(TelegramKeyboards::getId).toList());
            }
        });

        tMessagesRepository.deleteAllByIdNotInAndTelegramStageTelegramBot(messageIds, bot);
        tButtonsRepository.deleteAllByIdNotInAndTelegramKeyboardRowTelegramKeyboardTelegramStageTelegramBot(buttonIds, bot);
        tKeyboardRowsRepository.deleteAllByIdNotInAndTelegramKeyboardTelegramStageTelegramBot(rowIds, bot);
        tKeyboardsRepository.deleteAllByIdNotInAndTelegramStageTelegramBot(keyboardIds, bot);
        tStagesRepository.deleteAllByIdNotInAndTelegramBot(stages.stream().map(TelegramStages::getId).toList(), bot);
    }

    private TelegramStages getStageByFictiveId(TelegramStageTransferDto telegramStageTransfer, Long fictiveId) {
        Optional<TelegramStageDto> dto = telegramStageTransfer.getTelegramStages().stream()
                .filter(obj -> fictiveId.equals(obj.getFictiveId()) || fictiveId.equals(obj.getId()))
                .findFirst();
        if (dto.isPresent()) {
            TelegramStages stage = new TelegramStages();
            stage.setId(dto.get().getId());
            return stage;
        } else {
            throw new RuntimeException("Error find stage in link");
        }
    }

    private void validate(TelegramBots bot) {
        Asserts.check(StringUtils.isNotEmpty(bot.getBotToken()), "Bot token is empty");
        Asserts.check(StringUtils.isNotEmpty(bot.getBotName()), "Bot name is empty");
        checkUser(bot);
    }

    private void checkUser(TelegramBots bot) {
        TelegramBots botPo;
        if (!isNewBot(bot)) {
            botPo = tBotsRepository.getById(bot.getId());
        } else {
            botPo = bot;
        }
        Asserts.check(botPo.getUser().equals(userService.getCurrentUser()),
                "Error user");
    }

    private boolean isNewBot(TelegramBots bot) {
        return bot.getId() == null;
    }

    private void setSchedules(List<TelegramStages> stages, TelegramBots bot) {
        stages.forEach(stage -> {
            if (stage.getIsScheduleActive()) {
                stage.setTelegramBot(bot);
                schedulerService.handleStageSchedule(stage);
            }
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAllSchedule() {
        tStagesRepository.getAllByIsScheduleActiveAndTelegramBotIsActive(true, true)
                .forEach(stage -> schedulerService.handleStageSchedule(stage));
    }
}

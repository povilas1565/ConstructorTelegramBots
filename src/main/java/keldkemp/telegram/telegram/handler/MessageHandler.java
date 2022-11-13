package keldkemp.telegram.telegram.handler;

import keldkemp.telegram.models.*;
import keldkemp.telegram.repositories.*;
import keldkemp.telegram.telegram.domain.MessageTypes;
import keldkemp.telegram.telegram.domain.StageTypes;
import keldkemp.telegram.telegram.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

//TODO: Refactor
@Component
public class MessageHandler {

    Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private TelegramBotsRepository tBotsRepository;

    @Autowired
    private TelegramStagesRepository tStagesRepository;

    @Autowired
    private TelegramButtonsRepository tButtonsRepository;

    @Autowired
    private TelegramUsersRepository tUsersRepository;

    @Transactional
    public List<? extends BotApiMethod<?>> handle(Update update, String token) {
        TelegramBots bot = tBotsRepository.getTelegramBotsByBotToken(token);
        try {
            if (update.hasCallbackQuery()) {
                return handleCallbackQuery(update.getCallbackQuery(), bot);
            }
            return handlePrivateMessage(null, update.getMessage(), bot, StageTypes.MESSAGE_STAGE);
        } catch (RuntimeException e) {
            if (update.hasCallbackQuery()) {
                return handleError(update.getCallbackQuery().getMessage(), e);
            }
            return handleError(update.getMessage(), e);
        }
    }

    @Transactional
    public List<? extends BotApiMethod<?>> handleSchedule(Long stageId) {
        List<BotApiMethod<?>> list = new ArrayList<>();

        TelegramStages stage = tStagesRepository.getById(stageId);
        tUsersRepository.getAllByTelegramBot(stage.getTelegramBot()).forEach(user -> {
            Chat chat = new Chat(user.getTgUserId(), "private");
            chat.setUserName(user.getUserName());
            chat.setFirstName(user.getFirstName());
            chat.setLastName(user.getLastName());

            Message message = new Message();
            message.setChat(chat);

            list.addAll(handlePrivateMessage(stage, message, stage.getTelegramBot(), StageTypes.SCHEDULE_STAGE));
        });
        return list;
    }

    private List<? extends BotApiMethod<?>> handlePrivateMessage(TelegramStages stage, Message message, TelegramBots bot, StageTypes type) {
        saveUser(message, bot);
        TelegramButtons button = tButtonsRepository.getTelegramButtonsByButtonTextAndBotAndReplyType(message.getText(), bot);

        if (button == null || button.getCallbackData() == null) {
            if (type == StageTypes.MESSAGE_STAGE) {
                stage = tStagesRepository.getFirstStage(bot.getId(), false);
                if (stage == null) {
                    stage = tStagesRepository.getFirstStage(bot.getId(), true);
                }
            }
        } else {
            stage = button.getCallbackData();
        }

        return messageService.getMessages(stage, message, MessageTypes.SEND_MESSAGE);
    }

    private List<? extends BotApiMethod<?>> handleCallbackQuery(CallbackQuery callbackQuery, TelegramBots bot) {
        saveUser(callbackQuery.getMessage(), bot);
        TelegramStages stage = tStagesRepository.getById(Long.parseLong(callbackQuery.getData()));

        return messageService.getMessages(stage, callbackQuery.getMessage(), MessageTypes.EDIT_MESSAGE);
    }

    private List<? extends BotApiMethod<?>> handleError(Message message, RuntimeException e) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(e.getMessage());

        return List.of(sendMessage);
    }

    private void saveUser(Message message, TelegramBots bot) {
        Chat chat = message.getChat();

        TelegramUsers user = tUsersRepository.getByTgUserIdAndTelegramBot(chat.getId(), bot);
        if (user == null) {
            user = new TelegramUsers();
        }

        user.setTgUserId(chat.getId());
        user.setUserName(chat.getUserName());
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setTelegramBot(bot);

        tUsersRepository.saveAndFlush(user);
    }
}

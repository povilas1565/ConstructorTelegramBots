package keldkemp.telegram.services.impl;

import keldkemp.telegram.models.*;
import keldkemp.telegram.repositories.*;
import keldkemp.telegram.rest.dto.telegram.TelegramStageDto;
import keldkemp.telegram.rest.dto.telegram.TelegramStageTransferDto;
import keldkemp.telegram.rest.mappers.TelegramMapper;
import keldkemp.telegram.services.BeanFactoryService;
import keldkemp.telegram.services.TransactionService;
import keldkemp.telegram.services.UserService;
import keldkemp.telegram.telegram.service.SchedulerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {TelegramBotServiceImpl.class})
public class TelegramBotServiceImplTest {
    @MockBean
    TelegramBotsRepository tBotsRepository;
    @MockBean
    UserService userService;
    @MockBean
    TelegramStagesRepository tStagesRepository;
    @MockBean
    TelegramMessagesRepository tMessagesRepository;
    @MockBean
    TelegramKeyboardsRepository tKeyboardsRepository;
    @MockBean
    TelegramKeyboardRowsRepository tKeyboardRowsRepository;
    @MockBean
    TelegramButtonsRepository tButtonsRepository;
    @MockBean
    TelegramKeyboardTypesRepository tKeyboardTypesRepository;
    @MockBean
    TelegramMapper telegramMapper;
    @MockBean
    @Qualifier("telegramBotBeanService")
    BeanFactoryService telegramBeanFactory;
    @MockBean
    TransactionService transactionService;
    @MockBean
    SchedulerService schedulerService;
    @Autowired
    TelegramBotServiceImpl telegramBotServiceImpl;

    @Test
    void testGetKeyboardTypesEmpty() {
        List<TelegramKeyboardTypes> result = telegramBotServiceImpl.getKeyboardTypes();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetKeyboardTypes() {
        List<TelegramKeyboardTypes> types = getKeyboardTypes();

        when(telegramBotServiceImpl.getKeyboardTypes()).thenReturn(types);

        List<TelegramKeyboardTypes> result = telegramBotServiceImpl.getKeyboardTypes();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(types.get(0).getId(), result.get(0).getId());
        Assertions.assertEquals(types.get(0).getName(), result.get(0).getName());
        Assertions.assertEquals(types.get(1).getId(), result.get(1).getId());
        Assertions.assertEquals(types.get(1).getName(), result.get(1).getName());
    }

    @Test
    void testGetBots() {
        List<TelegramBots> bots = getTelegramBots();
        Users user = getUser();

        when(tBotsRepository.getTelegramBotsByUser(user)).thenReturn(bots);
        when(userService.getCurrentUser()).thenReturn(user);

        List<TelegramBots> result = telegramBotServiceImpl.getBots();
        Assertions.assertEquals(2, result.size());

        //bot1
        Assertions.assertEquals(bots.get(0).getId(), result.get(0).getId());
        Assertions.assertEquals(bots.get(0).getBotToken(), result.get(0).getBotToken());
        Assertions.assertEquals(bots.get(0).getBotName(), result.get(0).getBotName());
        Assertions.assertEquals(bots.get(0).getIsActive(), result.get(0).getIsActive());
        Assertions.assertEquals(bots.get(0).getUser(), result.get(0).getUser());

        //bot2
        Assertions.assertEquals(bots.get(1).getId(), result.get(1).getId());
        Assertions.assertEquals(bots.get(1).getBotToken(), result.get(1).getBotToken());
        Assertions.assertEquals(bots.get(1).getBotName(), result.get(1).getBotName());
        Assertions.assertEquals(bots.get(1).getIsActive(), result.get(1).getIsActive());
        Assertions.assertEquals(bots.get(1).getUser(), result.get(1).getUser());
    }

    @Test
    public void testGetBotsNull() {
        when(tBotsRepository.getTelegramBotsByUser(any())).thenReturn(null);
        when(userService.getCurrentUser()).thenReturn(new Users());

        List<TelegramBots> result = telegramBotServiceImpl.getBots();
        Assertions.assertNull(result);
    }

    @Test
    public void testGetBotsEmpty() {
        when(userService.getCurrentUser()).thenReturn(null);

        List<TelegramBots> result = telegramBotServiceImpl.getBots();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetBot() {
        TelegramBots bot = getTelegramBot();
        Users user = getUser();

        when(userService.getCurrentUser()).thenReturn(user);
        when(tBotsRepository.getById(1L)).thenReturn(bot);

        TelegramBots result = telegramBotServiceImpl.getBot(1L);
        Assertions.assertEquals(bot, result);
    }

    //TODO: Ниже надо все дописать
    @Test
    @Disabled
    void testSave() {
        when(userService.getCurrentUser()).thenReturn(new Users());
        //when(transactionService.doInTransactionAnnotation(any())).thenReturn(new V());

        TelegramBots result = telegramBotServiceImpl.save(new TelegramBots());
        Assertions.assertEquals(new TelegramBots(), result);
    }

    @Test
    @Disabled
    void testDelete() {
        when(userService.getCurrentUser()).thenReturn(new Users());

        telegramBotServiceImpl.delete(1L);
    }

    @Test
    @Disabled
    void testGetStages() {
        when(userService.getCurrentUser()).thenReturn(new Users());
        when(tStagesRepository.getTelegramStagesByTelegramBot(any())).thenReturn(List.of(new TelegramStages()));

        List<TelegramStages> result = telegramBotServiceImpl.getStages(1L);
        Assertions.assertEquals(List.of(new TelegramStages()), result);
    }

    @Test
    @Disabled
    void testSaveStages() {
        when(userService.getCurrentUser()).thenReturn(new Users());
        when(tKeyboardsRepository.getTelegramBotByKeyboard(anyLong())).thenReturn(new TelegramBots());
        when(tKeyboardRowsRepository.getTelegramBotByRow(anyLong())).thenReturn(new TelegramBots());
        when(telegramMapper.toTelegramStagesDtoFromPo(any())).thenReturn(List.of(new TelegramStageDto()));
        when(telegramMapper.toTelegramStagesPoFromDto(any())).thenReturn(List.of(new TelegramStages()));
        when(telegramMapper.toTelegramButtonsPoFromDto(any())).thenReturn(List.of(new TelegramButtons()));
        when(telegramMapper.toTelegramMessagesPoFromDto(any())).thenReturn(List.of(new TelegramMessages()));
        when(telegramMapper.toTelegramKeyboardsPoFromDto(any())).thenReturn(List.of(new TelegramKeyboards()));
        when(telegramMapper.toTelegramRowsPoFromDto(any())).thenReturn(List.of(new TelegramKeyboardRows()));

        TelegramStageTransferDto result = telegramBotServiceImpl.saveStages(new TelegramStageTransferDto(), 1L);
        Assertions.assertEquals(new TelegramStageTransferDto(), result);
    }

    @Test
    @Disabled
    void testDeleteStages() {
        when(userService.getCurrentUser()).thenReturn(new Users());

        telegramBotServiceImpl.deleteStages(1L);
    }

    private List<TelegramKeyboardTypes> getKeyboardTypes() {
        List<TelegramKeyboardTypes> keyboardTypes = new ArrayList<>();

        TelegramKeyboardTypes type1 = new TelegramKeyboardTypes();
        type1.setId(1L);
        type1.setName("InlineKeyboardMarkup");

        TelegramKeyboardTypes type2 = new TelegramKeyboardTypes();
        type2.setId(2L);
        type2.setName("ReplyKeyboardMarkup");

        keyboardTypes.add(type1);
        keyboardTypes.add(type2);

        return keyboardTypes;
    }

    private List<TelegramBots> getTelegramBots() {
        List<TelegramBots> bots = new ArrayList<>();

        TelegramBots bot1 = new TelegramBots();
        bot1.setId(1L);
        bot1.setBotName("bot1");
        bot1.setBotToken("token1");
        bot1.setIsActive(true);
        bot1.setUser(getUser());

        TelegramBots bot2 = new TelegramBots();
        bot2.setId(2L);
        bot2.setBotName("bot2");
        bot2.setBotToken("token2");
        bot2.setIsActive(true);
        bot2.setUser(getUser());

        bots.add(bot1);
        bots.add(bot2);

        return bots;
    }

    private TelegramBots getTelegramBot() {
        TelegramBots bot1 = new TelegramBots();
        bot1.setId(1L);
        bot1.setBotName("bot1");
        bot1.setBotToken("token1");
        bot1.setIsActive(true);
        bot1.setUser(getUser());

        return bot1;
    }

    private Users getUser() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("testik");
        user.setName("test");
        user.setPassword("123");

        return user;
    }
}

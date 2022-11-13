package keldkemp.telegram.services.impl;

import keldkemp.telegram.configs.SettingsValue;
import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.Users;
import keldkemp.telegram.repositories.TelegramBotsRepository;
import keldkemp.telegram.repositories.TelegramButtonsRepository;
import keldkemp.telegram.repositories.TelegramStagesRepository;
import keldkemp.telegram.repositories.TelegramUsersRepository;
import keldkemp.telegram.telegram.config.WebHookBot;
import keldkemp.telegram.telegram.handler.MessageHandler;
import keldkemp.telegram.telegram.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {TelegramBotBeanServiceImpl.class, LockServiceImpl.class, MessageHandler.class})
public class TelegramBotBeanServiceImplTest {
    @MockBean
    TelegramBotsRepository telegramBotsRepository;
    @MockBean
    SettingsValue settingsValue;
    @MockBean
    RestTemplate restTemplate;
    @MockBean
    @Qualifier("telegramHandlerThread")
    ExecutorService executorService;
    @MockBean
    ConfigurableApplicationContext applicationContext;
    @MockBean
    MessageService messageService;
    @MockBean
    TelegramStagesRepository tStagesRepository;
    @MockBean
    TelegramButtonsRepository tButtonsRepository;
    @MockBean
    TelegramUsersRepository tUsersRepository;
    @Autowired
    TelegramBotBeanServiceImpl telegramBotBeanServiceImpl;
    @Autowired
    LockServiceImpl lockService;
    @Autowired
    MessageHandler messageHandler;

    @Test
    public void testCreateBean() {
        TelegramBots bot = getTelegramBot();

        when(telegramBotsRepository.getTelegramBotsByBotToken(anyString())).thenReturn(bot);
        when(settingsValue.getAppUrl()).thenReturn("getAppUrlResponse");

        telegramBotBeanServiceImpl.createBean("token");
        WebHookBot botBean = telegramBotBeanServiceImpl.getBean("token");
        Assertions.assertEquals(bot.getBotToken(), botBean.getBotToken());
        Assertions.assertEquals(bot.getBotName(), botBean.getBotUsername());
    }

    @Test
    public void testGetBean() {
        TelegramBots bot = getTelegramBot();

        when(telegramBotsRepository.getTelegramBotsByBotToken(anyString())).thenReturn(bot);
        when(settingsValue.getAppUrl()).thenReturn("getAppUrlResponse");

        WebHookBot result = telegramBotBeanServiceImpl.getBean("token");
        Assertions.assertEquals(bot.getBotToken(), result.getBotToken());
        Assertions.assertEquals(bot.getBotName(), result.getBotUsername());
    }

    @Test
    public void testDeleteBean() {
        telegramBotBeanServiceImpl.deleteBean("object");
    }

    @Test
    public void testInitAll() {
        List<TelegramBots> bots = getTelegramBots();

        when(telegramBotsRepository.getTelegramBotsByBotToken("token")).thenReturn(bots.get(0));
        when(telegramBotsRepository.getTelegramBotsByBotToken("token2")).thenReturn(bots.get(1));
        when(telegramBotsRepository.getTelegramBotsByIsActive(anyBoolean())).thenReturn(bots);
        when(settingsValue.getAppUrl()).thenReturn("getAppUrlResponse");

        telegramBotBeanServiceImpl.initAll();

        WebHookBot bot1 = telegramBotBeanServiceImpl.getBean("token");
        WebHookBot bot2 = telegramBotBeanServiceImpl.getBean("token2");

        Assertions.assertEquals(bots.get(0).getBotToken(), bot1.getBotToken());
        Assertions.assertEquals(bots.get(0).getBotName(), bot1.getBotUsername());
        Assertions.assertEquals(bots.get(1).getBotToken(), bot2.getBotToken());
        Assertions.assertEquals(bots.get(1).getBotName(), bot2.getBotUsername());
    }

    @Test
    public void testGetBeanException() {
        NoSuchBeanDefinitionException exception = Assertions.assertThrows(
                NoSuchBeanDefinitionException.class, () -> telegramBotBeanServiceImpl.getBean("name", null)
        );
        Assertions.assertEquals("No bean named 'name' available", exception.getMessage());
    }

    @Test
    public void testDeleteBean2() {
        telegramBotBeanServiceImpl.deleteBean("name");
    }

    @Test
    public void testCheckBean() {
        boolean result = telegramBotBeanServiceImpl.checkBean("name");
        Assertions.assertFalse(result);
    }

    private TelegramBots getTelegramBot() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("pass");
        user.setName("name");

        TelegramBots telegramBot = new TelegramBots();
        telegramBot.setId(1L);
        telegramBot.setUser(user);
        telegramBot.setBotToken("token");
        telegramBot.setBotName("name");
        telegramBot.setIsActive(true);

        return telegramBot;
    }

    private List<TelegramBots> getTelegramBots() {
        List<TelegramBots> bots = new ArrayList<>();

        bots.add(getTelegramBot());

        TelegramBots bot = getTelegramBot();
        bot.setId(2L);
        bot.setBotName("name2");
        bot.setBotToken("token2");

        bots.add(bot);

        return bots;
    }
}

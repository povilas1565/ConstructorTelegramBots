package keldkemp.telegram.services.impl;

import keldkemp.telegram.configs.SettingsValue;
import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.repositories.TelegramBotsRepository;
import keldkemp.telegram.services.LockService;
import keldkemp.telegram.telegram.config.WebHookBot;
import keldkemp.telegram.telegram.handler.MessageHandler;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Service("telegramBotBeanService")
public class TelegramBotBeanServiceImpl extends BeanFactoryServiceImpl {

    final String LOCK_NAME = "TELEGRAM_BEAN_LOCK";

    @Autowired
    private TelegramBotsRepository telegramBotsRepository;

    @Autowired
    private SettingsValue settingsValue;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("telegramHandlerThread")
    private ExecutorService executorService;

    @Autowired
    private LockService lockService;

    @Override
    protected void createBean(String token) {
        lockService.doInLock(LOCK_NAME, () -> {
            if (super.checkBean(token)) {
                return;
            }
            String url = settingsValue.getAppUrl() + "/webhook/" + token;
            TelegramBots bot = telegramBotsRepository.getTelegramBotsByBotToken(token);
            if (!bot.getIsActive()) {
                return;
            }
            WebHookBot newBot = new WebHookBot(bot.getBotName(), bot.getBotToken(),
                    url, getHandler(), executorService);
            applicationContext.getBeanFactory().registerSingleton(token, newBot);
            setWebhook(url, token);
        });
    }

    @Override
    public WebHookBot getBean(String token) {
        try {
            return super.getBean(token, WebHookBot.class);
        } catch (NoSuchBeanDefinitionException e) {
            createBean(token);
        }
        return super.getBean(token, WebHookBot.class);
    }

    @Override
    public void deleteBean(Object object) {
        if (object instanceof TelegramBots) {
            lockService.doInLock(LOCK_NAME, () -> {
                String token = ((TelegramBots) object).getBotToken();
                super.deleteBean(token);
                deleteWebhook(token);
            });
        } else {
            throw new RuntimeException(object.getClass().getName() + " is not instanceof TelegramBots");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAll() {
        List<TelegramBots> telegramBots = telegramBotsRepository.getTelegramBotsByIsActive(true);
        telegramBots.forEach(bot -> createBean(bot.getBotToken()));
    }

    private void setWebhook(String url, String token) {
        String apiUrl = "https://api.telegram.org/bot" + token + "/setWebhook?url=" + url;
        restTemplate.getForEntity(apiUrl, String.class);
    }

    private void deleteWebhook(String token) {
        String apiUrl = "https://api.telegram.org/bot" + token + "/deleteWebhook";
        try {
            restTemplate.getForEntity(apiUrl, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Нету бота с таким токеном!", e);
        }
    }

    private MessageHandler getHandler() {
        return applicationContext.getBean("messageHandler", MessageHandler.class);
    }
}

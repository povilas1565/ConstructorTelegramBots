package keldkemp.telegram.telegram.config;

import keldkemp.telegram.telegram.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.ExecutorService;


public class WebHookBot extends TelegramWebhookBot {

    Logger logger = LoggerFactory.getLogger(WebHookBot.class);

    private final String botToken;
    private final String botPath;
    private final String botUsername;
    private final MessageHandler messageHandler;
    private final ExecutorService executorService;

    public WebHookBot(String botUsername, String botToken, String botPath, MessageHandler handler, ExecutorService executorService) {
        super();
        this.botPath = botPath;
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.messageHandler = handler;
        this.executorService = executorService;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        executorService.execute(() -> handleMessage(update));
        return null;
    }

    //TODO: Refactor
    private void handleMessage(Update update) {
        List<? extends BotApiMethod<?>> messages = messageHandler.handle(update, getBotToken());
        if (update.hasCallbackQuery()) {
            try {
                execute(new AnswerCallbackQuery(update.getCallbackQuery().getId()));
            } catch (Exception ignored) {

            }
        }
        messages.forEach(message -> {
            try {
                execute(message);
            } catch (Exception ignored) {

            }
        });
    }

    public void execute(List<? extends BotApiMethod<?>> messages) {
        messages.forEach(message -> {
            try {
                execute(message);
            } catch (Exception e) {
                logger.error("Error while executing message in schedule", e);
            }
        });
    }
}

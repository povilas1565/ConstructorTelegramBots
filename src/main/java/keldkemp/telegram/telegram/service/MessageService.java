package keldkemp.telegram.telegram.service;

import keldkemp.telegram.models.TelegramStages;
import keldkemp.telegram.telegram.domain.MessageTypes;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface MessageService {

    List<? extends BotApiMethod<?>> getMessages(TelegramStages stage, Message tMessage, MessageTypes type);
}

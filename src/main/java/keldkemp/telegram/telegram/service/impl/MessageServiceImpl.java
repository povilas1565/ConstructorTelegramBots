package keldkemp.telegram.telegram.service.impl;

import keldkemp.telegram.models.TelegramMessages;
import keldkemp.telegram.models.TelegramStages;
import keldkemp.telegram.repositories.TelegramMessagesRepository;
import keldkemp.telegram.telegram.domain.KeyboardTypes;
import keldkemp.telegram.telegram.domain.MessageTypes;
import keldkemp.telegram.telegram.domain.VariableTypes;
import keldkemp.telegram.telegram.service.KeyboardService;
import keldkemp.telegram.telegram.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    private final Map<String, VariableTypes> CONST_VARIABLE = new HashMap<>() {{
        put("{user.firstName}", VariableTypes.USER_FIRSTNAME);
        put("{user.lastName}", VariableTypes.USER_LASTNAME);
        put("{user.userName}", VariableTypes.USER_USERNAME);
        put("{user.id}", VariableTypes.USER_ID);
    }};

    @Autowired
    private TelegramMessagesRepository tMessagesRepository;

    @Autowired
    private KeyboardService keyboardService;

    @Override
    public List<? extends BotApiMethod<?>> getMessages(TelegramStages stage, Message tMessage, MessageTypes type) {
        TelegramMessages message = tMessagesRepository.getTelegramMessagesByTelegramStageId(stage.getId());
        KeyboardTypes keyboardType = keyboardService.getKeyboardType(stage);

        if (type == MessageTypes.EDIT_MESSAGE) {
            if (KeyboardTypes.REPLY_KEYBOARD == keyboardType) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(tMessage.getChatId().toString());
                deleteMessage.setMessageId(tMessage.getMessageId());

                SendMessage sendMessage = new SendMessage(tMessage.getChatId().toString(),
                        getMessageText(message.getMessageText(), tMessage));
                sendMessage.setReplyMarkup(keyboardService.getKeyboard(stage));

                return List.of(deleteMessage, sendMessage);
            } else {
                EditMessageText editMessageText = new EditMessageText(getMessageText(message.getMessageText(), tMessage));
                editMessageText.setChatId(tMessage.getChatId().toString());
                editMessageText.setMessageId(tMessage.getMessageId());
                if (keyboardType != null) {
                    editMessageText.setReplyMarkup(keyboardService.getKeyboard(stage));
                }

                return List.of(editMessageText);
            }
        } else if (type == MessageTypes.SEND_MESSAGE) {
            SendMessage sendMessage = new SendMessage(tMessage.getChatId().toString(),
                    getMessageText(message.getMessageText(), tMessage));
            if (keyboardType != null) {
                sendMessage.setReplyMarkup(keyboardService.getKeyboard(stage));
            }

            return List.of(sendMessage);
        }
        return null;
    }

    private String getMessageText(String text, Message tMessage) {
        List<String> newText = new ArrayList<>();
        newText.add(text);

        CONST_VARIABLE.forEach((k, v) -> {
            String info = getInfoInTelegramMessage(tMessage, v);
            if (info != null) {
                newText.set(0, newText.get(0).replace(k, info));
            }
        });
        return newText.get(0);
    }

    private String getInfoInTelegramMessage(Message tMessage, VariableTypes type) {
        return switch (type) {
            case USER_ID -> tMessage.getChat().getId().toString();
            case USER_USERNAME -> tMessage.getChat().getUserName();
            case USER_FIRSTNAME -> tMessage.getChat().getFirstName();
            case USER_LASTNAME -> tMessage.getChat().getLastName();
        };
    }
}

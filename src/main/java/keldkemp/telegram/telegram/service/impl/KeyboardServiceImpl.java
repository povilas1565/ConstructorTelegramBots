package keldkemp.telegram.telegram.service.impl;

import keldkemp.telegram.models.*;
import keldkemp.telegram.repositories.TelegramButtonsRepository;
import keldkemp.telegram.repositories.TelegramKeyboardRowsRepository;
import keldkemp.telegram.repositories.TelegramKeyboardsRepository;
import keldkemp.telegram.telegram.domain.KeyboardTypes;
import keldkemp.telegram.telegram.service.KeyboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardServiceImpl implements KeyboardService {

    @Autowired
    private TelegramKeyboardsRepository tKeyboardsRepository;

    @Autowired
    private TelegramKeyboardRowsRepository tKeyboardRowsRepository;

    @Autowired
    private TelegramButtonsRepository tButtonsRepository;

    @Override
    public <T extends ReplyKeyboard> T getKeyboard(TelegramStages stage) {
        TelegramKeyboards stageKeyboard = tKeyboardsRepository.getTelegramKeyboardsByTelegramStageId(stage.getId());
        if (stageKeyboard.getTelegramKeyboardType().getName().equals("InlineKeyboardMarkup")) {
            return (T) getInlineKeyboard(stageKeyboard);
        }
        else if (stageKeyboard.getTelegramKeyboardType().getName().equals("ReplyKeyboardMarkup")) {
            return (T) getReplyKeyboard(stageKeyboard);
        }
        return null;
    }

    @Override
    public <T extends ReplyKeyboard> T getKeyboard(TelegramStages stage, KeyboardTypes type) {
        TelegramKeyboards stageKeyboard = tKeyboardsRepository.getTelegramKeyboardsByTelegramStageId(stage.getId());
        if (type == KeyboardTypes.INLINE_KEYBOARD) {
            return (T) getInlineKeyboard(stageKeyboard);
        }
        else if (type == KeyboardTypes.REPLY_KEYBOARD) {
            return (T) getReplyKeyboard(stageKeyboard);
        }
        return null;
    }

    @Override
    public KeyboardTypes getKeyboardType(TelegramStages stage) {
        TelegramKeyboards keyboard = tKeyboardsRepository.getTelegramKeyboardsByTelegramStageId(stage.getId());
        if (keyboard == null) {
            return null;
        }
        return keyboard.getTelegramKeyboardType().getName().equals("InlineKeyboardMarkup") ?
                KeyboardTypes.INLINE_KEYBOARD : KeyboardTypes.REPLY_KEYBOARD;
    }

    public <T extends ReplyKeyboard> T getKeyboard(TelegramKeyboards stageKeyboard, Class<T> tClass) {
        if (tClass == InlineKeyboardMarkup.class) {
            return tClass.cast(getInlineKeyboard(stageKeyboard));
        } else if (tClass == ReplyKeyboardMarkup.class) {
            return tClass.cast(getReplyKeyboard(stageKeyboard));
        }
        return null;
    }

    public InlineKeyboardMarkup getInlineKeyboard(TelegramKeyboards stageKeyboard) {
        List<TelegramKeyboardRows> stageKeyboardRows = tKeyboardRowsRepository.getTelegramKeyboardRowsByTelegramKeyboardIdOrderByOrd(stageKeyboard.getId());

        return getInlineKeyboard(stageKeyboardRows);
    }

    public ReplyKeyboardMarkup getReplyKeyboard(TelegramKeyboards stageKeyboard) {
        List<TelegramKeyboardRows> stageKeyboardRows = tKeyboardRowsRepository.getTelegramKeyboardRowsByTelegramKeyboardIdOrderByOrd(stageKeyboard.getId());

        return getReplyKeyboard(stageKeyboardRows);
    }

    private InlineKeyboardMarkup getInlineKeyboard(List<TelegramKeyboardRows> keyboardRows) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboardRows.forEach(keyboardRow -> {
            List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
            List<TelegramButtons> buttons = tButtonsRepository.getTelegramButtonsByTelegramKeyboardRowOrderByButtonOrd(keyboardRow);

            buttons.forEach(button -> {
                InlineKeyboardButton sendButton = new InlineKeyboardButton(button.getButtonText());

                if (button.getButtonLink() != null) {
                    sendButton.setUrl(button.getButtonLink());
                } else {
                    sendButton.setCallbackData(button.getCallbackData().getId().toString());
                }

                keyboardButtons.add(sendButton);
            });
            keyboard.add(keyboardButtons);
        });

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getReplyKeyboard(List<TelegramKeyboardRows> keyboardRows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> replyKeyboardRows = new ArrayList<>();

        keyboardRows.forEach(keyboardRow -> {
            KeyboardRow keyboardButtons = new KeyboardRow();
            List<TelegramButtons> buttons = tButtonsRepository.getTelegramButtonsByTelegramKeyboardRowOrderByButtonOrd(keyboardRow);

            buttons.forEach(button -> keyboardButtons.add(button.getButtonText()));
            replyKeyboardRows.add(keyboardButtons);
        });

        replyKeyboardMarkup.setKeyboard(replyKeyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }
}

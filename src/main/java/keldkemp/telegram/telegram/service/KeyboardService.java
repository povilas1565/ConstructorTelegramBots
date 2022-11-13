package keldkemp.telegram.telegram.service;

import keldkemp.telegram.models.TelegramStages;
import keldkemp.telegram.telegram.domain.KeyboardTypes;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface KeyboardService {

    <T extends ReplyKeyboard> T  getKeyboard(TelegramStages stage, KeyboardTypes type);

    <T extends ReplyKeyboard> T getKeyboard(TelegramStages stage);

    KeyboardTypes getKeyboardType(TelegramStages stage);
}

package keldkemp.telegram.services;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramKeyboardTypes;
import keldkemp.telegram.models.TelegramStages;
import keldkemp.telegram.rest.dto.telegram.TelegramStageTransferDto;

import java.util.List;

public interface TelegramBotService {

    /**
     * Get List telegram keyboard types.
     * @return List types
     */
    List<TelegramKeyboardTypes> getKeyboardTypes();

    /**
     * Get telegram stages by botId for current user.
     * @param botId bot id
     * @return List TelegramStages
     */
    List<TelegramStages> getStages(Long botId);

    /**
     * Save telegram stages by botId for current user.
     * @param telegramTransferDto stages
     * @param botId bot id
     * @return saved telegram stages
     */
    TelegramStageTransferDto saveStages(TelegramStageTransferDto telegramTransferDto, Long botId);

    /**
     * Delete telegram stages by botId for current user.
     * @param botId bot id
     */
    void deleteStages(Long botId);

    /**
     * Get List telegram bots for current user.
     * @return List
     */
    List<TelegramBots> getBots();

    /**
     * Get telegram bot by id.
     * @param id - telegram bot id
     * @return TelegramBots
     */
    TelegramBots getBot(Long id);

    /**
     * Save telegram bot by bot.
     * @param bot - telegram bot
     * @return Saved TelegramBots
     */
    TelegramBots save(TelegramBots bot);

    /**
     * Delete telegram bot by id.
     * @param id - telegram bot id
     */
    void delete(Long id);
}

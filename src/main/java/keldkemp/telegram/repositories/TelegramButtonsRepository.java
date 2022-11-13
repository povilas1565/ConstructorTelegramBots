package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramButtons;
import keldkemp.telegram.models.TelegramKeyboardRows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TelegramButtonsRepository extends JpaRepository<TelegramButtons, Long> {

    List<TelegramButtons> getTelegramButtonsByTelegramKeyboardRowIn(Collection<TelegramKeyboardRows> telegramKeyboardRow);

    List<TelegramButtons> getTelegramButtonsByTelegramKeyboardRowOrderByButtonOrd(TelegramKeyboardRows telegramKeyboardRow);

    @Query("select b from TelegramButtons b " +
            "inner join TelegramKeyboardRows r on b.telegramKeyboardRow.id = r.id " +
            "inner join TelegramKeyboards k on r.telegramKeyboard.id = k.id " +
            "inner join TelegramStages s on k.telegramStage.id = s.id " +
            "where s.telegramBot = :bot " +
            "and b.buttonText = :buttonText " +
            "and k.telegramKeyboardType.name = 'ReplyKeyboardMarkup'")
    TelegramButtons getTelegramButtonsByButtonTextAndBotAndReplyType(@Param("buttonText") String buttonText, @Param("bot") TelegramBots bot);

    void deleteAllByIdNotInAndTelegramKeyboardRowTelegramKeyboardTelegramStageTelegramBot(Collection<Long> id, TelegramBots bot);
}

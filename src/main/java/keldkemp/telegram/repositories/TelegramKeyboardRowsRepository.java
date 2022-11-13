package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramKeyboardRows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TelegramKeyboardRowsRepository extends JpaRepository<TelegramKeyboardRows, Long> {

    @Query("select b from TelegramBots b " +
            "inner join TelegramStages s on (b.id = s.telegramBot.id) " +
            "inner join TelegramKeyboards k on (s.id = k.telegramStage.id) " +
            "inner join TelegramKeyboardRows r on (k.id = r.telegramKeyboard.id) " +
            "where r.id = :rowId")
    TelegramBots getTelegramBotByRow(@Param("rowId") Long rowId);

    List<TelegramKeyboardRows> getTelegramKeyboardRowsByTelegramKeyboardIdOrderByOrd(Long keyboardId);

    void deleteAllByIdNotInAndTelegramKeyboardTelegramStageTelegramBot(Collection<Long> id, TelegramBots bot);
}

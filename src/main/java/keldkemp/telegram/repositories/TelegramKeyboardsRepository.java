package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramKeyboards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface TelegramKeyboardsRepository extends JpaRepository<TelegramKeyboards, Long> {

    @Query("select b from TelegramBots b " +
            "inner join TelegramStages s on (b.id = s.telegramBot.id) " +
            "inner join TelegramKeyboards k on (s.id = k.telegramStage.id) " +
            "where k.id = :keyboardId")
    TelegramBots getTelegramBotByKeyboard(@Param("keyboardId") Long keyboardId);

    TelegramKeyboards getTelegramKeyboardsByTelegramStageId(Long stageId);

    void deleteAllByIdNotInAndTelegramStageTelegramBot(Collection<Long> id, TelegramBots bot);
}

package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramStages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TelegramStagesRepository extends JpaRepository<TelegramStages, Long> {

    @Query("select stage from TelegramStages stage " +
            "where stage.telegramBot.id = :botId " +
            "and stage.previousStage is null " +
            "and stage.isScheduleActive = :isScheduleActive")
    TelegramStages getFirstStage(@Param("botId") Long botId, @Param("isScheduleActive") boolean isScheduleActive);

    void deleteAllByTelegramBot(TelegramBots telegramBot);

    void deleteAllByIdNotInAndTelegramBot(Collection<Long> id, TelegramBots bot);

    List<TelegramStages> getTelegramStagesByTelegramBot(TelegramBots telegramBot);

    List<TelegramStages> getAllByIdNotInAndTelegramBot(Collection<Long> id, TelegramBots bot);

    List<TelegramStages> getAllByIsScheduleActiveAndTelegramBotIsActive(Boolean isScheduleActive, Boolean botIsActive);
}

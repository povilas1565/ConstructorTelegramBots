package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface TelegramMessagesRepository extends JpaRepository<TelegramMessages, Long> {

    TelegramMessages getTelegramMessagesByTelegramStageId(Long stageId);

    void deleteAllByIdNotInAndTelegramStageTelegramBot(Collection<Long> id, TelegramBots bot);
}

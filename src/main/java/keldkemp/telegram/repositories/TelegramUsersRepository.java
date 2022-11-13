package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.TelegramUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelegramUsersRepository extends JpaRepository<TelegramUsers, Long> {

    List<TelegramUsers> getAllByTelegramBot(TelegramBots telegramBot);

    TelegramUsers getByTgUserIdAndTelegramBot(Long userId, TelegramBots telegramBot);
}

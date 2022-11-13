package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramBots;
import keldkemp.telegram.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelegramBotsRepository extends JpaRepository<TelegramBots, Long> {

    TelegramBots getTelegramBotsByBotToken(String token);

    List<TelegramBots> getTelegramBotsByIsActive(Boolean active);

    List<TelegramBots> getTelegramBotsByUser(Users user);
}

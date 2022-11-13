package keldkemp.telegram.repositories;

import keldkemp.telegram.models.TelegramKeyboardTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramKeyboardTypesRepository extends JpaRepository<TelegramKeyboardTypes, Long> {

}

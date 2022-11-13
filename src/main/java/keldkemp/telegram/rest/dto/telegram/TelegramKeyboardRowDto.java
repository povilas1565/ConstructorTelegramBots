package keldkemp.telegram.rest.dto.telegram;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TelegramKeyboardRowDto {
    private Long id;
    private Long ord;
    private List<TelegramButtonDto> telegramButtons;
}

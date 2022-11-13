package keldkemp.telegram.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class SettingsValue {

    @Value("${app.url}")
    private String appUrl;
}

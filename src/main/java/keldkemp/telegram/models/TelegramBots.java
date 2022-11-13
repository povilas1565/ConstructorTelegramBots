package keldkemp.telegram.models;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Data
public class TelegramBots {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_bots_seq")
    private Long id;

    @Column(name = "bot_name")
    private String botName;

    @Column(name = "bot_token", unique = true)
    private String botToken;

    @Column(name = "is_active")
    @ColumnDefault("true")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Lob
    @Column(name = "front_options")
    private String frontOptions;
}

package keldkemp.telegram.models;

import javax.persistence.*;

@Entity
public class TelegramMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_messages_seq")
    private Long id;

    @Column(length = 4000, name = "message_text")
    private String messageText;

    @ManyToOne
    @JoinColumn(name = "stage_id", referencedColumnName = "id")
    private TelegramStages telegramStage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public TelegramStages getTelegramStage() {
        return telegramStage;
    }

    public void setTelegramStage(TelegramStages telegramStage) {
        this.telegramStage = telegramStage;
    }
}

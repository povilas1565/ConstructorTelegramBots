package keldkemp.telegram.models;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TelegramStages {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_stages_seq")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "previous_stage", referencedColumnName = "id")
    private TelegramStages previousStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telegram_bot_id", referencedColumnName = "id")
    private TelegramBots telegramBot;

    @OneToMany(mappedBy = "telegramStage", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TelegramMessages> telegramMessages;

    @OneToMany(mappedBy = "telegramStage", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TelegramKeyboards> telegramKeyboards;

    @OneToMany(mappedBy = "callbackData", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TelegramButtons> telegramButtons;

    @Column(name = "front_node_id")
    private String frontNodeId;

    @Column(name = "is_schedule_active")
    @ColumnDefault("false")
    private Boolean isScheduleActive;

    @Column(name = "schedule_cron")
    private String scheduleCron;

    @Column(name = "schedule_data_time")
    private LocalDateTime scheduleDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPreviousStage() {
        if (previousStage == null) {
            return null;
        }
        return previousStage.getId();
    }

    public void setPreviousStage(Long previousStage) {
        TelegramStages stage = new TelegramStages();
        stage.setId(previousStage);

        this.previousStage = stage;
    }

    public TelegramBots getTelegramBot() {
        return telegramBot;
    }

    public void setTelegramBot(TelegramBots telegramBot) {
        this.telegramBot = telegramBot;
    }

    public List<TelegramMessages> getTelegramMessages() {
        return telegramMessages;
    }

    public void setTelegramMessages(List<TelegramMessages> telegramMessages) {
        this.telegramMessages = telegramMessages;
    }

    public void addTelegramMessage(TelegramMessages telegramMessage) {
        if (telegramMessage != null) {
            if (this.telegramMessages == null) {
                this.telegramMessages = new ArrayList<>();
            }
            telegramMessage.setTelegramStage(this);
            if (!this.telegramMessages.contains(telegramMessage)) {
                this.telegramMessages.add(telegramMessage);
            }
        }
    }

    public void addTelegramMessages(List<TelegramMessages> telegramMessages) {
        if (telegramMessages == null) {
            return;
        }
        telegramMessages.forEach(this::addTelegramMessage);
    }

    public List<TelegramKeyboards> getTelegramKeyboards() {
        return telegramKeyboards;
    }

    public void setTelegramKeyboards(List<TelegramKeyboards> telegramKeyboards) {
        this.telegramKeyboards = telegramKeyboards;
    }

    public void addTelegramKeyboard(TelegramKeyboards telegramKeyboard) {
        if (telegramKeyboard != null) {
            if (this.telegramKeyboards == null) {
                this.telegramKeyboards = new ArrayList<>();
            }
            telegramKeyboard.setTelegramStage(this);
            if (!this.telegramKeyboards.contains(telegramKeyboard)) {
                this.telegramKeyboards.add(telegramKeyboard);
            }
        }
    }

    public void addTelegramKeyboards(List<TelegramKeyboards> telegramKeyboards) {
        if (telegramKeyboards == null) {
            return;
        }
        telegramKeyboards.forEach(this::addTelegramKeyboard);
    }

    public List<TelegramButtons> getTelegramButtons() {
        return telegramButtons;
    }

    public void setTelegramButtons(List<TelegramButtons> telegramButtons) {
        this.telegramButtons = telegramButtons;
    }

    public String getFrontNodeId() {
        return frontNodeId;
    }

    public void setFrontNodeId(String frontNodeId) {
        this.frontNodeId = frontNodeId;
    }

    public boolean getIsScheduleActive() {
        return BooleanUtils.isTrue(isScheduleActive);
    }

    public void setIsScheduleActive(Boolean scheduleActive) {
        isScheduleActive = scheduleActive;
    }

    public String getScheduleCron() {
        return scheduleCron;
    }

    public void setScheduleCron(String scheduleCron) {
        this.scheduleCron = scheduleCron;
    }

    public LocalDateTime getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(LocalDateTime scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
    }
}

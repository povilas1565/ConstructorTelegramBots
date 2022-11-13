package keldkemp.telegram.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TelegramKeyboards {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_keyboards_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "keyboard_type_id", referencedColumnName = "id")
    private TelegramKeyboardTypes telegramKeyboardType;

    @ManyToOne
    @JoinColumn(name = "stage_id", referencedColumnName = "id")
    private TelegramStages telegramStage;

    @OneToMany(mappedBy = "telegramKeyboard", cascade = CascadeType.REMOVE)
    private List<TelegramKeyboardRows> telegramKeyboardRows;

    @Column(name = "front_node_id")
    private String frontNodeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TelegramKeyboardTypes getTelegramKeyboardType() {
        return telegramKeyboardType;
    }

    public void setTelegramKeyboardType(TelegramKeyboardTypes telegramKeyboardType) {
        this.telegramKeyboardType = telegramKeyboardType;
    }

    public TelegramStages getTelegramStage() {
        return telegramStage;
    }

    public void setTelegramStage(TelegramStages telegramStage) {
        this.telegramStage = telegramStage;
    }

    public List<TelegramKeyboardRows> getTelegramKeyboardRows() {
        return telegramKeyboardRows;
    }

    public void setTelegramKeyboardRows(List<TelegramKeyboardRows> telegramKeyboardRows) {
        this.telegramKeyboardRows = telegramKeyboardRows;
    }

    public void addTelegramKeyboardRow(TelegramKeyboardRows telegramKeyboardRow) {
        if (telegramKeyboardRow != null) {
            if (this.telegramKeyboardRows == null) {
                this.telegramKeyboardRows = new ArrayList<>();
            }
            telegramKeyboardRow.setTelegramKeyboard(this);
            if (!this.telegramKeyboardRows.contains(telegramKeyboardRow)) {
                this.telegramKeyboardRows.add(telegramKeyboardRow);
            }
        }
    }

    public void addTelegramKeyboardRows(List<TelegramKeyboardRows> telegramKeyboardRows) {
        if (telegramKeyboardRows == null) {
            return;
        }
        telegramKeyboardRows.forEach(this::addTelegramKeyboardRow);
    }

    public String getFrontNodeId() {
        return frontNodeId;
    }

    public void setFrontNodeId(String frontNodeId) {
        this.frontNodeId = frontNodeId;
    }
}

package keldkemp.telegram.models;

import javax.persistence.*;

@Entity
public class TelegramButtons {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_buttons_seq")
    private Long id;

    @Column(name = "button_text")
    private String buttonText;

    @Column(name = "button_link")
    private String buttonLink;

    @Column(name = "button_ord")
    private Long buttonOrd;

    @ManyToOne
    @JoinColumn(name = "data_stage_id", referencedColumnName = "id")
    private TelegramStages callbackData;

    @ManyToOne
    @JoinColumn(name = "keyboard_row_id", referencedColumnName = "id")
    private TelegramKeyboardRows telegramKeyboardRow;

    @Column(name = "front_node_id")
    private String frontNodeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public Long getButtonOrd() {
        return buttonOrd;
    }

    public void setButtonOrd(Long buttonOrd) {
        this.buttonOrd = buttonOrd;
    }

    public String getButtonLink() {
        return buttonLink;
    }

    public void setButtonLink(String buttonLink) {
        this.buttonLink = buttonLink;
    }

    public TelegramStages getCallbackData() {
        return callbackData;
    }

    public void setCallbackData(TelegramStages callbackData) {
        this.callbackData = callbackData;
    }

    public TelegramKeyboardRows getTelegramKeyboardRow() {
        return telegramKeyboardRow;
    }

    public void setTelegramKeyboardRow(TelegramKeyboardRows telegramKeyboardRow) {
        this.telegramKeyboardRow = telegramKeyboardRow;
    }

    public String getFrontNodeId() {
        return frontNodeId;
    }

    public void setFrontNodeId(String frontNodeId) {
        this.frontNodeId = frontNodeId;
    }
}

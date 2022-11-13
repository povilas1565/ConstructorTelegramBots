package keldkemp.telegram.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TelegramKeyboardRows {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_keyboard_rows_seq")
    private Long id;

    @Column(name = "keyboard_ord")
    private Long ord;

    @ManyToOne
    @JoinColumn(name = "keyboard_id", referencedColumnName = "id")
    private TelegramKeyboards telegramKeyboard;

    @OneToMany(mappedBy = "telegramKeyboardRow", cascade = CascadeType.REMOVE)
    private List<TelegramButtons> telegramButtons;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrd() {
        return ord;
    }

    public void setOrd(Long ord) {
        this.ord = ord;
    }

    public TelegramKeyboards getTelegramKeyboard() {
        return telegramKeyboard;
    }

    public void setTelegramKeyboard(TelegramKeyboards telegramKeyboard) {
        this.telegramKeyboard = telegramKeyboard;
    }

    public List<TelegramButtons> getTelegramButtons() {
        return telegramButtons;
    }

    public void setTelegramButtons(List<TelegramButtons> telegramButtons) {
        this.telegramButtons = telegramButtons;
    }

    public void addTelegramButton(TelegramButtons telegramButton) {
        if (telegramButton != null) {
            if (this.telegramButtons == null) {
                this.telegramButtons = new ArrayList<>();
            }
            telegramButton.setTelegramKeyboardRow(this);
            if (!this.telegramButtons.contains(telegramButton)) {
                this.telegramButtons.add(telegramButton);
            }
        }
    }

    public void addTelegramButtons(List<TelegramButtons> telegramButtons) {
        if (telegramButtons == null) {
            return;
        }
        telegramButtons.forEach(this::addTelegramButton);
    }
}

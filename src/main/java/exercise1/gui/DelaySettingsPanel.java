package exercise1.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

final class DelaySettingsPanel extends JPanel {

    private final int EXIT_UPPER_LIMIT = 60;
    private final int EXIT_LOWER_LIMIT = 20;
    private final int ENTRY_UPPER_LIMIT = 30;
    private final int ENTRY_LOWER_LIMIT = 10;

    private final DelayControl exitDelay;
    private final DelayControl entryDelay;

    DelaySettingsPanel() {
        setLayout(new GridLayout(2, 1, 0, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        this.exitDelay = new DelayControl(
                "ExitDelay ( " + EXIT_LOWER_LIMIT + " - " + EXIT_UPPER_LIMIT + " )",
                30,
                EXIT_UPPER_LIMIT,
                EXIT_LOWER_LIMIT
            );

        this.entryDelay = new DelayControl(
                "EntryDelay ( " + ENTRY_LOWER_LIMIT + " - " + ENTRY_UPPER_LIMIT + " )",
                30,
                ENTRY_UPPER_LIMIT,
                ENTRY_LOWER_LIMIT
            );

        add(exitDelay);
        add(entryDelay);
    }

    public void setExitDelay(int seconds) {
        this.exitDelay.setSeconds(seconds);
    }

    public void setEntryDelay(int seconds) {
        this.entryDelay.setSeconds(seconds);
    }
}

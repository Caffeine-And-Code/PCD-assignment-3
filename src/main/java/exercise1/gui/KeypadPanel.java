package exercise1.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

final class KeypadPanel extends JPanel {
    private static final int MAX_DIGITS_NUMBER = 5;

    private String enteredDigits;
    private final JLabel display;

    KeypadPanel() {

        this.enteredDigits = "";

        setLayout(new BorderLayout(0, 8));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        this.display = new JLabel("", SwingConstants.CENTER);
        this.display.setPreferredSize(new Dimension(0, 34));
        this.display.setBorder(new LineBorder(Color.GRAY, 1, true));

        JPanel keys = new JPanel(new GridLayout(3, 3, 6, 6));
        keys.setBackground(Color.WHITE);

        for (int value = 1; value <= 9; value++) {
            int digit = value;
            JButton key = new JButton(Integer.toString(digit));
            key.setFocusPainted(false);
            key.addActionListener(event -> pinButtonPressed(digit));
            keys.add(key);
        }

        add(display, BorderLayout.NORTH);
        add(keys, BorderLayout.CENTER);
    }

    private void pinButtonPressed(int digit) {
        this.enteredDigits += digit;



        if (enteredDigits.length() >= MAX_DIGITS_NUMBER) {
            this.enteredDigits = "";
        }
        display.setText("*".repeat(enteredDigits.length()));
    }
}

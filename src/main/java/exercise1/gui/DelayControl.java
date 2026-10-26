package exercise1.gui;

import javax.swing.*;
import java.awt.*;

final class DelayControl extends JPanel {
    private int seconds;
    private final int upperLimit;
    private final int lowerLimit;
    private final JLabel value;

    DelayControl(
            String name,
            int seconds,
            int upperLimit,
            int lowerLimit) {
        this.seconds = seconds;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;

        setLayout(new BorderLayout(0, 4));
        setBackground(Color.WHITE);

        JLabel title = new JLabel(name, SwingConstants.CENTER);
        this.value = new JLabel(this.seconds + " s", SwingConstants.CENTER);

        JButton decrease = new JButton("-");
        decrease.setPreferredSize(new Dimension(30, decrease.getPreferredSize().height));
        JButton increase = new JButton("+");
        increase.setPreferredSize(new Dimension(30, increase.getPreferredSize().height));
        decrease.setFocusPainted(false);
        increase.setFocusPainted(false);
        decrease.addActionListener(event -> setSeconds(this.seconds - 1));
        increase.addActionListener(event -> setSeconds(this.seconds + 1));

        JPanel stepper = new JPanel(new BorderLayout(5, 0));
        stepper.setBackground(Color.WHITE);
        stepper.add(decrease, BorderLayout.WEST);
        stepper.add(value, BorderLayout.CENTER);
        stepper.add(increase, BorderLayout.EAST);

        add(title, BorderLayout.NORTH);
        add(stepper, BorderLayout.CENTER);
    }

    public void setSeconds(int seconds) {
        if (seconds >= this.lowerLimit && seconds <= this.upperLimit) {
            this.seconds = seconds;
            this.value.setText(this.seconds + " s");
        }
    }
}

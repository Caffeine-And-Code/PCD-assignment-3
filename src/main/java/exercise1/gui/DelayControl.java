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
        JButton increase = new JButton("+");
        for (JButton button : new JButton[]{decrease, increase}) {
            button.setPreferredSize(new Dimension(30, 30));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setFocusPainted(false);
        }
        decrease.addActionListener(event -> setSeconds(this.seconds - 1));
        increase.addActionListener(event -> setSeconds(this.seconds + 1));

        this.value.setPreferredSize(new Dimension(50, 30));
        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        stepper.setBackground(Color.WHITE);
        stepper.add(decrease);
        stepper.add(value);
        stepper.add(increase);

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

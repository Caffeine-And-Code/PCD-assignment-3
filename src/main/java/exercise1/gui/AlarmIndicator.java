package exercise1.gui;

import javax.swing.*;
import java.awt.*;

final class AlarmIndicator extends JComponent {
    private boolean isOnAlarm;

    AlarmIndicator() {
        setPreferredSize(new Dimension(26, 26));
        setToolTipText("Alarm indicator");
        this.isOnAlarm = false;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(this.isOnAlarm) {
            graphics2D.setColor(new Color(255, 0,0));
            graphics2D.fillOval(3, 3, 19, 19);
        } else {
            graphics2D.setColor(new Color(69, 69, 69));
            graphics2D.drawOval(3, 3, 19, 19);
        }
        graphics2D.dispose();
    }

    public void setStatus(boolean isOnAlarm) {
        this.isOnAlarm = isOnAlarm;
        this.repaint();
    }
}

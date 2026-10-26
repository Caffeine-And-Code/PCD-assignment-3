package exercise1.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

final class ZoneOverviewPanel extends JPanel {
    ZoneOverviewPanel(Map<String, List<String>> sensorsByZone) {
        setLayout(new BorderLayout(0, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JPanel alarm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        alarm.setBackground(Color.WHITE);
        alarm.add(new JLabel("Alarm"));
        AlarmIndicator alarmIndicator = new AlarmIndicator();
        alarm.add(alarmIndicator);

        ZonePopupMenu popupMenu = new ZonePopupMenu(sensorsByZone);
        ZoneCanvas zoneCanvas = new ZoneCanvas(sensorsByZone.keySet().stream().toList(), popupMenu);

        add(alarm, BorderLayout.NORTH);
        add(zoneCanvas, BorderLayout.CENTER);
    }
}

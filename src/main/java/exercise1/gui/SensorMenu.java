package exercise1.gui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

final class SensorMenu extends JMenu {
    SensorMenu(String sensorName, String currentZone, List<String> zones) {
        super(sensorName);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    sendSignal(sensorName);
                }
            }
        });

        for (String destinationZone : zones) {
            JMenuItem destination = new JMenuItem(destinationZone);
            destination.setEnabled(!destinationZone.equals(currentZone));
            destination.addActionListener(event -> System.out.println(
                    "Spostato sensore '" + sensorName + "' da '" + currentZone
                            + "' a '" + destinationZone + "' (mock)"
            ));
            add(destination);
        }
    }

    private void sendSignal(String sensorName) {
        System.out.println("Segnale inviato dal sensore '" + sensorName + "' (mock)");
    }
}

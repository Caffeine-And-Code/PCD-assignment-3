package exercise1.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.List;

final class SensorMenu extends Menu {
    SensorMenu(String sensorName, String currentZone, List<String> zones) {
        super(sensorName);
        setOnShowing(event -> sendSignal(sensorName));

        for (String destinationZone : zones) {
            MenuItem destination = new MenuItem(destinationZone);
            destination.setDisable(destinationZone.equals(currentZone));
            destination.setOnAction(event -> System.out.println(
                    "Spostato sensore '" + sensorName + "' da '" + currentZone
                            + "' a '" + destinationZone + "' (mock)"
            ));
            getItems().add(destination);
        }
    }

    private void sendSignal(String sensorName) {
        System.out.println("Segnale inviato dal sensore '" + sensorName + "' (mock)");
    }
}

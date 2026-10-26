package exercise1.gui;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.List;
import java.util.Map;

final class ZonePopupMenu extends ContextMenu {
    private final List<String> zones;
    private final Map<String, List<String>> sensorsByZone;

    ZonePopupMenu(Map<String, List<String>> sensorsByZone) {
        this.sensorsByZone = sensorsByZone;
        zones = sensorsByZone.keySet().stream().toList();
    }

    void showFor(Node owner, double screenX, double screenY, String zone) {
        rebuild(zone);
        show(owner, screenX, screenY);
    }

    private void rebuild(String zone) {
        getItems().clear();

        MenuItem title = new MenuItem(zone);
        title.setDisable(true);
        getItems().addAll(title, new SeparatorMenuItem());

        List<String> sensors = sensorsByZone.getOrDefault(zone, List.of());
        if (sensors.isEmpty()) {
            MenuItem empty = new MenuItem("Nessun sensore");
            empty.setDisable(true);
            getItems().add(empty);
            return;
        }

        for (String sensor : sensors) {
            getItems().add(new SensorMenu(sensor, zone, zones));
        }
    }
}

package exercise1.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ZoneOverviewPanel extends BorderPane {
    private final ZoneCanvas zoneCanvas;

    ZoneOverviewPanel() {
        final var sensorsByZone = new HashMap<String, List<String>>(Map.of());
        setPadding(new Insets(8));
        setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 8;");

        AlarmIndicator alarmIndicator = new AlarmIndicator();
        HBox alarm = new HBox(6, new Label("Alarm"), alarmIndicator);
        alarm.setAlignment(Pos.CENTER_RIGHT);

        ZonePopupMenu popupMenu = new ZonePopupMenu(sensorsByZone);
        zoneCanvas = new ZoneCanvas(sensorsByZone.keySet().stream().toList(), popupMenu);

        setTop(alarm);
        setCenter(zoneCanvas);
        BorderPane.setMargin(zoneCanvas, new Insets(6, 0, 0, 0));
    }

    public ZoneCanvas getZoneCanvas() {
        return zoneCanvas;
    }
}

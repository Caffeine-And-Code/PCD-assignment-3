package exercise1.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.pekko.actor.ActorRef;

import java.util.*;

public final class ZoneOverviewPanel extends BorderPane {
    private final ObservableMap<String, List<String>> sensorsByZone =
            FXCollections.observableMap(new LinkedHashMap<>());

    private final ZoneCanvas zoneCanvas;
    private final AlarmIndicator alarmIndicator;
    private final Label systemStateLabel = new Label();

    public ZoneOverviewPanel(ActorRef uiActor) {
        setPadding(new Insets(8));
        setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 8;");

        alarmIndicator = new AlarmIndicator();
        HBox alarm = new HBox(6, systemStateLabel, alarmIndicator);
        alarm.setAlignment(Pos.CENTER_RIGHT);

        ZonePopupMenu popupMenu = new ZonePopupMenu(sensorsByZone, uiActor);
        zoneCanvas = new ZoneCanvas(List.of(), popupMenu);

        setTop(alarm);
        setCenter(zoneCanvas);
        BorderPane.setMargin(zoneCanvas, new Insets(6, 0, 0, 0));
    }

    public void setSensorsByZone(Map<String, List<String>> newMap) {
        sensorsByZone.clear();
        sensorsByZone.putAll(newMap);
        zoneCanvas.setLabels(List.copyOf(sensorsByZone.keySet().stream().sorted().toList()));
    }

    public Label getSystemStateLabel() {
        return systemStateLabel;
    }

    public void setAlarmActive(boolean active) {
        alarmIndicator.setStatus(active);
    }
}
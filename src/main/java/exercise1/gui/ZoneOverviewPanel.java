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

    private final javafx.scene.layout.VBox selection = new javafx.scene.layout.VBox(4);
    private final Map<String, javafx.scene.control.CheckBox> choices = new LinkedHashMap<>();
    private Map<String, Boolean> zoneStates = Map.of();
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

        javafx.scene.control.Button all = new javafx.scene.control.Button("Select all zones");
        all.setOnAction(event -> choices.values().forEach(box -> box.setSelected(true)));
        selection.getChildren().add(all);
        setBottom(selection);
        setTop(alarm);
        setCenter(zoneCanvas);
        BorderPane.setMargin(zoneCanvas, new Insets(6, 0, 0, 0));
    }

    public void setSensorsByZone(Map<String, List<String>> newMap) {
        Map<String, Boolean> previous = new HashMap<>();
        choices.forEach((zone, box) -> previous.put(zone, box.isSelected()));
        choices.clear();
        if (selection.getChildren().size() > 1) selection.getChildren().remove(1, selection.getChildren().size());
        newMap.keySet().stream().sorted().forEach(zone -> {
            var box = new javafx.scene.control.CheckBox(zone);
            box.setSelected(previous.getOrDefault(zone, true));
            choices.put(zone, box);
            selection.getChildren().add(box);
        });
        sensorsByZone.clear();
        sensorsByZone.putAll(newMap);
        zoneCanvas.setLabels(List.copyOf(sensorsByZone.keySet().stream().sorted().toList()));
        zoneCanvas.setZoneStates(zoneStates);
    }

    public List<String> getSelectedZones() {
        return choices.entrySet().stream().filter(e -> e.getValue().isSelected()).map(Map.Entry::getKey).toList();
    }

    public void setZoneStates(Map<String, Boolean> states) {
        zoneStates = Map.copyOf(states);
        zoneCanvas.setZoneStates(states);
    }

    public Label getSystemStateLabel() {
        return systemStateLabel;
    }

    public void setAlarmActive(boolean active) {
        alarmIndicator.setStatus(active);
    }
}
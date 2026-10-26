package exercise1.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ZoneOverviewPanel extends BorderPane {
    // unica fonte di verità, condivisa con il menu (LinkedHashMap per mantenere l'ordine dei tile)
    private final ObservableMap<String, List<String>> sensorsByZone =
            FXCollections.observableMap(new LinkedHashMap<>());

    private final ZoneCanvas zoneCanvas;
    private final AlarmIndicator alarmIndicator = new AlarmIndicator();
    private final Label systemStateLabel = new Label();

    public ZoneOverviewPanel() {
        setPadding(new Insets(8));
        setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 8;");

        AlarmIndicator alarmIndicator = new AlarmIndicator();
        HBox alarm = new HBox(6, systemStateLabel, alarmIndicator);
        alarm.setAlignment(Pos.CENTER_RIGHT);

        ZonePopupMenu popupMenu = new ZonePopupMenu(sensorsByZone);
        zoneCanvas = new ZoneCanvas(List.of(), popupMenu);

        setTop(alarm);
        setCenter(zoneCanvas);
        BorderPane.setMargin(zoneCanvas, new Insets(6, 0, 0, 0));
    }

    /** Sostituisce tutte le zone e i relativi sensori. */
    public void setSensorsByZone(Map<String, List<String>> newMap) {
        sensorsByZone.clear();
        sensorsByZone.putAll(newMap);
        zoneCanvas.setLabels(List.copyOf(sensorsByZone.keySet()));
    }

    public Label getSystemStateLabel() {
        return systemStateLabel;
    }


    /** Aggiorna i sensori di una singola zona; se la zona è nuova aggiunge anche il tile. */
    public void updateZoneSensors(String zone, List<String> sensors) {
        boolean isNewZone = !sensorsByZone.containsKey(zone);
        sensorsByZone.put(zone, List.copyOf(sensors));
        if (isNewZone) {
            zoneCanvas.setLabels(List.copyOf(sensorsByZone.keySet()));
        }
    }

    public void setAlarmActive(boolean active) {
        alarmIndicator.setStatus(active); // adatta al metodo reale di AlarmIndicator
    }
}
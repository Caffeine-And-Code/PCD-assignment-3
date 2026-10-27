package exercise1.gui;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

final class ZoneCanvas extends TilePane {
    private static final int TILE_WIDTH = 120;
    private static final int TILE_HEIGHT = 90;
    private static final int PADDING = 3;
    private static final double SATURATION = 0.25;
    private static final double BRIGHTNESS = 0.95;

    private final ListProperty<String> labels =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ZonePopupMenu menu;
    private Map<String, Boolean> zoneStates = new HashMap<>();

    void setZoneStates(Map<String, Boolean> states) {
        zoneStates = Map.copyOf(states);
        rebuildTiles();
    }

    ZoneCanvas(List<String> initialLabels, ZonePopupMenu menu) {
        this.menu = menu;

        setPadding(new Insets(PADDING));
        setHgap(PADDING);
        setVgap(PADDING);
        setPrefTileWidth(TILE_WIDTH);
        setPrefTileHeight(TILE_HEIGHT);
        setTileAlignment(Pos.CENTER);
        setStyle("-fx-background-color: white;");

        setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                menu.hide();
            }
        });

        labels.addListener((ListChangeListener<String>) change -> rebuildTiles());

        setLabels(initialLabels);
    }

    // --- API ---

    void setLabels(List<String> newLabels) {
        labels.setAll(newLabels); // un solo evento di change
    }

    List<String> getLabels() {
        return labels.get();
    }

    ListProperty<String> labelsProperty() {
        return labels;
    }

    // --- interno ---

    private void rebuildTiles() {
        getChildren().clear();
        for (String label : labels) {
            getChildren().add(createTile(label));
        }
    }

    private StackPane createTile(String label) {
        double hue = new Random(label.hashCode()).nextDouble() * 360;
        Color color = Color.hsb(hue, SATURATION, BRIGHTNESS);

        StackPane tile = new StackPane(new Label(label + (zoneStates.getOrDefault(label, false) ? "\nActive" : "\nInactive")));
        tile.setMinSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setMaxSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setStyle(
                "-fx-background-color: " + toHex(color) + ";"
                        + "-fx-border-color: #454545;"
        );

        tile.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                menu.showFor(tile, event.getScreenX(), event.getScreenY(), label);
                event.consume();
            }
        });

        return tile;
    }

    private String toHex(Color color) {
        return String.format(
                "#%02X%02X%02X",
                Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255)
        );
    }
}
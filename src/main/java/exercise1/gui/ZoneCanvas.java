package exercise1.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public class ZoneCanvas extends TilePane {
    private static final int TILE_WIDTH = 120;
    private static final int TILE_HEIGHT = 90;
    private static final int PADDING = 3;
    private static final double SATURATION = 0.25;
    private static final double BRIGHTNESS = 0.95;

    public ZoneCanvas(List<String> labels, ZonePopupMenu menu) {
        setPadding(new Insets(PADDING));
        setHgap(PADDING);
        setVgap(PADDING);
        setPrefTileWidth(TILE_WIDTH);
        setPrefTileHeight(TILE_HEIGHT);
        setTileAlignment(Pos.CENTER);
        setStyle("-fx-background-color: white;");

        for (String label : labels) {
            StackPane tile = createTile(label);
            tile.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    menu.showFor(tile, event.getScreenX(), event.getScreenY(), label);
                    event.consume();
                }
            });
            getChildren().add(tile);
        }

        setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                menu.hide();
            }
        });
    }

    private StackPane createTile(String label) {
        double hue = new Random(label.hashCode()).nextDouble() * 360;
        Color color = Color.hsb(hue, SATURATION, BRIGHTNESS);

        StackPane tile = new StackPane(new Label(label));
        tile.setMinSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setMaxSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setStyle(
                "-fx-background-color: " + toHex(color) + ";"
                        + "-fx-border-color: #454545;"
        );
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

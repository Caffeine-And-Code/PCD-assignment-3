package exercise1.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

final class KeypadPanel extends VBox {
    private static final int MAX_DIGITS_NUMBER = 5;

    private String enteredDigits = "";
    private final Label display = new Label();

    KeypadPanel() {
        super(8);
        setPadding(new Insets(8));
        setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 8;");

        display.setAlignment(Pos.CENTER);
        display.setMaxWidth(Double.MAX_VALUE);
        display.setMinHeight(34);
        display.setStyle("-fx-border-color: gray; -fx-border-radius: 6;");

        GridPane keys = new GridPane();
        keys.setHgap(6);
        keys.setVgap(6);
        keys.setAlignment(Pos.CENTER);

        for (int value = 1; value <= 9; value++) {
            int digit = value;
            Button key = new Button(Integer.toString(digit));
            key.setPrefSize(48, 36);
            key.setMaxWidth(Double.MAX_VALUE);
            key.setFocusTraversable(false);
            key.setOnAction(event -> pinButtonPressed(digit));
            keys.add(key, (value - 1) % 3, (value - 1) / 3);
        }

        getChildren().addAll(display, keys);
    }

    private void pinButtonPressed(int digit) {
        enteredDigits += digit;
        if (enteredDigits.length() >= MAX_DIGITS_NUMBER) {
            enteredDigits = "";
        }
        display.setText("*".repeat(enteredDigits.length()));
    }
}

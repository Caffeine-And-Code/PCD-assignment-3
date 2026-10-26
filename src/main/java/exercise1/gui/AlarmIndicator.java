package exercise1.gui;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

final class AlarmIndicator extends StackPane {
    private final Circle indicator = new Circle(10);

    AlarmIndicator() {
        setMinSize(26, 26);
        setPrefSize(26, 26);
        setMaxSize(26, 26);
        Tooltip.install(this, new Tooltip("Alarm indicator"));
        setStatus(false);
        getChildren().add(indicator);
    }

    public void setStatus(boolean isOnAlarm) {      indicator.setFill(isOnAlarm ? Color.RED : Color.TRANSPARENT);
        indicator.setStroke(isOnAlarm ? Color.RED : Color.rgb(69, 69, 69));
    }
}

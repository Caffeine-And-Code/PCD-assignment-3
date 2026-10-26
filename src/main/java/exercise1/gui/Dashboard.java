package exercise1.gui;

import exercise1.protocols.MessagesProtocol;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.pekko.actor.ActorRef;

public final class Dashboard extends BorderPane {
    public Dashboard(ActorRef uiActor) {
        VBox controls = new VBox(10);
        final var errorLabel = new Label("");
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-text-fill: #b42318; -fx-font-size: 13px; -fx-font-weight: bold;");
        controls.setPrefWidth(200);
        controls.getChildren().add(new KeypadPanel(uiActor));

        DelaySettingsPanel delaySettings = new DelaySettingsPanel(uiActor);
        controls.getChildren().add(delaySettings);
        controls.getChildren().add(errorLabel);

        setLeft(controls);

        final var zoneOverview = new ZoneOverviewPanel();
        uiActor.tell(new MessagesProtocol.GetZonesRequest(), ActorRef.noSender());
        setCenter(zoneOverview);

        BorderPane.setMargin(getCenter(), new Insets(0, 0, 0, 10));
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 8;");

        uiActor.tell(
                new MessagesProtocol.BindUi(
                        delaySettings.getEntryDelay(),
                        delaySettings.getExitDelay(),
                        zoneOverview.getZoneCanvas(),
                        errorLabel
                ),
                ActorRef.noSender()
        );
    }
}

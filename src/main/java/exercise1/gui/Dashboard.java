package exercise1.gui;

import exercise1.protocols.MessagesProtocol;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.pekko.actor.ActorRef;

import java.util.Map;

public final class Dashboard extends BorderPane {
    public Dashboard(ActorRef uiActor) {
        VBox controls = new VBox(10);
        controls.setPrefWidth(200);
        controls.getChildren().add(new KeypadPanel());

        DelaySettingsPanel delaySettings = new DelaySettingsPanel(uiActor);
        controls.getChildren().add(delaySettings);

        setLeft(controls);
        setCenter(new ZoneOverviewPanel(Map.of()));
        BorderPane.setMargin(getCenter(), new Insets(0, 0, 0, 10));
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 8;");

        uiActor.tell(
                new MessagesProtocol.BindUi(delaySettings.getEntryDelay(), delaySettings.getExitDelay()),
                ActorRef.noSender()
        );
    }
}

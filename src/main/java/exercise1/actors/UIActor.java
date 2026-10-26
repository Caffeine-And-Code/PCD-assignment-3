package exercise1.actors;

import exercise1.protocols.MessagesProtocol.*;
import javafx.application.Platform;
import org.apache.pekko.actor.AbstractActorWithStash;
import org.apache.pekko.actor.Props;

import javax.swing.*;

public class UIActor extends AbstractActorWithStash {

    private JLabel entryDelayRef;
    private JLabel exitDelayRef;
    // TODO: zoneCanvasRef, alarmRef

    public static Props props() {
        //noinspection deprecation
        return Props.create(UIActor.class, UIActor::new);
    }

    @Override
    public Receive createReceive() {
        //noinspection deprecation
        return receiveBuilder()
                .match(BindUi.class, this::handleBindUi)
                .matchAny(msg -> stash())
                .build();
    }

    private Receive ready() {
        //noinspection deprecation
        return receiveBuilder()
                .match(BindUi.class, this::handleBindUi) // enables the re-bind
                .match(GetZonesReply.class, this::handleGetZoneReply)
                .match(ExitDelayUpdated.class, this::handleExitDelayUpdated)
                .match(EntryDelayUpdated.class, this::handleEntryDelayUpdated)
                .match(InvalidPinEntered.class, this::handleInvalidPinEntered)
                .match(UpdatedZoneStates.class, this::handleUpdatedZoneStates)
                .match(UpdatedZoneSensors.class, this::handleUpdatedZoneSensors)
                .match(AlarmActiveUpdate.class, this::handleAlarmActiveUpdate)
                .build();
    }

    private void handleBindUi(BindUi ui) {
        this.entryDelayRef = ui.entryDelayLabel();
        this.exitDelayRef = ui.exitDelayLabel();

        // now process the stashed messages
        getContext().become(ready());
        unstashAll();
    }

    private void handleGetZoneReply(GetZonesReply data) {
        Platform.runLater(() -> {
            // aggiorna lo ZoneCanvas
        });
    }

    private void handleExitDelayUpdated(ExitDelayUpdated data) {
        Platform.runLater(() -> exitDelayRef.setText(String.valueOf(data.seconds())));
    }

    private void handleEntryDelayUpdated(EntryDelayUpdated data) {
        Platform.runLater(() -> entryDelayRef.setText(String.valueOf(data.seconds())));
    }

    private void handleInvalidPinEntered(InvalidPinEntered data) {
        Platform.runLater(() -> {
            // mostra l'errore del PIN
        });
    }

    private void handleUpdatedZoneStates(UpdatedZoneStates data) {
        Platform.runLater(() -> {
            // aggiorna lo stato delle zone
        });
    }

    private void handleUpdatedZoneSensors(UpdatedZoneSensors data) {
        Platform.runLater(() -> {
            // aggiorna i sensori
        });
    }

    private void handleAlarmActiveUpdate(AlarmActiveUpdate data) {
        Platform.runLater(() -> {
            // aggiorna l'indicatore dell'allarme
        });
    }
}
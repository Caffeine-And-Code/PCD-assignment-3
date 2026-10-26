package exercise1.actors;

import exercise1.gui.DelayControl;
import exercise1.gui.ZoneCanvas;
import exercise1.protocols.MessagesProtocol.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.apache.pekko.actor.AbstractActorWithStash;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;

public class UIActor extends AbstractActorWithStash {
    private static final int PIN_LENGHT = 5;

    private final ActorRef controlUnit;

    private DelayControl entryDelayRef;
    private DelayControl exitDelayRef;
    private ZoneCanvas zoneCanvas;
    private Label errorLabel;
    private String currentPin = "";
    // TODO: zoneCanvasRef, alarmRef

    public static Props props(ActorRef controlUnit) {
        return Props.create(UIActor.class, () -> new UIActor(controlUnit));
    }

    private UIActor(ActorRef controlUnit) {
        this.controlUnit = controlUnit;
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
                // "backend" messages
                .match(GetZonesReply.class, this::handleGetZoneReply)
                .match(ExitDelayUpdated.class, this::handleExitDelayUpdated)
                .match(EntryDelayUpdated.class, this::handleEntryDelayUpdated)
                .match(InvalidPinEntered.class, this::handleInvalidPinEntered)
                .match(UpdatedZoneStates.class, this::handleUpdatedZoneStates)
                .match(UpdatedZoneSensors.class, this::handleUpdatedZoneSensors)
                .match(AlarmActiveUpdate.class, this::handleAlarmActiveUpdate)
                // "frontend" messages
                .match(EnterPinDigit.class, this::handleEnterPinDigit)
                .match(UpdateEntryDelay.class, this::onUpdateEntryRequest)
                .match(UpdateExitDelay.class, this::onUpdateExitRequest)
                .match(GetZonesRequest.class, this::onGetZoneRequest)
                .build();
    }

    private void handleBindUi(BindUi ui) {
        this.entryDelayRef = ui.entryDelayLabel();
        this.exitDelayRef = ui.exitDelayLabel();
        this.errorLabel = ui.errorLabel();
        this.zoneCanvas = ui.zoneCanvas();

        // now process the stashed messages
        getContext().become(ready());
        unstashAll();
    }

    /** -- Responses from the Backend -- **/

    private void handleGetZoneReply(GetZonesReply data) {
        Platform.runLater(() -> this.zoneCanvas.setLabels(data.zoneSensors().keySet().stream().toList()));
    }

    private void handleExitDelayUpdated(ExitDelayUpdated data) {
        Platform.runLater(() -> exitDelayRef.setSeconds(data.seconds()));
    }

    private void handleEntryDelayUpdated(EntryDelayUpdated data) {
        Platform.runLater(() -> entryDelayRef.setSeconds(data.seconds()));
    }

    private void handleInvalidPinEntered(InvalidPinEntered data) {
        Platform.runLater(() -> {
            this.errorLabel.setText("Invalid Pin Provided");
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> errorLabel.setText(""));
            pause.play();
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

    /** -- Frontend Requests forwarded to ControlUnit -- **/

    private void onUpdateExitRequest(UpdateExitDelay data){
        controlUnit.tell(data, this.getSelf());
    }

    private void onUpdateEntryRequest(UpdateEntryDelay data){
        controlUnit.tell(data, this.getSelf());
    }

    private void onGetZoneRequest(GetZonesRequest data) {
        controlUnit.tell(data, this.getSelf());
    }

    private void handleEnterPinDigit(EnterPinDigit data) {
        if (currentPin.length() >= PIN_LENGHT) {
            return;
        }

        currentPin += data.digit();

        if (currentPin.length() >= PIN_LENGHT) {
            controlUnit.tell(new PinEnteredWithZones(currentPin, data.zonesToActivate()), this.getSelf());

            currentPin = "";
        }
    }
}

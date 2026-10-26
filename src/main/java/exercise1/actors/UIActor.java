package exercise1.actors;

import exercise1.gui.DelayControl;
import exercise1.gui.ZoneOverviewPanel;
import exercise1.protocols.MessagesProtocol.*;
import exercise1.protocols.SystemState;
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
    private ZoneOverviewPanel zoneCanvas;
    private Label errorLabel;
    private Label systemStateLabel;
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
                .match(SystemStateUpdated.class, this::handleSystemStateUpdated)
                .match(ExitDelayUpdated.class, this::handleExitDelayUpdated)
                .match(EntryDelayUpdated.class, this::handleEntryDelayUpdated)
                .match(InvalidPinEntered.class, this::handleInvalidPinEntered)
                .match(UpdatedZoneStates.class, this::handleUpdatedZoneStates)
                .match(UpdatedZoneSensors.class, this::handleUpdatedZoneSensors)
                .match(AlarmActiveUpdate.class, this::handleAlarmActiveUpdate)
                // "frontend" messages
                .match(AssignSensorToZone.class, this::onAssignSensorToZone)
                .match(EnterPinDigit.class, this::onEnterPinDigit)
                .match(UpdateEntryDelay.class, this::onUpdateEntryRequest)
                .match(UpdateExitDelay.class, this::onUpdateExitRequest)
                .match(GetZonesRequest.class, this::onGetZoneRequest)
                .match(GetSystemStateRequest.class, this::onGetSystemStateRequest)
                .match(CreateZone.class, this::onCreateZoneRequest)
                .build();
    }

    private void handleBindUi(BindUi ui) {
        this.entryDelayRef = ui.entryDelayLabel();
        this.exitDelayRef = ui.exitDelayLabel();
        this.errorLabel = ui.errorLabel();
        this.zoneCanvas = ui.zoneCanvas();
        this.systemStateLabel = ui.systemStateLabel();

        // now process the stashed messages
        getContext().become(ready());
        unstashAll();
    }

    /** -- Responses from the Backend -- **/

    private void handleGetZoneReply(GetZonesReply data) {
        Platform.runLater(() -> this.zoneCanvas.setSensorsByZone(data.zoneSensors()));
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

    private void handleSystemStateUpdated(SystemStateUpdated data) {
        String label = switch (data.state()) {
            case DISARMED -> "Disarmed";
            case EXIT_DELAY -> "Exit delay";
            case ARMED -> "Armed";
            case ENTRY_DELAY -> "Entry delay";
            case ALARM -> "Alarm";
        };

        Platform.runLater(() -> {
            systemStateLabel.setText(label);
            zoneCanvas.setAlarmActive(data.state() == SystemState.ARMED);
        });
    }

    private void handleUpdatedZoneStates(UpdatedZoneStates data) {
        Platform.runLater(() -> {
            // aggiorna lo stato delle zone
        });
    }

    private void handleUpdatedZoneSensors(UpdatedZoneSensors data) {
        Platform.runLater(() -> this.zoneCanvas.setSensorsByZone(data.zoneSensors()));
    }

    private void handleAlarmActiveUpdate(AlarmActiveUpdate data) {
        Platform.runLater(() -> {
            // aggiorna l'indicatore dell'allarme
        });
    }

    /** -- Frontend Requests forwarded to ControlUnit -- **/

    private void onCreateZoneRequest(CreateZone createZone) {
        controlUnit.tell(createZone, this.getSelf());
    }

    private void onUpdateExitRequest(UpdateExitDelay data){
        controlUnit.tell(data, this.getSelf());
    }

    private void onUpdateEntryRequest(UpdateEntryDelay data){
        controlUnit.tell(data, this.getSelf());
    }

    private void onGetZoneRequest(GetZonesRequest data) {
        controlUnit.tell(data, this.getSelf());
    }

    private void onGetSystemStateRequest(GetSystemStateRequest data) {
        controlUnit.tell(data, getSelf());
    }

    private void onAssignSensorToZone(AssignSensorToZone assignSensorToZone) {
        controlUnit.tell(assignSensorToZone, getSelf());
    }

    private void onEnterPinDigit(EnterPinDigit data) {
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

package exercise1.actors;

import exercise1.protocols.MessagesProtocol.*;
import org.apache.pekko.actor.AbstractActorWithStash;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Cancellable;
import org.apache.pekko.actor.Props;
import org.apache.pekko.japi.pf.ReceiveBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlUnitActor extends AbstractActorWithStash {

    private record ExitTimeout() {
    }

    private record EntryTimeout() {
    }

    private final HashMap<String, List<String>> zoneSensors = new HashMap<>();
    private final HashMap<String, Boolean> activeZones = new HashMap<>();
    private int exitDelay = 30; //seconds
    private int entryDelay = 30; //seconds
    private final String correctPin = "12345";

    private Cancellable entryTimeout;

    public void setZoneSensors(Map<String, List<String>> initialZoneSensors) {
        zoneSensors.clear();
        activeZones.clear();
        initialZoneSensors.forEach((zoneId, sensors) -> {
            zoneSensors.put(zoneId, new ArrayList<>(sensors));
            activeZones.put(zoneId, false);
        });
    }

    @Override
    public Receive createReceive() {
        return disarmed();
    }

    private Receive disarmed() {
        //noinspection deprecation
        return defaultBuilder()
                .match(CreateZone.class, this::OnCreateZone)
                .match(DeleteZone.class, this::OnDeleteZone)
                .match(AssignSensorToZone.class, this::OnAssignSensorToZone)
                .match(UpdateExitDelay.class, this::OnUpdateExitDelay)
                .match(UpdateEntryDelay.class, this::OnUpdateEntryDelay)
                .match(PinEnteredWithZones.class, this::OnPinEnteredFromDisarmed)
                .build();
    }

    private Receive exitDelay() {
        //noinspection deprecation
        return defaultBuilder()
                .match(ExitTimeout.class, this::OnExitTimeout)
                .build();
    }

    private Receive armed() {
        //noinspection deprecation
        return defaultBuilder()
                .match(SensorDetection.class, this::OnSensorDetection)
                .build();
    }

    private Receive entryDelay() {
        //noinspection deprecation
        return defaultBuilder()
                .match(EntryTimeout.class, this::OnEntryTimeout)
                .match(PinEntered.class, this::OnPinEnteredFromArmed)
                .build();
    }

    private Receive alarm() {
        //noinspection deprecation
        return defaultBuilder()
                .match(PinEntered.class, this::OnPinEnteredFromArmed)
                .build();
    }

    private ReceiveBuilder defaultBuilder() {
        //noinspection deprecation
        return receiveBuilder()
                .match(GetZonesRequest.class, this::OnGetZonesRequest);
    }

    private ReceiveBuilder stashCRUD(ReceiveBuilder builder) {
        return builder
                .match(CreateZone.class, mag -> stash())
                .match(DeleteZone.class, mag -> stash())
                .match(AssignSensorToZone.class, mag -> stash())
                .match(UpdateExitDelay.class, mag -> stash())
                .match(UpdateEntryDelay.class, mag -> stash());
    }

    public void OnGetZonesRequest(GetZonesRequest req) {
        getSender().tell(new GetZonesReply(zoneSensors), getSelf());
    }

    public void OnCreateZone(CreateZone createZone) {
        var zoneId = createZone.zoneId().trim();
        if (!zoneId.isEmpty()) {
            getContext().actorOf(Props.create(ZoneActor.class, getSender(), zoneId), zoneId);
            this.zoneSensors.put(zoneId, new ArrayList<>());
            activeZones.put(zoneId, false);
        }

        sendUpdatedZoneSensors(getSender());
    }

    public void OnDeleteZone(DeleteZone deleteZone) {
        var zoneId = deleteZone.zoneId();
        if (zoneSensors.containsKey(zoneId) && zoneSensors.get(zoneId).isEmpty()) {
            zoneSensors.remove(zoneId);
            activeZones.remove(zoneId);
            getContext().findChild(zoneId).ifPresent(zoneRef -> getContext().stop(zoneRef));
        }

        sendUpdatedZoneSensors(getSender());
    }

    public void OnAssignSensorToZone(AssignSensorToZone assignSensorToZone) {
        var zoneId = assignSensorToZone.zoneId();
        var sensorId = assignSensorToZone.sensorId();
        if (zoneSensors.containsKey(zoneId)) {
            for (var zone : zoneSensors.keySet()) {
                if (zoneSensors.get(zone).contains(sensorId)) {
                    zoneSensors.get(zone).remove(sensorId);
                    zoneSensors.get(zoneId).add(sensorId);
                }
            }
        }

        sendUpdatedZoneSensors(getSender());
    }

    public void OnUpdateExitDelay(UpdateExitDelay updateExitDelay) {
        var seconds = updateExitDelay.seconds();
        if (seconds >= 20 && seconds <= 60) {
            exitDelay = seconds;
        }

        getSender().tell(new ExitDelayUpdated(exitDelay), getSelf());
    }

    public void OnUpdateEntryDelay(UpdateEntryDelay updateEntryDelay) {
        var seconds = updateEntryDelay.seconds();
        if (seconds >= 10 && seconds <= 30) {
            entryDelay = seconds;
        }

        getSender().tell(new EntryDelayUpdated(entryDelay), getSelf());
    }

    public void OnPinEnteredFromDisarmed(PinEnteredWithZones pinEntered) {
        var pinAttempt = pinEntered.pin();
        if (pinAttempt.equals(correctPin)) {
            for (var zoneId : pinEntered.zonesToActive()) {
                if (activeZones.containsKey(zoneId)) {
                    activeZones.put(zoneId, true);
                } else {
                    activeZones.put(zoneId, false);
                }
            }
            enterExitDelay();
        } else {
            getSender().tell(new InvalidPinEntered(), getSelf());
        }
    }

    public void OnPinEnteredFromArmed(PinEntered pinEntered) {
        var pinAttempt = pinEntered.pin();
        if (pinAttempt.equals(correctPin)) {
            if (entryTimeout != null) {
                entryTimeout.cancel();
            }
            enterDisarmed();
        } else {
            getSender().tell(new InvalidPinEntered(), getSelf());
        }
    }

    public void OnSensorDetection(SensorDetection sensorDetection) {
        enterEntryDelay();
    }

    public void OnExitTimeout(ExitTimeout exitTimeout) {
        enterArmed();
    }

    public void OnEntryTimeout(EntryTimeout entryTimeout) {
        enterAlarm();
    }

    private void sendUpdatedZoneSensors(ActorRef to) {
        to.tell(new UpdatedZoneSensors(zoneSensors), getSelf());
    }

    private void enterExitDelay() {
        getContext().become(exitDelay());

        getContext().getSystem().getScheduler().scheduleOnce(
                Duration.ofSeconds(exitDelay),
                getSelf(),
                new ExitTimeout(),
                getContext().getDispatcher(),
                getSelf()
        );

        for (var zoneId : activeZones.keySet()) {
            if (activeZones.get(zoneId) == true) {
                getContext().findChild(zoneId).ifPresent(ref -> ref.tell(new ActiveZone(), getSelf()));
            } else {
                getContext().findChild(zoneId).ifPresent(ref -> ref.tell(new DeactiveZone(), getSelf()));
            }
        }
    }

    private void enterEntryDelay() {
        getContext().become(entryDelay());

        entryTimeout = getContext().getSystem().getScheduler().scheduleOnce(
                Duration.ofSeconds(entryDelay),
                getSelf(),
                new EntryTimeout(),
                getContext().getDispatcher(),
                getSelf()
        );
    }

    private void enterArmed() {
        getContext().become(armed());
    }

    private void enterAlarm() {
        getContext().become(alarm());
    }

    private void enterDisarmed() {
        getContext().become(disarmed());

        unstashAll();
    }
}

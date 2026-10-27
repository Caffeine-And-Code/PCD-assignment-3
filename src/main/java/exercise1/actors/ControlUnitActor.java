package exercise1.actors;

import exercise1.protocols.MessagesProtocol.*;
import exercise1.protocols.SystemState;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Cancellable;
import org.apache.pekko.actor.Props;
import org.apache.pekko.japi.pf.ReceiveBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ControlUnitActor extends AbstractActor {

    public record ExitTimeout() {
    }

    public record EntryTimeout() {
    }

    private final HashMap<String, List<String>> zoneSensors = new HashMap<>();
    private SystemState state = SystemState.DISARMED;
    private ActorRef uiActorRef;
    private int exitDelay = 30; //seconds
    private int entryDelay = 30; //seconds
    private final String correctPin = "12345";

    private Cancellable entryTimeout;
    private Cancellable exitTimeout;

    private final Set<String> activeZones = new HashSet<>();

    public ControlUnitActor(Map<String, List<String>> initialZoneSensors){
        initialZoneSensors.forEach((zoneId, sensors) -> {
            zoneSensors.put(zoneId, new ArrayList<>(sensors));
        });
    }

    @Override
    public void preStart(){
        this.zoneSensors.forEach((zoneName, sensors) -> {
            ActorRef zone = getContext().actorOf(Props.create(ZoneActor.class, getSelf(), zoneName), zoneName);
            for (String sensor : sensors) {
                getContext().actorOf(Props.create(SensorActor.class, zone), sensor);
            }
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
                .match(PinEntered.class, this::OnPinEnteredFromArmed)
                .match(ExitTimeout.class, this::OnExitTimeout)
                .build();
    }

    private Receive armed() {
        //noinspection deprecation
        return defaultBuilder()
                .match(SensorDetection.class, this::OnSensorDetection)
                .match(PinEntered.class, this::OnPinEnteredFromArmed)
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
                .match(GetZonesRequest.class, this::OnGetZonesRequest)
                .match(GetSystemStateRequest.class, this::onGetSystemStateRequest);
    }

    public void OnGetZonesRequest(GetZonesRequest req) {
        getSender().tell(new GetZonesReply(getZoneSensorCopy()), getSelf());
    }

    private void onGetSystemStateRequest(GetSystemStateRequest request) {
        uiActorRef = getSender();
        uiActorRef.tell(new SystemStateUpdated(state), getSelf());
        uiActorRef.tell(new ExitDelayUpdated(exitDelay), getSelf());
        uiActorRef.tell(new EntryDelayUpdated(entryDelay), getSelf());
        publishZones();
    }

    public void OnCreateZone(CreateZone createZone) {
        var zoneId = createZone.zoneId() == null ? "" : createZone.zoneId().trim();
        if (!zoneId.isEmpty() && org.apache.pekko.actor.ActorPath.isValidPathElement(zoneId)
                && !getContext().findChild(zoneId).isPresent()) {
            getContext().actorOf(Props.create(ZoneActor.class, getSelf(), zoneId), zoneId);
            this.zoneSensors.put(zoneId, new ArrayList<>());
        } else {
            getSender().tell(new OperationRejected("Use a unique zone name without spaces or slashes."), getSelf());
        }

        sendUpdatedZoneSensors(getSender());
        publishZones();
    }

    public void OnDeleteZone(DeleteZone deleteZone) {
        var zoneId = deleteZone.zoneId();
        if (zoneSensors.containsKey(zoneId) && zoneSensors.get(zoneId).isEmpty()) {
            zoneSensors.remove(zoneId);
            getContext().findChild(zoneId).ifPresent(zoneRef -> getContext().stop(zoneRef));
        }

        sendUpdatedZoneSensors(getSender());
        publishZones();
    }

    public void OnAssignSensorToZone(AssignSensorToZone assignSensorToZone) {
        var zoneId = assignSensorToZone.zoneId();
        var sensorId = assignSensorToZone.sensorId();
        if (zoneSensors.containsKey(zoneId) && zoneSensors.values().stream().anyMatch(s -> s.contains(sensorId))) {
            zoneSensors.values().forEach(sensors -> sensors.remove(sensorId));
            zoneSensors.get(zoneId).add(sensorId);
            getContext().findChild(sensorId).ifPresent(sensor ->
                sensor.tell(new SensorActor.SetZone(getContext().findChild(zoneId).orElseThrow()), getSelf()));
        }

        sendUpdatedZoneSensors(getSender());
        publishZones();
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
        if (correctPin.equals(pinAttempt)) {
            if (pinEntered.zonesToActive() == null || pinEntered.zonesToActive().isEmpty()
                    || !zoneSensors.keySet().containsAll(pinEntered.zonesToActive())) {
                getSender().tell(new OperationRejected("Select at least one existing zone before arming."), getSelf());
                return;
            }
            activeZones.clear();
            activeZones.addAll(pinEntered.zonesToActive());
            enterExitDelay();
        } else {
            getSender().tell(new InvalidPinEntered(), getSelf());
        }
    }

    public void OnPinEnteredFromArmed(PinEntered pinEntered) {
        var pinAttempt = pinEntered.pin();
        if (correctPin.equals(pinAttempt)) {
            if (entryTimeout != null) {
                entryTimeout.cancel();
            }
            enterDisarmed();
        } else {
            getSender().tell(new InvalidPinEntered(), getSelf());
        }
    }

    public void OnSensorDetection(SensorDetection sensorDetection) {
        if (activeZones.stream().anyMatch(zone -> getContext().findChild(zone)
                .filter(getSender()::equals).isPresent())) enterEntryDelay();
    }

    public void OnExitTimeout(ExitTimeout exitTimeout) {
        enterArmed();
    }

    public void OnEntryTimeout(EntryTimeout entryTimeout) {
        enterAlarm();
    }

    private void sendUpdatedZoneSensors(ActorRef to) {
        to.tell(new UpdatedZoneSensors(getZoneSensorCopy()), getSelf());
    }

    private void enterExitDelay() {
        getContext().become(exitDelay());
        updateState(SystemState.EXIT_DELAY);

        exitTimeout = getContext().getSystem().getScheduler().scheduleOnce(
                Duration.ofSeconds(exitDelay),
                getSelf(),
                new ExitTimeout(),
                getContext().getDispatcher(),
                getSelf()
        );
    }

    private void enterEntryDelay() {
        getContext().become(entryDelay());
        updateState(SystemState.ENTRY_DELAY);

        entryTimeout = getContext().getSystem().getScheduler().scheduleOnce(
                Duration.ofSeconds(entryDelay),
                getSelf(),
                new EntryTimeout(),
                getContext().getDispatcher(),
                getSelf()
        );
    }

    private void enterArmed() {
        activeZones.forEach(zone -> getContext().findChild(zone)
                .ifPresent(ref -> ref.tell(new ActiveZone(), getSelf())));
        getContext().become(armed());
        updateState(SystemState.ARMED);
    }

    private void enterAlarm() {
        getContext().become(alarm());
        updateState(SystemState.ALARM);
    }

    private void enterDisarmed() {
        if (exitTimeout != null) exitTimeout.cancel();
        if (entryTimeout != null) entryTimeout.cancel();
        activeZones.clear();
        zoneSensors.keySet().forEach(zone -> getContext().findChild(zone)
            .ifPresent(ref -> ref.tell(new DeactiveZone(), getSelf())));
        getContext().become(disarmed());
        updateState(SystemState.DISARMED);
    }

    private void updateState(SystemState newState) {
        state = newState;
        publishZones();
        if (uiActorRef != null) {
            uiActorRef.tell(new SystemStateUpdated(state), getSelf());
        }
    }

    private void publishZones() {
        if (uiActorRef != null) {
            uiActorRef.tell(new UpdatedZoneStates(zoneSensors.keySet().stream().collect(
                Collectors.toUnmodifiableMap(zone -> zone, zone ->
                    state != SystemState.DISARMED && state != SystemState.EXIT_DELAY && activeZones.contains(zone)))), getSelf());
            uiActorRef.tell(new AlarmActiveUpdate(state == SystemState.ALARM), getSelf());
        }
    }

    @Override public void postStop() {
        if (exitTimeout != null) exitTimeout.cancel();
        if (entryTimeout != null) entryTimeout.cancel();
    }

    private Map<String, List<String>> getZoneSensorCopy(){
        return zoneSensors.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                e -> List.copyOf(e.getValue())
            ));
    }
}

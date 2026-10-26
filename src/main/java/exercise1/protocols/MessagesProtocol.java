package exercise1.protocols;

import java.util.List;
import javax.swing.*;
import java.util.Map;

public abstract class MessagesProtocol {

    // Internally for actor comunication
    public record ActiveZone(){}
    public record DeactiveZone(){}
    public record ZoneActivated(String zoneId){}
    public record ZoneDeactivated(String zoneId){}

    // From UI to actors
    public record GetZonesRequest(){} //controlUnit
    public record CreateZone(String zoneId){} //controlUnit
    // Only if no sensor in the zone is present
    public record DeleteZone(String zoneId){} //controlUnit
    public record AssignSensorToZone(String zoneId, String sensorId){} //controlUnit
    public record UpdateExitDelay(int seconds){} //controlUnit
    public record UpdateEntryDelay(int seconds){} //controlUnit
    public record PinEnteredWithZones(String pin, List<String> zonesToActive){} //controlUnit
    public record PinEntered(String pin){} //controlUnit
    public record SensorDetection(){} //actorSelection("/controlUnit/{sensorId}")

    // To UI from actors
    public record GetZonesReply(Map<String, List<String>> zoneSensors){}
    public record ExitDelayUpdated(int seconds){}
    public record EntryDelayUpdated(int seconds){}
    public record InvalidPinEntered(){}
    public record UpdatedZoneStates(Map<String, Boolean> zoneStates){}
    public record UpdatedZoneSensors(Map<String, List<String>> zoneSensors){}
    public record AlarmActiveUpdate(Boolean alarmActive){}

    // From UI to UIActor
    public record EnterPinDigit(int digit){}
    public record BindUi(JLabel entryDelayLabel, JLabel exitDelayLabel){}
    // update entry delay
    // update exit delay
    // assign sensor to zone
    // sensor detection
}

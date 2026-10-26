package exercise1.protocols;

import org.apache.pekko.actor.ActorRef;

import java.util.List;
import java.util.Map;

public abstract class MessagesProtocol {

    // Internally for actor comunication
    public static record ActiveZone(){}
    public static record DeactiveZone(){}
    public static record ZoneActivated(String zoneId){}
    public static record ZoneDeactivated(String zoneId){}

    // From UI to actors
    public static record GetZonesRequest(){} //controlUnit
    public static record CreateZone(String zoneId){} //controlUnit
    // Only if no sensor in the zone is present
    public static record DeleteZone(String zoneId){} //controlUnit
    public static record AssignSensorToZone(ActorRef zoneRef){} //controlUnit
    public static record UpdateExitDelay(int seconds){} //controlUnit
    public static record UpdateEntryDelay(int seconds){} //controlUnit
    public static record PinEntered(String pin, List<String> zonesToActive){} //controlUnit
    public static record SensorDetection(){} //actorSelection("/controlUnit/{sensorId}")

    // To UI from actors
    public static record GetZonesReply(Map<String, List<String>> zoneSensors){}
    public static record ExitDelayUpdated(int seconds){}
    public static record EntryDelayUpdated(int seconds){}
    public static record InvalidPinEntered(){}
    public static record UpdatedZoneStates(Map<String, Boolean> zoneStates){}
    public static record UpdatedZoneSensors(Map<String, List<String>> zoneSensors){}
    public static record AlarmActiveUpdate(Boolean alarmActive){}

}

package exercise1.actors;

import exercise1.protocols.MessagesProtocol.AssignSensorToZone;
import exercise1.protocols.MessagesProtocol.SensorDetection;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;

public class SensorActor extends AbstractActor {
    public ActorRef zoneRef;
    private final String identifier;

    public SensorActor(ActorRef zoneRef, String identifier){
        this.zoneRef = zoneRef;
        this.identifier = identifier;
    }

    @Override
    public Receive createReceive() {
        //noinspection deprecation
        return receiveBuilder()
                .match(SensorDetection.class, this::onSensorDetection)
                .match(AssignSensorToZone.class, this::onAssignSensorToZone)
                .build();
    }

    public void onSensorDetection(SensorDetection sensorDetection){
        zoneRef.tell(sensorDetection, getSelf());
    }

    public void onAssignSensorToZone(AssignSensorToZone assignSensorToZone){
        //this.zoneRef = assignSensorToZone.zoneRef();
    }
}

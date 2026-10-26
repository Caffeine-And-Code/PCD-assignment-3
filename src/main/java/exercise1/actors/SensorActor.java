package exercise1.actors;

import exercise1.protocols.MessagesProtocol.*;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;

public class SensorActor extends AbstractActor {
    public ActorRef zoneRef;

    public SensorActor(ActorRef zoneRef){
        this.zoneRef = zoneRef;
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

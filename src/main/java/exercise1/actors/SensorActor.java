package exercise1.actors;

import exercise1.protocols.MessagesProtocol;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;

public class SensorActor extends AbstractActor {
    public ActorRef zoneRef;

    public SensorActor(ActorRef zoneRef){
        this.zoneRef = zoneRef;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessagesProtocol.SensorDetection.class, this::onSensorDetection)
                .match(MessagesProtocol.AssignSensorToZone.class, this::onAssignSensorToZone)
                .build();
    }

    public void onSensorDetection(MessagesProtocol.SensorDetection sensorDetection){
        zoneRef.tell(sensorDetection, getSelf());
    }

    public void onAssignSensorToZone(MessagesProtocol.AssignSensorToZone assignSensorToZone){
        this.zoneRef = assignSensorToZone.zoneRef();
    }
}

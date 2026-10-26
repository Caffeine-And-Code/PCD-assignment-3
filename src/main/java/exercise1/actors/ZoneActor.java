package exercise1.actors;

import exercise1.protocols.MessagesProtocol.*;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;

public class ZoneActor extends AbstractActor {
    private final ActorRef uiActorRef;
    private final String zoneId;

    public ZoneActor(ActorRef uiActorRef, String zoneId){
        this.uiActorRef = uiActorRef;
        this.zoneId = zoneId;
    }

    @Override
    public Receive createReceive() {
        return inactive();
    }

    public Receive inactive(){
        //noinspection deprecation
        return receiveBuilder()
                .match(ActiveZone.class, this::activeZone)
                .match(DeactiveZone.class, this::deactivateZone)
                .build();
    }

    public Receive active(){
        //noinspection deprecation
        return receiveBuilder()
                .match(ActiveZone.class, this::activeZone)
                .match(DeactiveZone.class, this::deactivateZone)
                .match(SensorDetection.class, this::forwardSensorDetection)
                .build();
    }

    private void activeZone(ActiveZone activateZone){
        getContext().become(active());
        uiActorRef.tell(new ZoneActivated(zoneId), getSelf());
    }

    private void deactivateZone(DeactiveZone deactivateZone){
        getContext().become(inactive());
        uiActorRef.tell(new ZoneDeactivated(zoneId), getSelf());
    }

    private void forwardSensorDetection(SensorDetection detection){
        getContext().getParent().tell(detection, getSelf());
    }
}

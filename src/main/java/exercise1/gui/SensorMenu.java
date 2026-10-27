package exercise1.gui;

import exercise1.protocols.MessagesProtocol;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.apache.pekko.actor.ActorRef;

import java.util.List;

final class SensorMenu extends Menu {
    SensorMenu(String sensorName, String currentZone, List<String> zones, ActorRef uiActor) {
        super(sensorName);
        MenuItem detect = new MenuItem("Simulate detection");
        detect.setOnAction(event -> uiActor.tell(new MessagesProtocol.SensorDetectionRequested(sensorName), ActorRef.noSender()));
        getItems().add(detect);

        for (String destinationZone : zones) {
            MenuItem destination = new MenuItem(destinationZone);
            destination.setDisable(destinationZone.equals(currentZone));
            destination.setOnAction(event -> uiActor.tell(new MessagesProtocol.AssignSensorToZone(destinationZone, sensorName), ActorRef.noSender()));
            getItems().add(destination);
        }
    }

}

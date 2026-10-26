package exercise1;

import exercise1.actors.ControlUnitActor;
import exercise1.actors.SensorActor;
import exercise1.actors.UIActor;
import exercise1.actors.ZoneActor;
import exercise1.gui.Dashboard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AlarmApplication extends Application {
    private ActorSystem system;

    @Override
    public void start(Stage stage) {
        system = ActorSystem.create("alarm");
        List<String> sensors = new ArrayList<>(Arrays.asList("door1", "door2", "door3", "windows1", "windows2", "windows3"));
        String zoneName = "zone1";
        final var zoneRef = system.actorOf(Props.create(ZoneActor.class, zoneName), zoneName);
        for (var sensor : sensors) {
            system.actorOf(Props.create(SensorActor.class, zoneRef, sensor), sensor);
        }
        ActorRef controlUnit = system.actorOf(Props.create(ControlUnitActor.class), "controlUnit");
        ActorRef ui = system.actorOf(UIActor.props(controlUnit), "ui");

        stage.setTitle("Smart Home Alarm");
        stage.setScene(new Scene(new Dashboard(ui), 760, 560));
        stage.setMinWidth(650);
        stage.setMinHeight(470);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        if (system != null) {
            system.terminate();
        }
    }
}

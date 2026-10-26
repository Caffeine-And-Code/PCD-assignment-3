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

import java.util.List;
import java.util.Map;

public final class AlarmApplication extends Application {
    private ActorSystem system;

    @Override
    public void start(Stage stage) {
        system = ActorSystem.create("alarm");
        Map<String, List<String>> initialZoneSensors = Map.of(
                "Atrio", List.of("Porta1", "Movimento1", "Finestra1", "Finestra2"),
                "Soggiorno", List.of("Porta2", "Porta3", "Movimento2", "Finestra3")
        );
        ActorRef controlUnit = system.actorOf(Props.create(ControlUnitActor.class, () -> {
            ControlUnitActor actor = new ControlUnitActor();
            actor.setZoneSensors(initialZoneSensors);
            return actor;
        }), "controlUnit");
        ActorRef ui = system.actorOf(UIActor.props(controlUnit), "ui");

        initialZoneSensors.forEach((zoneName, sensors) -> {
            ActorRef zoneRef = system.actorOf(Props.create(ZoneActor.class, ui, zoneName), zoneName);
            for (String sensor : sensors) {
                system.actorOf(Props.create(SensorActor.class, zoneRef, sensor), sensor);
            }
        });

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

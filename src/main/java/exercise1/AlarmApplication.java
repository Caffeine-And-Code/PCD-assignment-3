package exercise1;

import exercise1.actors.ControlUnitActor;
import exercise1.actors.UIActor;
import exercise1.gui.Dashboard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

public final class AlarmApplication extends Application {
    private ActorSystem system;

    @Override
    public void start(Stage stage) {
        system = ActorSystem.create("alarm");
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

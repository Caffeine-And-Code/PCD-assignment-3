package exercise1;

import exercise1.actors.ControlUnitActor;
import exercise1.actors.UIActor;
import exercise1.gui.Dashboard;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("alarm");
        ActorRef controlUnit = system.actorOf(Props.create(ControlUnitActor.class), "controlUnit");
        ActorRef ui = system.actorOf(UIActor.props(controlUnit), "ui");

        SwingUtilities.invokeLater(() -> {
            Dashboard frame = new Dashboard(ui);
            frame.setVisible(true);
        });
    }
}

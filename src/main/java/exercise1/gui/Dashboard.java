package exercise1.gui;

import exercise1.protocols.MessagesProtocol;
import org.apache.pekko.actor.ActorRef;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

public final class Dashboard extends JFrame {
    public Dashboard(ActorRef uiActor) {
        super("Smart Home Alarm");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setPreferredSize(new Dimension(200, 0));
        controls.add(new KeypadPanel());
        controls.add(Box.createRigidArea(new Dimension(0, 10)));
        final var delayLayout = new DelaySettingsPanel(uiActor);
        controls.add(delayLayout);
        controls.add(Box.createVerticalGlue());

        JPanel content = new JPanel(new BorderLayout(10, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        content.add(controls, BorderLayout.WEST);
        content.add(new ZoneOverviewPanel(Map.of()), BorderLayout.CENTER);

        setContentPane(content);
        setMinimumSize(new Dimension(650, 470));
        setSize(760, 560);
        setLocationRelativeTo(null);
        uiActor.tell(new MessagesProtocol.BindUi(delayLayout.getEntryDelay(), delayLayout.getExitDelay()), ActorRef.noSender());
    }
}

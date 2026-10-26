package exercise1.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

final class ZonePopupMenu extends JPopupMenu {
    private static final int TILE_WIDTH = 120;
    private static final int TILE_HEIGHT = 90;
    private static final int PADDING = 3;
    private static final int CELL_WIDTH = TILE_WIDTH + PADDING;
    private static final int CELL_HEIGHT = TILE_HEIGHT + PADDING;

    private final List<String> zones;
    private final Map<String, List<String>> sensorsByZone;

    ZonePopupMenu(Map<String, List<String>> sensorsByZone) {
        this.sensorsByZone = sensorsByZone;
        this.zones = sensorsByZone.keySet().stream().toList();
    }

    @Override
    public void show(Component invoker, int x, int y) {
        String zone = zoneAt(invoker, x, y);
        if (zone == null) {
            return;
        }

        rebuild(zone);
        super.show(invoker, x, y);
    }

    private void rebuild(String zone) {
        removeAll();

        JMenuItem title = new JMenuItem(zone);
        title.setEnabled(false);
        add(title);
        addSeparator();

        List<String> sensors = sensorsByZone.getOrDefault(zone, List.of());
        if (sensors.isEmpty()) {
            JMenuItem empty = new JMenuItem("Nessun sensore");
            empty.setEnabled(false);
            add(empty);
            return;
        }

        for (String sensor : sensors) {
            add(new SensorMenu(sensor, zone, zones));
        }
    }

    private String zoneAt(Component invoker, int x, int y) {
        if (x < PADDING || y < PADDING) {
            return null;
        }

        int columns = Math.max(1, (invoker.getWidth() - PADDING) / CELL_WIDTH);
        int relativeX = x - PADDING;
        int relativeY = y - PADDING;

        if (relativeX % CELL_WIDTH >= TILE_WIDTH || relativeY % CELL_HEIGHT >= TILE_HEIGHT) {
            return null;
        }

        int index = (relativeY / CELL_HEIGHT) * columns + relativeX / CELL_WIDTH;
        return index < zones.size() ? zones.get(index) : null;
    }
}

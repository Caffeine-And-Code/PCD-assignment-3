package exercise1.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

public class ZoneCanvas extends JPanel {
    /** Layout **/
    private static final int TILE_WIDTH = 120;
    private static final int TILE_HEIGHT = 90;
    private static final int PADDING = 3;
    private static final int CELL_WIDTH = TILE_WIDTH + PADDING;
    private static final int CELL_HEIGHT = TILE_HEIGHT + PADDING;
    /** Colors **/
    private static final float SATURATION = 0.25f;
    private static final float BRIGHTNESS = 0.95f;

    private record Tile(String label, Color color) {
        static Tile of(String label) {
            float hue = new Random(label.hashCode()).nextFloat();

            return new Tile(label, Color.getHSBColor(hue, SATURATION, BRIGHTNESS));
        }
    }

    private final List<Tile> tiles;

    public ZoneCanvas(List<String> labels, JPopupMenu menu) {
        this.tiles = labels.stream().map(Tile::of).toList();
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // right-click on a rectangle shows the menu, anywhere else hides it
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }

                if (indexAt(e.getPoint()) >= 0) {
                    menu.show(ZoneCanvas.this, e.getX(), e.getY());
                } else {
                    menu.setVisible(false);
                }
            }
        });
    }

    private int getAvailableColumns() {
        return Math.max(1, (getWidth() - PADDING) / CELL_WIDTH);
    }

    private Rectangle boundsOf(int index) {
        int cols = getAvailableColumns();
        int x = PADDING + (index % cols) * CELL_WIDTH;
        int y = PADDING + (index / cols) * CELL_HEIGHT;

        return new Rectangle(x, y, TILE_WIDTH, TILE_HEIGHT);
    }

    private int indexAt(Point p) {
        for (int i = 0; i < tiles.size(); i++) {
            if (boundsOf(i).contains(p)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < tiles.size(); i++) {
            Rectangle r = boundsOf(i);
            Tile tile = tiles.get(i);

            g2.setColor(tile.color());
            g2.fill(r);
            g2.setColor(Color.DARK_GRAY);
            g2.draw(r);

            int tx = r.x + (r.width - fm.stringWidth(tile.label())) / 2;
            int ty = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(tile.label(), tx, ty);
        }
        g2.dispose();
    }
}

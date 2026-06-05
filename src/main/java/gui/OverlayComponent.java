package gui;

import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;

public interface OverlayComponent {
    boolean isComponentActive();
    boolean interceptCellRendering(int screenX, int screenY);
    String getCustomGlyph(int screenX, int screenY, String baseGlyph);
    Color getCustomColor(int screenX, int screenY, Color baseColor);

    /**
     * @return true if the event was fully handled and should block player action movement ticks.
     */
    boolean interpretKeystroke(KeyCode code);
}
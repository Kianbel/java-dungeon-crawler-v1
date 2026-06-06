package gui;

import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import util.Position;

public class TargetReticle implements OverlayComponent {
    private boolean isEnabled = false;
    private final Position reticleScreenPos;
    private int boundW, boundH;

    public TargetReticle(int viewW, int viewH) {
        this.boundW = viewW;
        this.boundH = viewH;
        this.reticleScreenPos = new Position(viewW / 2, viewH / 2);
    }

    public void updateBounds(int width, int height) {
        this.boundW = width;
        this.boundH = height;
        if (reticleScreenPos.x >= boundW) reticleScreenPos.x = boundW - 1;
        if (reticleScreenPos.y >= boundH) reticleScreenPos.y = boundH - 1;
    }

    public void toggleState() { this.isEnabled = !this.isEnabled; }
    @Override public boolean isComponentActive() { return isEnabled; }

    @Override
    public boolean interceptCellRendering(int sx, int sy) {
        return isEnabled && (sx == reticleScreenPos.x && sy == reticleScreenPos.y);
    }

    @Override public String getCustomGlyph(int sx, int sy, String bg) { return "⌖"; }

    // Bind styling color directly to central UI variables class
    @Override public Color getCustomColor(int sx, int sy, Color bc) { return UITheme.OVERLAY_RETICLE; }

    @Override
    public boolean interpretKeystroke(KeyCode code) {
        switch (code) {
            case W, UP -> { if (reticleScreenPos.y > 0) reticleScreenPos.y--; }
            case S, DOWN -> { if (reticleScreenPos.y < boundH - 1) reticleScreenPos.y++; }
            case A, LEFT -> { if (reticleScreenPos.x > 0) reticleScreenPos.x--; }
            case D, RIGHT -> { if (reticleScreenPos.x < boundW - 1) reticleScreenPos.x++; }
            case ESCAPE, X -> isEnabled = false;
        }
        return true;
    }
}
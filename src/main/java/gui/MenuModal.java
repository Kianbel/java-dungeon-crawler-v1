package gui;

import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import java.util.function.Consumer;

public class MenuModal implements OverlayComponent {
    private boolean visible = false;
    private final String menuPrompt;
    private int highlightedItem = 0;
    private Consumer<Integer> outputCallback;

    private int screenW;
    private int screenH;

    public MenuModal(String prompt) {
        this.menuPrompt = prompt;
    }

    public void updateScreenDimensions(int width, int height) {
        this.screenW = width;
        this.screenH = height;
    }

    public void invokePrompt(Consumer<Integer> callback) {
        this.outputCallback = callback;
        this.visible = true;
    }

    @Override public boolean isComponentActive() { return visible; }

    @Override
    public boolean interceptCellRendering(int sx, int sy) {
        int bx = screenW / 2 - 20;
        int ex = screenW / 2 + 20;
        int by = screenH / 2 - 3;
        int ey = screenH / 2 + 3;
        return visible && (sx >= bx && sx <= ex && sy >= by && sy <= ey);
    }

    @Override
    public String getCustomGlyph(int sx, int sy, String bg) {
        int bx = screenW / 2 - 20;
        int ex = screenW / 2 + 20;
        int by = screenH / 2 - 3;
        int ey = screenH / 2 + 3;

        if (sy == by || sy == ey) return "■";
        if (sx == bx || sx == ex) return "│";

        int lineY = sy - by;
        int txtIndex = sx - bx - 2;

        if (lineY == 2 && txtIndex >= 0 && txtIndex < menuPrompt.length()) {
            return String.valueOf(menuPrompt.charAt(txtIndex));
        }

        String opt1 = "[Y] Accept" + (highlightedItem == 0 ? " <" : "");
        String opt2 = "[N] Reject" + (highlightedItem == 1 ? " <" : "");

        if (lineY == 4 && txtIndex >= 0 && txtIndex < opt1.length()) return String.valueOf(opt1.charAt(txtIndex));
        if (lineY == 5 && txtIndex >= 0 && txtIndex < opt2.length()) return String.valueOf(opt2.charAt(txtIndex));

        return " ";
    }

    // Bind styling color directly to central UI variables class
    @Override public Color getCustomColor(int sx, int sy, Color bc) { return UITheme.OVERLAY_MODAL; }

    @Override
    public boolean interpretKeystroke(KeyCode code) {
        switch (code) {
            case W, UP -> highlightedItem = 0;
            case S, DOWN -> highlightedItem = 1;
            case ENTER, SPACE -> {
                visible = false;
                if (outputCallback != null) outputCallback.accept(highlightedItem);
            }
            case ESCAPE -> visible = false;
        }
        return true;
    }
}
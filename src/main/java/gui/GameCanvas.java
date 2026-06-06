package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameCanvas {
    private final Canvas nativeCanvas;
    private final GraphicsContext gc;

    private int gridColumns;
    private int gridRows;
    private double cellWidth;
    private double cellHeight;
    private Font font;
    private double fontAscent;

    public GameCanvas(Canvas nativeCanvas, double initialFontSize) {
        this.nativeCanvas = nativeCanvas;
        this.gc = nativeCanvas.getGraphicsContext2D();
        updateFontSize(initialFontSize);
    }

    public void updateFontSize(double fontSize) {
        this.font = Font.font(UITheme.FONT_FAMILY, fontSize);
        this.cellHeight = fontSize;
        this.cellWidth = fontSize * 0.60;

        this.gridColumns = (int) Math.floor(nativeCanvas.getWidth() / cellWidth);
        this.gridRows = (int) Math.floor(nativeCanvas.getHeight() / cellHeight);

        this.fontAscent = cellHeight * 0.78;
    }

    public void clearCanvas() {
        // Pull context values safely from central variables configuration
        gc.setFill(UITheme.CANVAS_VOID);
        gc.fillRect(0, 0, nativeCanvas.getWidth(), nativeCanvas.getHeight());
    }

    public void drawCharacter(int gridX, int gridY, String character, Color textColor) {
        if (gridX < 0 || gridX >= gridColumns || gridY < 0 || gridY >= gridRows) return;

        double renderPixelX = gridX * cellWidth;
        double renderPixelY = gridY * cellHeight;

        gc.setFont(this.font);
        gc.setFill(textColor);
        gc.fillText(character, renderPixelX, renderPixelY + fontAscent);
    }

    public int getGridColumns() { return gridColumns; }
    public int getGridRows() { return gridRows; }
}
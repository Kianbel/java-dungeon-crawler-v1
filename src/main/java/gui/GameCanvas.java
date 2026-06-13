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

    public void drawCharacter(int gridX, int gridY, String character, Color textColor, double offsetX, double offsetY) {
        if (gridX < 0 || gridX >= gridColumns || gridY < 0 || gridY >= gridRows) return;

        // Add the pixel offsets directly to the layout position
        double renderPixelX = (gridX * cellWidth) + offsetX;
        double renderPixelY = (gridY * cellHeight) + offsetY;

        gc.setFont(this.font);
        gc.setFill(textColor);
        gc.fillText(character, renderPixelX, renderPixelY + fontAscent);
    }

    public void drawString(int x, int y, String text, double fontSize, Color color, double offsetX, double offsetY) {
        if (x < 0 || x >= gridColumns || y < 0 || y >= gridRows) return;

        final double renderPixelX = (x * cellWidth) + offsetX;
        final double renderPixelY = (y * cellHeight) + offsetY;

        Font font = Font.font(UITheme.FONT_FAMILY, fontSize);
        gc.setFont(font);
        gc.setFill(color);
        gc.fillText(text, renderPixelX, renderPixelY + fontAscent);
    }

    // Update your health bar method too so it glides along with the monster!
    public void drawHealthBar(int gridX, int gridY, double hpPercent, double offsetX, double offsetY) {
        if (gridX < 0 || gridX >= gridColumns || gridY < 0 || gridY >= gridRows) return;

        double renderPixelX = (gridX * cellWidth) + offsetX;
        double renderPixelY = (gridY * cellHeight) + offsetY;

        double barWidth = cellWidth;
        double barHeight = cellHeight * 0.15;
        double barY = renderPixelY + cellHeight - barHeight;

        gc.setFill(Color.CRIMSON);
        gc.fillRect(renderPixelX, barY, barWidth, barHeight);

        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(renderPixelX, barY, barWidth * hpPercent, barHeight);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(renderPixelX, barY, barWidth, barHeight);
    }

    public int getGridColumns() { return gridColumns; }
    public int getGridRows() { return gridRows; }
}
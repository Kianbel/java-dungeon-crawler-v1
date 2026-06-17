package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

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

    public void clearCanvas() {
        // Pull context values safely from central variables configuration
        gc.setFill(UITheme.CANVAS_VOID);
        gc.fillRect(0, 0, nativeCanvas.getWidth(), nativeCanvas.getHeight());
    }

    /*
    COMMENTED OUT SQUARE TILES
     */

    public void updateFontSize(double fontSize) {
        this.font = Font.font(UITheme.GENERAL_FONT_FAMILY, fontSize);
        this.cellHeight = fontSize;
        this.cellWidth = fontSize; // 1. Set cell width equal to height to make grid cells perfect squares

        this.gridColumns = (int) Math.floor(nativeCanvas.getWidth() / cellWidth);
        this.gridRows = (int) Math.floor(nativeCanvas.getHeight() / cellHeight);

        // Vertical font alignment adjustment
        this.fontAscent = cellHeight * 0.78;
    }
    public void drawCharacter(int gridX, int gridY, String character, Color textColor, double offsetX, double offsetY) {
        if (gridX < 0 || gridX >= gridColumns || gridY < 0 || gridY >= gridRows) return;

        gc.setFont(this.font);
        gc.setFill(textColor);

        // 1. Calculate the exact horizontal center of the cell for both methods
        double cellCenterX = (gridX * cellWidth) + (cellWidth / 2.0) + offsetX;
        double renderPixelY = (gridY * cellHeight) + offsetY;

        String stretchableCharacters = "▓░▒·.⌸";

        gc.save();
        // 2. Set alignment to CENTER so characters always anchor from their middle point
        gc.setTextAlign(TextAlignment.CENTER);

        if (character != null && !character.isBlank() && stretchableCharacters.contains(character)) {
            // --- METHOD A: CENTERED & HORIZONTALLY STRETCHED ---
            // Translates to the center first, meaning the 1.66x stretch scales symmetrically outward
            gc.translate(cellCenterX, renderPixelY + fontAscent);

            double scaleX = 1.0 / 0.60;
            gc.scale(scaleX, 1.0);

            gc.fillText(character, 0, 0); // Drawn exactly at the transformed center origin
        } else {
            // --- METHOD B: PERFECTLY CENTERED (NATURAL RATIO) ---
            // Keeps heroes, items, and monsters sharp and completely un-distorted
            gc.fillText(character, cellCenterX, renderPixelY + fontAscent);
        }

        // gc.restore() automatically resets text alignment back to TextAlignment.LEFT for safety
        gc.restore();
    }

//    public void updateFontSize(double fontSize) {
//        this.font = Font.font(UITheme.GENERAL_FONT_FAMILY, fontSize);
//        this.cellHeight = fontSize;
//        this.cellWidth = fontSize * 0.60;
//
//        this.gridColumns = (int) Math.floor(nativeCanvas.getWidth() / cellWidth);
//        this.gridRows = (int) Math.floor(nativeCanvas.getHeight() / cellHeight);
//
//        this.fontAscent = cellHeight * 0.78;
//    }
//
//    public void drawCharacter(int gridX, int gridY, String character, Color textColor, double offsetX, double offsetY) {
//        if (gridX < 0 || gridX >= gridColumns || gridY < 0 || gridY >= gridRows) return;
//
//        // Add the pixel offsets directly to the layout position
//        double renderPixelX = (gridX * cellWidth) + offsetX;
//        double renderPixelY = (gridY * cellHeight) + offsetY;
//
//        gc.setFont(this.font);
//        gc.setFill(textColor);
//        gc.fillText(character, renderPixelX, renderPixelY + fontAscent);
//    }

    public void drawString(int x, int y, String text, double fontSize, Color color, double offsetX, double offsetY) {
        if (x < 0 || x >= gridColumns || y < 0 || y >= gridRows) return;
        final double renderPixelX = (x * cellWidth) + (cellWidth/2.0) + offsetX;
        final double renderPixelY = (y * cellHeight) + (cellWidth/2.0) + offsetY;

        Font font = Font.font(UITheme.TEXT_POPUP_FONT_FAMILY, fontSize);
        if(Character.isDigit(text.charAt(0))) font = Font.font(UITheme.TEXT_POPUP_FONT_FAMILY, FontWeight.BOLD, fontSize);
        gc.setFont(font);
        gc.setFill(color);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, renderPixelX, renderPixelY + fontAscent);
        gc.setTextAlign(TextAlignment.LEFT);
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
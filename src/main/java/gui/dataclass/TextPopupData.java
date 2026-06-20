package gui.dataclass;

import javafx.scene.paint.Color;
import util.Position;

public class TextPopupData {
    public String text;
    public Color color;
    public Position position;
    public double opacity;
    public double pixelOffsetY;

    public TextPopupData(String text, Color color, Position position) {
        this.text = text;
        this.color = color;
        this.position = position;
        this.opacity = 0;
        this.pixelOffsetY = 0.0;
    }
}

package item.weapon;

import javafx.scene.paint.Color;

public class Halberd extends Weapon {
    public Halberd() {
        super("Halberd", 14);
        overrideCharacter("↑");
        overrideColor(Color.YELLOW.darker().darker());
    }
}

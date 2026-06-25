package item.weapon;

import javafx.scene.paint.Color;

public class Claymore extends Weapon {
    public Claymore() {
        super("Claymore", 18);
        overrideCharacter("🗡");
        overrideColor(Color.CYAN.darker().darker());
    }
}

package item.weapon;

import javafx.scene.paint.Color;

public class GreatSword extends Weapon {
    public GreatSword() {
        super("Greatsword", 12, 0.3);
        overrideCharacter("🗡");
        overrideColor(Color.CYAN);
    }
}

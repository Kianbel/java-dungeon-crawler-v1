package item.weapon;

import javafx.scene.paint.Color;

public class Mace extends Weapon {
    public Mace() {
        super("Mace", 8, 0.2);
        overrideCharacter("📍");
        overrideColor(Color.GRAY);
    }
}

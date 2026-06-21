package item.weapon;

import javafx.scene.paint.Color;

public class Dagger extends Weapon {
    public Dagger() {
        super("Dagger", 5, 0.2);
        overrideCharacter("🔪");
        overrideColor(Color.GRAY);
    }
}

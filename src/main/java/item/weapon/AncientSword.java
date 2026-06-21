package item.weapon;

import javafx.scene.paint.Color;

public class AncientSword extends Weapon {
    public AncientSword() {
        super("Ancient Sword", 5, 0.1);
        overrideCharacter("🗡");
        overrideColor(Color.BROWN);
    }
}

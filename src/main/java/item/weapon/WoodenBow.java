package item.weapon;

import javafx.scene.paint.Color;

public class WoodenBow extends RangedWeapon {
    public WoodenBow() {
        super("Wooden Bow", 5);
        overrideCharacter("D");
        overrideColor(Color.BROWN);
    }
}

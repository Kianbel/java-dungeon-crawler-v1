package item.weapon;

import javafx.scene.paint.Color;

public class GreatClub extends Weapon {
    public GreatClub() {
        super("Great Club", 14, 0.1);
        overrideCharacter("¡");
        overrideColor(Color.CYAN);
    }
}

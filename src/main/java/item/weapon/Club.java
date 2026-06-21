package item.weapon;

import javafx.scene.paint.Color;

public class Club extends Weapon {
    public Club() {
        super("Club", 7, 0.1);
        overrideCharacter("¡");
        overrideColor(Color.GRAY);
    }
}

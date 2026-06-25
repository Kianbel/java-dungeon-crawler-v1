package item.weapon;

import javafx.scene.paint.Color;

public class DaggerAxe extends Weapon {
    public DaggerAxe() {
        super("Dagger-axe", 15);
        overrideCharacter("↑");
        overrideColor(Color.SADDLEBROWN.darker().darker());
    }
}

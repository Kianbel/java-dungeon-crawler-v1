package item.weapon;

import javafx.scene.paint.Color;

public class GiantSpiderFang extends Weapon {
    public GiantSpiderFang() {
        super("Giant Spider Fang", 6, 0.2);
        overrideCharacter(",");
        overrideColor(Color.WHITE);
    }
}

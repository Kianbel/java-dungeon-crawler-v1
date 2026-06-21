package item.weapon;

import javafx.scene.paint.Color;

public class GiantSpiderFang extends Weapon {
    public GiantSpiderFang() {
        super("Giant Spider Fang", 5, 0.1);
        overrideCharacter(",");
        overrideColor(Color.WHITE);
    }
}

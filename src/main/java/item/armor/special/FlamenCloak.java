package item.armor.special;

import javafx.scene.paint.Color;

public class FlamenCloak extends SpecialArmor {
    public FlamenCloak() {
        super("Flamen Cloak", 15);
        overrideCharacter("👘");
        overrideColor(Color.ORANGE);
        // TODO: make wearer immune to fire
    }

    @Override
    public void wearEffect() {

    }

    @Override
    public void takeOffEffect() {

    }
}

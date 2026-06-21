package item.armor;

import javafx.scene.paint.Color;

public class BareLeatherTunic extends Armor {
    public BareLeatherTunic() {
        super("Bare Leather Tunic", 1);
        overrideCharacter("👕");
        overrideColor(Color.SANDYBROWN.darker());
    }
}

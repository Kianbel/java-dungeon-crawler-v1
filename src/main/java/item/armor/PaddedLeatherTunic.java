package item.armor;

import javafx.scene.paint.Color;

public class PaddedLeatherTunic extends Armor {

    public PaddedLeatherTunic() {
        super("Padded Leather Tunic", 3);
        overrideCharacter("👕");
        overrideColor(Color.DARKGRAY.darker());
    }
}

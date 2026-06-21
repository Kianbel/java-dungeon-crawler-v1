package item.armor;

import gui.dataclass.UITheme;

public class RatSkinTunic extends Armor {
    public RatSkinTunic() {
        super("Rat Skin Tunic", 2);
        overrideCharacter("👕");
        overrideColor(UITheme.ENTITY_RAT.darker().darker().darker());
    }
}

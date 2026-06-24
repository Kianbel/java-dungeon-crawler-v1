package item.armor;

import javafx.scene.paint.Color;

public class ReinforcedIronBreastplate extends Armor {
    public ReinforcedIronBreastplate() {
        super("Reinforced Iron Breastplate", 13);
        overrideCharacter("🥋");
        overrideColor(Color.WHITESMOKE.darker());
    }
}

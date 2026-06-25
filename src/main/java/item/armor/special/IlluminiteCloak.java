package item.armor.special;

import core.GameManager;
import entity.Player;
import javafx.scene.paint.Color;

public class IlluminiteCloak extends SpecialArmor {
    public IlluminiteCloak() {
        super("Illuminite Cloak", 15);
        overrideCharacter("👘");
        overrideColor(Color.BLUEVIOLET.brighter().brighter());
    }

    @Override
    public void wearEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.setIlluminationRange(player.getIlluminationRange() * 2);
    }

    @Override
    public void takeOffEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.resetIlluminationRange();
    }
}

package item.armor.special;

import core.GameManager;
import entity.Player;
import javafx.scene.paint.Color;

public class Shield extends SpecialArmor {
    public Shield() {
        super("Shield", 10);
        overrideCharacter("🛡");
        overrideColor(Color.RED.darker());
    }

    @Override
    public void wearEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.armorPenetrationChance = 0.8;
    }

    @Override
    public void takeOffEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.armorPenetrationChance = player.DEFAULT_ARMOR_PENETRATION_CHANCE;
    }
}

package item.armor.special;

import core.GameManager;
import entity.Player;
import gui.GUIManager;
import javafx.scene.paint.Color;

public class HeavyShield extends SpecialArmor {
    public HeavyShield() {
        super("Heavy Shield", 15);
        overrideCharacter("🛡");
        overrideColor(Color.RED.darker());
    }

    @Override
    public void wearEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.hungerDecreaseMovesCooldown = 20;
    }

    @Override
    public void takeOffEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.resetHungerDecreaseCooldown();
    }
}

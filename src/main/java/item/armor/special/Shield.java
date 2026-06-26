package item.armor.special;

import core.GameManager;
import entity.Player;
import javafx.scene.paint.Color;

public class Shield extends SpecialArmor {
    public Shield() {
        super("Shield", 20);
        overrideCharacter("🛡");
        overrideColor(Color.RED.darker());
    }

    @Override
    public void wearEffect() {
        Player player = GameManager.getInstance().getPlayer();
    }

    @Override
    public void takeOffEffect() {
        Player player = GameManager.getInstance().getPlayer();
    }
}

package item.armor.special;

import core.GameManager;
import entity.Player;
import javafx.scene.paint.Color;

public class PrecisionThingamabob extends SpecialArmor {
    public PrecisionThingamabob() {
        super("Precision Thingamabob", 8);
        overrideCharacter("◘");
        overrideColor(Color.WHITESMOKE);
    }

    @Override
    public void wearEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.isStunnable = false;
    }

    @Override
    public void takeOffEffect() {
        Player player = GameManager.getInstance().getPlayer();
        player.isStunnable = true;
    }
}

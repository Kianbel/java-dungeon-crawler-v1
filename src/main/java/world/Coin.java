package world;

import core.EntityRoomManager;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
import javafx.scene.paint.Color;
import util.Position;

public class Coin extends InteractableTile {
    public int amount;

    public Coin(Position roomLayoutPosition, int amount) {
        super(roomLayoutPosition, false);
        this.amount = amount;
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(entity instanceof Player p) {
            p.coins += amount;
            GUIManager.getInstance().setCoins(p.coins);
            GUIManager.getInstance().printLog("+" + amount + " coins.", Color.GOLD);
            GUIManager.getInstance().triggerTextPopup("+" + amount, Color.GOLD, p.position);
            EntityRoomManager.getInstance().removeInteractableTile(this);
        }
    }
}

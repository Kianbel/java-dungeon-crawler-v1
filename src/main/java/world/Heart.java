package world;

import core.EntityRoomManager;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
import util.Position;

public class Heart extends InteractableTile {
    private int healAmount;

    public Heart(Position roomLayoutPosition, int healAmount) {
        super(roomLayoutPosition, false);
        this.healAmount = healAmount;
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(entity instanceof Player p) {
            p.setHealth(p.health + healAmount);
            GUIManager.getInstance().printLog("[WORLD]: +" + healAmount + " HP", UITheme.LOG_WORLD);
            EntityRoomManager.getInstance().removeInteractableTile(this);
        }
    }
}

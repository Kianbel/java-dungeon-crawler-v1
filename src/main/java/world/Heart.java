package world;

import core.EntityRoomManager;
import entity.Entity;
import entity.Player;
import gui.AudioManager;
import gui.GUIManager;
import javafx.scene.paint.Color;
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
            GUIManager.getInstance().printLog("+" + healAmount + " HP", Color.GREEN);
            GUIManager.getInstance().triggerTextPopup("+" + healAmount, Color.GREEN, p.position);
            AudioManager.getInstance().playSFX("pickup");
            EntityRoomManager.getInstance().removeInteractableTile(this);
        }
    }
}

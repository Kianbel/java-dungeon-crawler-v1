package world;

import core.EntityRoomManager;
import entity.Entity;
import entity.Player;
import entity.monster.GiantSpider;
import gui.GUIManager;
import javafx.scene.paint.Color;
import util.Position;

public class Web extends InteractableTile {
    private final int STUN_MOVE_AMOUNT = 3;

    public Web(Position position) {
        super(position, false);
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(!(entity instanceof GiantSpider)) {
            entity.stun(STUN_MOVE_AMOUNT);
            EntityRoomManager.getInstance().removeInteractableTile(this);
            if(entity instanceof Player) {
                GUIManager.getInstance().triggerColorFlash(Color.WHITESMOKE);
            }
        }
    }
}

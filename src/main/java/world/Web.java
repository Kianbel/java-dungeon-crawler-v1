package world;

import core.EntityRoomManager;
import entity.Entity;
import entity.GiantSpider;
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
        }
    }
}

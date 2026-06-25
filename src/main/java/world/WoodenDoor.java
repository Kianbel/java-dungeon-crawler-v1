package world;

import core.EntityRoomManager;
import entity.Entity;
import gui.AudioManager;
import util.Position;

public class WoodenDoor extends InteractableTile {
    public WoodenDoor(Position roomLayoutPosition) {
        super(roomLayoutPosition, true);
        isLightOccluding = true;
    }

    @Override
    public void onEntityBump(Entity entity) {
        EntityRoomManager.getInstance().removeInteractableTile(this);
        AudioManager.getInstance().playSFX("door_enter");
    }
}

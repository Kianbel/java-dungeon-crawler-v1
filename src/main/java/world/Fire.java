package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import util.Position;

public class Fire extends InteractableTile {
    public Fire(Position roomLayoutPosition) {
        super(roomLayoutPosition, false);
    }

    @Override
    public void onEntityEnter(Entity entity) {
        final int FIRE_DAMAGE = 5;
        entity.hurt(FIRE_DAMAGE);

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
        currentRoom.removeInteractableTile(this);
    }
}

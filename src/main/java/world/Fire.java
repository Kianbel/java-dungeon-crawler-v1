package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
import util.Position;

public class Fire extends InteractableTile {
    public Fire(Position roomLayoutPosition) {
        super(roomLayoutPosition, false);
    }

    @Override
    public void onEntityEnter(Entity entity) {
        final int FIRE_DAMAGE = 5;
        entity.hurt(FIRE_DAMAGE);
        if(entity instanceof Player p) {
            GUIManager.getInstance().triggerColorFlash(UITheme.ITILE_FIRE, 1000);
        }

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
        currentRoom.removeInteractableTile(this);
    }
}

package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import util.Position;

public class Fire extends InteractableTile {
    public Fire(Position roomLayoutPosition) {
        super(roomLayoutPosition, false);
    }

    @Override
    public void onEntityEnter(Entity entity) {
        final int FIRE_DAMAGE = 5;
        entity.hurt(FIRE_DAMAGE);
        if(entity instanceof Player) {
            GUIManager.getInstance().triggerColorFlash(UITheme.ITILE_FIRE, 120);
            AudioManager.getInstance().playSFX("burn");
        }

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
        currentRoom.removeInteractableTile(this);
    }
}

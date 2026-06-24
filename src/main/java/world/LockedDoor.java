package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.Item;
import item.key.Key;
import item.key.TreasureRoomKey;
import util.Position;

import java.util.List;

public class LockedDoor extends InteractableTile {

    private final Class<? extends Key> keyClass;

    public LockedDoor(Position roomLayoutPosition, Class<? extends Key> keyClass) {
        super(roomLayoutPosition, true);
        this.keyClass = keyClass;
        isLightOccluding = true;
    }

    public LockedDoor(Position roomLayoutPosition) {
        this(roomLayoutPosition, TreasureRoomKey.class);
    }

    @Override
    public void onEntityBump(Entity entity) {
        if(entity instanceof Player p) {
            List<Item> inventory = p.getInventory();
            for(Item item : inventory) {
                if(item.getClass().equals(keyClass)) {
                    p.removeItemFromInventory(item);

                    Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
                    currentRoom.removeInteractableTile(this);

                    currentRoom.addInteractableTile(new OpenedDoor(roomLayoutPosition));

                    return;
                }
            }

            GUIManager.getInstance().printLog("It seems like the door needs a key.", UITheme.LOG_WORLD);
            GUIManager.getInstance().printLog("The key must be around here somewhere...", UITheme.LOG_WORLD);
            GUIManager.getInstance().triggerTextPopup("needs a key", UITheme.LOG_WORLD, p.position);
        }
    }
}

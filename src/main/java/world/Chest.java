package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import gui.GUIManager;
import gui.UITheme;
import util.Position;

public class Chest extends InteractableTile {
    private final InteractableTile chestDrop;

    public Chest(Position roomLayoutPosition, InteractableTile chestDrop) {
        super(roomLayoutPosition, true);
        this.chestDrop = chestDrop;
    }

    @Override
    public void onEntityBump(Entity entity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);

        if(chestDrop instanceof Coin) {
            GUIManager.getInstance().printLog("You break open the chest and it dropped some coins", UITheme.LOG_WORLD);
        }
        else if(chestDrop instanceof Heart) {
            GUIManager.getInstance().printLog("You break open the chest and it dropped a heart", UITheme.LOG_WORLD);
        }
        else if(chestDrop instanceof DroppedWeapon w) {
            GUIManager.getInstance().printLog("You break open the chest and it dropped " + w.weapon, UITheme.LOG_WORLD);
        }

        EntityRoomManager.getInstance().removeInteractableTile(this);
        currentRoom.addInteractableTile(chestDrop);
    }
}

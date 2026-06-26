package world;

import core.EntityRoomManager;
import core.GameManager;
import core.room.type.Room;
import entity.Entity;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.armor.*;
import item.weapon.*;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Chest extends InteractableTile {
    private InteractableTile chestDrop;

    public Chest(Position roomLayoutPosition, InteractableTile chestDrop) {
        super(roomLayoutPosition, true);
        this.chestDrop = chestDrop;
        isLightOccluding = true;
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
        else if(chestDrop instanceof DroppedItem droppedItem) {
            GUIManager.getInstance().printLog("You break open the chest and it dropped " + droppedItem.item.name, UITheme.LOG_WORLD);
        }

        EntityRoomManager.getInstance().removeInteractableTile(this);
        AudioManager.getInstance().playSFX("chest_open");
        currentRoom.addInteractableTile(chestDrop);
    }
}

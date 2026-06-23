package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import gui.GUIManager;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
import util.Position;
import util.Randomizer;


public class PressurePlateTrap extends Trap {
    private boolean isDud;

    public PressurePlateTrap(Position roomLayoutPosition) {
        super(roomLayoutPosition, false);
        if(Math.random() <= 0.5) isDud = true;
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(isDud) return;

        InteractableTile dispense = new Web(roomLayoutPosition);
        switch (Randomizer.pick(1,2)) {
            case 1 -> dispense = new Web(roomLayoutPosition);
            case 2 -> dispense = new SpikeTrap(roomLayoutPosition);
        }

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
        currentRoom.removeInteractableTile(this);
        currentRoom.addInteractableTile(dispense);
        dispense.onEntityEnter(entity);

        GUIManager.getInstance().triggerTextPopup("TRAP TRIGGERED!", Color.WHITE, roomLayoutPosition);
    }
}

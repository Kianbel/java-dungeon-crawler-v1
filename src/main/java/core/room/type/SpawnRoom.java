package core.room.type;

import core.EntityRoomManager;
import core.room.loader.RoomLayoutRegistry;
import entity.Player;
import item.weapon.IronBlade;
import item.weapon.Weapon;
import util.Position;
import util.TILE;
import world.*;

public class SpawnRoom extends Room {

    public SpawnRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(SpawnRoom.class);
        super(layout, minimapPosition);

        addInteractableTile(new OpenedDoor(new Position(3, 3)));
        addInteractableTile(new Staircase(new Position(5, 3)));
        addInteractableTile(new Chest(new Position(6,3), new DroppedItem(new Position(6,3), new IronBlade())));
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntityRoomManager.getInstance().addEntityToRoom(new Player(new Position(length/2, height/2)), this);
    }
}

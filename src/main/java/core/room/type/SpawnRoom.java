package core.room.type;

import core.EntityRoomManager;
import core.room.loader.RoomLayoutRegistry;
import entity.Player;
import util.Position;
import util.TILE;
import world.LevelDoor;
import world.LockedDoor;
import world.OpenedDoor;
import world.Staircase;

public class SpawnRoom extends Room {

    public SpawnRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(SpawnRoom.class);
        super(layout, minimapPosition);

        addInteractableTile(new OpenedDoor(new Position(3, 3)));
        addInteractableTile(new LevelDoor(new Position(4, 3)));
        addInteractableTile(new Staircase(new Position(5, 3)));
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntityRoomManager.getInstance().addEntityToRoom(new Player(new Position(length/2, height/2)), this);
    }
}

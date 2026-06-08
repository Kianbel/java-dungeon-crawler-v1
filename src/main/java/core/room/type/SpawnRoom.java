package core.room.type;

import core.EntityRoomManager;
import entity.Player;
import util.Position;

public class SpawnRoom extends Room {

    public SpawnRoom(int height, int length, Position minimapPosition) {
        super(height, length, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntityRoomManager.getInstance().addEntityToRoom(new Player(new Position(length/2, height/2)), this);
    }
}

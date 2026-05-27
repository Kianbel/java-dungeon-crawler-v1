package world;

import core.EntityRoomManager;
import entity.Player;
import util.Position;

public class SpawnRoom extends Room {

    public SpawnRoom(int height, int length, Position minimapPosition) {
        super(height, length, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        if(layout == null) throw new RuntimeException("Room not generated");

        EntityRoomManager.getInstance().addEntityToRoom(new Player(new Position(length/2, height/2)), this);
    }
}

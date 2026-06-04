package core.room;

import core.EntityRoomManager;
import entity.Player;
import util.Position;
import world.Chest;

public class SpawnRoom extends Room {

    public SpawnRoom(int height, int length, Position minimapPosition) {
        super(height, length, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot populate with entities as room has not generated");

        EntityRoomManager.getInstance().addEntityToRoom(new Player(new Position(length/2, height/2)), this);
        addInteractableTile(new Chest(new Position(length/2+2, height/2+2)));
    }
}

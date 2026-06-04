package core.room;

import core.EntityRoomManager;
import entity.GiantSpider;
import util.Position;

import java.util.List;

public class InfestedRoom extends  Room {

    public InfestedRoom(int height, int length, Position minimapPosition) {
        super(height, length, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot populate with entities as room has not generated");

        final int MONSTER_AMOUNT = 4;

        List<Position> spawnablePositions = getSpawnablePositions();
        for(int i = 0; i < MONSTER_AMOUNT; i++) {
            Position randomSpawnPosition = spawnablePositions.remove((int) (Math.random() * 100 % (spawnablePositions.size())));
            EntityRoomManager.getInstance().addEntityToRoom(new GiantSpider(randomSpawnPosition), this);
        }
    }
}

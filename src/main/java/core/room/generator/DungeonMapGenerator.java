package core.room.generator;

import util.MAP;
import util.Position;

public class DungeonMapGenerator {
    private MAP[][] mapLayout;
    private Generator generator;

    public DungeonMapGenerator(Generator generator) {
        this.generator = generator;
    }

    public void generate(int roomsAmount) {
        mapLayout = generator.start(roomsAmount);
    }

    public MAP[][] getMapLayout() {
        if(mapLayout == null) throw new RuntimeException("Dungeon map layout has not been generated");
        return mapLayout;
    }

    public Position getSpawnRoomMapPosition() {
        if(generator.spawnRoomPosition == null) throw new RuntimeException("cannot get spawn room position, generate map first");
        return generator.spawnRoomPosition;
    }

    public Position getBossRoomMapPosition() {
        if(generator.bossRoomPosition == null) throw new RuntimeException("cannot get last room position, generate map first");
        return generator.bossRoomPosition;
    }
}

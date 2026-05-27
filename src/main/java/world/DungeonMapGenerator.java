package world;

import util.Position;
import util.TILE;

public class DungeonMapGenerator {
    private TILE[][] mapLayout;
    private Generator generator;

    public DungeonMapGenerator(Generator generator) {
        this.generator = generator;
    }

    public void generate(int roomsAmount) {
        mapLayout = generator.start(roomsAmount);
    }

    public TILE[][] getMapLayout() {
        if(mapLayout == null) throw new RuntimeException("Dungeon map layout has not been generated");
        return mapLayout;
    }

    public Position getFirstRoomMinimapPosition() {
        if(generator.firstRoomPosition == null) throw new RuntimeException("cannot get firstRoomPosition, generate map first");
        return generator.firstRoomPosition;
    }

    public Position getLastRoomMinimapPosition() {
        if(generator.lastRoomPosition == null) throw new RuntimeException("cannot get lastRoomPosition, generate map first");
        return generator.lastRoomPosition;
    }
}

package world;

import util.TILE;

public class DungeonMapGenerator {
    private TILE[][] mapLayout;
    private Generator generator;

    public DungeonMapGenerator(Generator generator) {
        mapLayout = null;
        this.generator = generator;
    }

    public void generate(int roomsAmount) {
        mapLayout = generator.start(roomsAmount);
    }

    public TILE[][] getMapLayout() {
        if(mapLayout == null) throw new RuntimeException("Dungeon map layout has not been generated");
        return mapLayout;
    }
}

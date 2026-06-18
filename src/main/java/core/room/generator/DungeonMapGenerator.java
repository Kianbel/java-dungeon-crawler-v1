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

    public void printMapLayout() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('\t');
        for(int i = 0; i < mapLayout[0].length; i++) {
            stringBuilder.append(Integer.toHexString(i));
        }
        stringBuilder.append('\n');
        for(int i = 0; i < mapLayout.length; i++) {
            stringBuilder.append(i);
            stringBuilder.append('\t');
            for(int j = 0; j < mapLayout[0].length; j++) {
                MAP tile = mapLayout[i][j];
                if(tile == null) {
                    stringBuilder.append('.');
                    continue;
                }
                switch(tile) {
                    case SPAWN -> stringBuilder.append('s');
                    case BOSS -> stringBuilder.append('b');
                    case TREASURE -> stringBuilder.append('t');
                    case NORMAL -> stringBuilder.append('n');
                    case EXTRA -> stringBuilder.append('e');
                    case VCORRIDOR -> stringBuilder.append('|');
                    case HCORRIDOR -> stringBuilder.append('-');
                    default -> stringBuilder.append('.');
                }
            }
            stringBuilder.append('\n');
        }
        System.out.println(stringBuilder);
    }
}

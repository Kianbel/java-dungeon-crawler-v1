package world;

import util.TILE;

public class Room {
    public int length;
    public int width;
    private int[][] layout;

    public Room(int length, int width) {
        this.length = length;
        this.width = width;
        layout = new int[length][width];
    }

    public void generateWithDoors(boolean north, boolean east, boolean south, boolean west) {
        for(int i = 0; i < length; i++) {
            layout[i][0] = TILE.WALL.ordinal();
            layout[i][length-1] = TILE.WALL.ordinal();
        }
        for(int i = 0; i < width; i++) {
            layout[0][i] = TILE.WALL.ordinal();
            layout[width-1][i] = TILE.WALL.ordinal();
        }

        layout[0][width/2] = TILE.DOOR.ordinal();
        layout[length-1][width/2] = TILE.DOOR.ordinal();
        layout[length/2][0] = TILE.DOOR.ordinal();
        layout[length/2][width-1] = TILE.DOOR.ordinal();
    }
}

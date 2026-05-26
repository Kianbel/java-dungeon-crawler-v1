package world;

import util.TILE;

public class Room {
    public int height;
    public int length;
    private TILE[][] layout;

    public Room(int height, int length) {
        this.height = height;
        this.length = length;
        layout = new TILE[height][length];
    }

    public void generateWithDoors(boolean north, boolean east, boolean south, boolean west) {
        for(int i = 0; i < height; i++) {
            layout[i][0] = TILE.WALL;
            layout[i][height-1] = TILE.WALL;
        }
        for(int i = 0; i < length; i++) {
            layout[0][i] = TILE.WALL;
            layout[length-1][i] = TILE.WALL;
        }

        layout[0][length/2] = TILE.DOOR;
        layout[height-1][length/2] = TILE.DOOR;
        layout[height/2][0] = TILE.DOOR;
        layout[height/2][length-1] = TILE.DOOR;
    }
}

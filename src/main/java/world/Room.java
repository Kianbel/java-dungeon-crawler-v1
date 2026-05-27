package world;

import util.Position;
import util.ROOM_TYPE;
import util.TILE;

import java.util.Arrays;

public abstract class Room {
    public int height;
    public int length;
    public Position minimapPosition;
    protected TILE[][] layout;

    public Room(int height, int length, Position minimapPosition) {
        this.height = height;
        this.length = length;
        this.minimapPosition = minimapPosition;
        layout = new TILE[height][length];
    }

    public void generateWithDoors(boolean north, boolean east, boolean south, boolean west) {
        for(TILE[] row : layout) {
            Arrays.fill(row, TILE.FLOOR);
        }

        for(int i = 0; i < height; i++) {
            layout[i][0] = TILE.WALL;
            layout[i][height-1] = TILE.WALL;
        }
        for(int i = 0; i < length; i++) {
            layout[0][i] = TILE.WALL;
            layout[length-1][i] = TILE.WALL;
        }

        if(north) layout[0][length/2] = TILE.DOOR;
        if(south) layout[height-1][length/2] = TILE.DOOR;
        if(west) layout[height/2][0] = TILE.DOOR;
        if(east) layout[height/2][length-1] = TILE.DOOR;
    }

    public TILE[][] getLayout() {
        return layout;
    }

    public abstract void populateWithEntities();
}

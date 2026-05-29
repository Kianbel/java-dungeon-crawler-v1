package world;

import javafx.geometry.Pos;
import util.Position;
import util.ROOM_TYPE;
import util.TILE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Room {
    public int height;
    public int length;
    public Position minimapPosition;
    protected TILE[][] layout;

    protected boolean isRoomGenerated = false;

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
        isRoomGenerated = true;
    }

    public TILE[][] getLayout() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot get roomLayout as room has not generated");
        return layout;
    }

    public abstract void populateWithEntities();

    public List<Position> getSpawnablePositions() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot get spawnablePositions as room has not generated");

        List<Position> spawnablePositions = new ArrayList<>();
        // TODO: cannot spawn 1 block all sides surrounding a door

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < length; x++) {
                if(layout[y][x] == TILE.FLOOR) spawnablePositions.add(new Position(x, y));
            }
        }
        return spawnablePositions;
    }
}

package core.room;

import util.Position;
import util.TILE;
import world.InteractableTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Room {
    public int height;
    public int length;
    public Position minimapPosition;
    protected TILE[][] layout;
    public int id;
    private List<InteractableTile> interactableTiles = new ArrayList<>();

    protected boolean isRoomGenerated = false;

    public Room(int height, int length, Position minimapPosition) {
        this.height = height;
        this.length = length;
        this.minimapPosition = minimapPosition;
        layout = new TILE[height][length];
        id = this.hashCode();
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

    public List<InteractableTile> getInteractableTiles() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot get interactableTiles as room has not generated");
        return interactableTiles;
    }

    public boolean addInteractableTile(InteractableTile tile) {
        Position targetPosition = tile.roomLayoutPosition;
        if(targetPosition.x < 0 || targetPosition.x >= length) throw new RuntimeException("Cannot put interactable tile to room due to invalid position");
        if(targetPosition.y < 0 || targetPosition.y >= height) throw new RuntimeException("Cannot put interactable tile to room due to invalid position");
        if(layout[targetPosition.y][targetPosition.x] != TILE.FLOOR) throw new RuntimeException("Cannot put interactable tile to room due to room tile is already there");

        for(InteractableTile t : interactableTiles) {
            if(t.roomLayoutPosition.x == tile.roomLayoutPosition.x && t.roomLayoutPosition.y == tile.roomLayoutPosition.y) {
                return false;
            }
        }

        return interactableTiles.add(tile);
    }

    public boolean removeInteractableTile(InteractableTile tile) {
        if(!interactableTiles.contains(tile)) throw new RuntimeException("Cannot find interactable tile: " + tile);
        return interactableTiles.remove(tile);
    }
}

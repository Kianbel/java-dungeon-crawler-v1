package core.room.type;

import util.Position;
import util.TILE;
import world.InteractableTile;

import java.util.*;

public abstract class Room {
    public int height;
    public int length;
    public Position minimapPosition;
    public int id = this.hashCode();

    protected static final int MAX_ROOM_LENGTH = 30;
    protected static final int MAX_ROOM_HEIGHT = 25;
    protected static final int MIN_ROOM_LENGTH = 15;
    protected static final int MIN_ROOM_HEIGHT = 15;


    private List<InteractableTile> interactableTiles = new ArrayList<>();

    protected TILE[][] layout;
    protected boolean isRoomGenerated = false;

    public Room(int height, int length, Position minimapPosition) {
        this.height = height;
        this.length = length;
        this.minimapPosition = minimapPosition;
        layout = new TILE[height][length];

        for(TILE[] row : layout) {
            Arrays.fill(row, TILE.FLOOR);
        }
    }

    public Room(TILE[][] layout, Position minimapPosition, int height, int length) {
        this.minimapPosition = minimapPosition;

        if(layout != null) {
            this.height = layout.length;
            this.length = layout[0].length;
            this.layout = layout;
        }
        else {
            this.height = height;
            this.length = length;
            this.layout = new TILE[height][length];

            for(TILE[] row : this.layout) {
                Arrays.fill(row, TILE.FLOOR);
            }
        }
    }

    public void generate(boolean hasDoorNorth, boolean hasDoorEast, boolean hasDoorSouth, boolean hasDoorWest) {
        for(int i = 0; i < height; i++) {
            layout[i][0] = TILE.WALL;
            layout[i][length-1] = TILE.WALL;
        }
        for(int i = 0; i < length; i++) {
            layout[0][i] = TILE.WALL;
            layout[height-1][i] = TILE.WALL;
        }

        // --- RANDOM PASSABLE_OBSTACLES (FOR DECORATIONS) ---
        final int FLOOR_DECOR_AMOUNT = (int) (length * height * 0.05);
        for(int i = 0; i < FLOOR_DECOR_AMOUNT; i++) {
            final int x = new Random().nextInt(1, length-1);
            final int y = new Random().nextInt(1, height-1);
            if(layout[y][x] == TILE.FLOOR) layout[y][x] = TILE.PASSABLE_OBSTACLE;
        }

        if(hasDoorNorth) layout[0][length/2] = TILE.DOOR;
        if(hasDoorSouth) layout[height-1][length/2] = TILE.DOOR;
        if(hasDoorWest) layout[height/2][0] = TILE.DOOR;
        if(hasDoorEast) layout[height/2][length-1] = TILE.DOOR;
        isRoomGenerated = true;
    }

    public TILE[][] getLayout() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot get roomLayout as room has not generated");
        return layout;
    }

    public void populateWithEntities() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot populate with entities as room has not generated");
    }

    public List<Position> getSpawnablePositions() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot get spawnablePositions as room has not generated");

        List<Position> spawnablePositions = new ArrayList<>();
        // TODO: cannot spawn 1 block all sides surrounding a door

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < length; x++) {
                TILE tile = layout[y][x];
                if(tile == TILE.FLOOR) spawnablePositions.add(new Position(x, y));
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
        if(layout[targetPosition.y][targetPosition.x] != TILE.FLOOR) throw new RuntimeException("Cannot put " + tile + " to room due to " + layout[targetPosition.y][targetPosition.x] + " is already there");

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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(ID:" + id + ")";
    }
}

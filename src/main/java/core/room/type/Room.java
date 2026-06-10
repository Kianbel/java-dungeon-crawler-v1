package core.room.type;

import util.DIRECTION;
import util.Position;
import util.TILE;
import world.Box;
import world.InteractableTile;

import java.util.*;

public abstract class Room {
    public int height;
    public int length;
    public Position minimapPosition;
    public int id = this.hashCode();

    private Map<DIRECTION, Position> doorPositionsHashmap = new HashMap<>();
    private List<InteractableTile> interactableTiles = new ArrayList<>();
    private List<Position> playerTravelledPositions = new ArrayList<>();

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

    public Room(TILE[][] layout, Position minimapPosition) {
        this.layout = layout;
        this.minimapPosition = minimapPosition;
        this.height = layout.length;
        this.length = layout[0].length;
    }

    public void generate(boolean hasDoorNorth, boolean hasDoorEast, boolean hasDoorSouth, boolean hasDoorWest) {
        // --- RANDOM PASSABLE_OBSTACLES (FOR DECORATIONS) ---
        final Random rand = new Random();
        final int FLOOR_DECOR_AMOUNT = (int) (length * height * 0.05);
        for(int i = 0; i < FLOOR_DECOR_AMOUNT; i++) {
            final int x = rand.nextInt(1, length-1);
            final int y = rand.nextInt(1, height-1);
            if(layout[y][x] == TILE.FLOOR) layout[y][x] = TILE.PASSABLE_OBSTACLE;
        }

        // --- HANDLE BOX/DOOR GENERATION ---
        final double BOX_SPAWN_CHANCE = 0.6;

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < length; x++) {
                if(layout[y][x] == TILE.BOX) {
                    layout[y][x] = TILE.FLOOR;
                    if(Math.random() <= BOX_SPAWN_CHANCE) addInteractableTile(new Box(new Position(x,y)));
                }
                else if(layout[y][x] == TILE.DOOR) {
                    // if tile above door is not wall, door is south door
                    if(y-1 >= 0 && layout[y-1][x] != TILE.WALL && layout[y-1][x] != TILE.EMPTY) {
                        if(!hasDoorSouth) layout[y][x] = TILE.WALL;
                        else doorPositionsHashmap.put(DIRECTION.SOUTH, new Position(x,y-1));
                    }
                    // if tile below door is not wall, door is north door
                    if(y+1 < height && layout[y+1][x] != TILE.WALL && layout[y+1][x] != TILE.EMPTY) {
                        if(!hasDoorNorth) layout[y][x] = TILE.WALL;
                        else doorPositionsHashmap.put(DIRECTION.NORTH, new Position(x,y+1));
                    }
                    // if tile left of door is not wall, door is east door
                    if(x-1 >= 0 && layout[y][x-1] != TILE.WALL && layout[y][x-1] != TILE.EMPTY) {
                        if(!hasDoorEast) layout[y][x] = TILE.WALL;
                        else doorPositionsHashmap.put(DIRECTION.EAST, new Position(x-1, y));
                    }
                    // if tile right of door is not wall, door is west door
                    if(x+1 < length && layout[y][x+1] != TILE.WALL && layout[y][x+1] != TILE.EMPTY) {
                        if(!hasDoorWest) layout[y][x] = TILE.WALL;
                        else doorPositionsHashmap.put(DIRECTION.WEST, new Position(x+1, y));
                    }
                }
            }
        }
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

    public void addPlayerTravelledPosition(Position position) {
        playerTravelledPositions.add(position);
    }

    public List<Position> getPlayerTravelledPositions() {
        return playerTravelledPositions;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(ID:" + id + ")";
    }

    public Position getEnteringPositionFromDirection(DIRECTION fromDirection) {
        System.out.println(fromDirection);
        switch (fromDirection) {
            case NORTH -> {
                return doorPositionsHashmap.get(DIRECTION.SOUTH);
            }
            case SOUTH -> {
                return doorPositionsHashmap.get(DIRECTION.NORTH);
            }
            case EAST -> {
                return doorPositionsHashmap.get(DIRECTION.WEST);
            }
            case WEST -> {
                return doorPositionsHashmap.get(DIRECTION.EAST);
            }
            default -> throw new RuntimeException("Invalid fromDirection: " + fromDirection);
        }
    }
}

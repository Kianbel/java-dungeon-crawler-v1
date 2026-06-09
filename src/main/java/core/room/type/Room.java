package core.room.type;

import util.DIRECTION;
import util.Position;
import util.TILE;
import world.InteractableTile;

import java.util.*;

public abstract class Room {
    public int height;
    public int length;
    public Position minimapPosition;
    public int id = this.hashCode();

    Map<DIRECTION, Position> doorPositions = new HashMap<>();

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

        // --- GENERATE DOORS ---
        boolean stop = false;
        for(int i = 0; i < height; i++) {
            if(stop) break;
            for(int j = 0; j < length; j++) {
                if(layout[i][j] == TILE.DOOR) {
                    if(!hasDoorNorth) layout[i][j] = TILE.WALL;
                    else doorPositions.put(DIRECTION.NORTH, new Position(i, j));
                    stop = true;
                    break;
                }
            }
        }
        stop = false;
        for(int i = height-1; i >= 0; i--) {
            if(stop) break;
            for(int j = 0; j < length; j++) {
                if(layout[i][j] == TILE.DOOR) {
                    if(!hasDoorSouth) layout[i][j] = TILE.WALL;
                    else doorPositions.put(DIRECTION.SOUTH, new Position(i, j));
                    stop = true;
                    break;
                }
            }
        }
        stop = false;
        for(int i = 0; i < length; i++) {
            if(stop) break;
            for(int j = 0; j < height; j++) {
                if(layout[j][i] == TILE.DOOR) {
                    if(!hasDoorWest) layout[j][i] = TILE.WALL;
                    else doorPositions.put(DIRECTION.WEST, new Position(j, i));
                    stop = true;
                    break;
                }
            }
        }
        stop = false;
        for(int i = length-1; i >= 0; i--) {
            if(stop) break;
            for(int j = 0; j < height; j++) {
                if(layout[j][i] == TILE.DOOR) {
                    if(!hasDoorEast) layout[j][i] = TILE.WALL;
                    else doorPositions.put(DIRECTION.EAST, new Position(j, i));
                    stop = true;
                    break;
                }
            }
        }
        isRoomGenerated = true;
    }

    // TODO: fix room enter wrong door teleport
    public Position getEnterDoorPositionFromUnitPos(Position unitPos) {
        if(unitPos.x > 1 || unitPos.x < -1) throw new RuntimeException("Invalid unitPos: " + unitPos);
        if(unitPos.y > 1 || unitPos.y < -1) throw new RuntimeException("Invalid unitPos: " + unitPos);

        if(unitPos.x == 0 && unitPos.y == 1) return doorPositions.get(DIRECTION.NORTH);
        else if(unitPos.x == 0 && unitPos.y == -1) return doorPositions.get(DIRECTION.SOUTH);
        else if(unitPos.x == -1 && unitPos.y == 0) return doorPositions.get(DIRECTION.WEST);
        else return doorPositions.get(DIRECTION.EAST);
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(ID:" + id + ")";
    }
}

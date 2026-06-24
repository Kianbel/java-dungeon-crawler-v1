package core.room.type;

import util.DIRECTION;
import util.Position;
import util.Randomizer;
import util.TILE;
import world.*;
import world.Chest;

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

    private Random random = new Random();

    public Room(TILE[][] layout, Position minimapPosition) {
        this.layout = layout;
        int randomRotationAmount = random.nextInt(4);
        for(int i = 0; i < randomRotationAmount; i++) {
            rotateLayout90Degrees(this.layout);
        }
        this.minimapPosition = minimapPosition;
        this.height = layout.length;
        this.length = layout[0].length;
    }

    public void generate(boolean hasDoorNorth, boolean hasDoorEast, boolean hasDoorSouth, boolean hasDoorWest) {
        // --- RANDOM OBSTACLES --
        if(this instanceof ExtraRoom || this instanceof NormalRoom) {
            List<Position> replaceablePositions = new ArrayList<>();
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < length; x++) {
                    if(layout[y][x] == TILE.FLOOR) replaceablePositions.add(new Position(x,y));
                }
            }

            double FLOOR_OBSTACLES_AMOUNT = replaceablePositions.size() * 0.1;
            for(int i = 0; i < FLOOR_OBSTACLES_AMOUNT; i++) {
                Position replacedPos = replaceablePositions.remove(random.nextInt(replaceablePositions.size()));
                switch (Randomizer.pick(1,2,3)) {
                    case 1 -> layout[replacedPos.y][replacedPos.x] = TILE.GRASS;
                    case 2 -> layout[replacedPos.y][replacedPos.x] = TILE.WEB;
                    case 3 -> layout[replacedPos.y][replacedPos.x] = TILE.SPIKE;
                }
            }
            FLOOR_OBSTACLES_AMOUNT = replaceablePositions.size() * 0.1;
            for(int i = 0; i < FLOOR_OBSTACLES_AMOUNT; i++) {
                Position replacedPos = replaceablePositions.remove(random.nextInt(replaceablePositions.size()));
                addInteractableTile(new PressurePlateTrap(replacedPos));
            }
        }


        // --- HANDLE BOX/DOOR GENERATION ---
        final double BOX_SPAWN_CHANCE = 0.6;
        final double BOOKSHELF_SPAWN_CHANCE = 0.8;
        final double SOLID_OBSTACLE_SPAWN_CHANCE = 0.5;
        final double WEB_SPAWN_CHANCE = 0.5;
        final double CARPET_PUT_CHANCE = 0.6;

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < length; x++) {
                switch(layout[y][x]) {
                    case SOLID_OBSTACLE -> {
                        if(Math.random() > SOLID_OBSTACLE_SPAWN_CHANCE) this.layout[y][x] = TILE.FLOOR;
                    }
                    case BOOKSHELF -> {
                        if(Math.random() > BOOKSHELF_SPAWN_CHANCE) this.layout[y][x] = TILE.FLOOR;
                    }
                    case CARPET -> {
                        if(Math.random() <= CARPET_PUT_CHANCE) this.layout[y][x] = TILE.FLOOR;
                    }
                    case SPIKE -> {
                        this.layout[y][x] = TILE.FLOOR;
                        addInteractableTile(new SpikeTrap(new Position(x,y)));
                    }
                    case LOCKED_DOOR -> {
                        this.layout[y][x] = TILE.FLOOR;
                        addInteractableTile(new LockedDoor(new Position(x,y)));
                    }
                    case STAIRCASE -> {
                        this.layout[y][x] = TILE.FLOOR;
                        addInteractableTile(new Staircase(new Position(x,y)));
                    }
                    case FIRE -> {
                        this.layout[y][x] = TILE.FLOOR;
                        addInteractableTile(new Fire(new Position(x,y)));
                    }
                    case WEB -> {
                        this.layout[y][x] = TILE.FLOOR;
                        if(Math.random() <= WEB_SPAWN_CHANCE) addInteractableTile(new Web(new Position(x,y)));
                    }
                    case CHEST -> {
                        this.layout[y][x] = TILE.FLOOR;
                        Position currentPos = new Position(x,y);
                        addInteractableTile(new Chest(currentPos));
                    }
                    case BOX -> {
                        layout[y][x] = TILE.FLOOR;
                        if(Math.random() <= BOX_SPAWN_CHANCE) addInteractableTile(new Pot(new Position(x, y)));
                    }
                    case DOOR -> {
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

        boolean[][] interactableTileCache = new boolean[height][length];
        for(InteractableTile interactableTile : interactableTiles) {
            interactableTileCache[interactableTile.roomLayoutPosition.y][interactableTile.roomLayoutPosition.x] = true;
        }

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < length; x++) {
                TILE tile = layout[y][x];
                if(tile.isWalkable() && !interactableTileCache[y][x]) spawnablePositions.add(new Position(x, y));
            }
        }
        return spawnablePositions;
    }

    public List<InteractableTile> getInteractableTiles() {
        if(!isRoomGenerated) throw new RuntimeException("Cannot get interactableTiles as room has not generated");
        return interactableTiles;
    }

    public void addSkeletonTileAt(Position position) {
        if(layout[position.y][position.x].isWalkable()) {
            layout[position.y][position.x] = TILE.SKELETON;
        }
    }

    public void addInteractableTile(InteractableTile tile) {
        Position targetPosition = tile.roomLayoutPosition;
        if(targetPosition.x < 0 || targetPosition.x >= length) throw new RuntimeException("Cannot put interactable tile to room due to invalid position");
        if(targetPosition.y < 0 || targetPosition.y >= height) throw new RuntimeException("Cannot put interactable tile to room due to invalid position");

        for(int i = 0; i < interactableTiles.size(); i++) {
            InteractableTile interactableTile = interactableTiles.get(i);
            if(interactableTile.roomLayoutPosition.equals(tile.roomLayoutPosition)) {
                interactableTiles.set(i, tile);
                return;
            }
        }
        interactableTiles.add(tile);
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

    private void rotateLayout90Degrees(TILE[][] matrix) {
        int n = matrix.length;
        int blockSize = 64; // Optimized for typical CPU cache lines

        // 1. Cache-friendly Transpose using Tiling
        for (int r = 0; r < n; r += blockSize) {
            for (int c = 0; c < n; c += blockSize) {

                // Process the current block
                for (int i = r; i < Math.min(r + blockSize, n); i++) {
                    for (int j = c; j < Math.min(c + blockSize, n); j++) {
                        if (i < j) {
                            TILE temp = matrix[i][j];
                            matrix[i][j] = matrix[j][i];
                            matrix[j][i] = temp;
                        }
                    }
                }

            }
        }

        // 2. Reverse rows
        for (int i = 0; i < n; i++) {
            int left = 0;
            int right = n - 1;
            while (left < right) {
                TILE temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }

    }
}

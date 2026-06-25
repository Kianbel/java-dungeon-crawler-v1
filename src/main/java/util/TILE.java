package util;

public enum TILE {
    // Room tiles
    WALL,
    FLOOR,
    DOOR,
    GRASS,
    WATER,
    SOLID_OBSTACLE,
    PASSABLE_OBSTACLE,
    BOOKSHELF,
    BOX,
    WEB,
    FIRE,
    CHEST,
    LOCKED_DOOR,
    STAIRCASE,
    CARPET,
    IRON_BAR,
    SPIKE,
    SHOOTER,
    POSSIBLE_KEY,
    SKELETON,
    BRIDGE,
    WOODEN_DOOR,
    BREAKABLE_WALL,
    EMPTY;

    public boolean isWalkable() {
        return this == FLOOR ||
                this == PASSABLE_OBSTACLE ||
                this == GRASS ||
                this == CARPET ||
                this == SKELETON ||
                this == BRIDGE;
    }

    public boolean isLightOccluding() {
        return this == WALL ||
                this == BOOKSHELF ||
                this == DOOR ||
                this == GRASS ||
                this == WOODEN_DOOR ||
                this == BREAKABLE_WALL;
    }
}

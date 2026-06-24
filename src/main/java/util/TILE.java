package util;

public enum TILE {
    // Room tiles
    WALL,
    FLOOR,
    DOOR,
    GRASS,
    WATER,
    SOLID_OBSTACLE,
    PASSABLE_DECOR,
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
    EMPTY;

    public boolean isWalkable() {
        return this == FLOOR ||
                this == PASSABLE_DECOR ||
                this == GRASS ||
                this == CARPET ||
                this == SKELETON ||
                this == BRIDGE;
    }

    public boolean isLightOccluding() {
        return this == WALL ||
                this == BOOKSHELF ||
                this == DOOR ||
                this == GRASS;
    }
}

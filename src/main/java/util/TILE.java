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
    EMPTY;

    public boolean isWalkable() {
        return this == FLOOR ||
                this == PASSABLE_OBSTACLE ||
                this == GRASS ||
                this == CARPET;
    }

    public boolean isLightOccluding() {
        return this == WALL ||
                this == BOOKSHELF ||
                this == DOOR;
    }
}

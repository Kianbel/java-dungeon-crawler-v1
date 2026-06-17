package util;

public enum TILE {
    // Room tiles
    EMPTY,
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
    TORCH,
    CHEST,
    LEVEL_DOOR,
    LOCKED_DOOR,
    STAIRCASE;

    public boolean isWalkable() {
        return this == FLOOR || this == PASSABLE_OBSTACLE || this == GRASS;
    }
}

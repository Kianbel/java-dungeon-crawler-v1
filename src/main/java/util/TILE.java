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
    BOX;

    public boolean isWalkable() {
        return this == FLOOR || this == PASSABLE_OBSTACLE || this == GRASS;
    }
}

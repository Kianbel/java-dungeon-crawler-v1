package world;

import util.TILE;

public abstract class Generator {
    public static final int MAP_HEIGHT = 13;
    public static final int MAP_LENGTH = 13;

    public abstract TILE[][] start(int roomsAmount);
}

package core;

import util.Position;
import util.TILE;

public abstract class Generator {
    public static final int MAP_HEIGHT = 13;
    public static final int MAP_LENGTH = 13;
    public Position firstRoomPosition = null;
    public Position lastRoomPosition = null;

    public abstract TILE[][] start(int roomsAmount);
}

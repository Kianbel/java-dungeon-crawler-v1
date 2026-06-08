package core.room.generator;

import util.MAP;
import util.Position;

public abstract class Generator {
    public static final int MAP_HEIGHT = 13;
    public static final int MAP_LENGTH = 13;
    public Position spawnRoomPosition = null;
    public Position bossRoomPosition = null;

    public abstract MAP[][] start(int roomsAmount);
}

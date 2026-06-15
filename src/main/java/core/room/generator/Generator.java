package core.room.generator;

import util.MAP;
import util.Position;

public abstract class Generator {
    public Position spawnRoomPosition = null;
    public Position bossRoomPosition = null;

    public abstract MAP[][] start(int roomsAmount);
}

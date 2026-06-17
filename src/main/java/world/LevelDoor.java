package world;

import item.key.LevelKey;
import util.Position;

public class LevelDoor extends LockedDoor {
    public LevelDoor(Position roomLayoutPosition) {
        super(roomLayoutPosition, LevelKey.class);
    }
}

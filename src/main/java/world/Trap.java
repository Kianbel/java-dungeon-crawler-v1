package world;

import util.Position;

public abstract class Trap extends InteractableTile {
    public Trap(Position roomLayoutPosition, boolean isSolid) {
        super(roomLayoutPosition, isSolid);
    }
}

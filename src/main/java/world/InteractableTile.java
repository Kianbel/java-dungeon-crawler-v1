package world;

import entity.Entity;
import util.Position;

public abstract class InteractableTile {
    public Position roomLayoutPosition;

    public InteractableTile(Position roomLayoutPosition) {
        this.roomLayoutPosition = roomLayoutPosition;
    }

    public abstract void interact(Entity doer);
}

package world;

import entity.Entity;
import util.Position;

public abstract class InteractableTile {
    public Position roomLayoutPosition;

    public InteractableTile(Position roomLayoutPosition) {
        this.roomLayoutPosition = roomLayoutPosition;
    }

    public void onEntityEnter(Entity entity) {}

    public void onEntityStay(Entity entity) {}

    public void onEntityExit(Entity entity) {}

    public void onInteract(Entity entity) {}
}

package world;

import entity.Entity;
import util.Position;

public abstract class InteractableTile {
    public Position roomLayoutPosition;
    public boolean isSolid = false;

    public InteractableTile(Position roomLayoutPosition, boolean isSolid) {
        this.roomLayoutPosition = roomLayoutPosition;
        this.isSolid = isSolid;
    }

    public void onEntityEnter(Entity entity) {}

    public void onEntityStay(Entity entity) {}

    public void onEntityExit(Entity entity) {}

    public void onInteract(Entity entity) {}

    public void onEntityBump(Entity entity) {}
}

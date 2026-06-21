package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import javafx.scene.paint.Color;
import util.Position;

public abstract class InteractableTile {
    public Position roomLayoutPosition;
    public boolean isSolid = false;
    public boolean isLightOccluding = false;
    private Color color = null;
    private String character = null;

    public InteractableTile(Position roomLayoutPosition, boolean isSolid) {
        this.roomLayoutPosition = roomLayoutPosition;
        this.isSolid = isSolid;
    }

    public void overrideColor(Color color) {
        if(color == null) return;
        this.color = color;
    }

    public void resetColor() {
        this.color = null;
    }

    public Color getColor() {
        return color;
    }

    public void overrideCharacter(String character) {
        if(character == null) return;
        this.character = character;
    }

    public void resetCharacter() {
        this.character = null;
    }

    public String getCharacter() {
        return character;
    }

    public void onEntityEnter(Entity entity) {}

    public void onEntityStay(Entity entity) {}

    public void onEntityExit(Entity entity) {}

    public void onInteract(Entity entity) {}

    public void onEntityBump(Entity entity) {}

    public boolean isInBounds(int roomHeight, int roomLength) {
        return roomLayoutPosition.x >= 0 && roomLayoutPosition.x < roomLength && roomLayoutPosition.y >= 0 && roomLayoutPosition.y < roomHeight;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}

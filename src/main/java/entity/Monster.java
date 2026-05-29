package entity;

import core.EntityRoomManager;
import item.Weapon;
import util.Position;
import util.TILE;
import world.Room;

import java.util.List;

public abstract class Monster extends Entity implements MoveAfterPlayer {
    public Monster(String name, int health, int armor, Weapon weapon, Position position) {
        super(name, health, armor, weapon, position);
    }

    public abstract void pathfindToPlayerPosition();

    protected boolean isValidTargetWalkPosition(Position targetPosition) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] roomLayout = currentRoom.getLayout();
        final int roomHeight = roomLayout.length;
        final int roomLength = roomLayout[0].length;

        if(targetPosition.x < 0 || targetPosition.x >= roomLength) return false;
        if(targetPosition.y < 0 || targetPosition.y >= roomHeight) return false;
        if(roomLayout[targetPosition.y][targetPosition.x] != TILE.FLOOR) return false;

        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
        for(Entity e : entities) {
            if(e == this) continue;
            if(e.position.x == targetPosition.x && e.position.y == targetPosition.y) return false;
        }
        return true;
    }
}

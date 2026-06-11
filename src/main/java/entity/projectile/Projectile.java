package entity.projectile;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.MoveAfterPlayer;
import util.Position;
import util.TILE;
import weapon.GenericDamager;

public abstract class Projectile extends Entity implements MoveAfterPlayer {
    protected final Position movementUnitPos;

    public Projectile(String name, int damage, Position movementUnitPos, Position position) {
        super(name, 1, 0, new GenericDamager(damage, 0.1), position);
        this.movementUnitPos = movementUnitPos;
    }

    protected void move() {
        Position targetPosition = position.add(movementUnitPos);
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] roomLayout = currentRoom.getLayout();
        TILE tile = roomLayout[targetPosition.y][targetPosition.x];
        switch(tile) {
            case TILE.FLOOR, GRASS, PASSABLE_OBSTACLE: break;
            default:
                die();
        }

        walk(movementUnitPos);
    }
}

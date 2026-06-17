package entity.projectile;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.MoveAfterPlayer;
import util.Position;
import util.TILE;
import item.weapon.GenericDamager;

import java.util.List;

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

        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
        for(Entity e : entities) {
            if(e.equals(this)) continue;
            if((e.position.equals(targetPosition) || e.position.equals(position)) && !(e instanceof Projectile)) {
                attack(e);
                die();
                return;
            }
        }

        TILE tile = roomLayout[targetPosition.y][targetPosition.x];
        if(!tile.isWalkable()) {
            die();
            return;
        }

        walk(movementUnitPos);
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        attack(attacker);
    }

    @Override
    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            int inflictedDamage = weapon.getCalculatedAttackDamage();
            targetEntity.hurt(inflictedDamage, this);
            die();
        }
        else throw new RuntimeException(this + " cannot attack " + targetEntity + " as target is not in same room");
    }
}

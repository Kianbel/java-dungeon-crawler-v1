package entity.projectile;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.MoveAfterPlayer;
import gui.GUIManager;
import javafx.scene.paint.Color;
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
                return;
        }

        Entity player = EntityRoomManager.getInstance().getPlayer();
        if(player.position.equals(targetPosition)) {
            attack(player);
        }
        else walk(movementUnitPos);
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

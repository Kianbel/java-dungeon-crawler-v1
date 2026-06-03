package entity;

import core.EntityRoomManager;
import gui.GUIManager;
import weapon.Weapon;
import javafx.scene.paint.Color;
import util.Position;
import util.TILE;
import core.Room;
import world.Coin;
import world.InteractableTile;

import java.util.List;

public abstract class Entity {
    public String name;
    public int health;
    public int armor;
    public Weapon weapon;
    public Position position;
    public int id;
    public int stunCounter;

    public Entity(String name, int health, int armor, Weapon weapon, Position position) {
        this.name = name;
        this.armor = armor;
        this.health = health;
        this.weapon = weapon;
        this.position = position;
        id = this.hashCode();
        stunCounter = 0;
    }


    public void walk(Position unitPos) {
        if(unitPos.x > 1 || unitPos.x < -1) throw new RuntimeException("unitPos.x must be in range -1 to 1");
        if(unitPos.y > 1 || unitPos.y < -1) throw new RuntimeException("unitPos.y must be in range -1 to 1");

        Position dPos = new Position(position.x + unitPos.x, position.y + unitPos.y);

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] currentRoomLayout = currentRoom.getLayout();

        switch(currentRoomLayout[dPos.y][dPos.x]) {
            case TILE.FLOOR -> {
                position = dPos;
                handleOnEntityEnterInteractableTiles();
            }
            case TILE.DOOR -> {
                Room targetRoom = getAdjacentRoomFromUnitPos(unitPos);
                EntityRoomManager.getInstance().transferEntityFromToRoom(this, currentRoom, targetRoom);
                fixEntityPositionAfterTransferFromUnitPos(unitPos);
            }
        }
    }

    public void die() {
        health = 0;
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        EntityRoomManager.getInstance().removeEntityFromRoom(this, currentRoom);

        currentRoom.addInteractableTile(new Coin(position, 5));
    }

    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            int inflictedDamage = weapon.getCalculatedAttackDamage();
            targetEntity.hurt(inflictedDamage, this);
        }
        else throw new RuntimeException(this + " cannot attack " + targetEntity + " as target is not in same room");
    }

    public void hurt(int damage, Entity attacker) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(attacker, currentRoom)) {
            damage -= armor;
            if(damage < 0) {
                damage = 0;
            }

            if(damage == 0) {
                if(attacker instanceof Player) {
                    GUIManager.getInstance().printLog("You missed!", Color.YELLOW);
                }
            }

            health -= damage;
            if(health <= 0) {
                health = 0;
                die();
            }
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Override to implement stun.
     * @param moveCounter number of moves the entity is stunned for.
     */
    public void stun(int moveCounter) {};

    protected Room getAdjacentRoomFromUnitPos(Position unitPos) {
        unitPos.x *= 2;
        unitPos.y *= 2;

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        Position targetRoomPos = new Position(currentRoom.minimapPosition.x + unitPos.x, currentRoom.minimapPosition.y + unitPos.y);

        List<Room> rooms = EntityRoomManager.getInstance().getRooms();
        for(Room r : rooms) {
            if(r.minimapPosition.x == targetRoomPos.x &&
                    r.minimapPosition.y == targetRoomPos.y &&
                    r != currentRoom
            ) return r;
        }
        return null;
    }

    protected void fixEntityPositionAfterTransferFromUnitPos(Position unitPos) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        final int roomHeight = currentRoom.getLayout().length;
        final int roomLength = currentRoom.getLayout()[0].length;
        if(unitPos.x < 0) {
            position.x = roomLength-2;
            position.y = roomHeight/2;
        }
        else if(unitPos.x > 0) {
            position.x = 1;
            position.y = roomHeight/2;
        }
        else if(unitPos.y < 0) {
            position.x = roomLength/2;
            position.y = roomHeight-2;
        }
        else if(unitPos.y > 0) {
            position.x = roomLength/2;
            position.y = 1;
        }
    }

    private void handleOnEntityEnterInteractableTiles() {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        List<InteractableTile> interactableTiles = currentRoom.getInteractableTiles();

        for(int i = 0; i < interactableTiles.size(); i++) {
            InteractableTile tile = interactableTiles.get(i);
            if(tile.roomLayoutPosition.equals(this.position)) {
                tile.onEntityEnter(this);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Entity: %s(A:%d,H:%d,W:%s,POS:%s)", name, armor, health, weapon.name, position);
    }
}

package entity;

import core.EntityRoomManager;
import item.Weapon;
import util.Position;
import util.TILE;
import world.Room;

import java.util.List;
import java.util.Objects;

public abstract class Entity {
    public String name;
    public int armor;
    public int health;
    public Weapon weapon;
    public Position position;

    public Entity(String name, int armor, int health, Weapon weapon, Position position) {
        this.name = name;
        this.armor = armor;
        this.health = health;
        this.weapon = weapon;
        this.position = position;
    }


    public void walk(Position unitPos) {
        if(unitPos.x > 1 || unitPos.x < -1) throw new RuntimeException("unitPos.x must be in range -1 to 1");
        if(unitPos.y > 1 || unitPos.y < -1) throw new RuntimeException("unitPos.y must be in range -1 to 1");

        Position dPos = new Position(position.x + unitPos.x, position.y + unitPos.y);

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] currentRoomLayout = currentRoom.getLayout();
        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);

        switch(currentRoomLayout[dPos.y][dPos.x]) {
            case TILE.FLOOR -> {
                for(Entity e : entities) {
                    if(Objects.equals(e.position, dPos) && e != this) return;
                }
                position = dPos;
            }
            case TILE.DOOR -> {
                Room targetRoom = getAdjacentRoomFromUnitPos(unitPos);
                EntityRoomManager.getInstance().transferEntityFromToRoom(this, currentRoom, targetRoom);
                fixEntityPositionAfterTransferFromUnitPos(unitPos);
            }
        }
    }

    public abstract void die();
    public abstract void attack(Entity entity);
    public abstract void hurt(int damage, boolean isCritical, Entity attacker);
    public abstract void update();

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

    @Override
    public String toString() {
        return String.format("Entity: %s(A: %d, H:%d, W:%s)", name, armor, health, weapon.name);
    }
}

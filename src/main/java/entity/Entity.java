package entity;

import core.EntityRoomManager;
import core.IlluminationData;
import gui.GUIManager;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
import util.DIRECTION;
import item.weapon.Weapon;
import util.Position;
import util.TILE;
import core.room.type.Room;
import world.InteractableTile;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Entity {
    public String name;
    public int health;
    public int maxHealth;
    public int armor;
    public Weapon weapon;
    public Position position;
    public final int id;
    public int stunCounter;

    private Color color;
    private final IlluminationData illuminationData;

    protected Random random = new Random();

    public Entity(String name, int health, int armor, Weapon weapon, Position position) {
        this.name = name;
        this.armor = armor;
        this.health = health;
        this.maxHealth = health;
        this.weapon = weapon;
        this.position = position;
        this.id = this.hashCode();
        this.illuminationData = new IlluminationData();
        this.stunCounter = 0;
        this.color = null;
    }


    public void walk(Position unitPos) {
        if(unitPos.x > 1 || unitPos.x < -1) throw new RuntimeException("Invalid unit pos: " + unitPos);
        if(unitPos.y > 1 || unitPos.y < -1) throw new RuntimeException("Invalid unit pos: " + unitPos);

        Position dPos = new Position(position.x + unitPos.x, position.y + unitPos.y);

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] currentRoomLayout = currentRoom.getLayout();

        TILE tile = currentRoomLayout[dPos.y][dPos.x];
        if(tile.isWalkable()) {
            Position oldPos = position;
            position = dPos;
            GUIManager.getInstance().triggerMoveAnimation(this, oldPos);
            handleOnEntityEnterInteractableTiles();
        }
        else if(tile == TILE.DOOR) {
            Position oldUnitPos = new Position(unitPos.x, unitPos.y);
            Room targetRoom = getAdjacentRoomFromUnitPos(unitPos);
            EntityRoomManager.getInstance().transferEntityFromToRoom(this, currentRoom, targetRoom);
            fixEntityPositionAfterTransferFromUnitPos(oldUnitPos);
            if(this instanceof Player) {
                GUIManager.getInstance().triggerRoomTransitionFlash();
            }
        }
    }

    public void die() {
        health = 0;
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(currentRoom == null) return;
        currentRoom.addSkeletonTileAt(position);
        EntityRoomManager.getInstance().removeEntityFromRoom(this, currentRoom);
    }

    /** NOTE: THE VALUES MUST BE IN ASCENDING ORDER
     * @param map a hashmap of all droppable tiles of an entity on death
     *            where KEY: InteractableTile and VALUE: drop chance
     */
    protected void dropOnDeath(Map<InteractableTile, Double> map) {
        double random = Math.random();
        for(Map.Entry<InteractableTile, Double> set : map.entrySet()) {
            if(random <= set.getValue()) {
                Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
                if(currentRoom != null) currentRoom.addInteractableTile(set.getKey());
                return;
            }
        }
    }

    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            GUIManager.getInstance().triggerAttackAnimation(this, targetEntity);

            int inflictedDamage = weapon.getCalculatedAttackDamage();
            targetEntity.hurt(inflictedDamage, this);
        }
        else throw new RuntimeException(this + " cannot attack " + targetEntity + " as target is not in same room");
    }

    public void hurt(int damage) {
        damage -= armor;
        if(damage < 0) {
            damage = 0;
        }

        GUIManager.getInstance().triggerTextPopup(damage+"", UITheme.NORMAL_DAMAGE, position);

        health -= damage;
        if(health <= 0) {
            health = 0;
            die();
        }
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
                    GUIManager.getInstance().triggerTextPopup("miss", UITheme.MISS, position);
                }
            }

            if(attacker.weapon.isCritical(damage)) GUIManager.getInstance().triggerTextPopup(damage+"", UITheme.CRITICAL_DAMAGE, position);
            else GUIManager.getInstance().triggerTextPopup(damage+"", UITheme.NORMAL_DAMAGE, position);

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
        Position targetRoomPos = currentRoom.minimapPosition.add(unitPos);

        List<Room> rooms = EntityRoomManager.getInstance().getRooms();
        for(Room r : rooms) {
            if(r.minimapPosition.equals(targetRoomPos) && r != currentRoom) {
                return r;
            }
        }
        System.out.println("Cannot find target room at pos: " + targetRoomPos);
        return null;
    }

    protected void fixEntityPositionAfterTransferFromUnitPos(Position unitPos) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        DIRECTION fromDirection;
        if(unitPos.x == 0 && unitPos.y == 1) {
            fromDirection = DIRECTION.SOUTH;
        }
        else if(unitPos.x == 0 && unitPos.y == -1) {
            fromDirection = DIRECTION.NORTH;
        }
        else if(unitPos.x == 1 && unitPos.y == 0) {
            fromDirection = DIRECTION.EAST;
        }
        else if(unitPos.x == -1 && unitPos.y == 0) {
            fromDirection = DIRECTION.WEST;
        }
        else {
            throw new RuntimeException("Invalid unit pos: " + unitPos);
        }
        position = currentRoom.getEnteringPositionFromDirection(fromDirection);
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

    public boolean isInBounds(int roomHeight, int roomLength) {
        return position.x >= 0 && position.x < roomLength && position.y >= 0 && position.y < roomHeight;
    }

    public void setIlluminated(boolean b) {
        illuminationData.isIlluminated = b;
    }

    public void setIlluminationRange(int range) {
        illuminationData.illuminationRange = range;
    }

    public int getIlluminationRange() {
        return illuminationData.illuminationRange;
    }

    public boolean isIlluminated() {
        return illuminationData.isIlluminated;
    }

    public void overrideColor(Color color) {
        this.color = color;
    }

    public void resetColor() {
        this.color = null;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return String.format("Entity: %s(A:%d,H:%d,W:%s,POS:%s)", name, armor, health, weapon.name, position);
    }
}

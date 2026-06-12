package entity.monster;

import core.EntityRoomManager;
import entity.Entity;
import entity.MoveAfterPlayer;
import entity.Player;
import gui.GUIManager;
import javafx.scene.paint.Color;
import weapon.Weapon;
import util.Position;
import util.TILE;
import core.room.type.Room;
import world.InteractableTile;

import java.util.List;
import java.util.Random;

public abstract class Monster extends Entity implements MoveAfterPlayer {
    public Monster(String name, int health, int armor, Weapon weapon, Position position) {
        super(name, health, armor, weapon, position);
    }

    /** Returns the unit vector position towards player position.
     * diagonal direction is disabled by default
     * @return Position(unitX, unitY)
     */
    protected Position pathfindToPlayerPosition() {
        return pathfindToPlayerPosition(false);
    }
    /** Returns the unit vector position towards player position.
     * @return Position(unitX, unitY)
     */
    protected Position pathfindToPlayerPosition(boolean allowDiagonal) {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;
        double distance = Math.sqrt(dx*dx + dy*dy);
        double unitX = dx / distance;
        double unitY = dy / distance;

        double angleToPlayer = Math.abs(Math.atan2(unitY, unitX)) * (180.0 / Math.PI);
        double diagonalAngle = angleToPlayer % 90;

//        GUIManager.getInstance().printLog(String.format("%.3f", diagonalAngle), Color.PINK);

        if(diagonalAngle >= 25 && diagonalAngle <= 65 && allowDiagonal) {
            return new Position((int) Math.signum(dx), (int) Math.signum(dy));
        }
        unitX = 0;
        unitY = 0;
        int absX = Math.abs(dx);
        int absY = Math.abs(dy);
        if(absX > absY) unitX = (int) Math.signum(dx);
        else if(absY > absX) unitY = (int) Math.signum(dy);
        else {
            if(Math.random() * 100 % 2 == 0) unitX = (int) Math.signum(dx);
            else unitY = (int) Math.signum(dy);
        }
        return new Position((int) unitX, (int) unitY);
    }

    protected boolean isValidTargetPosition(Position targetPosition) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] roomLayout = currentRoom.getLayout();

        if(targetPosition.x < 0 || targetPosition.x >= roomLayout[0].length) return false;
        if(targetPosition.y < 0 || targetPosition.y >= roomLayout.length) return false;
        TILE tile = roomLayout[targetPosition.y][targetPosition.x];
        switch(tile) {
            case TILE.FLOOR, GRASS, PASSABLE_OBSTACLE: break;
            default: return false;
        }

        List<InteractableTile> interactableTiles = currentRoom.getInteractableTiles();
        for(InteractableTile interactableTile : interactableTiles) {
            if(interactableTile.roomLayoutPosition.equals(targetPosition) && interactableTile.isSolid) return false;
        }

        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
        for(Entity e : entities) {
            if(e == this) continue;
            if(e instanceof Player) continue;
            if(e.position.equals(targetPosition)) return false;
        }
        return true;
    }

    protected int getDistanceFromPlayer() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        int dx = player.position.x - position.x;
        int dy = player.position.y - position.y;
        return (int) Math.sqrt(dx*dx + dy*dy);
    }
}

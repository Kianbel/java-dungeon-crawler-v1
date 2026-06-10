package entity;

import core.EntityRoomManager;
import weapon.Weapon;
import util.Position;
import util.TILE;
import core.room.type.Room;
import world.InteractableTile;

import java.util.List;

public abstract class Monster extends Entity implements MoveAfterPlayer {
    public Monster(String name, int health, int armor, Weapon weapon, Position position) {
        super(name, health, armor, weapon, position);
    }
    /**
     * Returns the unit vector position towards player position.
     *
     * @return Position(unitX, unitY)
     */
    protected Position pathfindToPlayerPosition() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;

        int unitX = (dx == 0) ? 0 : ((dx < 0) ? -1 : 1);
        int unitY = (dy == 0) ? 0 : ((dy < 0) ? -1 : 1);

        if(Math.abs(unitX) == 1 && Math.abs(unitY) == 1) {
            if(isValidTargetPosition(new Position(position.x, position.y+unitY))) return new Position(0, unitY);
            else if(isValidTargetPosition(new Position(position.x+unitX, position.y))) return new Position(unitX, 0);
            else return new Position(0,0);
        }
        return new Position(unitX, unitY);
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
}

package entity;

import core.EntityRoomManager;
import util.TILE;
import weapon.GiantSpiderFang;
import util.Position;
import core.Room;
import world.InteractableTile;
import world.Web;

import java.util.List;

public class GiantSpider extends Monster implements RangeAttack {
    private boolean hasBackedOffOnce = false;

    public GiantSpider(Position position) {
        super("Spider", 20, 0, new GiantSpiderFang(), position);
    }

    @Override
    public void makeMove() {
        final int WEBBING_DISTANCE_TO_PLAYER_THRESHOLD = 6;
        final int AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD = 3;
        final double WEBBING_CHANCE = 0.1;

        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;
        int distanceToPlayer = (int) Math.sqrt(dx*dx + dy*dy);

        if(health <= 10) {
            // TODO: back off to 3 tiles behind in terms of player's position
//            if(backOffTo())
        }

        if(distanceToPlayer <= WEBBING_DISTANCE_TO_PLAYER_THRESHOLD) {
            if(distanceToPlayer <= AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD) {
                handleWalk();
                return;
            }

            if(Math.random() <= WEBBING_CHANCE) {
                if(shoot(playerPosition)) return;
            }
        }
        handleWalk();
    }

    private void handleWalk() {
        final double WALK_CHANCE = 0.6;

        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        Position unitPos = pathfindToPlayerPosition();
        if(unitPos.x == 0 && unitPos.y == 0) return;

        Position targetPosition = new Position(position.x+unitPos.x, position.y+unitPos.y);

        if(playerPosition.x == targetPosition.x && playerPosition.y == targetPosition.y) {
            attack(player);
        }
        else if(isValidTargetPosition(targetPosition)){
            if(Math.random() <= WALK_CHANCE) walk(unitPos);
        }
    }

    private boolean backOffTo(Position targetPosition) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] roomLayout = currentRoom.getLayout();

        if(targetPosition.x < 0 || targetPosition.x >= currentRoom.length) return false;
        if(targetPosition.y < 0 || targetPosition.y >= currentRoom.height) return false;
        if(roomLayout[targetPosition.y][targetPosition.x] != TILE.FLOOR) return false;

        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
        for(Entity e : entities) {
            if(e == this) continue;
            if(e.position.x == targetPosition.x && e.position.y == targetPosition.y) {
                return false;
            }
        }
        position = targetPosition;
        return true;
    }

    @Override
    public boolean shoot(Position targetPosition) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);

        Position temp = new Position(targetPosition.x-1, targetPosition.y);

        InteractableTile web = new Web(temp);
        return currentRoom.addInteractableTile(web);
    }
}

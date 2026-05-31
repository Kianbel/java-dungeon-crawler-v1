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
    public boolean isAggressive = true;

    public GiantSpider(Position position) {
        super("Giant Spider",
                20,
                0,
                new GiantSpiderFang(),
                position
        );
    }

    @Override
    public void makeMove() {
        final int WEBBING_DISTANCE_TO_PLAYER_THRESHOLD = 5;
        final int AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD = 3;
        final double WEBBING_CHANCE = 0.2;
        final double BACK_OFF_CHANCE = 0.9;
        final double BACK_OFF_HEALTH_THRESHOLD = 10;

        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;
        int distanceToPlayer = (int) Math.sqrt(dx*dx + dy*dy);

        if(distanceToPlayer <= 2) {
            if(health <= BACK_OFF_HEALTH_THRESHOLD) {
                isAggressive = false;
                if(Math.random() <= BACK_OFF_CHANCE) if(backOff()) return;
            }
        }
        if(distanceToPlayer <= WEBBING_DISTANCE_TO_PLAYER_THRESHOLD) {
            if(distanceToPlayer <= AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD && isAggressive) {
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

    public boolean backOff() {
        Position playerPosition = EntityRoomManager.getInstance().getPlayer().position;

        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;
        int unitX = (dx == 0) ? 0 : ((dx < 0) ? -1 : 1);
        int unitY = (dy == 0) ? 0 : ((dy < 0) ? -1 : 1);

        Position targetPosition = new Position(position.x-unitX, position.y-unitY);
        if(isValidTargetPosition(targetPosition)) {
            walk(new Position(-unitX, -unitY));
            return true;
        }

        System.out.println(this + " cannot back off to " + targetPosition);
        return false;
    }

    private Position getBackOffTargetPosition() {
        final int BACK_OFF_DISTANCE = 3;

        Position playerPosition = EntityRoomManager.getInstance().getPlayer().position;
        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;

        if(dx == 0 && dy == 0) return new Position(0,0);
        if(dx != 0) {
            return new Position(position.x-dx*BACK_OFF_DISTANCE, position.y);
        }
        return new Position(position.x, position.y-dy*BACK_OFF_DISTANCE);
    }

    @Override
    public boolean shoot(Position targetPosition) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);

        // TODO: shoot near/at player
        Position temp = new Position(targetPosition.x-1, targetPosition.y);

        InteractableTile web = new Web(temp);
        return currentRoom.addInteractableTile(web);
    }
}

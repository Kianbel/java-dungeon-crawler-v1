package entity;

import core.EntityRoomManager;
import weapon.Fist;
import util.Position;

public class Zombie extends Monster {
    public Zombie(Position position) {
        super("Zombie", 10, 0, new Fist(), position);
    }

    public void makeMove() {
        Position unitPos = pathfindToPlayerPosition();
        if(unitPos.x == 0 && unitPos.y == 0) return;

        Position targetPosition = new Position(position.x+unitPos.x, position.y+unitPos.y);
        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;

        if(playerPosition.x == targetPosition.x && playerPosition.y == targetPosition.y) {
            attack(player);
        }
        else if(isValidTargetPosition(targetPosition)){
            final double WALK_CHANCE = 0.4;
            if(Math.random() <= WALK_CHANCE) walk(unitPos);
        }
    }
}

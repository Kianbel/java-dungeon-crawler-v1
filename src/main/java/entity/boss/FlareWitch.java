package entity.boss;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.monster.Monster;
import entity.projectile.Fireball;
import util.Position;
import weapon.Fist;

import java.util.Random;

public class FlareWitch extends Monster {
    private int castCooldown = 0;
    private final Random random = new Random();

    public FlareWitch(Position position) {
        super("Flare Witch", 100, 2, new Fist(), position);
    }

    @Override
    public void makeMove() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position unitPos = pathfindToPlayerPosition();

        final int MIN_CAST_COOLDOWN = 1;
        final int MAX_CAST_COOLDOWN = 1;

        if(castCooldown <= 0) {
            castSingleFireball(pathfindToPlayerPosition(true));
            castCooldown = random.nextInt(MIN_CAST_COOLDOWN,MAX_CAST_COOLDOWN+1);
        }
        castCooldown--;

        handleWalk();
    }

    private void handleWalk() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        Position unitPos = pathfindToPlayerPosition();
        if(unitPos.x == 0 && unitPos.y == 0) return;

        Position targetPosition = new Position(position.x+unitPos.x, position.y+unitPos.y);

        if(playerPosition.x == targetPosition.x && playerPosition.y == targetPosition.y) {
            attack(player);
        }
        else if(isValidTargetPosition(targetPosition)){
            final double WALK_CHANCE = 0.5;
            if(Math.random() <= WALK_CHANCE) walk(unitPos);
        }
    }

    private void castSingleFireball(Position unitPos) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        Position fireballSpawnPosition = position.add(unitPos);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(unitPos, fireballSpawnPosition), currentRoom);
    }
}

package entity.boss;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.monster.Monster;
import entity.projectile.Fireball;
import util.Position;
import weapon.Fist;

public class FlareWitch extends Monster {
    private final int MAX_CAST_COOLDOWN = 5;
    private int castCooldown = 0;

    public FlareWitch(Position position) {
        super("Flare Witch", 100, 2, new Fist(), position);
    }

    @Override
    public void makeMove() {
        Position unitPosToPlayer = pathfindToPlayerPosition();
        int playerDistance = getDistanceFromPlayer();
        Entity player = EntityRoomManager.getInstance().getPlayer();

        final double WALK_CHANCE = 0.5;

        if(playerDistance == 1) attack(player);
        else {
            if(Math.random() <= WALK_CHANCE) {
                walk(unitPosToPlayer);
            }
            else {
                if(castCooldown == 0) {
                    cast(unitPosToPlayer);
                    castCooldown = MAX_CAST_COOLDOWN;
                }
            }
        }
        if(castCooldown > 0) castCooldown--;
    }

    private void cast(Position unitPos) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        Position fireballSpawnPosition = position.add(unitPos);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(unitPos, fireballSpawnPosition), currentRoom);
    }
}

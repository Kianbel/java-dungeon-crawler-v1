package core;

import core.room.type.Room;
import entity.monster.Monster;
import util.Position;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class EntitySpawner {
    private final Room room;

    public EntitySpawner(Room room) {
        this.room = room;
    }

    public void spawnMonstersAmount(Function<Position, ? extends Monster> monsterInstance, int monsterAmount) {
        List<Position> spawnablePositions = room.getSpawnablePositions();
        for(int i = 0; i < monsterAmount; i++) {
            Position randomSpawnPosition = spawnablePositions.remove(new Random().nextInt(spawnablePositions.size()));

            Monster monster = monsterInstance.apply(randomSpawnPosition);
            EntityRoomManager.getInstance().addEntityToRoom(monster, room);
        }
    }
}

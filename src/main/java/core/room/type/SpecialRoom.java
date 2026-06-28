package core.room.type;

import core.EntitySpawner;
import core.GameManager;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.Bat;
import entity.monster.Rat;
import entity.monster.Zombie;
import util.Position;
import util.TILE;

import java.util.Random;

public class SpecialRoom extends Room {
    public SpecialRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(SpecialRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        Random random = new Random();
        EntitySpawner entitySpawner = new EntitySpawner(this);
        int floor = GameManager.getInstance().getCurrentFloor();

        switch (floor) {
            case 1 -> {
                entitySpawner.spawnMonstersAmount(Bat::new, random.nextInt(5,10));
                entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(5,10));
                entitySpawner.spawnMonstersAmount(Zombie::new, random.nextInt(5,10));
            }
            case 2 -> {}
            case 3 -> {}
            case 4 -> {}
            case 5 -> {}
        }
    }
}

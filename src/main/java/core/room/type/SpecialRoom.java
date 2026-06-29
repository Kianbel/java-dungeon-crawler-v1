package core.room.type;

import core.EntitySpawner;
import core.GameManager;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.*;
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
                entitySpawner.spawnMonstersAmount(Kobold::new, random.nextInt(2,5));
            }
            default -> {
                entitySpawner.spawnMonstersAmount(Bat::new, random.nextInt(5,10));
                entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(5,10));
                entitySpawner.spawnMonstersAmount(Kobold::new, random.nextInt(2+floor,5+floor));
                entitySpawner.spawnMonstersAmount(Goblin::new, random.nextInt(floor,5+floor));
            }
        }
    }
}

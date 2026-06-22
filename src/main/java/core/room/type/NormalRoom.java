package core.room.type;

import core.EntitySpawner;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.GiantSpider;
import entity.monster.Kobold;
import entity.monster.Zombie;
import util.Position;
import util.Randomizer;
import util.TILE;

import java.util.Random;

public class NormalRoom extends Room {

    private Random random = new Random();

    public NormalRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(NormalRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        entitySpawner.spawnMonstersAmount(GiantSpider::new, random.nextInt(0,3));
        entitySpawner.spawnMonstersAmount(Kobold::new, random.nextInt(0,3));
    }
}

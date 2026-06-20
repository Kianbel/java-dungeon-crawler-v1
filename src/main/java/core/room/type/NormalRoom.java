package core.room.type;

import core.EntitySpawner;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.GiantSpider;
import entity.monster.Zombie;
import util.Position;
import util.TILE;

import java.util.Random;

public class NormalRoom extends Room {
    public NormalRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(NormalRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        if(Math.random() <= 0.3) entitySpawner.spawnMonstersAmount(GiantSpider::new, random.nextInt(1,3));
        else entitySpawner.spawnMonstersAmount(Zombie::new, random.nextInt(1,6));
    }
}

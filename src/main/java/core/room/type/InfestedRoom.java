package core.room.type;

import core.EntitySpawner;
import core.room.loader.RoomLayoutRegistry;
import entity.GiantSpider;
import entity.Zombie;
import util.Position;
import util.TILE;

import java.util.Random;


public class InfestedRoom extends Room {

    public InfestedRoom(Position minimapPosition) {
//        Random rand = new Random();
//        int height = Math.clamp(rand.nextInt(MAX_ROOM_HEIGHT), MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT);
//        int length = Math.clamp(rand.nextInt(MAX_ROOM_LENGTH), MIN_ROOM_LENGTH, MAX_ROOM_LENGTH);
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(InfestedRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        if(Math.random() <= 0.3) entitySpawner.spawnMonstersAmount(GiantSpider::new, new Random().nextInt(1,3));
        entitySpawner.spawnMonstersAmount(Zombie::new, new Random().nextInt(1,6));
    }
}

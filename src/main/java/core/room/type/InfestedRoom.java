package core.room.type;

import core.EntitySpawner;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.GiantSpider;
import entity.monster.Zombie;
import util.Position;
import util.TILE;

import java.util.Random;


public class InfestedRoom extends Room {

    public InfestedRoom(Position minimapPosition) {
        TILE[][] layout;
        if(Math.random() <= 0.2) {
            layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(ClearRoom.class);
        }
        else layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(InfestedRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        if(Math.random() <= 0.3) entitySpawner.spawnMonstersAmount(GiantSpider::new, new Random().nextInt(1,3));
//        entitySpawner.spawnMonstersAmount(GiantSpider::new, new Random().nextInt(1,3));
        entitySpawner.spawnMonstersAmount(Zombie::new, new Random().nextInt(1,6));
    }
}

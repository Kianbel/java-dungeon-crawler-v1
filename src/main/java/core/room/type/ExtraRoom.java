package core.room.type;

import core.EntitySpawner;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.Rat;
import entity.monster.Zombie;
import util.Position;
import util.TILE;

public class ExtraRoom extends Room {
    public ExtraRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(ExtraRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        EntitySpawner entitySpawner = new EntitySpawner(this);
        entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(0,5));
        if(Math.random() <= 0.5) entitySpawner.spawnMonstersAmount(Zombie::new, random.nextInt(0,5));
    }
}

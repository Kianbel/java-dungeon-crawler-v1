package core.room.type;

import core.EntitySpawner;
import core.GameManager;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.*;
import util.Position;
import util.Randomizer;
import util.TILE;

public class ExtraRoom extends Room {
    public ExtraRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(ExtraRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(0,5));

        final int floor = GameManager.getInstance().getCurrentFloor();
        switch(floor) {
            case 1 -> {
                if(Math.random() <= 0.5) {
                    switch(Randomizer.pick(1,2,3)) {
                        case 1 -> entitySpawner.spawnMonstersAmount(Bat::new, random.nextInt(1,5));
                        case 2 -> entitySpawner.spawnMonstersAmount(Kobold::new, random.nextInt(1,5));
                    }
                }
                else if(Math.random() <= 0.5) {
                    entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(1,3));
                }
            }
            case 2 -> {
                if(Math.random() <= 0.5) {
                    if(Math.random() <= 0.3) entitySpawner.spawnMonstersAmount(Kobold::new, random.nextInt(3,5));
                    else entitySpawner.spawnMonstersAmount(GiantSpider::new, random.nextInt(1,4));
                }
                else if(Math.random() <= 0.3) {
                    entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(1,5));
                }
            }
            case 3 -> {

            }
            case 4 -> {

            }
            case 5 -> {

            }
        }
    }
}

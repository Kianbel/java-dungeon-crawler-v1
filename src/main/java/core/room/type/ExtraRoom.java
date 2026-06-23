package core.room.type;

import core.EntitySpawner;
import core.GameManager;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.*;
import util.Position;
import util.Randomizer;
import util.TILE;
import world.PressurePlateTrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExtraRoom extends Room {

    private Random random = new Random();

    public ExtraRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(ExtraRoom.class);

        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        final int floor = GameManager.getInstance().getCurrentFloor();
        switch(floor) {
            case 1 -> {
                switch(Randomizer.pick(1,2,3)) {
                    case 1 -> entitySpawner.spawnMonstersAmount(Bat::new, random.nextInt(1,5));
                    case 2 -> entitySpawner.spawnMonstersAmount(Kobold::new, random.nextInt(1,3));
                    case 3 -> entitySpawner.spawnMonstersAmount(Goblin::new, random.nextInt(1,3));
                }
                if(Math.random() <= 0.2) {
                    entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(1,3));
                }
            }
            case 2 -> {
                switch(Randomizer.pick(1,2)) {
                    case 1 -> entitySpawner.spawnMonstersAmount(Kobold::new, random.nextInt(3,5));
                    case 2 -> entitySpawner.spawnMonstersAmount(Goblin::new, random.nextInt(2,4));
                }
                if(Math.random() <= 0.3) {
                    switch(Randomizer.pick(1,2)) {
                        case 1 -> entitySpawner.spawnMonstersAmount(Rat::new, random.nextInt(1,3));
                        case 2 -> entitySpawner.spawnMonstersAmount(Bat::new, random.nextInt(1,3));
                    }
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

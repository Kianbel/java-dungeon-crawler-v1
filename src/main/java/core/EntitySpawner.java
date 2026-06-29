package core;

import core.room.type.Room;
import entity.Monster;
import entity.monster.*;
import util.Position;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class EntitySpawner {
    private final Room room;
    private final Random random = new Random();

    public EntitySpawner(Room room) {
        this.room = room;
    }

    public void spawnMonstersAmount(Function<Position, ? extends Monster> monsterInstance, int monsterAmount) {
        if(monsterAmount <= 0) return;

        List<Position> spawnablePositions = room.getSpawnablePositions();
        for(int i = 0; i < monsterAmount; i++) {
            Position randomSpawnPosition = spawnablePositions.remove(new Random().nextInt(spawnablePositions.size()));

            Monster monster = monsterInstance.apply(randomSpawnPosition);
            EntityRoomManager.getInstance().addEntityToRoom(monster, room);
        }
    }

    public void spawnMonstersBasedOnFloor(int floorLevel) {
        if(floorLevel <= 0 || floorLevel > 5) throw new RuntimeException("values of only 1-5 are allowed");

        int bats = 0;
        int rats = 0;
        int moths = 0;
        int roaches = 0;
        int juvGiantCentipedes = 0;
        int giantCentipedes = 0;
        int giantSpiders = 0;

        int zombies = 0;
        int kobolds = 0;
        int golems = 0;
        int gobSwordsmans = 0;
        int gobArchers = 0;
        int gobTanks = 0;
        int gobSummoner = 0;
        int flareApprentices = 0;

        switch (floorLevel) {
            case 1 -> {
                bats = 3;
                rats = 3;
                moths = 3;
                roaches = 3;
                zombies = 3;
                kobolds = 3;
            }
            case 2 -> {
                bats = 4;
                rats = 2;
                moths = 2;
                roaches = 3;
                juvGiantCentipedes = 2;
                zombies = 3;
                kobolds = 3;
                gobSwordsmans = 2;
                gobArchers = 2;
            }
            case 3 -> {
                bats = 5;
                rats = 1;
                moths = 1;
                roaches = 2;
                juvGiantCentipedes = 2;
                giantCentipedes = 1;
                giantSpiders = 1;
                zombies = 3;
                kobolds = 3;
                golems = 1;
                gobSwordsmans = 3;
                gobArchers = 3;
                gobTanks = 1;
                gobSummoner = 1;
            }
            case 4 -> {
                bats = 6;
                roaches = 1;
                juvGiantCentipedes = 3;
                giantCentipedes = 2;
                giantSpiders = 2;
                zombies = 1;
                kobolds = 1;
                golems = 2;
                gobSwordsmans = 3;
                gobArchers = 3;
                gobTanks = 1;
                gobSummoner = 1;
                flareApprentices = 1;
            }
            case 5 -> {
                bats = 7;
                juvGiantCentipedes = 5;
                giantCentipedes = 3;
                giantSpiders = 3;
                golems = 2;
                gobSwordsmans = 4;
                gobArchers = 4;
                gobTanks = 2;
                gobSummoner = 2;
                flareApprentices = 2;
            }
        }

        spawnMonstersAmount(Bat::new, get1ToMax(bats));
        spawnMonstersAmount(Rat::new, get1ToMax(rats));
        spawnMonstersAmount(Moth::new, get1ToMax(moths));
        spawnMonstersAmount(Roach::new, get1ToMax(roaches));
        spawnMonstersAmount(JuvenileGiantCentipede::new, get1ToMax(juvGiantCentipedes));
        spawnMonstersAmount(GiantCentipede::new, get1ToMax(giantCentipedes));
        spawnMonstersAmount(GiantSpider::new, get1ToMax(giantSpiders));
        spawnMonstersAmount(Zombie::new, get1ToMax(zombies));
        spawnMonstersAmount(Kobold::new, get1ToMax(kobolds));
        spawnMonstersAmount(Golem::new, get1ToMax(golems));
        spawnMonstersAmount(GoblinSwordsman::new, get1ToMax(gobSwordsmans));
        spawnMonstersAmount(GoblinArcher::new, get1ToMax(gobArchers));
        spawnMonstersAmount(GoblinTank::new, get1ToMax(gobTanks));
        spawnMonstersAmount(GoblinSummoner::new, get1ToMax(gobSummoner));
        spawnMonstersAmount(FlareApprentice::new, get1ToMax(flareApprentices));
    }

    private int get1ToMax(int max) {
        if(max <= 0) return 0;
        return random.nextInt(1,max+1);
    }
}

package core.room.type;

import core.EntityRoomManager;
import core.EntitySpawner;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.GiantSpider;
import entity.monster.Zombie;
import entity.projectile.Fireball;
import util.Position;
import util.Randomizer;
import util.TILE;
import weapon.AncientSword;
import weapon.IronBlade;
import weapon.Weapon;
import world.*;

import java.util.Random;

public class NormalRoom extends Room {
    private boolean isClear = false;

    public NormalRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(NormalRoom.class);
        super(layout, minimapPosition);

        if(Math.random() <= 0.2) isClear = true;
    }

    @Override
    public void populateWithEntities() {
        if(!isClear) return;

        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        if(Math.random() <= 0.3) entitySpawner.spawnMonstersAmount(GiantSpider::new, new Random().nextInt(1,3));
        entitySpawner.spawnMonstersAmount(Zombie::new, new Random().nextInt(1,6));
    }
}

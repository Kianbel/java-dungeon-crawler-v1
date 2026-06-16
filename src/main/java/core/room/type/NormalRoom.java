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

        // --- PLACE INTERACTABLE TILES ---
        int ROOM_HEIGHT = layout.length;
        int ROOM_LENGTH = layout[0].length;
        for(int y = 0; y < ROOM_HEIGHT; y++) {
            for(int x = 0; x < ROOM_LENGTH; x++) {
                TILE tile = layout[y][x];
                switch (tile) {
                    case TORCH -> {
                        this.layout[y][x] = TILE.FLOOR;
                        addInteractableTile(new Fire(new Position(x,y)));
                    }
                    case WEB -> {
                        this.layout[y][x] = TILE.FLOOR;
                        addInteractableTile(new Web(new Position(x,y)));
                    }
                    case CHEST -> {
                        this.layout[y][x] = TILE.FLOOR;
                        InteractableTile chestDrop = null;

                        Position currentPos = new Position(x,y);
                        switch(Randomizer.pick(1,2,3)) {
                            case 1 -> chestDrop = new Coin(currentPos, Randomizer.pick(5,10,15));
                            case 2 -> chestDrop = new Heart(currentPos, Randomizer.pick(10,15,20));
                            case 3 -> {
                                Weapon weapon = null;
                                switch(Randomizer.pick(1,2)) {
                                    case 1 -> weapon = new AncientSword();
                                    case 2 -> weapon = new IronBlade();
                                }
                                chestDrop = new DroppedWeapon(currentPos, weapon);
                            }
                        }
                        addInteractableTile(new Chest(currentPos, chestDrop));
                    }
                }
            }
        }

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

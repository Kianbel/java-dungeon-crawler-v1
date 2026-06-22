package core.room.type;

import core.EntitySpawner;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.GiantSpider;
import entity.monster.Zombie;
import util.Position;
import util.TILE;
import item.weapon.IronBlade;
import world.*;

import java.util.Random;

public class TreasureRoom extends Room {

    private Random random = new Random();

    public TreasureRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(TreasureRoom.class);
        super(layout, minimapPosition);

        Position chestPosition;
        if(layout == null) {
            chestPosition = new Position(length/2, height/2);
        }
        else {
            height = layout.length;
            length = layout[0].length;
            chestPosition = new Position(length/2, height/2);
        }
        this.layout[height/2][length/2] = TILE.FLOOR;

        InteractableTile chestDrop;
        double random = Math.random();
        if(random <= 0.4) chestDrop = new DroppedItem(chestPosition, new IronBlade());
        else if(random <= 0.8) chestDrop = new Heart(chestPosition, 20);
        else chestDrop = new Coin(chestPosition, 20);

        InteractableTile chest = new Chest(chestPosition, chestDrop);
        addInteractableTile(chest);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        EntitySpawner entitySpawner = new EntitySpawner(this);
        entitySpawner.spawnMonstersAmount(Zombie::new, random.nextInt(5,10));
        if(Math.random() <= 0.4) entitySpawner.spawnMonstersAmount(GiantSpider::new, random.nextInt(1,4));
    }
}

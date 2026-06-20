package world;

import core.EntityRoomManager;
import core.Game;
import core.room.type.Room;
import entity.Entity;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.weapon.*;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chest extends InteractableTile {
    private final InteractableTile chestDrop;

    public Chest(Position roomLayoutPosition) {

        List<WeightedObject> lootTable = new ArrayList<>();

        final int floor = Game.getInstance().getCurrentFloor();
        System.out.println(floor);
        switch (floor) {
            case 1 -> {
                lootTable.add(new WeightedObject(new Heart(roomLayoutPosition, 20), 5));
                lootTable.add(new WeightedObject(new Coin(roomLayoutPosition, 15), 5));
                lootTable.add(new WeightedObject(new IronBlade(), roomLayoutPosition, 2));
                lootTable.add(new WeightedObject(new Dagger(), roomLayoutPosition, 2));
                lootTable.add(new WeightedObject(new Club(), roomLayoutPosition, 2));
                lootTable.add(new WeightedObject(new GreatClub(), roomLayoutPosition, 0.5));
            }
            case 2 -> {
                lootTable.add(new WeightedObject(new Heart(roomLayoutPosition, 30), 5));
                lootTable.add(new WeightedObject(new Coin(roomLayoutPosition, 20), 5));
                lootTable.add(new WeightedObject(new GreatClub(), roomLayoutPosition, 2));
                lootTable.add(new WeightedObject(new Mace(), roomLayoutPosition, 2));
                lootTable.add(new WeightedObject(new GreatSword(), roomLayoutPosition, 0.5));
            }
            case 3 -> {}
            case 4 -> {}
            case 5 -> {}
        }

        InteractableTile dropBasedOnFloor;
        dropBasedOnFloor = (InteractableTile) Randomizer.rollWeightedObjects(lootTable);

        this(roomLayoutPosition, dropBasedOnFloor);
    }

    public Chest(Position roomLayoutPosition, InteractableTile chestDrop) {
        super(roomLayoutPosition, true);
        this.chestDrop = chestDrop;
        isLightOccluding = true;
    }

    @Override
    public void onEntityBump(Entity entity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);

        if(chestDrop instanceof Coin) {
            GUIManager.getInstance().printLog("You break open the chest and it dropped some coins", UITheme.LOG_WORLD);
        }
        else if(chestDrop instanceof Heart) {
            GUIManager.getInstance().printLog("You break open the chest and it dropped a heart", UITheme.LOG_WORLD);
        }
        else if(chestDrop instanceof DroppedItem droppedItem) {
            GUIManager.getInstance().printLog("You break open the chest and it dropped " + droppedItem.item.name, UITheme.LOG_WORLD);
        }

        EntityRoomManager.getInstance().removeInteractableTile(this);
        AudioManager.getInstance().playSFX("chest_open");
        currentRoom.addInteractableTile(chestDrop);
    }
}

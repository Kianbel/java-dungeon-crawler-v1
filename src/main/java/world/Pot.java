package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.food.MonsterMeat;
import item.heal.HealingPotion;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pot extends InteractableTile {
    public Pot(Position roomLayoutPosition) {
        super(roomLayoutPosition, true);
        isLightOccluding = true;
    }

    @Override
    public void onEntityEnter(Entity entity) {
        onEntityBump(entity);
    }

    @Override
    public void onEntityBump(Entity entity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
        EntityRoomManager.getInstance().removeInteractableTile(this);
        AudioManager.getInstance().playSFX("pot_break");

        final double DROP_CHANCE = 0.4;

        InteractableTile dropTile;
        Random random = new Random();
        if(Math.random() <= DROP_CHANCE) {
            List<WeightedObject> drops = new ArrayList<>();
            drops.add(new WeightedObject(new Heart(roomLayoutPosition, random.nextInt(5,16)), 4));
            drops.add(new WeightedObject(new Coin(roomLayoutPosition, random.nextInt(5,11)), 4));
            drops.add(new WeightedObject(new MonsterMeat(random.nextInt(1,3)), roomLayoutPosition, 4));
            drops.add(new WeightedObject(new HealingPotion(1), roomLayoutPosition, 1));

            dropTile = (InteractableTile) Randomizer.rollWeightedObjects(drops);

            if(entity instanceof Player) {
                if(dropTile instanceof Heart) GUIManager.getInstance().printLog("You break open a pot and it dropped a heart!", UITheme.LOG_WORLD);
                else if(dropTile instanceof Coin) GUIManager.getInstance().printLog("You break open a pot and it dropped some coins!", UITheme.LOG_WORLD);
                else if(dropTile instanceof DroppedItem droppedItem) {
                    if(droppedItem.item instanceof MonsterMeat) GUIManager.getInstance().printLog("You break open a pot and it dropped some bread!", UITheme.LOG_WORLD);
                    if(droppedItem.item instanceof HealingPotion) GUIManager.getInstance().printLog("You break open a pot and it dropped a healing potion!", UITheme.LOG_WORLD);
                }
            }
        }
        else dropTile = new ShatteredPot(roomLayoutPosition);

        currentRoom.addInteractableTile(dropTile);
    }
}

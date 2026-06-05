package world;

import core.EntityRoomManager;
import core.room.Room;
import entity.Entity;
import gui.GUIManager;
import gui.UITheme;
import util.Position;
import weapon.IronBlade;

public class Chest extends InteractableTile {
    private final double WEAPON_CHANCE = 0.9;
    private final double COIN_CHANCE = 1 - WEAPON_CHANCE;

    public Chest(Position roomLayoutPosition) {
        super(roomLayoutPosition, true);
    }

    @Override
    public void onEntityBump(Entity entity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);

        InteractableTile chestDrop;
        if(Math.random() <= COIN_CHANCE) {
            chestDrop = new Coin(roomLayoutPosition, 5);
            GUIManager.getInstance().printLog("You break open the chest and it dropped some coins", UITheme.LOG_WORLD);
        }
        else {
            chestDrop = new DroppedWeapon(roomLayoutPosition, new IronBlade());
            GUIManager.getInstance().printLog(String.format("You break open the chest and it dropped a %s (ATK: %d)", ((DroppedWeapon)chestDrop).weapon.name, ((DroppedWeapon)chestDrop).weapon.baseAttackDamage), UITheme.LOG_WORLD);
        }


        EntityRoomManager.getInstance().removeInteractableTile(this);
        currentRoom.addInteractableTile(chestDrop);
    }
}

package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.Item;
import item.armor.Armor;
import item.key.Key;
import item.weapon.Ammo;
import javafx.scene.paint.Color;
import util.Position;
import item.weapon.Weapon;

public class DroppedItem extends InteractableTile {
    public Item item;


    public DroppedItem(Position roomLayoutPosition, Item item) {
        super(roomLayoutPosition, false);
        this.item = item;

        overrideColor(item.color);
        overrideCharacter(item.character);
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(entity instanceof Player player) {
            Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
            AudioManager.getInstance().playSFX("pickup");

            if(item instanceof Weapon w) {
                GUIManager.getInstance().printLog("Picked up: " + item.name, UITheme.LOG_WORLD);
                GUIManager.getInstance().triggerTextPopup("+" + item.name, Color.LIGHTBLUE, player.position, 1000);
                Weapon oldWeapon = player.weapon;
                player.setWeapon(w);
                EntityRoomManager.getInstance().removeInteractableTile(this);

                if(oldWeapon != null) {
                    currentRoom.addInteractableTile(new DroppedItem(roomLayoutPosition, oldWeapon));
                }
            }
            else if(item instanceof Armor a) {
                GUIManager.getInstance().printLog("Picked up: " + item.name, UITheme.LOG_WORLD);
                GUIManager.getInstance().triggerTextPopup("+" + item.name, Color.LIGHTBLUE, player.position, 1000);
                Armor oldArmor = player.armor;
                player.setArmor(a);
                EntityRoomManager.getInstance().removeInteractableTile(this);

                if(oldArmor != null) {
                    currentRoom.addInteractableTile(new DroppedItem(roomLayoutPosition, oldArmor));
                }
            }
            else {
                if(item.amount > 1) {
                    GUIManager.getInstance().printLog(String.format("Picked up: %s ( x%d )", item.name, item.amount), UITheme.LOG_WORLD);
                    GUIManager.getInstance().triggerTextPopup("+" + item.name + " x" + item.amount, Color.LIGHTBLUE, player.position, 1000);
                }
                else {
                    GUIManager.getInstance().printLog("Picked up: " + item.name, UITheme.LOG_WORLD);
                    GUIManager.getInstance().triggerTextPopup("+" + item.name, Color.LIGHTBLUE, player.position, 1000);
                }
                player.addItemToInventory(item);
                EntityRoomManager.getInstance().removeInteractableTile(this);
            }
        }
    }
}

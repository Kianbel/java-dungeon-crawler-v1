package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
import item.Item;
import item.key.Key;
import javafx.scene.paint.Color;
import util.Position;
import item.weapon.Weapon;

public class DroppedItem extends InteractableTile {
    public Item item;

    public DroppedItem(Position roomLayoutPosition, Item item) {
        super(roomLayoutPosition, false);
        this.item = item;
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(entity instanceof Player p) {
            Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
            GUIManager.getInstance().printLog("Picked up: " + item.name, UITheme.LOG_WORLD);

            GUIManager.getInstance().triggerTextPopup("+" + item, Color.LIGHTBLUE, p.position, 1000);

            if(item instanceof Weapon w) {
                Weapon oldWeapon = p.weapon;
                p.setWeapon(w);
                EntityRoomManager.getInstance().removeInteractableTile(this);

                if(oldWeapon != null) {
                    currentRoom.addInteractableTile(new DroppedItem(roomLayoutPosition, oldWeapon));
                }
            }
            else if(item instanceof Key) {
                p.addItemToInventory(item);
                EntityRoomManager.getInstance().removeInteractableTile(this);
            }
        }
    }
}

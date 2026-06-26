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
                Weapon oldWeapon = player.weapon;

                int damageDifference = w.baseAttackDamage - oldWeapon.baseAttackDamage;
                double critDifference = w.critRate - oldWeapon.critRate;

                String dmgDiff = damageDifference == 0 ? "" : String.format("%+dATK", damageDifference);
                String critDiff = critDifference == 0.0 ? "" : String.format("%+.1fCRIT%%", critDifference);

                Color pickupColor;
                if (damageDifference >= 0 && critDifference >= 0) {
                    pickupColor = (damageDifference == 0 && critDifference == 0.0) ? UITheme.LOG_WORLD : Color.GREEN;
                } else if (damageDifference <= 0 && critDifference <= 0) {
                    pickupColor = Color.RED;
                } else {
                    double oldAverageDamage = oldWeapon.baseAttackDamage * oldWeapon.critRate;
                    double newAverageDamage = w.baseAttackDamage * w.critRate;

                    if(newAverageDamage > oldAverageDamage) pickupColor = Color.GREEN;
                    else if(newAverageDamage < oldAverageDamage) pickupColor = Color.RED;
                    else pickupColor = Color.YELLOW;
                }

                GUIManager.getInstance().triggerTextPopup(String.format("+%s %s %s", w.name, dmgDiff, critDiff), pickupColor, player.position, 1000);
                GUIManager.getInstance().printLog(String.format("Picked up: %s %s %s", w.name, dmgDiff, critDiff), pickupColor);
                player.setWeapon(w);
                EntityRoomManager.getInstance().removeInteractableTile(this);

                currentRoom.addInteractableTile(new DroppedItem(roomLayoutPosition, oldWeapon));
            }
            else if(item instanceof Armor a) {
                Armor oldArmor = player.armor;

                int armorDifference = a.armorPoints - oldArmor.armorPoints;

                String armorDiff = (armorDifference == 0) ? "" : String.format("%+dDEF", armorDifference);
                Color pickupColor;
                if(armorDifference > 0) pickupColor = Color.GREEN;
                else if(armorDifference < 0) pickupColor = Color.RED;
                else pickupColor = UITheme.LOG_WORLD;

                GUIManager.getInstance().triggerTextPopup(String.format("+%s %s", a.name, armorDiff), pickupColor, player.position, 1000);
                GUIManager.getInstance().printLog(String.format("Picked up: %s %s", a.name, armorDiff), pickupColor);
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

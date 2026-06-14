package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
import javafx.scene.paint.Color;
import util.Position;
import weapon.Weapon;

public class DroppedWeapon extends InteractableTile {
    public Weapon weapon;

    public DroppedWeapon(Position roomLayoutPosition, Weapon weapon) {
        super(roomLayoutPosition, false);
        this.weapon = weapon;
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(entity instanceof Player p) {
            Weapon oldWeapon = p.weapon;
            Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);

            GUIManager.getInstance().printLog("Picked up: " + weapon, UITheme.LOG_WORLD);
            GUIManager.getInstance().triggerTextPopup(String.format("+%s (ATK:%d)", weapon.name, weapon.baseAttackDamage), Color.LIGHTBLUE, p.position, 1000);
            p.setWeapon(weapon);
            EntityRoomManager.getInstance().removeInteractableTile(this);

            if(oldWeapon != null) {
                currentRoom.addInteractableTile(new DroppedWeapon(roomLayoutPosition, oldWeapon));
            }
        }
    }
}

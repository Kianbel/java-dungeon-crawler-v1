package world;

import core.EntityRoomManager;
import core.room.Room;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
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

            GUIManager.getInstance().printLog("[WORLD]: You picked up " + weapon.name, UITheme.LOG_WORLD);
            p.setWeapon(weapon);
            EntityRoomManager.getInstance().removeInteractableTile(this);

            if(oldWeapon != null) {
                currentRoom.addInteractableTile(new DroppedWeapon(roomLayoutPosition, oldWeapon));
            }
        }
    }
}

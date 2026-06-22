package entity;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.projectile.WeaponProjectile;
import item.weapon.Ammo;
import item.weapon.Weapon;
import util.Position;

public abstract class RangedMonster extends Monster {
    public Ammo ammo;

    public RangedMonster(String name, int health, int armor, Weapon weapon, Ammo ammo, Position position) {
        super(name, health, armor, weapon, position);
        this.ammo = ammo;
    }

    @Override
    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            Position projectileUnitPos = pathfindToPlayerPosition(true);

            Entity weaponProjectile = new WeaponProjectile(ammo, projectileUnitPos, position.add(projectileUnitPos));
            EntityRoomManager.getInstance().addEntityToRoom(weaponProjectile, currentRoom);
            ammo.decreaseAmount();
        }
        else throw new RuntimeException(this + " cannot attack " + targetEntity + " as target is not in same room");
    }

    @Override
    protected void makeSoundTextPopup() {

    }
}

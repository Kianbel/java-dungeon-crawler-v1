package entity.projectile;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import item.weapon.Ammo;
import util.Position;

import java.util.List;

public class WeaponProjectile extends Projectile {
    public WeaponProjectile(Ammo ammo, Position movementUnitPos, Position position) {
        super(ammo.name, ammo.damage, movementUnitPos, position);
        overrideColor(ammo.color);
        overrideCharacter(ammo.character);
        setIlluminated(true);
        setIlluminationRange(0);
    }
}
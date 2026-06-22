package entity.projectile;

import item.weapon.Ammo;
import util.Position;

public class WeaponProjectile extends Projectile {
    public WeaponProjectile(Ammo ammo, Position movementUnitPos, Position position) {
        super(ammo.name, ammo.damage, movementUnitPos, position);
        overrideColor(ammo.color);
        overrideCharacter(ammo.character);
        setIlluminated(true);
        setIlluminationRange(0);
    }
}

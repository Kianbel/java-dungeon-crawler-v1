package item.weapon;

import util.Position;

public abstract class RangedWeapon extends Weapon {


    public RangedWeapon(String name, int baseAttackDamage) {
        super(name, baseAttackDamage);
    }

    public RangedWeapon(String name, int baseAttackDamage, double critRate) {
        super(name, baseAttackDamage, critRate);
    }
}

package item.armor.special;

import item.armor.Armor;

public abstract class SpecialArmor extends Armor {
    public SpecialArmor(String name, int armorPoints) {
        super(name, armorPoints);
    }

    public abstract void wearEffect();
    public abstract void takeOffEffect();
}

package item.armor;

import item.Item;

public abstract class Armor extends Item {
    public int armorPoints;

    public Armor(String name, int armorPoints) {
        super(name);
        this.armorPoints = armorPoints;
    }

    @Override
    public String toString() {
        return String.format("%s (DEF:%d)", name, armorPoints);
    }
}

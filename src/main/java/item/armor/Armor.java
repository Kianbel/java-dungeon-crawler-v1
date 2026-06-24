package item.armor;

import item.Item;

import java.util.Random;

public abstract class Armor extends Item {
    public int armorPoints;

    public Armor(String name, int armorPoints) {
        super(name);
        this.armorPoints = Math.max(armorPoints + new Random().nextInt(3), 0);
    }

    @Override
    public String toString() {
        return String.format("%s (DEF:%d)", name, armorPoints);
    }
}

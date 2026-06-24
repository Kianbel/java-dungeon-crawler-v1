package item.weapon;

import item.Item;

import java.util.Random;

public abstract class Weapon extends Item {
    public int baseAttackDamage;
    public double critRate;
    public final double CRIT_MULTIPLIER = 2;

    public Weapon(String name, int baseAttackDamage, double critRate) {
        super(name);
        this.baseAttackDamage = Math.max(baseAttackDamage + new Random().nextInt(3), 0);
        this.critRate = critRate;
    }

    public Weapon(String name, int baseAttackDamage) {
        this(name, baseAttackDamage, 0.1);
        this.baseAttackDamage = baseAttackDamage;
    }

    public int getCalculatedAttackDamage() {
        final int error = (int) (baseAttackDamage * 0.3);

        int damageError = new Random().nextInt(-error, error+1);
        int attackDamage = Math.max(0, baseAttackDamage + damageError);

        if(Math.random() <= critRate) {
            return Math.toIntExact(Math.round(attackDamage * CRIT_MULTIPLIER));
        }
        return attackDamage;
    }

    public boolean isCritical(int damage) {
        return damage >= baseAttackDamage * CRIT_MULTIPLIER;
    }

    @Override
    public String toString() {
        return String.format("%s (ATK:%d | CRIT%%:%.1f)", name, baseAttackDamage, critRate);
    }
}

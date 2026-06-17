package item.weapon;

public abstract class Sword extends Weapon {
    public Sword(String name, int attackDamage, double critRate) {
        super(name, attackDamage, critRate);
    }

    public Sword(String name, int attackDamage) {
        super(name, attackDamage);
    }
}

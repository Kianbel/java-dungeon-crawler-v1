package item.weapon;

public class GenericDamager extends Weapon {
    public GenericDamager(String name, int baseAttackDamage, double critRate) {
        super(name, baseAttackDamage, critRate);
    }

    public GenericDamager(int baseAttackDamage, double critRate) {
        super("Generic Damager", baseAttackDamage, critRate);
    }
}

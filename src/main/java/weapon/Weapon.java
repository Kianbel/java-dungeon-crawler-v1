package weapon;

public abstract class Weapon {
    public String name;
    public int baseAttackDamage;
    public double critRate;
    public final int CRIT_MULTIPLIER = 2;
    private final int ATTACK_DAMAGE_ERROR_RANGE = 2;

    public Weapon(String name, int baseAttackDamage, double critRate) {
        this.name = name;
        this.baseAttackDamage = baseAttackDamage;
        this.critRate = critRate;
    }

    public Weapon(String name, int baseAttackDamage) {
        this.name = name;
        this.baseAttackDamage = baseAttackDamage;
        this.critRate = 0.1;
    }

    public int getCalculatedAttackDamage() {
        int damageError = (int) (Math.random() * 100 % ATTACK_DAMAGE_ERROR_RANGE*2+1);
        damageError = damageError - ATTACK_DAMAGE_ERROR_RANGE;
        int attackDamage = baseAttackDamage + damageError;
        if(attackDamage < 0) attackDamage = 0;

        if(Math.random() <= critRate) {
//            System.out.println("crit damage: " + attackDamage * CRIT_MULTIPLIER);
            return attackDamage * CRIT_MULTIPLIER;
        }
        return attackDamage;
    }

    @Override
    public String toString() {
        return String.format("%s (ATK: %d)", name, baseAttackDamage);
    }
}

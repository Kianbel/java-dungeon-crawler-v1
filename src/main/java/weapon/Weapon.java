package weapon;

public abstract class Weapon {
    public String name;
    public int attackDamage;
    public double critRate;
    public final int CRIT_MULTIPLIER = 2;

    public Weapon(String name, int attackDamage, double critRate) {
        this.name = name;
        this.attackDamage = attackDamage;
        this.critRate = critRate;
    }

    public int getCalculatedAttackDamage() {
        if(Math.random() <= critRate) return attackDamage * CRIT_MULTIPLIER;
        return attackDamage;
    }
}

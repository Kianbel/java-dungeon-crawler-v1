package item.weapon;

public class Fist extends Weapon {

    public Fist() {
        final String NAME = "Fist";
        final int ATTACK_DAMAGE = 2;
        final double CRIT_RATE = 0.1;

        super(NAME, ATTACK_DAMAGE, CRIT_RATE);
    }
}

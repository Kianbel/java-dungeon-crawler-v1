package entity;

import item.Fist;
import item.Weapon;
import util.Position;

public class Player extends Entity {
    public Player(Position position) {
        final String NAME = "Player";
        final int HEALTH = 100;
        final int ARMOR = 0;
        final Weapon WEAPON = new Fist();

        super(NAME, HEALTH, ARMOR, WEAPON, position);
    }

    @Override
    public void die() {

    }

    @Override
    public void attack(Entity entity) {

    }

    @Override
    public void hurt(int damage, boolean isCritical, Entity attacker) {

    }

    @Override
    public void update() {

    }
}

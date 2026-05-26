package entity;

import item.Fist;
import item.Weapon;
import util.DIRECTION;

public class Player extends Entity {
    public Player() {
        String NAME = "Player";
        int HEALTH = 100;
        int ARMOR = 0;
        Weapon WEAPON = new Fist();

        super(NAME, HEALTH, ARMOR, WEAPON);
    }

    @Override
    public void walk(DIRECTION direction) {

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

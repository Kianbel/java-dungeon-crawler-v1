package entity;

import item.Weapon;

public abstract class Monster extends Entity {
    public Monster(String name, int health, int armor, Weapon weapon) {
        super(name, health, armor, weapon);
    }
}

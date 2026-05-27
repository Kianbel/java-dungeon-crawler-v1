package entity;

import item.Weapon;
import util.Position;

public abstract class Monster extends Entity {
    public Monster(String name, int health, int armor, Weapon weapon, Position position) {
        super(name, health, armor, weapon, position);
    }
}

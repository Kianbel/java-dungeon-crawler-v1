package entity;

import item.Weapon;
import util.DIRECTION;

public abstract class Entity {
    public String name;
    public int armor;
    public int health;
    public Weapon weapon;

    public Entity(String name, int armor, int health, Weapon weapon) {
        this.name = name;
        this.armor = armor;
        this.health = health;
        this.weapon = weapon;
    }

    public abstract void walk(DIRECTION direction);
    public abstract void die();
    public abstract void attack(Entity entity);
    public abstract void hurt(int damage, boolean isCritical, Entity attacker);
    public abstract void update();
}

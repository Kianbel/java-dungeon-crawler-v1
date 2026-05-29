package entity;

import core.EntityRoomManager;
import item.Fist;
import item.Weapon;
import util.Position;
import world.Room;

public class Zombie extends Monster {
    public Zombie(Position position) {
        final String NAME = "Zombie";
        final int HEALTH = 20;
        final int ARMOR = 5;
        final Weapon WEAPON = new Fist();

        super(NAME, HEALTH, ARMOR, WEAPON, position);
    }


    @Override
    public void update() {

    }
}

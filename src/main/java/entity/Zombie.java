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
    public void pathfindToPlayerPosition() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;

        dx = (int) Math.ceil((double) dx / position.x);
        dy = (int) Math.ceil((double) dy / position.y);

        System.out.println(id + ":\t" + dx + " " + dy);

        // TODO: CONTINUE GET UNIT VECTOR FROM 2 POINTS
    }


    @Override
    public void makeMove() {
        pathfindToPlayerPosition();
    }
}

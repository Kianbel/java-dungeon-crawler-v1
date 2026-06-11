package entity.projectile;

import util.Position;

public class Fireball extends Projectile {
    public Fireball(Position moveDirectionInUnitPos, Position position) {
        super("Fireball", 8, moveDirectionInUnitPos, position);
    }

    @Override
    public void makeMove() {
        System.out.println(this + " moving to " + movementUnitPos);
        move();
    }
}

package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class Golem extends Monster {
    private final GolemFSM fsm;

    public Golem(Position position) {
        super("Golem", 50, 15, new GenericDamager(5, 0.1), position);
        fsm = new GolemFSM(this);
    }

    @Override
    public void makeMove() {
        super.makeMove();
        fsm.update();
    }

    @Override
    protected void makeSoundTextPopup() {

    }
}

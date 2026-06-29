package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class Roach extends Monster {
    private final RoachFSM fsm;

    public Roach(Position position) {
        super("Roach", 10, 0, new GenericDamager(3, 0.2), position);
        fsm = new RoachFSM(this);
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

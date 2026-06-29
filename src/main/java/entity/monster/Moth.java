package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class Moth extends Monster {
    private final MothFSM fsm;

    public Moth(Position position) {
        super("Moth", 10, 0, new GenericDamager(3, 0.2), position);
        fsm = new MothFSM(this);
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

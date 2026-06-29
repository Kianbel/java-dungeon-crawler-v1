package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class FlareApprentice extends Monster {
    private final FlareApprenticeFSM fsm;

    public FlareApprentice(Position position) {
        super("Flare Apprentice", 60, 10, new GenericDamager(10, 0.1), position);
        fsm = new FlareApprenticeFSM(this);
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

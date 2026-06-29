package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class GiantCentipede extends Monster {
    private final GiantCentipedeFSM fsm;

    public GiantCentipede(Position position) {
        super("Giant Centipede", 30, 3, new GenericDamager(5,0.1), position);
        fsm = new GiantCentipedeFSM(this);
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

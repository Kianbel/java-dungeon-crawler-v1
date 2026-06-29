package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class GoblinTank extends Monster {
    private final GoblinTankFSM fsm;

    public GoblinTank(Position position) {
        super("Goblin Tank", 50, 10, new GenericDamager(4, 0.1), position);
        fsm = new GoblinTankFSM(this);
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

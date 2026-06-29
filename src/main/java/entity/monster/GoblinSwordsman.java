package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class GoblinSwordsman extends Monster {
    private final GoblinSwordsmanFSM fsm;

    public GoblinSwordsman(Position position) {
        super("Goblin Swordsman", 30, 5, new GenericDamager(8, 0.3), position);
        fsm = new GoblinSwordsmanFSM(this);
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

package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import util.Position;

public class GoblinSummoner extends Monster {
    private final GoblinSummonerFSM fsm;

    public GoblinSummoner(Position position) {
        super("Goblin Wizard", 40, 8, new GenericDamager(8, 0.4), position);
        fsm = new GoblinSummonerFSM(this);
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

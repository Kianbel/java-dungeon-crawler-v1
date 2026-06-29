package entity.monster;

import entity.Monster;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class GoblinWizard extends Monster {
    private final GoblinWizardFSM fsm;

    public GoblinWizard(Position position) {
        super("Goblin Wizard", 40, 8, new GenericDamager(8, 0.4), position);
        fsm = new GoblinWizardFSM(this);
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

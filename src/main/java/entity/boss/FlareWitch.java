package entity.boss;

import entity.Entity;
import gui.dataclass.UITheme;
import util.Position;
import item.weapon.Fist;

public class FlareWitch extends Boss {
    private final FlareWitchFSM stateMachine;


    public FlareWitch(Position position) {
        super("Flare Witch", 100, 2, new Fist(), position);
        stateMachine = new FlareWitchFSM(this);
        stateMachine.setupInitialState();
        setIlluminated(true);
        overrideColor(UITheme.ENTITY_FLARE_WITCH);
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        super.hurt(damage, attacker);
        stateMachine.tryTeleport();
    }

    @Override
    public void makeMove() {
        stateMachine.update();
    }

    @Override
    protected void makeSoundTextPopup() {
        // do nothing
    }
}

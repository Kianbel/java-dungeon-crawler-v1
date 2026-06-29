package entity.monster;

import entity.Entity;
import entity.Monster;
import entity.Player;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;

public class JuvenileGiantCentipede extends Monster {
    private final JuvenileGiantCentipedeFSM fsm;

    public JuvenileGiantCentipede(Position position) {
        super("Juvenile Giant Centipede", 15, 3, new GenericDamager(3,0.1), position);
        fsm = new JuvenileGiantCentipedeFSM(this);
    }

    @Override
    public void makeMove() {
        super.makeMove();
        fsm.update();
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        super.hurt(damage, attacker);
        if(attacker instanceof Player) fsm.doAngered();
    }

    @Override
    protected void makeSoundTextPopup() {

    }
}

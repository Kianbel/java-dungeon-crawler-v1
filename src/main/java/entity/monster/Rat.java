package entity.monster;

import entity.Player;
import entity.fsm.RatFSM;
import item.weapon.GenericDamager;
import util.WeightedObject;
import entity.Entity;
import gui.GUIManager;
import gui.dataclass.UITheme;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;

public class Rat extends Monster {
    private final RatFSM stateMachine;

    public Rat(Position position) {
        super("Rat", 5, 0, new GenericDamager(2, 0.1), position);
        stateMachine = new RatFSM(this);
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("squeaks", UITheme.ENTITY_RAT, position);
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new Heart(position, 5), 3),
                new WeightedObject(new Coin(position, 5), 3),
                new WeightedObject(null, 5)
        ));

        dropOnDeath(lootTable);
        super.die();
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        super.hurt(damage, attacker);
        if(attacker instanceof Player) stateMachine.doAngered();
    }

    @Override
    public void makeMove() {
        super.makeMove();

        Position unitPos = new Position(0,0);
        switch(random.nextInt(4)) {
            case 0 -> unitPos.y = 1;
            case 1 -> unitPos.y = -1;
            case 2 -> unitPos.x = 1;
            case 3 -> unitPos.x = -1;
        }

        if(isValidTargetPosition(position.add(unitPos))) {
            walk(unitPos);
        }
    }
}

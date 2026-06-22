package entity.monster;

import entity.Monster;
import gui.GUIManager;
import gui.dataclass.UITheme;
import util.WeightedObject;
import item.weapon.GenericDamager;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;

public class Bat extends Monster {

    private final BatFSM stateMachine;

    public Bat(Position position) {
        super("Bat", 10, 0, new GenericDamager(3, 0.1), position);
        stateMachine = new BatFSM(this);
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
    public void makeMove() {
        super.makeMove();
        stateMachine.update();
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("screech", UITheme.ENTITY_BAT, position);
    }
}

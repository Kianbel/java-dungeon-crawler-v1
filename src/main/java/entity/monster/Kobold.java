package entity.monster;

import entity.Monster;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.food.MonsterMeat;
import util.WeightedObject;
import item.weapon.Dagger;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Kobold extends Monster {

    private final KoboldFSM stateMachine;

    public Kobold(Position position) {
        super("Kobold", 20, 2, new Dagger(), position);
        stateMachine = new KoboldFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new Dagger(), position, 1),
                new WeightedObject(new Heart(position, 5), 3),
                new WeightedObject(new Coin(position, 10), 2),
                new WeightedObject(new MonsterMeat(new Random().nextInt(2,5)), position, 4),
                new WeightedObject(null, 1)
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
        GUIManager.getInstance().triggerTextPopup("kruawg!", UITheme.ENTITY_KOBOLD, position);
    }
}

package entity.monster;

import entity.Monster;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.food.MonsterMeat;
import item.mobdrop.BatWing;
import util.Randomizer;
import util.WeightedObject;
import item.weapon.GenericDamager;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bat extends Monster {

    private final BatFSM stateMachine;

    public Bat(Position position) {
        super("Bat", 20, 0, new GenericDamager(3, 0.1), position);
        stateMachine = new BatFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new Heart(position, 5), 3),
                new WeightedObject(new Coin(position, 5), 3),
                new WeightedObject(new MonsterMeat(new Random().nextInt(1,3)), position, 6),
                new WeightedObject(null, 2)
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

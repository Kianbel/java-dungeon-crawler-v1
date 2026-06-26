package entity.monster;

import entity.RangedMonster;
import item.food.MonsterMeat;
import item.mobdrop.GoblinTeeth;
import item.weapon.Arrow;
import item.weapon.WoodenBow;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Goblin extends RangedMonster {
    private final GoblinFSM stateMachine;

    public Goblin(Position position) {
        super("Goblin", 30, 3, new WoodenBow(), new Arrow(999), position);
        this.stateMachine = new GoblinFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> drops = new ArrayList<>();
//        drops.add(new WeightedObject(new WoodenBow(), position, 1));
        drops.add(new WeightedObject(new GoblinTeeth(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT));
        drops.add(new WeightedObject(new MonsterMeat(new Random().nextInt(2,5)), position, MONSTER_MEAT_WEIGHT));
        drops.add(new WeightedObject(new Arrow(new Random().nextInt(3,8)), position, 50));
        drops.add(new WeightedObject(null, NULL_WEIGHT));
        dropOnDeath(drops);

        super.die();
    }

    @Override
    public void makeMove() {
        super.makeMove();
        stateMachine.update();
    }

    @Override
    protected void makeSoundTextPopup() {

    }
}

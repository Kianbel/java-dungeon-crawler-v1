package entity.monster;

import entity.RangedMonster;
import item.food.MonsterMeat;
import item.weapon.Arrow;
import item.weapon.WoodenBow;
import util.Position;
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
        drops.add(new WeightedObject(new Arrow(new Random().nextInt(3,8)), position, 5));
        drops.add(new WeightedObject(new MonsterMeat(new Random().nextInt(2,5)), position, 5));
        drops.add(new WeightedObject(null, 3));
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

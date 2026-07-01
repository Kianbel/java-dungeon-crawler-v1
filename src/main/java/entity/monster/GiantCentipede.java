package entity.monster;

import entity.Monster;
import item.food.MonsterMeat;
import item.mobdrop.CentipedeLeg;
import item.mobdrop.GoblinTeeth;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiantCentipede extends Monster {
    private final GiantCentipedeFSM fsm;

    public GiantCentipede(Position position) {
        super("Giant Centipede", 30, 3, new GenericDamager(5,0.1), position);
        fsm = new GiantCentipedeFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> drops = new ArrayList<>();
        drops.add(new WeightedObject(new CentipedeLeg(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT));
        drops.add(new WeightedObject(new MonsterMeat(new Random().nextInt(2,5)), position, MONSTER_MEAT_WEIGHT));
        drops.add(new WeightedObject(null, NULL_WEIGHT));
        dropOnDeath(drops);

        super.die();
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

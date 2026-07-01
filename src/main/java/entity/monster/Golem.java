package entity.monster;

import entity.Monster;
import item.food.MonsterMeat;
import item.mobdrop.GoblinTeeth;
import item.mobdrop.HardenedClay;
import item.mobdrop.PoppyFlower;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Golem extends Monster {
    private final GolemFSM fsm;

    public Golem(Position position) {
        super("Golem", 50, 15, new GenericDamager(5, 0.1), position);
        fsm = new GolemFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> drops = new ArrayList<>();
        drops.add(new WeightedObject(new HardenedClay(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT));
        drops.add(new WeightedObject(new PoppyFlower(Randomizer.pick(1,2)), position, 10));
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

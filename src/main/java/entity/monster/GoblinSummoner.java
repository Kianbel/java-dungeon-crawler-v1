package entity.monster;

import entity.Monster;
import item.food.MonsterMeat;
import item.mobdrop.BrokenSummonerStaff;
import item.mobdrop.GoblinTeeth;
import item.mobdrop.HardenedClay;
import item.weapon.GenericDamager;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoblinSummoner extends Monster {
    private final GoblinSummonerFSM fsm;

    public GoblinSummoner(Position position) {
        super("Goblin Wizard", 40, 8, new GenericDamager(8, 0.4), position);
        fsm = new GoblinSummonerFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> drops = new ArrayList<>();
        drops.add(new WeightedObject(new GoblinTeeth(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT));
        drops.add(new WeightedObject(new HardenedClay(Randomizer.pick(1,2)), position, 10));
        drops.add(new WeightedObject(new BrokenSummonerStaff(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT));
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

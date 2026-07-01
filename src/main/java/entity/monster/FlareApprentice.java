package entity.monster;

import entity.Monster;
import item.food.MonsterMeat;
import item.mobdrop.FlareCloak;
import item.mobdrop.GoblinTeeth;
import item.weapon.Arrow;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlareApprentice extends Monster {
    private final FlareApprenticeFSM fsm;

    public FlareApprentice(Position position) {
        super("Flare Apprentice", 60, 10, new GenericDamager(10, 0.1), position);
        fsm = new FlareApprenticeFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> drops = new ArrayList<>();
        drops.add(new WeightedObject(new FlareCloak(1), position, OWN_DROP_WEIGHT));
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

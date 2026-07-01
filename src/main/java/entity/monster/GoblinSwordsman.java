package entity.monster;

import entity.Monster;
import item.food.MonsterMeat;
import item.mobdrop.GoblinTeeth;
import item.mobdrop.SwordShard;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoblinSwordsman extends Monster {
    private final GoblinSwordsmanFSM fsm;

    public GoblinSwordsman(Position position) {
        super("Goblin Swordsman", 30, 5, new GenericDamager(8, 0.3), position);
        fsm = new GoblinSwordsmanFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> drops = new ArrayList<>();
        drops.add(new WeightedObject(new GoblinTeeth(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT));
        drops.add(new WeightedObject(new SwordShard(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT));
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

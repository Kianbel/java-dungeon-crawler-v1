package entity.monster;

import entity.Entity;
import entity.Monster;
import entity.Player;
import item.food.MonsterMeat;
import item.mobdrop.GoblinTeeth;
import item.mobdrop.RoachHead;
import item.weapon.GenericDamager;
import item.weapon.Weapon;
import util.Position;
import util.Randomizer;
import util.WeightedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Roach extends Monster {
    private final RoachFSM fsm;

    public Roach(Position position) {
        super("Roach", 10, 0, new GenericDamager(3, 0.2), position);
        fsm = new RoachFSM(this);
    }

    @Override
    public void die() {
        List<WeightedObject> drops = new ArrayList<>();
        drops.add(new WeightedObject(new RoachHead(Randomizer.pick(1)), position, OWN_DROP_WEIGHT));
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
    public void hurt(int damage, Entity attacker) {
        super.hurt(damage, attacker);
        if(attacker instanceof Player) fsm.doAngered();
    }

    @Override
    protected void makeSoundTextPopup() {

    }
}

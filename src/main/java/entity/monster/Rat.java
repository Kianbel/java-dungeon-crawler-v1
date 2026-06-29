package entity.monster;

import entity.Monster;
import entity.Player;
import item.food.MonsterMeat;
import item.mobdrop.RatSkin;
import item.weapon.GenericDamager;
import util.Randomizer;
import util.WeightedObject;
import entity.Entity;
import gui.GUIManager;
import gui.dataclass.UITheme;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rat extends Monster {
    private final RatFSM stateMachine;

    public Rat(Position position) {
        super("Rat", 10, 0, new GenericDamager(2, 0.1), position);
        stateMachine = new RatFSM(this);
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("squeaks", UITheme.ENTITY_RAT, position);
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new RatSkin(Randomizer.pick(1,2)), position, OWN_DROP_WEIGHT),
                new WeightedObject(new MonsterMeat(new Random().nextInt(1,3)), position, MONSTER_MEAT_WEIGHT),
                new WeightedObject(new Heart(position, 5), HEART_WEIGHT),
                new WeightedObject(new Coin(position, 5), COIN_WEIGHT),
                new WeightedObject(null, NULL_WEIGHT)
        ));

        dropOnDeath(lootTable);
        super.die();
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        super.hurt(damage, attacker);
        if(attacker instanceof Player) stateMachine.doAngered();
    }

    @Override
    public void makeMove() {
        super.makeMove();
        stateMachine.update();
    }
}

package entity.monster;

import entity.Monster;
import gui.dataclass.UITheme;
import item.food.MonsterMeat;
import item.mobdrop.GiantSpiderLeg;
import util.Randomizer;
import util.WeightedObject;
import gui.GUIManager;
import item.weapon.GiantSpiderFang;
import util.Position;
import world.*;

import java.util.*;

public class GiantSpider extends Monster {

    private GiantSpiderFSM stateMachine;

    public GiantSpider(Position position) {
        super("Giant Spider", 30, 5, new GiantSpiderFang(), position);
        this.stateMachine = new GiantSpiderFSM(this);
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("hiss", UITheme.ENTITY_SPIDER, position);
    }

    @Override
    public void makeMove() {
        super.makeMove();
        stateMachine.update();
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new GiantSpiderLeg(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT),
                new WeightedObject(new MonsterMeat(new Random().nextInt(1,3)), position, MONSTER_MEAT_WEIGHT),
                new WeightedObject(new GiantSpiderFang(), position, 20),
                new WeightedObject(new Heart(position, 5), HEART_WEIGHT),
                new WeightedObject(new Coin(position, 5), COIN_WEIGHT),
                new WeightedObject(null, NULL_WEIGHT)
        ));

        dropOnDeath(lootTable);
        super.die();
    }
}

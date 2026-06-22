package entity.monster;

import entity.Monster;
import gui.dataclass.UITheme;
import util.WeightedObject;
import gui.GUIManager;
import item.weapon.GiantSpiderFang;
import util.Position;
import world.*;

import java.util.*;

public class GiantSpider extends Monster {

    private GiantSpiderFSM stateMachine;

    public GiantSpider(Position position) {
        super("Giant Spider", 20, 0, new GiantSpiderFang(), position);
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
                new WeightedObject(new GiantSpiderFang(), position, 1),
                new WeightedObject(new Heart(position, 5), 3),
                new WeightedObject(new Coin(position, 5), 3),
                new WeightedObject(null, 5)
        ));

        dropOnDeath(lootTable);
        super.die();
    }
}

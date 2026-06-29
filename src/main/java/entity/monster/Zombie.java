package entity.monster;

import core.EntityRoomManager;
import entity.Monster;
import gui.dataclass.UITheme;
import item.mobdrop.RottingFlesh;
import item.weapon.GenericDamager;
import util.Randomizer;
import util.WeightedObject;
import entity.Entity;
import gui.GUIManager;
import item.weapon.Fist;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;

public class Zombie extends Monster {

    private ZombieFSM zombieFSM;

    public Zombie(Position position) {
        super("Zombie", 20, 0, new GenericDamager(5, 0.1), position);
        zombieFSM = new ZombieFSM(this);
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("groans", UITheme.ENTITY_ZOMBIE, position);
    }

    public void makeMove() {
        super.makeMove();
        zombieFSM.update();
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new RottingFlesh(Randomizer.pick(1,2,3)), position, OWN_DROP_WEIGHT),
                new WeightedObject(new Heart(position, 10), HEART_WEIGHT),
                new WeightedObject(new Coin(position, 5), COIN_WEIGHT),
                new WeightedObject(null, NULL_WEIGHT)
        ));

        dropOnDeath(lootTable);
        super.die();
    }
}

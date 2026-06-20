package entity.monster;

import util.WeightedObject;
import item.weapon.Dagger;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;

public class Kobold extends Monster {
    public Kobold(Position position) {
        super("Kobold", 20, 2, new Dagger(), position);
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new Dagger(), position, 1),
                new WeightedObject(new Heart(position, 5), 3),
                new WeightedObject(new Coin(position, 10), 3),
                new WeightedObject(null, 5)
        ));

        dropOnDeath(lootTable);
        super.die();
    }

    @Override
    protected void makeSoundTextPopup() {

    }
}

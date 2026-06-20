package entity.monster;

import core.EntityRoomManager;
import util.WeightedObject;
import entity.Entity;
import gui.GUIManager;
import javafx.scene.paint.Color;
import item.weapon.Fist;
import util.Position;
import world.Coin;
import world.Heart;

import java.util.ArrayList;
import java.util.List;

public class Zombie extends Monster {
    public Zombie(Position position) {
        super("Zombie", 10, 0, new Fist(), position);
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("groans", Color.DARKGREEN, position);
    }

    public void makeMove() {
        super.makeMove();
        Position unitPos = pathfindToPlayerPosition();
        if(unitPos.x == 0 && unitPos.y == 0) return;

        Position targetPosition = position.add(unitPos);
        Entity player = EntityRoomManager.getInstance().getPlayer();

        if(player.position.equals(targetPosition)) {
            attack(player);
        }
        else if(isValidTargetPosition(targetPosition)){
            final double WALK_CHANCE = 0.8;
            if(Math.random() <= WALK_CHANCE) walk(unitPos);
        }
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new Heart(position, 10), 3),
                new WeightedObject(new Coin(position, 5), 3),
                new WeightedObject(null, 5)
        ));

        dropOnDeath(lootTable);
        super.die();
    }
}

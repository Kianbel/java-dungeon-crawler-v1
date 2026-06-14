package entity.monster;

import core.EntityRoomManager;
import entity.Entity;
import gui.GUIManager;
import javafx.scene.paint.Color;
import weapon.Fist;
import util.Position;
import world.Coin;
import world.Heart;
import world.InteractableTile;

import java.util.HashMap;
import java.util.Map;

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
        Map<InteractableTile, Double> drops = new HashMap<>(Map.of(
                new Heart(position, 10), 0.5,
                new Coin(position, 5), 0.8
        ));
        dropOnDeath(drops);

        super.die();
    }
}

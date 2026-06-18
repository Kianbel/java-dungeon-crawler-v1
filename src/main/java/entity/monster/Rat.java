package entity.monster;

import core.EntityRoomManager;
import entity.Entity;
import gui.GUIManager;
import gui.UITheme;
import item.weapon.Fist;
import item.weapon.Weapon;
import javafx.scene.paint.Color;
import util.Position;

public class Rat extends Monster {
    public Rat(Position position) {
        // TODO: change weapon
        super("Rat", 5, 0, new Fist(), position);
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("squeaks", Color.WHITE, position);
    }

    @Override
    public void makeMove() {
        super.makeMove();

        Position unitPos = new Position(0,0);
        switch(random.nextInt(4)) {
            case 0 -> unitPos.y = 1;
            case 1 -> unitPos.y = -1;
            case 2 -> unitPos.x = 1;
            case 3 -> unitPos.x = -1;
        }

        if(isValidTargetPosition(position.add(unitPos))) {
            walk(unitPos);
        }

        Entity player = EntityRoomManager.getInstance().getPlayer();
        if(position.equals(player.position)) {
            player.hurt(random.nextInt(1,3));
            GUIManager.getInstance().printLog("You feel something pinched your legs...", UITheme.LOG_MONSTER_ACTION);
        }
    }
}

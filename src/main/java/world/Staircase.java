package world;

import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
import util.Position;

public class Staircase extends InteractableTile {
    public Staircase(Position roomLayoutPosition) {
        super(roomLayoutPosition, false);
        isLightOccluding = true;
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(entity instanceof Player p) {
            GUIManager.getInstance().printLog("You go down deeper into the dungeon...", UITheme.LOG_PLAYER_ACTION);
        }
    }
}

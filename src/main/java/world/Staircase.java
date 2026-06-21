package world;

import core.GameManager;
import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
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
            GameManager.getInstance().nextFloor();
            GUIManager.getInstance().triggerColorFlash(Color.BLACK, 1000);
        }
    }
}

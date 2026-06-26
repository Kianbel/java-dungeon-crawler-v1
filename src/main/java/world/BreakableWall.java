package world;

import core.EntityRoomManager;
import entity.Entity;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import util.Position;
import util.Randomizer;

public class BreakableWall extends InteractableTile {
    public BreakableWall(Position roomLayoutPosition) {
        super(roomLayoutPosition, true);
        isLightOccluding = true;
    }

    @Override
    public void onEntityBump(Entity entity) {
        EntityRoomManager.getInstance().removeInteractableTile(this);
        AudioManager.getInstance().playSFX("wall_break");
        switch (Randomizer.pick(1,2,3)) {
            case 1 -> GUIManager.getInstance().triggerTextPopup("ka pow", UITheme.LOG_WORLD, roomLayoutPosition);
            case 2 -> GUIManager.getInstance().triggerTextPopup("that hurts", UITheme.LOG_WORLD, roomLayoutPosition);
            case 3 -> {}
        }
    }
}

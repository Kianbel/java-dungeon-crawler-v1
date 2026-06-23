package world;

import core.EntityRoomManager;
import core.GameManager;
import entity.Entity;
import entity.Player;
import entity.monster.GiantSpider;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
import util.Position;

public class Web extends InteractableTile {
    private final int STUN_MOVE_AMOUNT = 3;

    public Web(Position position) {
        super(position, false);
        isLightOccluding = true;
        AudioManager.getInstance().playSFX("web_sling");
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(!(entity instanceof GiantSpider)) {
            entity.stun(STUN_MOVE_AMOUNT);
            EntityRoomManager.getInstance().removeInteractableTile(this);
            if(entity instanceof Player p) {
                GUIManager.getInstance().triggerTextPopup("webbed", Color.WHITESMOKE, p.position);
                GUIManager.getInstance().printLog("You stepped into a web and got stuck!", UITheme.LOG_PLAYER_ACTION);
                GUIManager.getInstance().triggerScreenShake();
                AudioManager.getInstance().playSFX("stun");
            }
        }
    }
}

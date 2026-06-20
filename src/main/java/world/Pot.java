package world;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import util.Position;

import java.util.Random;

public class Pot extends InteractableTile {
    public Pot(Position roomLayoutPosition) {
        super(roomLayoutPosition, true);
        isLightOccluding = true;
    }

    @Override
    public void onEntityBump(Entity entity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromInteractableTile(this);
        EntityRoomManager.getInstance().removeInteractableTile(this);
        AudioManager.getInstance().playSFX("pot_break");

        final double DROP_CHANCE = 0.2;
        final double COIN_CHANCE = 0.75;
        final double HEART_CHANCE = 1-COIN_CHANCE;

        InteractableTile dropTile;
        Random random = new Random();
        if(Math.random() <= DROP_CHANCE) {
            if(Math.random() <= HEART_CHANCE) {
                dropTile = new Heart(roomLayoutPosition, random.nextInt(5,16));
                GUIManager.getInstance().printLog("You break open a box and it dropped a heart!", UITheme.LOG_WORLD);
            }
            else {
                dropTile = new Coin(roomLayoutPosition, random.nextInt(5, 11));
                GUIManager.getInstance().printLog("You break open a box and it dropped some coins!", UITheme.LOG_WORLD);
            }
        }
        else dropTile = new ShatteredPot(roomLayoutPosition);

        currentRoom.addInteractableTile(dropTile);
    }
}

package world;

import gui.AudioManager;
import util.Position;

public class OpenedDoor extends InteractableTile {
    public OpenedDoor(Position roomLayoutPosition) {
        super(roomLayoutPosition, false);
        AudioManager.getInstance().playSFX("door_enter");
    }
}

package core.room.type;

import core.room.loader.RoomLayoutRegistry;
import util.Position;
import util.TILE;


public class SpecialRoom extends Room {
    public SpecialRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(SpecialRoom.class);
        super(layout, minimapPosition);
    }
}

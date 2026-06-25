package core.room.type;

import core.room.loader.RoomLayoutRegistry;
import util.Position;
import util.TILE;

public class StaircaseRoom extends Room {
    public StaircaseRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(StaircaseRoom.class);
        super(layout, minimapPosition);
    }
}

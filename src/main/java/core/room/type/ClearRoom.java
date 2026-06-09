package core.room.type;

import core.room.loader.RoomLayoutRegistry;
import util.Position;
import util.TILE;

public class ClearRoom extends Room {
    public ClearRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(ClearRoom.class);

        super(layout, minimapPosition);
    }
}

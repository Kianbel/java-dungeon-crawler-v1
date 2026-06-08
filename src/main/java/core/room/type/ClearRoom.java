package core.room.type;

import core.room.loader.RoomLayoutRegistry;
import util.Position;
import util.TILE;

public class ClearRoom extends Room {
    public ClearRoom(int height, int length, Position minimapPosition) {
        final double DECORATED_ROOM_CHANCE = 0.4;

        TILE[][] layout = null;
        if(Math.random() <= DECORATED_ROOM_CHANCE) {
            layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(ClearRoom.class);
        }

        super(layout, minimapPosition, height, length);
    }
}

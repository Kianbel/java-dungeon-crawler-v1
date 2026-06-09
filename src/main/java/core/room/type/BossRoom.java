package core.room.type;

import core.room.loader.RoomLayoutRegistry;
import util.Position;
import util.TILE;

public class BossRoom extends Room {
    public BossRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(BossRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();
    }
}

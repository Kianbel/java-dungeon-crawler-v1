package core.room.type;

import core.EntityRoomManager;
import core.room.loader.RoomLayoutRegistry;
import entity.Player;
import entity.boss.FlareWitch;
import util.Position;
import util.TILE;

import java.util.Random;

public class BossRoom extends Room {
    public BossRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(BossRoom.class);
        super(layout, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

//        EntityRoomManager.getInstance().addEntityToRoom(new Player(new Position(length/2+5, height/2+5)), this);

        Random random = new Random();

        Position bossSpawnPosition = new Position(length/2+random.nextInt(4,7), height/2+random.nextInt(4,7));
        EntityRoomManager.getInstance().addEntityToRoom(new FlareWitch(bossSpawnPosition), this);
    }
}

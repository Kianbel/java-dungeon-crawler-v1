package core.room.type;

import core.EntitySpawner;
import core.GameManager;
import core.room.loader.RoomLayoutRegistry;
import entity.monster.*;
import util.Position;
import util.Randomizer;
import util.TILE;

import java.util.Random;

public class NormalRoom extends Room {

    private Random random = new Random();

    public NormalRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(NormalRoom.class);
        super(layout, minimapPosition);
    }
}

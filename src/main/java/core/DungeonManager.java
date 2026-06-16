package core;

import core.room.generator.DrunkardWalk;
import core.room.generator.DungeonMapGenerator;
import core.room.loader.RoomLayoutLoader;
import core.room.type.*;
import util.MAP;
import util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonManager {
    private static final DungeonManager instance = new DungeonManager();

    private MAP[][] mapLayout;
    private List<Room> roomList = new ArrayList<>();

    private DungeonManager() {}
    public static DungeonManager getInstance() {
        return instance;
    }

    private final int ROOM_AMOUNT = 3;
    private final int MAP_HEIGHT = 21;
    private final int MAP_LENGTH = 21;

    public void generateDungeon() {
        DungeonMapGenerator dungeonMapGenerator = new DungeonMapGenerator(new DrunkardWalk(MAP_HEIGHT, MAP_LENGTH));
        dungeonMapGenerator.generate(ROOM_AMOUNT);
        mapLayout = dungeonMapGenerator.getMapLayout();

        RoomLayoutLoader.getInstance().loadAllLayouts("src/main/java/core/room/loader/layout");

        generateRooms();

        for(Room r : roomList) {
            EntityRoomManager.getInstance().addRoom(r);
            r.populateWithEntities();
        }
    }

    private void generateRooms() {
        roomList.clear();
        if(mapLayout == null) throw new RuntimeException("Cannot generate rooms as minimapLayout is not initialized");

        final int MAP_HEIGHT = mapLayout.length;
        final int MAP_LENGTH = mapLayout[0].length;

        for(int y = 0; y < MAP_HEIGHT; y++) {
            for(int x = 0; x < MAP_LENGTH; x++) {
                MAP mapTile = mapLayout[y][x];
                if(mapTile == null) continue;
                if(mapTile.isCorridor()) continue;

                boolean northDoor = false, eastDoor = false, southDoor = false, westDoor = false;

                if(y-1 >= 0 && mapLayout[y-1][x] == MAP.VCORRIDOR) northDoor = true;
                if(y+1 < MAP_HEIGHT && mapLayout[y+1][x] == MAP.VCORRIDOR) southDoor = true;
                if(x-1 >= 0 && mapLayout[y][x-1] == MAP.HCORRIDOR) westDoor = true;
                if(x+1 < MAP_LENGTH && mapLayout[y][x+1] == MAP.HCORRIDOR) eastDoor = true;

                Room newRoom;
                Position minimapPosition = new Position(x, y);

                switch(mapLayout[y][x]) {
                    case MAP.SPAWN -> newRoom = new SpawnRoom(minimapPosition);
                    case MAP.TREASURE -> newRoom = new TreasureRoom(minimapPosition);
                    case MAP.BOSS -> newRoom = new BossRoom(minimapPosition);
                    case MAP.NORMAL -> newRoom = new NormalRoom(minimapPosition);
                    default -> {
                        continue;
                    }
                }

                newRoom.generate(northDoor,eastDoor,southDoor,westDoor);
                roomList.add(newRoom);
            }
        }
    }

    public MAP[][] getMapLayout() {
        return mapLayout;
    }
}

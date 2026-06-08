package core;

import core.room.generator.DrunkardWalk;
import core.room.generator.DungeonMapGenerator;
import core.room.loader.RoomLayoutLoader;
import core.room.type.*;
import util.MAP;
import util.Position;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager {
    private static final DungeonManager instance = new DungeonManager();

    private MAP[][] mapLayout;
    private List<Room> roomList = new ArrayList<>();

    private DungeonManager() {}
    public static DungeonManager getInstance() {
        return instance;
    }

    public void generateDungeon() {
        int ROOM_AMOUNT = 10;

        DungeonMapGenerator dungeonMapGenerator = new DungeonMapGenerator(new DrunkardWalk());
        dungeonMapGenerator.generate(ROOM_AMOUNT);
        mapLayout = dungeonMapGenerator.getMapLayout();

        RoomLayoutLoader.getInstance().loadAllLayouts("src/main/java/core/room/loader/layout");
        dungeonMapGenerator.printMapLayout();

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
                if(mapTile == MAP.VCORRIDOR || mapTile == MAP.HCORRIDOR) continue;

                boolean northDoor = false, eastDoor = false, southDoor = false, westDoor = false;

                if(y-1 >= 0 && mapLayout[y-1][x] == MAP.VCORRIDOR) northDoor = true;
                if(y+1 < MAP_HEIGHT && mapLayout[y+1][x] == MAP.VCORRIDOR) southDoor = true;
                if(x-1 >= 0 && mapLayout[y][x-1] == MAP.HCORRIDOR) westDoor = true;
                if(x+1 < MAP_LENGTH && mapLayout[y][x+1] == MAP.HCORRIDOR) eastDoor = true;

                final int ROOM_LENGTH = 15;
                final int ROOM_HEIGHT = 15;

                Room newRoom;
                Position minimapPosition = new Position(x, y);

                switch(mapLayout[y][x]) {
                    case MAP.SPAWN -> newRoom = new SpawnRoom(11, 11, minimapPosition);
                    case MAP.INFESTED -> newRoom = new InfestedRoom(minimapPosition);
                    case MAP.TREASURE -> newRoom = new TreasureRoom(11, 11, minimapPosition);
                    case MAP.BOSS -> newRoom = new BossRoom(ROOM_HEIGHT, ROOM_LENGTH, minimapPosition);
                    case MAP.CLEAR -> newRoom = new ClearRoom(ROOM_HEIGHT, ROOM_LENGTH, minimapPosition);
                    default -> {
                        continue;
                    }
                }

                newRoom.generate(northDoor,eastDoor,southDoor,westDoor);
                roomList.add(newRoom);
            }
        }

        int i = 1;
        for(Room r : roomList) {
            System.out.println((i++) + " " + r.getClass().getSimpleName());
        }
    }

    public MAP[][] getMapLayout() {
        return mapLayout;
    }
}

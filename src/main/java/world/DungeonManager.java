package world;

import core.EntityRoomPositionManager;
import util.TILE;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager {
    private static DungeonManager instance = null;

    private TILE[][] minimapOverviewLayout;
    private List<Room> roomList = new ArrayList<>();

    private DungeonManager() {}
    public static DungeonManager getInstance() {
        if(instance == null) instance = new DungeonManager();
        return instance;
    }

    public void generateDungeon() {
        int ROOM_AMOUNT = 10;

        DungeonMapGenerator dungeonMapGenerator = new DungeonMapGenerator(new DrunkardWalk());
        dungeonMapGenerator.generate(ROOM_AMOUNT);
        minimapOverviewLayout = dungeonMapGenerator.getMapLayout();

        generateRooms();

        for(Room r : roomList) {
            EntityRoomPositionManager.getInstance().addRoom(r);
        }
    }

    private void generateRooms() {
        roomList.clear();
        if(minimapOverviewLayout == null) throw new RuntimeException("Cannot generate rooms as minimapLayout is not initialized");

        final int MAP_HEIGHT = minimapOverviewLayout.length;
        final int MAP_LENGTH = minimapOverviewLayout[0].length;

        for(int y = 0; y < minimapOverviewLayout.length; y++) {
            for(int x = 0; x < minimapOverviewLayout[0].length; x++) {
                if(minimapOverviewLayout[y][x] == TILE.ROOM) {
                    boolean northDoor = false, eastDoor = false, southDoor = false, westDoor = false;

                    if(y-1 >= 0 && minimapOverviewLayout[y-1][x] == TILE.VCORRIDOR) northDoor = true;
                    if(y+1 < MAP_HEIGHT && minimapOverviewLayout[y+1][x] == TILE.VCORRIDOR) southDoor = true;
                    if(x-1 >= 0 && minimapOverviewLayout[y][x-1] == TILE.VCORRIDOR) westDoor = true;
                    if(x+1 < MAP_LENGTH && minimapOverviewLayout[y][x+1] == TILE.VCORRIDOR) eastDoor = true;

                    final int ROOM_LENGTH = 11;
                    final int ROOM_HEIGHT = 11;
                    Room newRoom = new Room(ROOM_HEIGHT, ROOM_LENGTH);
                    newRoom.generateWithDoors(northDoor, eastDoor, southDoor, westDoor);
                    roomList.add(newRoom);
                }
            }
        }
        System.out.println("Rooms added to list: " + roomList.size());
    }

    public TILE[][] getMinimapOverviewLayout() {
        return minimapOverviewLayout;
    }
}

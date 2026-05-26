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
    }

    public TILE[][] getMinimapOverviewLayout() {
        return minimapOverviewLayout;
    }
}

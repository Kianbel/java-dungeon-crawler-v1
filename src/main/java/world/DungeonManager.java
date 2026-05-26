package world;

import core.EntityRoomPositionManager;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager {
    private static DungeonManager instance = null;

    private List<Room> roomList = new ArrayList<>();

    private DungeonManager() {}
    public DungeonManager getInstance() {
        if(instance == null) instance = new DungeonManager();
        return instance;
    }

    public void generateDungeon() {
        DungeonMapGenerator mapGenerator;

    }
}

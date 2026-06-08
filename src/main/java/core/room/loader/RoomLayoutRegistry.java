package core.room.loader;

import core.room.type.Room;
import util.TILE;

import java.util.*;

public class RoomLayoutRegistry {
    private static final RoomLayoutRegistry instance = new RoomLayoutRegistry();

    private Map<Class<? extends Room>, List<TILE[][]>> hashmap = new HashMap<>();

    private RoomLayoutRegistry() {}
    public static RoomLayoutRegistry getInstance() { return instance;};

    public void addLayout(Class<? extends Room> roomClass, TILE[][] layout) {
        if(hashmap.containsKey(roomClass)) {
            hashmap.get(roomClass).add(layout);
            return;
        }

        List<TILE[][]> layouts = new ArrayList<>();
        layouts.add(layout);
        hashmap.put(roomClass, layouts);
    }

    public TILE[][] getRandomLayoutFromRoomClass(Class<? extends Room> roomClass) {
        List<TILE[][]> layouts = hashmap.get(roomClass);
        if(layouts.isEmpty()) {
            System.out.println("No layouts exists for " + roomClass.getSimpleName());
            return null;
        }
        TILE[][] layout = layouts.get(new Random().nextInt(layouts.size()));
        return layout;
    }
}

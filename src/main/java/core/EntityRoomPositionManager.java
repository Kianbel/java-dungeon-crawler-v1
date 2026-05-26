package core;

import entity.Entity;
import world.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRoomPositionManager {
    private static EntityRoomPositionManager instance = null;
    private Map hashmap = new HashMap<Room, List<Entity>>();

    private EntityRoomPositionManager() {}

    public EntityRoomPositionManager getInstance() {
        if(instance == null) instance = new EntityRoomPositionManager();
        return instance;
    }

    public void addRoom(Room room) {
        hashmap.put(room, new ArrayList<Entity>());
    }

    public void removeRoom(Room room) {
        hashmap.remove(room);
    }

    public boolean addEntityToRoom(Entity entity, Room room) {
        if(hashmap.get(room) != null) throw new RuntimeException("Room: " + room + " not found");

        return ((ArrayList<Entity>) hashmap.get(room)).add(entity);
    }

    public boolean transferEntityFromToRooms(Entity entity, Room fromRoom, Room toRoom) {
        if(hashmap.get(fromRoom) != null) throw new RuntimeException("fromRoom: " + fromRoom + " is null");
        if(hashmap.get(toRoom) != null) throw new RuntimeException("toRoom: " + fromRoom + " is null");

        ArrayList<Entity> entities = (ArrayList<Entity>) hashmap.get(fromRoom);
        if(!entities.remove(entity)) throw new RuntimeException("Entity: " + entity + " cant be found");
        entities = (ArrayList<Entity>) hashmap.get(toRoom);

        return entities.add(entity);
    }

    public List<Entity> getEntitiesInRoom(Room room) {
        if(hashmap.get(room) != null) throw new RuntimeException("Room: " + room + " not found");
        return (List<Entity>) hashmap.get(room);
    }
}

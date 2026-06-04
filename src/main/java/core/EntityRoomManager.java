package core;

import core.room.Room;
import entity.Entity;
import entity.Player;
import world.InteractableTile;

import java.util.*;

public class EntityRoomManager {
    private static final EntityRoomManager instance = new EntityRoomManager();
    private Map<Room, List<Entity>> hashmap = new HashMap<>();

    private EntityRoomManager() {}

    public static EntityRoomManager getInstance() {
        return instance;
    }

    public void addRoom(Room room) {
        if(room == null) throw new RuntimeException("Cannot add room as room is null");
        hashmap.putIfAbsent(room, new ArrayList<>());
    }

    public void clear() {
        hashmap.clear();
    }

    public void removeRoom(Room room) {
        hashmap.remove(room);
    }

    public Entity getPlayer() {
        for(Map.Entry<Room, List<Entity>> set : hashmap.entrySet()) {
            for(Entity e : set.getValue()) {
                if(e instanceof Player) return e;
            }
        }
        throw new RuntimeException("Player cannot be found");
    }

    public boolean addEntityToRoom(Entity entity, Room room) {
        if(!hashmap.containsKey(room)) throw new RuntimeException("Room: " + room + " not found");
        return hashmap.get(room).add(entity);
    }

    public boolean removeEntityFromRoom(Entity entity, Room room) {
        if(!hashmap.containsKey(room)) throw new RuntimeException("Room: " + room + " not found");
        return hashmap.get(room).remove(entity);
    }

    public boolean isEntityInRoom(Entity entity, Room room) {
        if(!hashmap.containsKey(room)) throw new RuntimeException("Room: " + room + " not found");
        return hashmap.get(room).contains(entity);
    }

    public boolean transferEntityFromToRoom(Entity entity, Room fromRoom, Room toRoom) {
        if(hashmap.get(fromRoom) == null) throw new RuntimeException("fromRoom: " + fromRoom + " is null");
        if(hashmap.get(toRoom) == null) throw new RuntimeException("toRoom: " + fromRoom + " is null");

        List<Entity> entities = hashmap.get(fromRoom);
        if(!entities.remove(entity)) throw new RuntimeException("Entity: " + entity + " cant be found");
        entities = hashmap.get(toRoom);

        return entities.add(entity);
    }

    public List<Entity> getEntitiesInRoom(Room room) {
        List<Entity> entities = hashmap.get(room);
        if(entities == null) throw new RuntimeException("Room: " + room + " not found");

        return Collections.unmodifiableList(entities);
    }

    public Room getRoomFromEntity(Entity entity) {
        for(Map.Entry<Room, List<Entity>> set : hashmap.entrySet()) {
            if(set.getValue().contains(entity)) return set.getKey();
        }
        throw new RuntimeException("Room cannot be found using entity: " + entity.name);
    }

    public List<Room> getRooms() {
        return hashmap.keySet().stream().toList();
    }

    public Room getPlayerRoom() {
        for(Map.Entry<Room, List<Entity>> set : hashmap.entrySet()) {
            for(Entity e : set.getValue()) {
                if(e instanceof Player) return set.getKey();
            }
        }
        throw new RuntimeException("Player cannot be found");
    }

    public List<InteractableTile> getInteractableTilesFromRoom(Room room) {
        for(Room r : hashmap.keySet()) {
            if(r == room) return r.getInteractableTiles();
        }
        return null;
    }

    public boolean removeInteractableTile(InteractableTile tile) {
        for(Room r : hashmap.keySet()) {
            if(r.getInteractableTiles().remove(tile)) return true;
        }
        return false;
    }

    public Room getRoomFromInteractableTile(InteractableTile tile) {
        for(Room r : hashmap.keySet()) {
            if(r.getInteractableTiles().contains(tile)) return r;
        }
        return null;
    }
}

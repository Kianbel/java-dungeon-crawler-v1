package core;

import util.Position;
import util.MAP;

import java.util.ArrayList;
import java.util.List;

public class DrunkardWalk extends Generator {

    private MAP[][] mapLayout = new MAP[MAP_HEIGHT][MAP_LENGTH];
    private Position walker = new Position(MAP_LENGTH/2, MAP_HEIGHT/2);
    private int currentRoomAmount = 1;

    @Override
    public MAP[][] start(int roomsAmount) {
        int attempts = 0;
        mapLayout[walker.y][walker.x] = MAP.SPAWN;
        spawnRoomPosition = new Position(walker.x, walker.y);

        int MAX_ATTEMPTS = 8;
        while(currentRoomAmount < roomsAmount && attempts < MAX_ATTEMPTS) {
            switch ((int) (Math.random() * 100 % 4)) {
                case 0 -> {
                    if (!putRoomAt(walker.x, walker.y - 2)) attempts++;
                }
                case 1 -> {
                    if (!putRoomAt(walker.x + 2, walker.y)) attempts++;
                }
                case 2 -> {
                    if (!putRoomAt(walker.x, walker.y + 2)) attempts++;
                }
                case 3 -> {
                    if (!putRoomAt(walker.x - 2, walker.y)) attempts++;
                }
            }
        }
        bossRoomPosition = new Position(walker.x, walker.y);

        makeSpecificRooms();

        return mapLayout;
    }

    private void makeSpecificRooms() {
        List<Position> transformableRooms = new ArrayList<>();

        for(int y = 0; y < MAP_HEIGHT; y++) {
            for(int x = 0; x < MAP_LENGTH; x++) {
                MAP mapTile = mapLayout[y][x];
                if(mapTile == null) continue;;
                if(mapTile == MAP.VCORRIDOR || mapTile == MAP.HCORRIDOR) continue;

                if(spawnRoomPosition.x == x && spawnRoomPosition.y == y) {
                    mapLayout[y][x] = MAP.SPAWN;
                    continue;
                }
                if(bossRoomPosition.x == x && bossRoomPosition.y == y) {
                    mapLayout[y][x] = MAP.BOSS;
                    continue;
                }

                transformableRooms.add(new Position(x, y));
            }
        }

        // --- TREASURE ROOM ---
        Position treasureRoomPos = transformableRooms.remove((int) (Math.random() * 100 % transformableRooms.size()));
        mapLayout[treasureRoomPos.y][treasureRoomPos.x] = MAP.TREASURE;

        // --- CLEAR ROOMS (HALF OF TOTAL ROOMS) ---
        int clearRoomAmount = transformableRooms.size()/2;
        for(int i = 0; i < clearRoomAmount; i++) {
            Position clearRoomPos = transformableRooms.remove((int) (Math.random() * 100 % transformableRooms.size()));
            mapLayout[clearRoomPos.y][clearRoomPos.x] = MAP.CLEAR;
        }
    }

    private boolean putRoomAt(int x, int y) {
        if(x < 0 || x >= MAP_LENGTH) return false;
        if(y < 0 || y >= MAP_HEIGHT) return false;

        if(x - walker.x < 0) mapLayout[walker.y][walker.x-1] = MAP.HCORRIDOR;
        else if(x - walker.x > 0) mapLayout[walker.y][walker.x+1] = MAP.HCORRIDOR;
        else if(y - walker.y < 0) mapLayout[walker.y-1][walker.x] = MAP.VCORRIDOR;
        else if(y - walker.y > 0) mapLayout[walker.y+1][walker.x] = MAP.VCORRIDOR;

        walker.x = x;
        walker.y = y;
        if(mapLayout[y][x] != MAP.INFESTED) {
            mapLayout[y][x] = MAP.INFESTED;
            currentRoomAmount++;
            return true;
        }
        return false;
    }
}
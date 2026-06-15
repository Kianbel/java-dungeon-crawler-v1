package core.room.generator;

import util.Position;
import util.MAP;
import util.Randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrunkardWalk extends Generator {

    private MAP[][] mapLayout;
    private Position walker;
    private int currentRoomAmount = 1;

    private final int MAP_HEIGHT;
    private final int MAP_LENGTH;

    public DrunkardWalk(int mapHeight, int mapLength) {
        MAP_HEIGHT = mapHeight;
        MAP_LENGTH = mapLength;
        mapLayout = new MAP[mapHeight][mapLength];
        walker = new Position(mapLength/2, mapHeight/2);
    }

    @Override
    public MAP[][] start(int roomsAmount) {
        mapLayout[walker.y][walker.x] = MAP.SPAWN;
        spawnRoomPosition = new Position(walker.x, walker.y);

        while(currentRoomAmount < roomsAmount) {
            switch (Randomizer.pick(1, 2, 3, 4)) {
                case 1 -> putRoomAt(walker.x, walker.y - 2);
                case 2 -> putRoomAt(walker.x + 2, walker.y);
                case 3 -> putRoomAt(walker.x, walker.y + 2);
                case 4 -> putRoomAt(walker.x - 2, walker.y);
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
                if(mapTile.isCorridor()) continue;

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
        Random random = new Random();
        Position treasureRoomPos = transformableRooms.remove(random.nextInt(transformableRooms.size()));
        mapLayout[treasureRoomPos.y][treasureRoomPos.x] = MAP.TREASURE;

        // --- CLEAR ROOMS (1/3 OF TOTAL ROOMS) ---
        int clearRoomAmount = transformableRooms.size()/3;
        for(int i = 0; i < clearRoomAmount; i++) {
            random = new Random();
            Position clearRoomPos = transformableRooms.remove(random.nextInt(transformableRooms.size()));
            mapLayout[clearRoomPos.y][clearRoomPos.x] = MAP.CLEAR;
        }
    }

    private void putRoomAt(int x, int y) {
        if(x < 0 || x >= MAP_LENGTH) return;
        if(y < 0 || y >= MAP_HEIGHT) return;

        if(x - walker.x < 0) mapLayout[walker.y][walker.x-1] = MAP.HCORRIDOR;
        else if(x - walker.x > 0) mapLayout[walker.y][walker.x+1] = MAP.HCORRIDOR;
        else if(y - walker.y < 0) mapLayout[walker.y-1][walker.x] = MAP.VCORRIDOR;
        else if(y - walker.y > 0) mapLayout[walker.y+1][walker.x] = MAP.VCORRIDOR;

        walker.x = x;
        walker.y = y;
        if(mapLayout[y][x] != MAP.INFESTED) {
            mapLayout[y][x] = MAP.INFESTED;
            currentRoomAmount++;
        }
    }
}
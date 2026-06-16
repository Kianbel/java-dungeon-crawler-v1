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
        for(int y = 0; y < MAP_HEIGHT; y++) {
            for(int x = 0; x < MAP_LENGTH; x++) {
                MAP mapTile = mapLayout[y][x];
                if(mapTile == null) continue;;
                if(mapTile.isCorridor()) continue;

                if(spawnRoomPosition.equals(x,y)) {
                    mapLayout[y][x] = MAP.SPAWN;
                }
                else if(bossRoomPosition.equals(x,y)) {
                    mapLayout[y][x] = MAP.BOSS;
                }
            }
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
        if(mapLayout[y][x] != MAP.NORMAL) {
            mapLayout[y][x] = MAP.NORMAL;
            currentRoomAmount++;
        }
    }
}
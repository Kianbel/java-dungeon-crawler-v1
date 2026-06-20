package core.room.generator;

import util.Position;
import util.MAP;
import util.Randomizer;

import java.util.*;

public class DrunkardWalk extends Generator {

    private MAP[][] mapLayout;
    private Position walker;
    private int currentRoomAmount = 1;

    private final int MAP_HEIGHT;
    private final int MAP_LENGTH;

    public DrunkardWalk(int mapHeight, int mapLength) {
        MAP_HEIGHT = mapHeight;
        MAP_LENGTH = mapLength;
        walker = new Position(mapLength/2, mapHeight/2);
        mapLayout = new MAP[mapHeight][mapLength];

        for(MAP[] row : mapLayout) {
            Arrays.fill(row, MAP.EMPTY);
        }
    }

    @Override
    public MAP[][] start(int roomsAmount) {
        mapLayout[walker.y][walker.x] = MAP.SPAWN;
        spawnRoomPosition = new Position(walker.x, walker.y);

        while(currentRoomAmount < roomsAmount) {
            switch (Randomizer.pick(1, 2, 3, 4)) {
                case 1 -> { // North
                    if (walker.y - 2 >= 0) {
                        if (mapLayout[walker.y - 1][walker.x] != MAP.EMPTY && mapLayout[walker.y - 1][walker.x].isCorridor()) {
                            walker.y -= 2;
                        } else {
                            putRoomAt(walker.x, walker.y - 2);
                        }
                    }
                }
                case 2 -> { // East
                    if (walker.x + 2 < MAP_LENGTH) {
                        if (mapLayout[walker.y][walker.x + 1] != MAP.EMPTY && mapLayout[walker.y][walker.x + 1].isCorridor()) {
                            walker.x += 2;
                        } else {
                            putRoomAt(walker.x + 2, walker.y);
                        }
                    }
                }
                case 3 -> { // South
                    if (walker.y + 2 < MAP_HEIGHT) {
                        if (mapLayout[walker.y + 1][walker.x] != MAP.EMPTY && mapLayout[walker.y + 1][walker.x].isCorridor()) {
                            walker.y += 2;
                        } else {
                            putRoomAt(walker.x, walker.y + 2);
                        }
                    }
                }
                case 4 -> { // West
                    if (walker.x - 2 >= 0) {
                        if (mapLayout[walker.y][walker.x - 1] != MAP.EMPTY && mapLayout[walker.y][walker.x - 1].isCorridor()) {
                            walker.x -= 2;
                        } else {
                            putRoomAt(walker.x - 2, walker.y);
                        }
                    }
                }
            }
        }
        bossRoomPosition = new Position(walker.x, walker.y);

        makeSpecificRooms();

        return mapLayout;
    }

    private void makeSpecificRooms() {
        List<Position> roomsToChange = new ArrayList<>();

        for(int y = 0; y < MAP_HEIGHT; y++) {
            for(int x = 0; x < MAP_LENGTH; x++) {
                MAP mapTile = mapLayout[y][x];
                if(mapTile == MAP.EMPTY) continue;
                if(mapTile.isCorridor()) continue;

                if(spawnRoomPosition.equals(x,y)) {
                    mapLayout[y][x] = MAP.SPAWN;
                }
                else if(bossRoomPosition.equals(x,y)) {
                    mapLayout[y][x] = MAP.BOSS;
                }
                else roomsToChange.add(new Position(x,y));
            }
        }

        // --- TREASURE ROOMS ---
        Random random = new Random();
        Position treasureRoomPos = roomsToChange.remove(random.nextInt(roomsToChange.size()));
        mapLayout[treasureRoomPos.y][treasureRoomPos.x] = MAP.TREASURE;

        // --- PUT NORMAL ROOMS ---
        final double NORMAL_ROOM_AMOUNT = 0.1 * roomsToChange.size();
        for(int i = 0; i < NORMAL_ROOM_AMOUNT; i++) {
            Position normalRoomPos = roomsToChange.remove(random.nextInt(roomsToChange.size()));
            mapLayout[normalRoomPos.y][normalRoomPos.x] = MAP.NORMAL;
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
        if(mapLayout[y][x] != MAP.EXTRA) {
            mapLayout[y][x] = MAP.EXTRA;
            currentRoomAmount++;
        }
    }
}
package core.room.generator;

import core.GameManager;
import util.Position;
import util.MAP;
import util.Randomizer;

import java.util.*;

public class DrunkardWalk extends Generator {
    private Random random = new Random();
    private MAP[][] mapLayout;
    private Position walker;
    private int currentRoomAmount = 1;

    private final int MAP_HEIGHT;
    private final int MAP_LENGTH;

    public DrunkardWalk(int mapHeight, int mapLength) {
        MAP_HEIGHT = mapHeight;
        MAP_LENGTH = mapLength;
        mapLayout = new MAP[mapHeight][mapLength];
    }

    @Override
    public MAP[][] start(int roomsAmount) {
        while (true) {
            for (MAP[] row : mapLayout) {
                Arrays.fill(row, MAP.EMPTY);
            }

            walker = new Position(MAP_LENGTH / 2, MAP_HEIGHT / 2);
            currentRoomAmount = 1;

            mapLayout[walker.y][walker.x] = MAP.SPAWN;
            spawnRoomPosition = new Position(walker.x, walker.y);

            while (currentRoomAmount < roomsAmount) {
                switch (Randomizer.pick(1, 2, 3, 4)) {
                    case 1 -> { // North
                        if (walker.y - 2 >= 0) {
                            if (mapLayout[walker.y - 1][walker.x] != MAP.EMPTY && mapLayout[walker.y - 1][walker.x].isCorridor()) {
                                // Step skipped to maintain pathing logic
                            } else {
                                putRoomAt(walker.x, walker.y - 2);
                            }
                        }
                    }
                    case 2 -> { // East
                        if (walker.x + 2 < MAP_LENGTH) {
                            if (mapLayout[walker.y][walker.x + 1] != MAP.EMPTY && mapLayout[walker.y][walker.x + 1].isCorridor()) {
                                // Step skipped to maintain pathing logic
                            } else {
                                putRoomAt(walker.x + 2, walker.y);
                            }
                        }
                    }
                    case 3 -> { // South
                        if (walker.y + 2 < MAP_HEIGHT) {
                            if (mapLayout[walker.y + 1][walker.x] != MAP.EMPTY && mapLayout[walker.y + 1][walker.x].isCorridor()) {
                                // Step skipped to maintain pathing logic
                            } else {
                                putRoomAt(walker.x, walker.y + 2);
                            }
                        }
                    }
                    case 4 -> { // West
                        if (walker.x - 2 >= 0) {
                            if (mapLayout[walker.y][walker.x - 1] != MAP.EMPTY && mapLayout[walker.y][walker.x - 1].isCorridor()) {
                                // Step skipped to maintain pathing logic
                            } else {
                                putRoomAt(walker.x - 2, walker.y);
                            }
                        }
                    }
                }
            }
            bossRoomPosition = new Position(walker.x, walker.y);

            int dx = Math.abs(spawnRoomPosition.x - bossRoomPosition.x);
            int dy = Math.abs(spawnRoomPosition.y - bossRoomPosition.y);

            // In your grid system, steps move by 2 units.
            // Rooms are adjacent if one delta is exactly 2 and the other is 0.
            boolean isAdjacentOrSame = (dx == 2 && dy == 0) || (dx == 0 && dy == 2) || (dx == 0 && dy == 0);

            if (!isAdjacentOrSame) {
                break; // Valid layout found! Exit the retry loop.
            }
        }

        makeSpecificRooms();
        return mapLayout;
    }

    private void makeSpecificRooms() {
        List<Position> roomsToChange = new ArrayList<>();

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_LENGTH; x++) {
                MAP mapTile = mapLayout[y][x];
                if (mapTile == MAP.EMPTY || mapTile.isCorridor()) continue;

                if (spawnRoomPosition.equals(x, y)) {
                    mapLayout[y][x] = MAP.SPAWN;
                } else if (bossRoomPosition.equals(x, y)) {
                    if(GameManager.getInstance().getCurrentFloor() % 5 == 0) mapLayout[y][x] = MAP.BOSS;
                    else mapLayout[y][x] = MAP.STAIR;
                } else {
                    roomsToChange.add(new Position(x, y));
                }
            }
        }

        if (!roomsToChange.isEmpty()) {
            int changeableRoomsAmount = roomsToChange.size();
            int treasureRoomsAmount = Math.toIntExact(Math.round(changeableRoomsAmount * 0.2));
            int specialRoomsAmount = Math.toIntExact(Math.round(changeableRoomsAmount * 0.4));

            // --- TREASURE ROOMS ---
            for(int i = 0; i < treasureRoomsAmount; i++) {
                Position treasureRoomPos = roomsToChange.remove(random.nextInt(roomsToChange.size()));
                mapLayout[treasureRoomPos.y][treasureRoomPos.x] = MAP.TREASURE;
            }

            // --- SPECIAL ROOMS ---
            for(int i = 0; i < specialRoomsAmount; i++) {
                Position specialRoomPos = roomsToChange.remove(random.nextInt(roomsToChange.size()));
                mapLayout[specialRoomPos.y][specialRoomPos.x] = MAP.SPECIAL;
            }
        }
    }

    private void putRoomAt(int x, int y) {
        if (x < 0 || x >= MAP_LENGTH || y < 0 || y >= MAP_HEIGHT) return;

        if (x - walker.x < 0) mapLayout[walker.y][walker.x - 1] = MAP.HCORRIDOR;
        else if (x - walker.x > 0) mapLayout[walker.y][walker.x + 1] = MAP.HCORRIDOR;
        else if (y - walker.y < 0) mapLayout[walker.y - 1][walker.x] = MAP.VCORRIDOR;
        else if (y - walker.y > 0) mapLayout[walker.y + 1][walker.x] = MAP.VCORRIDOR;

        walker.x = x;
        walker.y = y;

        if (mapLayout[y][x] != MAP.NORMAL) {
            mapLayout[y][x] = MAP.NORMAL;
            currentRoomAmount++;
        }
    }
}
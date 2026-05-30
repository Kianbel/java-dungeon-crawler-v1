package core;

import util.Position;
import util.TILE;

public class DrunkardWalk extends Generator {

    private TILE[][] mapLayout = new TILE[MAP_HEIGHT][MAP_LENGTH];
    private Position walker = new Position(MAP_LENGTH/2, MAP_HEIGHT/2);
    private int currentRoomAmount = 1;

    @Override
    public TILE[][] start(int roomsAmount) {
        int attempts = 0;
        mapLayout[walker.x][walker.y] = TILE.ROOM;
        firstRoomPosition = new Position(walker.x, walker.y);

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
        lastRoomPosition = new Position(walker.x, walker.y);
        System.out.println("maxRooms: " + roomsAmount + "| roomsMade: " + currentRoomAmount);
        return mapLayout;
    }

    private boolean putRoomAt(int x, int y) {
        if(x < 0 || x >= MAP_LENGTH) return false;
        if(y < 0 || y >= MAP_HEIGHT) return false;

        if(x - walker.x < 0) mapLayout[walker.y][walker.x-1] = TILE.HCORRIDOR;
        else if(x - walker.x > 0) mapLayout[walker.y][walker.x+1] = TILE.HCORRIDOR;
        else if(y - walker.y < 0) mapLayout[walker.y-1][walker.x] = TILE.VCORRIDOR;
        else if(y - walker.y > 0) mapLayout[walker.y+1][walker.x] = TILE.VCORRIDOR;

        walker.x = x;
        walker.y = y;
        if(mapLayout[y][x] != TILE.ROOM) {
            mapLayout[y][x] = TILE.ROOM;
            currentRoomAmount++;
            return true;
        }
        return false;
    }
}
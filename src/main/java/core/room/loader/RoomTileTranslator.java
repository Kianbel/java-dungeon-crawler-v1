package core.room.loader;

import util.TILE;

import java.io.CharConversionException;
import java.util.HashMap;
import java.util.Map;

public class RoomTileTranslator {
    private Map<Character, TILE> hashmap;

    public RoomTileTranslator() {
        hashmap = new HashMap<>(Map.of(
                ' ', TILE.EMPTY,
                'w', TILE.WALL,
                'd', TILE.DOOR,
                'f', TILE.FLOOR,
                'p', TILE.PUDDLE,
                'c', TILE.COBWEB,
                'g', TILE.GRASS,
                'b', TILE.BARREL
        ));
    }

    public TILE translate(char c) {
        return hashmap.get(Character.toLowerCase(c));
    }
}

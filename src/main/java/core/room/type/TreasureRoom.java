package core.room.type;

import core.room.loader.RoomLayoutRegistry;
import item.armor.*;
import item.key.TreasureRoomKey;
import item.weapon.*;
import util.Position;
import util.TILE;
import world.DroppedItem;
import world.InteractableTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureRoom extends Room {
    public TreasureRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(TreasureRoom.class);
        super(layout, minimapPosition);

        handleKeySpawn();
    }

    public void handleKeySpawn() {
        List<Position> possibleKeysPos = new ArrayList<>();
        for(int y = 0; y < layout.length; y++) {
            for(int x = 0; x < layout[0].length; x++) {
                if(layout[y][x] == TILE.POSSIBLE_KEY) possibleKeysPos.add(new Position(x,y));
            }
        }
        for(Position p : possibleKeysPos) {
            this.layout[p.y][p.x] = TILE.FLOOR;
        }

        Position keyPos = possibleKeysPos.remove(new Random().nextInt(possibleKeysPos.size()));
        InteractableTile key = new DroppedItem(keyPos, new TreasureRoomKey());
        addInteractableTile(key);
    }
}

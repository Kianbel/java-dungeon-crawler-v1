package core.room.type;

import core.room.loader.RoomLayoutRegistry;
import util.Position;
import util.TILE;
import weapon.IronBlade;
import world.*;

public class TreasureRoom extends Room {
    public TreasureRoom(int height, int length, Position minimapPosition) {
        final double DECORATED_ROOM_CHANCE = 0.4;

        TILE[][] layout = null;
        if(Math.random() <= DECORATED_ROOM_CHANCE) {
            layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(TreasureRoom.class);
        }
        super(layout, minimapPosition, height, length);

        Position chestPosition;
        if(layout == null) {
            chestPosition = new Position(length/2, height/2);
        }
        else {
            height = layout.length;
            length = layout[0].length;
            chestPosition = new Position(length/2, height/2);
        }
        this.layout[height/2][length/2] = TILE.FLOOR;

        InteractableTile chestDrop;
        double random = Math.random();
        // TODO: investigate chest sometimes not dropping weapons
        chestDrop = new DroppedWeapon(chestPosition, new IronBlade());
//        if(random <= 0.4) chestDrop = new DroppedWeapon(chestPosition, new IronBlade());
//        else if(random <= 0.8) chestDrop = new Heart(chestPosition, 20);
//        else chestDrop = new Coin(chestPosition, 20);

        InteractableTile chest = new Chest(chestPosition, chestDrop);
        addInteractableTile(chest);
    }
}

package core.room;

import util.Position;
import weapon.IronBlade;
import world.*;

public class TreasureRoom extends Room {
    public TreasureRoom(int height, int length, Position minimapPosition) {
        super(height, length, minimapPosition);

        Position chestPosition = new Position(length/2, height/2);
        InteractableTile chestDrop;
        double random = Math.random();
        if(random <= 0.4) chestDrop = new DroppedWeapon(chestPosition, new IronBlade());
        else if(random <= 0.8) chestDrop = new Heart(chestPosition, 20);
        else chestDrop = new Coin(chestPosition, 20);

        InteractableTile chest = new Chest(chestPosition, chestDrop);
        addInteractableTile(chest);
    }
}

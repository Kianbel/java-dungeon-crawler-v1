package core.room.type;

import core.EntityRoomManager;
import core.Game;
import core.room.loader.RoomLayoutRegistry;
import entity.Player;
import item.weapon.IronBlade;
import item.weapon.Weapon;
import util.Position;
import util.TILE;
import world.*;

public class SpawnRoom extends Room {

    public SpawnRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(SpawnRoom.class);
        super(layout, minimapPosition);

        addInteractableTile(new Staircase(new Position(5, 3)));
        addInteractableTile(new Chest(new Position(6,3)));
    }

    @Override
    public void populateWithEntities() {
        super.populateWithEntities();

        if(Game.getInstance().getPlayer() == null) {
            Player player = new Player(new Position(length/2, height/2));
            EntityRoomManager.getInstance().addEntityToRoom(player, this);
            Game.getInstance().setPlayer(player);
        }
    }
}

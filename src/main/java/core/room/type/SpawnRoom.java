package core.room.type;

import core.EntityRoomManager;
import core.GameManager;
import core.room.loader.RoomLayoutRegistry;
import entity.Player;
import util.Position;
import util.TILE;
import world.NormalChest;
import world.Staircase;

public class SpawnRoom extends Room {

    public SpawnRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(SpawnRoom.class);

        super(layout, minimapPosition);

        addInteractableTile(new Staircase(new Position(5, 3)));
//        addInteractableTile(new NormalChest(new Position(6,3)));
//        addInteractableTile(new NormalChest(new Position(7,3)));
//        addInteractableTile(new NormalChest(new Position(8,3)));
//        addInteractableTile(new NormalChest(new Position(6,4)));
//        addInteractableTile(new NormalChest(new Position(7,4)));
//        addInteractableTile(new NormalChest(new Position(8,4)));
//        addInteractableTile(new NormalChest(new Position(9,4)));
    }

    @Override
    public void populateWithEntities() {
        if(GameManager.getInstance().getPlayer() == null) {
            Player player = new Player(new Position(length/2, height/2));
            EntityRoomManager.getInstance().addEntityToRoom(player, this);
            GameManager.getInstance().setPlayer(player);
        }

//        EntitySpawner entitySpawner = new EntitySpawner(this);
//        entitySpawner.spawnMonstersAmount(Bat::new, 1);
    }
}

package entity.boss;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Monster;
import gui.GUIManager;
import item.weapon.Weapon;
import javafx.scene.paint.Color;
import util.Position;
import world.Staircase;

import java.util.ArrayList;
import java.util.List;

public abstract class Boss extends Monster {
    protected final List<Entity> summonedEntities = new ArrayList<>();

    public Boss(String name, int health, int armor, Weapon weapon, Position position) {
        super(name, health, armor, weapon, position);
    }

    /** Override to add chest drop
     */
    @Override
    public void die() {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);

        for(Entity e : summonedEntities) {
            e.die();
        }
        summonedEntities.clear();

        GUIManager.getInstance().triggerColorFlash(Color.WHITESMOKE, 500);
        Position playerPosition = EntityRoomManager.getInstance().getPlayer().position;

        Position stairSpawnPos = new Position(currentRoom.length/2, currentRoom.height/2);
        if(playerPosition.equals(stairSpawnPos)) stairSpawnPos.x++;
        currentRoom.addInteractableTile(new Staircase(stairSpawnPos));

        super.die();
    }

    public void addSummon(Entity summon) {
        summonedEntities.add(summon);
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        EntityRoomManager.getInstance().addEntityToRoom(summon, currentRoom);
    }
}

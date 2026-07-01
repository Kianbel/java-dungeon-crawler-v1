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

    @Override
    public void die() {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);

        for(Entity e : summonedEntities) {
            if(EntityRoomManager.getInstance().isEntityInRoom(e, currentRoom)) e.die();
        }
        summonedEntities.clear();

        GUIManager.getInstance().triggerColorFlash(Color.WHITESMOKE, 500);

        // TODO: add chant sfx
        // TODO: add end screen after killing boss
        // TODO; add boss drop like fire gem for lore, like the hero needs the gem for
        //      making world peace or whatever. and then say something like, the dungeon
        //      became safe, for now...

        super.die();
    }

    public void addSummon(Entity summon) {
        summonedEntities.add(summon);
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        EntityRoomManager.getInstance().addEntityToRoom(summon, currentRoom);
    }
}

package entity;

import core.EntityRoomManager;
import gui.GUIManager;
import item.Fist;
import item.Weapon;
import javafx.scene.paint.Color;
import util.Position;
import world.Room;

public class Player extends Entity {
    public Player(Position position) {
        final String NAME = "Player";
        final int HEALTH = 100;
        final int ARMOR = 0;
        final Weapon WEAPON = new Fist();

        super(NAME, HEALTH, ARMOR, WEAPON, position);
    }

    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            int inflictedDamage = weapon.getCalculatedAttackDamage();
            targetEntity.hurt(inflictedDamage, this);

            GUIManager.getInstance().printLog(name + " attacked " + targetEntity.name + " for " + inflictedDamage + "HP.", Color.RED);

            if(!targetEntity.isAlive()) {
                GUIManager.getInstance().printLog("You killed " + targetEntity.name + ".", Color.GREEN);
            }
        }
        else throw new RuntimeException(this + " cannot attack " + targetEntity + " as target is not in same room");
    }

    @Override
    public void die() {
        health = 0;
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        EntityRoomManager.getInstance().removeEntityFromRoom(this, currentRoom);

        GUIManager.getInstance().printLog("You died!", Color.RED);
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(attacker, currentRoom)) {
            health -= damage;
            GUIManager.getInstance().printLog(attacker.name + " hurt you for " + damage + "HP.", Color.RED);
            if(health <= 0) die();
        }
    }
}

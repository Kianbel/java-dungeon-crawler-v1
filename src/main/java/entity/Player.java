package entity;

import core.EntityRoomManager;
import gui.GUIManager;
import weapon.Weapon;
import weapon.WoodenSword;
import javafx.scene.paint.Color;
import util.Position;
import core.Room;

import java.util.List;

public class Player extends Entity {
    public int coins = 0;
    public int hpPotions = 0;

    public Player(Position position) {
        final String NAME = "Player";
        final int HEALTH = 100;
        final int ARMOR = 0;
        final Weapon WEAPON = new WoodenSword();

        super(NAME, HEALTH, ARMOR, WEAPON, position);

        activateGodMode();

        GUIManager.getInstance().setHP(HEALTH);
        GUIManager.getInstance().setArmor(ARMOR);
        GUIManager.getInstance().setWeapon(WEAPON.name);
        GUIManager.getInstance().setCoins(0);
        GUIManager.getInstance().setPotions(0);
    }

    public void activateGodMode() {
        System.out.println("!!! PLAYER IN GOD MODE !!!");
        armor = 1000;
    }

    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            int inflictedDamage = weapon.getCalculatedAttackDamage();
            targetEntity.hurt(inflictedDamage, this);

            GUIManager.getInstance().printLog(String.format("You attacked %s for %sHP. (Remaining: %sHP)", targetEntity.name, inflictedDamage, targetEntity.health), Color.RED);

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
            damage -= armor;
            if(damage < 0) damage = 0;
            health -= damage;
            if(health < 0) health = 0;

            GUIManager.getInstance().printLog(attacker.name + " hurt you for " + damage + "HP.", Color.RED);
            GUIManager.getInstance().setHP(health);

            if(health == 0) die();
        }
    }

    public void handleMove(Position unitPos) {
        if(stunCounter > 0) {
            GUIManager.getInstance().printLog("Can't move! You are stunned for " + stunCounter + " more turns.", Color.YELLOW);
            stunCounter--;
            return;
        }

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);

        Position targetPosition = new Position(position.x+unitPos.x, position.y+ unitPos.y);
        for(Entity e : entities) {
            if(e == this) continue;
            if(e.position.x == targetPosition.x && e.position.y == targetPosition.y) {
                attack(e);
                return;
            }
        }
        walk(unitPos);
    }

    @Override
    public void stun(int moveCount) {
        if(stunCounter > 0) return;
        stunCounter = moveCount;
        GUIManager.getInstance().printLog("You got stunned!", Color.YELLOW);
    }
}

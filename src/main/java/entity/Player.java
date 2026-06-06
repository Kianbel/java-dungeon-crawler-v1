package entity;

import core.EntityRoomManager;
import gui.GUIManager;
import gui.UITheme;
import weapon.Weapon;
import weapon.AncientSword;
import util.Position;
import core.room.Room;
import world.InteractableTile;

import java.util.List;

public class Player extends Entity {
    public int coins = 0;
    public int hpPotions = 100;
    public int hunger = 100;

    final int HUNGER_DECREASE_MOVE_COOLDOWN = 20;
    private int hungerDecreaseCounter = HUNGER_DECREASE_MOVE_COOLDOWN;
    final int HUNGER_DECREASE_AMOUNT = 5;

    public Player(Position position) {
        final String NAME = "Player";
        final int HEALTH = 100;
        final int ARMOR = 0;
        final Weapon WEAPON = new AncientSword();

        super(NAME, HEALTH, ARMOR, WEAPON, position);

        activateGodMode();

        GUIManager.getInstance().setHP(HEALTH);
        GUIManager.getInstance().setHunger(hunger);
        GUIManager.getInstance().setArmor(ARMOR);
        GUIManager.getInstance().setWeapon(WEAPON);
        GUIManager.getInstance().setCoins(0);
        GUIManager.getInstance().setPotions(0);
    }

    public void activateGodMode() {
        System.out.println("!!! PLAYER IN GOD MODE !!!");
        armor = 1000;
        hungerDecreaseCounter = 9999999;
        hunger = 99999999;
    }

    public void handleMove(Position unitPos) {
        if(stunCounter > 0) {
            GUIManager.getInstance().printLog("[PLAYER]: Can't move! You are stunned for " + stunCounter + " more turns.", UITheme.LOG_PLAYER_ACTION);
            stunCounter--;
            return;
        }

        handleHungerDecrease();

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);

        Position targetPosition = new Position(position.x+unitPos.x, position.y+ unitPos.y);
        for(Entity e : entities) {
            if(e == this) continue;
            if(e.position.equals(targetPosition)) {
                attack(e);
                return;
            }
        }

        List<InteractableTile> interactableTiles = currentRoom.getInteractableTiles();
        for(InteractableTile tile: interactableTiles) {
            if(tile.roomLayoutPosition.equals(targetPosition) && tile.isSolid) {
                tile.onEntityBump(this);
                return;
            }
        }

        walk(unitPos);
    }

    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            int inflictedDamage = weapon.getCalculatedAttackDamage();
            targetEntity.hurt(inflictedDamage, this);

            GUIManager.getInstance().printLog(String.format("[PLAYER]: You attacked %s for %sHP. (Remaining: %sHP)", targetEntity.name, inflictedDamage, targetEntity.health), UITheme.LOG_PLAYER_ACTION);

            if(!targetEntity.isAlive()) {
                GUIManager.getInstance().printLog("[PLAYER]: You killed " + targetEntity.name + ".", UITheme.LOG_PLAYER_ACTION);
            }
        }
        else throw new RuntimeException(this + " cannot attack " + targetEntity + " as target is not in same room");
    }

    @Override
    public void die() {
        health = 0;
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        EntityRoomManager.getInstance().removeEntityFromRoom(this, currentRoom);

        GUIManager.getInstance().printLog("[PLAYER]: You died!", UITheme.LOG_MONSTER_ACTION);
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(attacker, currentRoom)) {
            damage -= armor;
            if(damage < 0) damage = 0;
            health -= damage;
            if(health < 0) health = 0;

            GUIManager.getInstance().printLog("[MONSTER]: " + attacker.name + " hurt you for " + damage + "HP.", UITheme.LOG_MONSTER_ACTION);
            GUIManager.getInstance().setHP(health);
//            GUIManager.getInstance().triggerHurtFlash();

            if(health == 0) die();
        }
    }

    @Override
    public void stun(int moveCount) {
        if(stunCounter > 0) return;
        stunCounter = moveCount;
        GUIManager.getInstance().printLog("[PLAYER]: You got stunned!", UITheme.LOG_PLAYER_ACTION);
    }

    public void setHealth(int health) {
        this.health = Math.clamp(health, 0, 100);
        GUIManager.getInstance().setHP(this.health);
    }

    public void setArmor(int armor) {
        this.armor = Math.clamp(armor, 0, 10);
        GUIManager.getInstance().setArmor(this.armor);
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        GUIManager.getInstance().setWeapon(weapon);
    }

    public void setCoins(int coins) {
        this.coins = coins;
        GUIManager.getInstance().setCoins(coins);
    }

    public void setHpPotions(int hpPotions) {
        this.hpPotions = hpPotions;
        GUIManager.getInstance().setPotions(hpPotions);
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
        GUIManager.getInstance().setHunger(hunger);
    }

    private void handleHungerDecrease() {
        if(hungerDecreaseCounter > 0) {
            hungerDecreaseCounter--;
            return;
        }
        hunger -= HUNGER_DECREASE_AMOUNT;
        if(hunger < 0) {
            hunger = 0;
            setHealth(health - 5);
            GUIManager.getInstance().printLog("[PLAYER]: You are starving", UITheme.LOG_CRITICAL);
        }
        GUIManager.getInstance().setHunger(hunger);

        hungerDecreaseCounter = HUNGER_DECREASE_MOVE_COOLDOWN;
    }
}

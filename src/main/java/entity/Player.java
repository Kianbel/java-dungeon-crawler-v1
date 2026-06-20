package entity;

import core.EntityRoomManager;
import entity.monster.Monster;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.HealthPotion;
import item.Item;
import item.key.LevelKey;
import item.key.RoomKey;
import item.weapon.DevOneShotWeapon;
import javafx.scene.paint.Color;
import item.weapon.Weapon;
import item.weapon.AncientSword;
import util.Position;
import core.room.type.Room;
import world.InteractableTile;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    public int coins = 0;
    public int hunger = 100;
    public boolean isDead = false;
    private List<Item> inventory = new ArrayList<>();

    private final int HUNGER_DECREASE_MOVE_COOLDOWN = 50;
    private int hungerDecreaseCounter = HUNGER_DECREASE_MOVE_COOLDOWN;
    private final int HUNGER_DECREASE_AMOUNT = 5;

    private int putTravelledPositionCtr = 0;

    private boolean godModeEnabled = false;

    public Player(Position position) {
        super("Player", 100, 0, new AncientSword(), position);

        setIlluminated(true);
        setIlluminationRange(8);

        setHealth(health);
        setHunger(hunger);
        setArmor(armor);
        setWeapon(weapon);
        setCoins(coins);

        addItemToInventory(new LevelKey());
        addItemToInventory(new RoomKey());
        addItemToInventory(new HealthPotion());
    }

    public void toggleGodMode() {
        System.out.println("!!! PLAYER IN GOD MODE !!!");

        godModeEnabled = !godModeEnabled;
        if(godModeEnabled) {
            GUIManager.getInstance().printDevLog("GOD MODE ENABLED");
            armor = 1000;
            hungerDecreaseCounter = 9999999;
            hunger = 99999999;
            setWeapon(new DevOneShotWeapon());
            overrideColor(Color.RED);
        }
        else {
            GUIManager.getInstance().printDevLog("DISABLED GOD MODE");
            armor = 0;
            hungerDecreaseCounter = HUNGER_DECREASE_MOVE_COOLDOWN;
            hunger = 100;
            setWeapon(new AncientSword());
            resetColor();
        }
    }

    @Override
    public void walk(Position unitPos) {
        super.walk(unitPos);
    }

    public void handleMove(Position unitPos) {
        if(stunCounter > 0) {
            GUIManager.getInstance().triggerTextPopup("stunned", Color.YELLOW, position);
            GUIManager.getInstance().printLog("Can't move! You are stunned for " + stunCounter + " more turns.", UITheme.LOG_PLAYER_ACTION);
            stunCounter--;
            return;
        }

        handleHungerDecrease();

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);

        Position targetPosition = position.add(unitPos);
        for(int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if(e == this) continue;

            if(e.position.equals(targetPosition)) {
                if(e instanceof Monster m) {
                    attack(m);
                    return;
                }
            }
        }

        List<InteractableTile> interactableTiles = currentRoom.getInteractableTiles();
        for(InteractableTile tile: interactableTiles) {
            if(tile.roomLayoutPosition.equals(targetPosition) && tile.isSolid) {
                GUIManager.getInstance().triggerAttackAnimation(this, tile.roomLayoutPosition);
                tile.onEntityBump(this);
                return;
            }
        }

        // for handling darkness (refer GameController)
        putTravelledPositionCtr++;
        if(putTravelledPositionCtr > 3) {
            currentRoom.addPlayerTravelledPosition(position);
            putTravelledPositionCtr = 0;
        }
        walk(unitPos);
    }

    public void attack(Entity targetEntity) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        if(EntityRoomManager.getInstance().isEntityInRoom(targetEntity, currentRoom)) {
            int inflictedDamage = weapon.getCalculatedAttackDamage();
            targetEntity.hurt(inflictedDamage, this);

            GUIManager.getInstance().triggerAttackAnimation(this, targetEntity);

            if(!targetEntity.isAlive()) {
                GUIManager.getInstance().printLog("You killed " + targetEntity.name + ".", UITheme.LOG_PLAYER_ACTION);
            }
        }
        else throw new RuntimeException(this + " cannot attack " + targetEntity + " as target is not in same room");
    }

    @Override
    public void die() {
        health = 0;
        isDead = true;
        GUIManager.getInstance().printLog("You died!", UITheme.LOG_MONSTER_ACTION);
    }

    @Override
    public void hurt(int damage) {
        hurt(damage, null);
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        damage -= armor;
        if(damage < 0) damage = 0;
        health -= damage;
        if(health < 0) health = 0;

        if(damage == 0) {
            GUIManager.getInstance().triggerTextPopup("miss", UITheme.MISS, position);
            return;
        }

        if(attacker != null) {
            if(attacker.weapon.isCritical(damage)) GUIManager.getInstance().triggerTextPopup(damage+"", UITheme.CRITICAL_DAMAGE, position);
            else GUIManager.getInstance().triggerTextPopup(damage+"", UITheme.PLAYER_TAKE_DAMAGE, position);
        }
        else {
            GUIManager.getInstance().triggerTextPopup(damage+"", UITheme.PLAYER_TAKE_DAMAGE, position);
        }

        GUIManager.getInstance().triggerScreenShake(4, 200);
        GUIManager.getInstance().setHP(health);

        if(health == 0) die();
    }

    @Override
    public void stun(int moveCount) {
        if(stunCounter > 0) return;
        stunCounter = moveCount;
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
            GUIManager.getInstance().printLog("You are starving", Color.ORANGE);
        }
        GUIManager.getInstance().setHunger(hunger);

        hungerDecreaseCounter = HUNGER_DECREASE_MOVE_COOLDOWN;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItemToInventory(Item item) {
        inventory.add(item);
        GUIManager.getInstance().updateInventory(inventory);
    }

    public void removeItemFromInventory(Item item) {
        inventory.remove(item);
        GUIManager.getInstance().updateInventory(inventory);
    }
}

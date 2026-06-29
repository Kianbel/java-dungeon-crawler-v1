package entity;

import core.EntityRoomManager;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import item.Item;
import item.armor.Armor;
import item.armor.BareLeatherTunic;
import item.armor.DevArmor;
import item.food.Food;
import item.heal.HealingPotion;
import item.weapon.DevOneShotWeapon;
import javafx.scene.paint.Color;
import item.weapon.Weapon;
import item.weapon.AncientSword;
import util.Position;
import core.room.type.Room;
import world.InteractableTile;
import world.SpikeTrap;
import world.WoodenDoor;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    public int coins = 0;
    public int hunger = 100;
    public Armor armor = new BareLeatherTunic();
    public boolean isDead = false;
    private List<Item> inventory = new ArrayList<>();

    private final int DEFAULT_ILLUMINATION_RANGE = 3;

    private final int DEFAULT_HUNGER_DECREASE_MOVES_COOLDOWN = 50;
    public int hungerDecreaseMovesCooldown = DEFAULT_HUNGER_DECREASE_MOVES_COOLDOWN;
    public int hungerDecreaseCounter = hungerDecreaseMovesCooldown;

    private final int NATURAL_HEALING_MOVE_COOLDOWN = 15;
    private int naturalHealingDecreaseCounter = NATURAL_HEALING_MOVE_COOLDOWN;

    private int putTravelledPositionCtr = 0;

    private boolean godModeEnabled = false;

    public Player(Position position) {
        super("Player", 100, 0, new AncientSword(), position);

        setIlluminated(true);
        setIlluminationRange(DEFAULT_ILLUMINATION_RANGE);

        setHealth(health);
        setHunger(hunger);
        setArmor(armor);
        setWeapon(weapon);
        setCoins(coins);
    }

    public void toggleGodMode() {
        System.out.println("!!! PLAYER IN GOD MODE !!!");

        godModeEnabled = !godModeEnabled;
        if(godModeEnabled) {
            GUIManager.getInstance().printDevLog("GOD MODE ENABLED");
            hungerDecreaseCounter = 9999999;
            hunger = 99999999;
            setWeapon(new DevOneShotWeapon());
            setArmor(new DevArmor());
            overrideColor(Color.RED);
        }
        else {
            GUIManager.getInstance().printDevLog("DISABLED GOD MODE");
            hungerDecreaseCounter = DEFAULT_HUNGER_DECREASE_MOVES_COOLDOWN;
            hunger = 100;
            setWeapon(new AncientSword());
            setArmor(new BareLeatherTunic());
            resetColor();
        }
    }

    @Override
    public void walk(Position unitPos) {
        super.walk(unitPos);
        AudioManager.getInstance().playSFX("walk");
    }

    public void handleMove(Position unitPos) {
        if(stunCounter > 0) {
            GUIManager.getInstance().triggerTextPopup("stunned", Color.YELLOW, position);
            GUIManager.getInstance().printLog("Can't move! You are stunned for " + stunCounter + " more turns.", UITheme.LOG_PLAYER_ACTION);
            AudioManager.getInstance().playSFX("stun");
            stunCounter--;
            return;
        }

        handleNaturalHealing();
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
                GUIManager.getInstance().triggerScreenShake(2,100);
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
            GUIManager.getInstance().triggerScreenShake();

            if(!targetEntity.isAlive()) {
                GUIManager.getInstance().printLog("You killed " + targetEntity.name + ".", UITheme.LOG_PLAYER_ACTION);
            }
            else {
                AudioManager.getInstance().playSFX("attack");
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
        hurt(damage, attacker, false);
    }

    public void hurt(int damage, Entity attacker, boolean penetrateArmor) {
        naturalHealingDecreaseCounter = NATURAL_HEALING_MOVE_COOLDOWN;

        final int DAMAGE_NULLIFIER = 20; // around max armor points among all armor

        int damageTaken;
        if(penetrateArmor) damageTaken = damage;
        else damageTaken = damage * DAMAGE_NULLIFIER / (DAMAGE_NULLIFIER + this.armor.armorPoints);
        setHealth(health - damageTaken);

        if(damageTaken == 0) {
            GUIManager.getInstance().triggerTextPopup("miss", UITheme.MISS, position);
            return;
        }

        if(attacker != null) {
            if(attacker.weapon.isCritical(damageTaken)) GUIManager.getInstance().triggerTextPopup(damageTaken+"", UITheme.CRITICAL_DAMAGE, position);
            else GUIManager.getInstance().triggerTextPopup(damageTaken+"", UITheme.PLAYER_TAKE_DAMAGE, position);
        }
        else {
            GUIManager.getInstance().triggerTextPopup(damageTaken+"", UITheme.PLAYER_TAKE_DAMAGE, position);
        }

        GUIManager.getInstance().triggerScreenShake();
        AudioManager.getInstance().playSFX("hurt");
        GUIManager.getInstance().setHP(health);

        if(health == 0) die();
    }

    @Override
    public void stun(int moveCount) {
        if(!isStunnable) return;

        if(stunCounter > 0) return;
        stunCounter = moveCount;
    }

    public void setHealth(int health) {
        this.health = Math.clamp(health, 0, 100);
        GUIManager.getInstance().setHP(this.health);
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
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
        if(hunger < 0) hunger = 0;
        this.hunger = hunger;
        GUIManager.getInstance().setHunger(hunger);
    }

    private void handleNaturalHealing() {
        if(hunger <= 0 || health >= maxHealth) return;

        if(naturalHealingDecreaseCounter > 0) {
            naturalHealingDecreaseCounter--;
            return;
        }

        int heal = random.nextInt(5,10);
        setHealth(health + heal);
        setHunger(hunger - random.nextInt(1,5));
        GUIManager.getInstance().triggerTextPopup("+"+heal, UITheme.TEXT_POPUP_HEAL, position);
        naturalHealingDecreaseCounter = NATURAL_HEALING_MOVE_COOLDOWN;
    }

    private void handleHungerDecrease() {
        if(hungerDecreaseCounter > 0) {
            hungerDecreaseCounter--;
            return;
        }
        setHunger(hunger - random.nextInt(1,5));
        if(hunger <= 0) {
            hurt(5);
            GUIManager.getInstance().triggerScreenShake();
            GUIManager.getInstance().printLog("You are starving", UITheme.STAT_HUNGER);
            GUIManager.getInstance().triggerTextPopup("I need to eat", UITheme.STAT_HUNGER, position);
        }
        hungerDecreaseCounter = hungerDecreaseMovesCooldown;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItemToInventory(Item item) {
        for(int i = 0; i < item.amount; i++) {
            inventory.add(item);
        }
        GUIManager.getInstance().updateInventory(inventory);
    }

    public void eat() {
        if(hunger >= 100) {
            GUIManager.getInstance().triggerTextPopup("i'm full", UITheme.STAT_HUNGER, position);
            return;
        }

        for(Item item : inventory) {
            if(item instanceof Food f) {
                removeItemFromInventory(f);
                setHunger(Math.clamp(hunger + f.hungerPoints, 0, 100));
                GUIManager.getInstance().triggerTextPopup("+" + f.hungerPoints, UITheme.STAT_HUNGER, position);
                // TODO: food eat sound
                return;
            }
        }
        GUIManager.getInstance().triggerTextPopup("no food to eat", UITheme.STAT_HUNGER, position);
    }

    public void heal() {
        if(health >= maxHealth) {
            GUIManager.getInstance().triggerTextPopup("i'm at full health", UITheme.STAT_HEALTH, position);
            return;
        }

        for(Item item : inventory) {
            if(item instanceof HealingPotion healingPotion) {
                removeItemFromInventory(healingPotion);
                setHealth(Math.clamp(health + healingPotion.healPoints, 0, 100));
                GUIManager.getInstance().triggerTextPopup("+" + healingPotion.healPoints, UITheme.STAT_HEALTH, position);
                // TODO: heal sounds
                return;
            }
        }
        GUIManager.getInstance().triggerTextPopup("no healing potions", UITheme.STAT_HEALTH, position);
    }

    public void removeItemFromInventory(Item item) {
        inventory.remove(item);
        GUIManager.getInstance().updateInventory(inventory);
    }

    public void resetIlluminationRange() {
        setIlluminationRange(DEFAULT_ILLUMINATION_RANGE);
    }

    public void resetHungerDecreaseCooldown() {
        hungerDecreaseCounter = DEFAULT_HUNGER_DECREASE_MOVES_COOLDOWN;
    }
}

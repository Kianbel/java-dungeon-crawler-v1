package core;

import entity.Player;
import entity.boss.FlareWitch;
import entity.monster.GiantSpider;
import entity.monster.Zombie;
import entity.projectile.Fireball;
import gui.GlyphRegistry;
import gui.GlyphStyle;
import gui.UITheme;
import javafx.scene.paint.Color;
import util.TILE;
import world.*;

public class GameData {
    private static final GameData instance = new GameData();
    private GameData() {}
    public static GameData getInstance() {return instance;}

    private int currentDungeonFloor = 2;
    private double monsterHealthMultiplier = 1.2;

    public void increaseDungeonFloor() {
        currentDungeonFloor++;}

    public int getCurrentDungeonFloor() {
        return currentDungeonFloor;
    }

    public double getMonsterHealthMultiplier() {
        return monsterHealthMultiplier;
    }

    public void loadColorPaletteBasedOnDungeonFloor() {
        switch(currentDungeonFloor) {
            case 1 -> { // FIRE - /* https://lospec.com/palette-list/micro-roguelike */
                UITheme.TILE_WALL = Color.web("#e36433");
                UITheme.TILE_DOOR = Color.web("#731144");
                UITheme.TILE_FLOOR = Color.web("#731144");
                UITheme.TILE_GRASS = Color.web("#e3884e");
                UITheme.TILE_WATER = Color.web("#ecb55f");
                UITheme.TILE_BOOKSHELF = Color.web("#8c3f5d");
                UITheme.TILE_PASSABLE_OBSTACLE = Color.web("#731144");
                UITheme.TILE_SOLID_OBSTACLE = Color.PINK;

                // --- INTERACTABLE TILES ---
                UITheme.ITILE_BOX = Color.web("#ba6156");
                UITheme.ITILE_BROKEN_BOX = Color.web("#ba6156");
                UITheme.ITILE_DROPPED_WEAPON = Color.web("#ffffeb");
                UITheme.ITILE_COIN = Color.web("#ffe478");
                UITheme.ITILE_HEART = Color.web("#eb564b");
                UITheme.ITILE_FIRE = Color.web("#f2a65e");
                UITheme.ITILE_WEB = Color.web("#c2c2d1");
                UITheme.ITILE_CHEST = Color.web("#8c3f5d");
                UITheme.ITILE_LOCKED_DOOR = Color.web("#e36433");
                UITheme.ITILE_OPENED_DOOR = Color.web("#731144");
                UITheme.ITILE_STAIRCASE = Color.web("#f2a65e");

                // --- ENTITIES ---
                UITheme.ENTITY_ZOMBIE = Color.web("#3ca370");
                UITheme.ENTITY_SPIDER = Color.web("#8c3f5d");
                UITheme.ENTITY_FLARE_WITCH = Color.web("#eb564b");

                // --- PROJECTILES ---
                UITheme.PROJECTILE_FIREBALL = Color.web("#f2a65e");
            }
            case 2 -> {
                UITheme.TILE_WALL = Color.web("#1e579c");
                UITheme.TILE_FLOOR = Color.web("#252446");
                UITheme.TILE_DOOR = UITheme.TILE_FLOOR;
                UITheme.TILE_GRASS = Color.web("#0098db");
                UITheme.TILE_WATER = Color.web("#201533");
                UITheme.TILE_BOOKSHELF = Color.web("#203562");
                UITheme.TILE_PASSABLE_OBSTACLE = Color.web("#252446");
                UITheme.TILE_SOLID_OBSTACLE = Color.PINK;

                // --- INTERACTABLE TILES ---
                UITheme.ITILE_BOX = Color.web("#203562");
                UITheme.ITILE_BROKEN_BOX = Color.web("#203562");
                UITheme.ITILE_DROPPED_WEAPON = Color.web("#ffffff");
                UITheme.ITILE_COIN = Color.web("#ffe478");
                UITheme.ITILE_HEART = Color.web("#eb564b");
                UITheme.ITILE_FIRE = Color.web("#0ce6f2");
                UITheme.ITILE_WEB = Color.web("#1e579c");
                UITheme.ITILE_CHEST = Color.web("#0ce6f2");
                UITheme.ITILE_LOCKED_DOOR = UITheme.TILE_WALL;
                UITheme.ITILE_OPENED_DOOR = UITheme.TILE_FLOOR;
                UITheme.ITILE_STAIRCASE = Color.web("#ffffff");

                // --- ENTITIES ---
                UITheme.ENTITY_ZOMBIE = Color.web("#3ca370");
                UITheme.ENTITY_SPIDER = Color.web("#8c3f5d");
                UITheme.ENTITY_FLARE_WITCH = Color.web("#eb564b");

                // --- PROJECTILES ---
                UITheme.PROJECTILE_FIREBALL = Color.web("#f2a65e");
            }
            case 3 -> {

            }
            case 4 -> {

            }
        }
    }

    public void loadTileRegistryBasedOnDungeonFloor() {
        switch (currentDungeonFloor) {
            case 1 -> {
                GlyphRegistry.getInstance().registerTiles(TILE.WALL, new GlyphStyle[]{
                        new GlyphStyle("▓", UITheme.TILE_WALL)
                });

                GlyphRegistry.getInstance().registerTiles(TILE.FLOOR, new GlyphStyle[]{
                        new GlyphStyle("·", UITheme.TILE_FLOOR),
                        new GlyphStyle(".", UITheme.TILE_FLOOR),
                        new GlyphStyle(".", UITheme.TILE_FLOOR),
                        new GlyphStyle("░", UITheme.TILE_FLOOR)
                });

                GlyphRegistry.getInstance().registerTiles(TILE.DOOR, new GlyphStyle[]{
                        new GlyphStyle("⌸", UITheme.TILE_DOOR)
                });
                GlyphRegistry.getInstance().registerTiles(TILE.GRASS, new GlyphStyle[] {
                        new GlyphStyle("\"", UITheme.TILE_GRASS),
                        new GlyphStyle("'", UITheme.TILE_GRASS),
                        new GlyphStyle("⚘", UITheme.TILE_GRASS),
                        new GlyphStyle(".", UITheme.TILE_GRASS),
                        new GlyphStyle("*", UITheme.TILE_GRASS),
                        new GlyphStyle("⊹", UITheme.TILE_GRASS),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.WATER, new GlyphStyle[] {
                        new GlyphStyle("▓", UITheme.TILE_WATER),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.SOLID_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("⛩", UITheme.TILE_SOLID_OBSTACLE),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.PASSABLE_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("ノ", UITheme.TILE_PASSABLE_OBSTACLE),
                        new GlyphStyle("ᵕ", UITheme.TILE_PASSABLE_OBSTACLE),
                        new GlyphStyle("⛓", UITheme.TILE_PASSABLE_OBSTACLE),
                        new GlyphStyle("⚰", UITheme.TILE_PASSABLE_OBSTACLE),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.BOOKSHELF, new GlyphStyle[] {
                        new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
                });

                // --- INTERACTABLE TILE REGISTRY ---
                GlyphRegistry.getInstance().registerInteractableTile(Web.class, "#", UITheme.ITILE_WEB);
                GlyphRegistry.getInstance().registerInteractableTile(Coin.class, "$", UITheme.ITILE_COIN);
                GlyphRegistry.getInstance().registerInteractableTile(Chest.class, "C", UITheme.ITILE_CHEST);
                GlyphRegistry.getInstance().registerInteractableTile(DroppedItem.class, "/", UITheme.ITILE_DROPPED_WEAPON);
                GlyphRegistry.getInstance().registerInteractableTile(Heart.class, "♥", UITheme.ITILE_HEART);
                GlyphRegistry.getInstance().registerInteractableTile(Box.class, "⮽", UITheme.ITILE_BOX);
                GlyphRegistry.getInstance().registerInteractableTile(BrokenBox.class, "%", UITheme.ITILE_BROKEN_BOX);
                GlyphRegistry.getInstance().registerInteractableTile(Fire.class, "\uD83D\uDD25", UITheme.ITILE_FIRE);
                GlyphRegistry.getInstance().registerInteractableTile(LockedDoor.class, "⌸", UITheme.ITILE_LOCKED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(OpenedDoor.class, "⌸", UITheme.ITILE_OPENED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(Staircase.class, "目", UITheme.ITILE_STAIRCASE);

                // --- MONSTER ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
                GlyphRegistry.getInstance().registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
                GlyphRegistry.getInstance().registerEntity(GiantSpider.class, "S", UITheme.ENTITY_SPIDER);
                GlyphRegistry.getInstance().registerEntity(FlareWitch.class, "༒", UITheme.ENTITY_FLARE_WITCH);

                // --- PROJECTILE ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Fireball.class, "\uD83D\uDD25", UITheme.PROJECTILE_FIREBALL);

            }
            case 2 -> {
                GlyphRegistry.getInstance().registerTiles(TILE.WALL, new GlyphStyle[]{
                        new GlyphStyle("🌾", UITheme.TILE_WALL),
                });

                GlyphRegistry.getInstance().registerTiles(TILE.FLOOR, new GlyphStyle[]{
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle("░", UITheme.TILE_FLOOR.darker().darker())
                });

                GlyphRegistry.getInstance().registerTiles(TILE.DOOR, new GlyphStyle[]{
                        new GlyphStyle("⌸", UITheme.TILE_DOOR)
                });
                GlyphRegistry.getInstance().registerTiles(TILE.GRASS, new GlyphStyle[] {
                        new GlyphStyle("\"", UITheme.TILE_GRASS),
                        new GlyphStyle("'", UITheme.TILE_GRASS),
                        new GlyphStyle("⚘", UITheme.TILE_GRASS),
                        new GlyphStyle(".", UITheme.TILE_GRASS),
                        new GlyphStyle("*", UITheme.TILE_GRASS),
                        new GlyphStyle("⊹", UITheme.TILE_GRASS),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.WATER, new GlyphStyle[] {
                        new GlyphStyle("▓", UITheme.TILE_WATER),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.SOLID_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("⛩", UITheme.TILE_SOLID_OBSTACLE),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.PASSABLE_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("ノ", UITheme.TILE_PASSABLE_OBSTACLE),
                        new GlyphStyle("ᵕ", UITheme.TILE_PASSABLE_OBSTACLE),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.BOOKSHELF, new GlyphStyle[] {
                        new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
                });

                // --- INTERACTABLE TILE REGISTRY ---
                GlyphRegistry.getInstance().registerInteractableTile(Web.class, "🕸", UITheme.ITILE_WEB);
                GlyphRegistry.getInstance().registerInteractableTile(Coin.class, "$", UITheme.ITILE_COIN);
                GlyphRegistry.getInstance().registerInteractableTile(Chest.class, "C", UITheme.ITILE_CHEST);
                GlyphRegistry.getInstance().registerInteractableTile(DroppedItem.class, "/", UITheme.ITILE_DROPPED_WEAPON);
                GlyphRegistry.getInstance().registerInteractableTile(Heart.class, "♥", UITheme.ITILE_HEART);
                GlyphRegistry.getInstance().registerInteractableTile(Box.class, "⮽", UITheme.ITILE_BOX);
                GlyphRegistry.getInstance().registerInteractableTile(BrokenBox.class, "%", UITheme.ITILE_BROKEN_BOX);
                GlyphRegistry.getInstance().registerInteractableTile(Fire.class, "\uD83D\uDD25", UITheme.ITILE_FIRE);
                GlyphRegistry.getInstance().registerInteractableTile(LockedDoor.class, "⌸", UITheme.ITILE_LOCKED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(OpenedDoor.class, "⌸", UITheme.ITILE_OPENED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(Staircase.class, "目", UITheme.ITILE_STAIRCASE);

                // --- MONSTER ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
                GlyphRegistry.getInstance().registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
                GlyphRegistry.getInstance().registerEntity(GiantSpider.class, "S", UITheme.ENTITY_SPIDER);
                GlyphRegistry.getInstance().registerEntity(FlareWitch.class, "༒", UITheme.ENTITY_FLARE_WITCH);

                // --- PROJECTILE ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Fireball.class, "\uD83D\uDD25", UITheme.PROJECTILE_FIREBALL);

            }
            case 3 -> {

            }
            case 4 -> {

            }
        }
    }
}

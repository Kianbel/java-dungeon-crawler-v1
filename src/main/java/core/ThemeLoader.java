package core;

import entity.Player;
import entity.boss.FlareWitch;
import entity.monster.*;
import entity.projectile.Fireball;
import entity.projectile.WeaponProjectile;
import gui.GlyphRegistry;
import gui.dataclass.GlyphStyle;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
import util.LEVEL_THEME;
import util.TILE;
import world.*;

public class ThemeLoader {
    private static final ThemeLoader instance = new ThemeLoader();
    private ThemeLoader() {}
    public static ThemeLoader getInstance() { return instance; }

    public void loadTheme(LEVEL_THEME theme) {
        if(theme == null) theme = LEVEL_THEME.DUNGEON;

        switch (theme) {
            case DUNGEON -> {
                // --- TILES ---
                // https://lospec.com/palette-list/micro-roguelike
                UITheme.TILE_WALL = Color.web("#43434f");
                UITheme.TILE_DOOR = Color.web("#731144");
                UITheme.TILE_FLOOR = Color.web("#731144").darker().darker();
                UITheme.TILE_FLOOR_MOSSY = Color.web("#3ca370").darker().darker().darker().darker();
                UITheme.TILE_GRASS = Color.web("#e3884e");
                UITheme.TILE_WATER = Color.web("#6476e8");
                UITheme.TILE_BOOKSHELF = Color.web("#8c3f5d");
                UITheme.TILE_PASSABLE_OBSTACLE = Color.web("#731144");
                UITheme.TILE_SOLID_OBSTACLE = Color.web("#8c3f5d");
                UITheme.TILE_CARPET = Color.web("#731144");
                UITheme.TILE_IRON_BAR = Color.web("#e36433");
                UITheme.TILE_SKELETON = Color.web("#c2c2d1");
                UITheme.TILE_BRIDGE = Color.web("#ba6156");


                // --- INTERACTABLE TILES ---
                UITheme.ITITLE_POT = Color.web("#ba6156");
                UITheme.ITILE_SHATTERED_POT = Color.web("#ba6156");
                UITheme.ITILE_DROPPED_ITEM = Color.LIGHTPINK;
                UITheme.ITILE_COIN = Color.web("#ffe478");
                UITheme.ITILE_HEART = Color.web("#eb564b");
                UITheme.ITILE_FIRE = Color.web("#f2a65e");
                UITheme.ITILE_WEB = Color.web("#c2c2d1");
                UITheme.ITILE_NORMAL_CHEST = Color.web("#ba6156");
                UITheme.ITILE_TREASURE_CHEST = Color.web("#ffffeb");
                UITheme.ITILE_LOCKED_DOOR = Color.web("#ffffeb");
                UITheme.ITILE_OPENED_DOOR = Color.web("#731144");
                UITheme.ITILE_STAIRCASE = Color.web("#ffffeb");
                UITheme.ITILE_SPIKE_TRAP = Color.web("#e36433");
                UITheme.ITILE_PRESSURE_PLATE_TRAP = UITheme.TILE_FLOOR;
                UITheme.ITILE_WOODEN_DOOR = Color.web("#ba6156");

                // --- ENTITIES PALETTE ---
                UITheme.ENTITY_ZOMBIE = Color.web("#3ca370");
                UITheme.ENTITY_SPIDER = Color.web("#8c3f5d");
                UITheme.ENTITY_FLARE_WITCH = Color.web("#eb564b");
                UITheme.ENTITY_RAT = Color.web("#731144");
                UITheme.ENTITY_BAT = Color.web("#6476e8");
                UITheme.ENTITY_KOBOLD = Color.web("#ba6156");
                UITheme.ENTITY_GOBLIN = Color.web("#3ca370");

                // --- PROJECTILES
                UITheme.PROJECTILE_FIREBALL = Color.web("#f2a65e");

                /*
                -------------------------------------------------
                |                 TILE REGISTRY                 |
                -------------------------------------------------
                 */

                GlyphRegistry.getInstance().registerTiles(TILE.WALL, new GlyphStyle[]{
                        new GlyphStyle("▓", UITheme.TILE_WALL),
                });

                GlyphRegistry.getInstance().registerTiles(TILE.FLOOR, new GlyphStyle[]{
                        new GlyphStyle("░", UITheme.TILE_FLOOR),
                        new GlyphStyle("░", UITheme.TILE_FLOOR_MOSSY),
                        new GlyphStyle(".", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                });

                GlyphRegistry.getInstance().registerTiles(TILE.DOOR, new GlyphStyle[]{
                        new GlyphStyle("⌸", UITheme.TILE_DOOR)
                });
                GlyphRegistry.getInstance().registerTiles(TILE.GRASS, new GlyphStyle[] {
                        new GlyphStyle("🌾", UITheme.TILE_GRASS),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.WATER, new GlyphStyle[] {
                        new GlyphStyle("░", UITheme.TILE_WATER),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.SOLID_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("⛩", UITheme.TILE_SOLID_OBSTACLE),
                        new GlyphStyle("🖾", UITheme.TILE_SOLID_OBSTACLE),
                        new GlyphStyle("┬", UITheme.TILE_SOLID_OBSTACLE),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.PASSABLE_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("▒", UITheme.TILE_WATER.darker())
                });
                GlyphRegistry.getInstance().registerTiles(TILE.BOOKSHELF, new GlyphStyle[] {
                        new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.CARPET, new GlyphStyle[] {
                        new GlyphStyle("🏽", UITheme.TILE_CARPET),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.IRON_BAR, new GlyphStyle[] {
                        new GlyphStyle("⛓", UITheme.TILE_IRON_BAR),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.SKELETON, new GlyphStyle[] {
                        new GlyphStyle("☠", UITheme.TILE_SKELETON),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.BRIDGE, new GlyphStyle[] {
                        new GlyphStyle("☰", UITheme.TILE_BRIDGE),
                });

                // --- INTERACTABLE TILE REGISTRY ---
                GlyphRegistry.getInstance().registerInteractableTile(Web.class, "🕸", UITheme.ITILE_WEB);
                GlyphRegistry.getInstance().registerInteractableTile(Coin.class, "©", UITheme.ITILE_COIN);
                GlyphRegistry.getInstance().registerInteractableTile(NormalChest.class, "💼", UITheme.ITILE_NORMAL_CHEST);
                GlyphRegistry.getInstance().registerInteractableTile(TreasureChest.class, "💼", UITheme.ITILE_TREASURE_CHEST);
                GlyphRegistry.getInstance().registerInteractableTile(DroppedItem.class, "&", UITheme.ITILE_DROPPED_ITEM);
                GlyphRegistry.getInstance().registerInteractableTile(Heart.class, "♥", UITheme.ITILE_HEART);
                GlyphRegistry.getInstance().registerInteractableTile(Pot.class, "⚱", UITheme.ITITLE_POT);
                GlyphRegistry.getInstance().registerInteractableTile(ShatteredPot.class, "%", UITheme.ITILE_SHATTERED_POT);
                GlyphRegistry.getInstance().registerInteractableTile(Fire.class, "🔥", UITheme.ITILE_FIRE);
                GlyphRegistry.getInstance().registerInteractableTile(LockedDoor.class, "⌸", UITheme.ITILE_LOCKED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(OpenedDoor.class, "⌸", UITheme.ITILE_OPENED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(Staircase.class, "目", UITheme.ITILE_STAIRCASE);
                GlyphRegistry.getInstance().registerInteractableTile(SpikeTrap.class, "♒", UITheme.ITILE_SPIKE_TRAP);
                GlyphRegistry.getInstance().registerInteractableTile(PressurePlateTrap.class, "~", UITheme.ITILE_PRESSURE_PLATE_TRAP);
                GlyphRegistry.getInstance().registerInteractableTile(WoodenDoor.class, "⌸", UITheme.ITILE_WOODEN_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(BreakableWall.class, "▒", UITheme.TILE_WALL);

                // --- MONSTER ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
                GlyphRegistry.getInstance().registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
                GlyphRegistry.getInstance().registerEntity(GiantSpider.class, "🕷", UITheme.ENTITY_SPIDER);
                GlyphRegistry.getInstance().registerEntity(FlareWitch.class, "༒", UITheme.ENTITY_FLARE_WITCH);
                GlyphRegistry.getInstance().registerEntity(Rat.class, "🐁", UITheme.ENTITY_RAT);
                GlyphRegistry.getInstance().registerEntity(Bat.class, "🦇", UITheme.ENTITY_BAT);
                GlyphRegistry.getInstance().registerEntity(Kobold.class, "🦎", UITheme.ENTITY_KOBOLD);
                GlyphRegistry.getInstance().registerEntity(Goblin.class, "g", UITheme.ENTITY_GOBLIN);


                // --- PROJECTILE ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Fireball.class, "🔥", UITheme.PROJECTILE_FIREBALL);
                GlyphRegistry.getInstance().registerEntity(WeaponProjectile.class, "?", Color.PINK);
            }
            case SANDSTONE -> {
                // --- TILES ---
                // https://lospec.com/palette-list/sandy-06
                UITheme.TILE_WALL = Color.web("#e7d99c");
                UITheme.TILE_DOOR = Color.web("#653019");
                UITheme.TILE_FLOOR = Color.web("#653019").darker().darker();
                UITheme.TILE_FLOOR_MOSSY = Color.web("#653019").darker().darker().darker().darker();
                UITheme.TILE_GRASS = Color.web("#e3ba66");
                UITheme.TILE_WATER = Color.web("#6476e8");
                UITheme.TILE_BOOKSHELF = Color.web("#8c3f5d");
                UITheme.TILE_PASSABLE_OBSTACLE = Color.web("#731144");
                UITheme.TILE_SOLID_OBSTACLE = Color.web("#8c3f5d");
                UITheme.TILE_CARPET = Color.web("#731144");
                UITheme.TILE_IRON_BAR = Color.web("#e36433");
                UITheme.TILE_SKELETON = Color.web("#c2c2d1");
                UITheme.TILE_BRIDGE = Color.web("#ba6156");


                // --- INTERACTABLE TILES ---
                UITheme.ITITLE_POT = Color.web("#ba6156");
                UITheme.ITILE_SHATTERED_POT = Color.web("#ba6156");
                UITheme.ITILE_DROPPED_ITEM = Color.LIGHTPINK;
                UITheme.ITILE_COIN = Color.web("#ffe478");
                UITheme.ITILE_HEART = Color.web("#eb564b");
                UITheme.ITILE_FIRE = Color.web("#f2a65e");
                UITheme.ITILE_WEB = Color.web("#c2c2d1");
                UITheme.ITILE_NORMAL_CHEST = Color.web("#ba6156");
                UITheme.ITILE_TREASURE_CHEST = Color.web("#ffffeb");
                UITheme.ITILE_LOCKED_DOOR = Color.web("#ffffeb");
                UITheme.ITILE_OPENED_DOOR = Color.web("#731144");
                UITheme.ITILE_STAIRCASE = Color.web("#ffffeb");
                UITheme.ITILE_SPIKE_TRAP = Color.web("#e36433");
                UITheme.ITILE_PRESSURE_PLATE_TRAP = UITheme.TILE_FLOOR;
                UITheme.ITILE_WOODEN_DOOR = Color.web("#ba6156");

                // --- ENTITIES PALETTE ---
                UITheme.ENTITY_ZOMBIE = Color.web("#3ca370");
                UITheme.ENTITY_SPIDER = Color.web("#8c3f5d");
                UITheme.ENTITY_FLARE_WITCH = Color.web("#eb564b");
                UITheme.ENTITY_RAT = Color.web("#731144");
                UITheme.ENTITY_BAT = Color.web("#6476e8");
                UITheme.ENTITY_KOBOLD = Color.web("#ba6156");
                UITheme.ENTITY_GOBLIN = Color.web("#3ca370");

                // --- PROJECTILES
                UITheme.PROJECTILE_FIREBALL = Color.web("#f2a65e");


                /*
                -------------------------------------------------
                |                 TILE REGISTRY                 |
                -------------------------------------------------
                 */

                GlyphRegistry.getInstance().registerTiles(TILE.WALL, new GlyphStyle[]{
                        new GlyphStyle("▓", UITheme.TILE_WALL),
                });

                GlyphRegistry.getInstance().registerTiles(TILE.FLOOR, new GlyphStyle[]{
                        new GlyphStyle("░", UITheme.TILE_FLOOR),
                        new GlyphStyle("░", UITheme.TILE_FLOOR_MOSSY),
                        new GlyphStyle(".", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                        new GlyphStyle(" ", UITheme.TILE_FLOOR),
                });

                GlyphRegistry.getInstance().registerTiles(TILE.DOOR, new GlyphStyle[]{
                        new GlyphStyle("⌸", UITheme.TILE_DOOR)
                });
                GlyphRegistry.getInstance().registerTiles(TILE.GRASS, new GlyphStyle[] {
                        new GlyphStyle("🌾", UITheme.TILE_GRASS),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.WATER, new GlyphStyle[] {
                        new GlyphStyle("░", UITheme.TILE_WATER),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.SOLID_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("⛩", UITheme.TILE_SOLID_OBSTACLE),
                        new GlyphStyle("🖾", UITheme.TILE_SOLID_OBSTACLE),
                        new GlyphStyle("┬", UITheme.TILE_SOLID_OBSTACLE),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.PASSABLE_OBSTACLE, new GlyphStyle[] {
                        new GlyphStyle("▒", UITheme.TILE_WATER.darker())
                });
                GlyphRegistry.getInstance().registerTiles(TILE.BOOKSHELF, new GlyphStyle[] {
                        new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.CARPET, new GlyphStyle[] {
                        new GlyphStyle("🏽", UITheme.TILE_CARPET),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.IRON_BAR, new GlyphStyle[] {
                        new GlyphStyle("⛓", UITheme.TILE_IRON_BAR),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.SKELETON, new GlyphStyle[] {
                        new GlyphStyle("☠", UITheme.TILE_SKELETON),
                });
                GlyphRegistry.getInstance().registerTiles(TILE.BRIDGE, new GlyphStyle[] {
                        new GlyphStyle("☰", UITheme.TILE_BRIDGE),
                });

                // --- INTERACTABLE TILE REGISTRY ---
                GlyphRegistry.getInstance().registerInteractableTile(Web.class, "🕸", UITheme.ITILE_WEB);
                GlyphRegistry.getInstance().registerInteractableTile(Coin.class, "©", UITheme.ITILE_COIN);
                GlyphRegistry.getInstance().registerInteractableTile(NormalChest.class, "💼", UITheme.ITILE_NORMAL_CHEST);
                GlyphRegistry.getInstance().registerInteractableTile(TreasureChest.class, "💼", UITheme.ITILE_TREASURE_CHEST);
                GlyphRegistry.getInstance().registerInteractableTile(DroppedItem.class, "&", UITheme.ITILE_DROPPED_ITEM);
                GlyphRegistry.getInstance().registerInteractableTile(Heart.class, "♥", UITheme.ITILE_HEART);
                GlyphRegistry.getInstance().registerInteractableTile(Pot.class, "⚱", UITheme.ITITLE_POT);
                GlyphRegistry.getInstance().registerInteractableTile(ShatteredPot.class, "%", UITheme.ITILE_SHATTERED_POT);
                GlyphRegistry.getInstance().registerInteractableTile(Fire.class, "🔥", UITheme.ITILE_FIRE);
                GlyphRegistry.getInstance().registerInteractableTile(LockedDoor.class, "⌸", UITheme.ITILE_LOCKED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(OpenedDoor.class, "⌸", UITheme.ITILE_OPENED_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(Staircase.class, "目", UITheme.ITILE_STAIRCASE);
                GlyphRegistry.getInstance().registerInteractableTile(SpikeTrap.class, "♒", UITheme.ITILE_SPIKE_TRAP);
                GlyphRegistry.getInstance().registerInteractableTile(PressurePlateTrap.class, "~", UITheme.ITILE_PRESSURE_PLATE_TRAP);
                GlyphRegistry.getInstance().registerInteractableTile(WoodenDoor.class, "⌸", UITheme.ITILE_WOODEN_DOOR);
                GlyphRegistry.getInstance().registerInteractableTile(BreakableWall.class, "▒", UITheme.TILE_WALL);

                // --- MONSTER ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
                GlyphRegistry.getInstance().registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
                GlyphRegistry.getInstance().registerEntity(GiantSpider.class, "🕷", UITheme.ENTITY_SPIDER);
                GlyphRegistry.getInstance().registerEntity(FlareWitch.class, "༒", UITheme.ENTITY_FLARE_WITCH);
                GlyphRegistry.getInstance().registerEntity(Rat.class, "🐁", UITheme.ENTITY_RAT);
                GlyphRegistry.getInstance().registerEntity(Bat.class, "🦇", UITheme.ENTITY_BAT);
                GlyphRegistry.getInstance().registerEntity(Kobold.class, "🦎", UITheme.ENTITY_KOBOLD);
                GlyphRegistry.getInstance().registerEntity(Goblin.class, "g", UITheme.ENTITY_GOBLIN);


                // --- PROJECTILE ENTITY REGISTRY ---
                GlyphRegistry.getInstance().registerEntity(Fireball.class, "🔥", UITheme.PROJECTILE_FIREBALL);
                GlyphRegistry.getInstance().registerEntity(WeaponProjectile.class, "?", Color.PINK);
            }
        }

        System.out.println("loaded: " + theme);
    }
}

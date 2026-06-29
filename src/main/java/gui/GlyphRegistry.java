package gui;

import entity.*;
import entity.boss.FlareWitch;
import entity.monster.*;
import entity.projectile.Fireball;
import entity.projectile.WeaponProjectile;
import gui.dataclass.GlyphStyle;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
import util.TILE;
import world.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GlyphRegistry {
    private static final GlyphRegistry instance = new GlyphRegistry();

    private final Map<TILE, GlyphStyle[]> tileRegistry = new EnumMap<>(TILE.class);
    private final Map<Class<? extends InteractableTile>, GlyphStyle> interactableTileRegistry = new HashMap<>();
    private final Map<Class<? extends Entity>, GlyphStyle> entityRegistry = new HashMap<>();

    private final GlyphStyle voidStyle = new GlyphStyle(" ", UITheme.CANVAS_VOID);
    private final GlyphStyle defaultStyle = new GlyphStyle("?", Color.MAGENTA);

    private GlyphRegistry() {
        tileRegistry.put(TILE.WALL, new GlyphStyle[]{
                new GlyphStyle("▓", UITheme.TILE_WALL),
        });

        tileRegistry.put(TILE.FLOOR, new GlyphStyle[]{
//                new GlyphStyle("·", UITheme.TILE_FLOOR),
                new GlyphStyle("░", UITheme.TILE_FLOOR),
                new GlyphStyle("░", UITheme.TILE_FLOOR_MOSSY),
                new GlyphStyle(".", UITheme.TILE_FLOOR),
                new GlyphStyle(" ", UITheme.TILE_FLOOR),
                new GlyphStyle(" ", UITheme.TILE_FLOOR),
                new GlyphStyle(" ", UITheme.TILE_FLOOR),
                new GlyphStyle(" ", UITheme.TILE_FLOOR),
                new GlyphStyle(" ", UITheme.TILE_FLOOR),
        });

        tileRegistry.put(TILE.DOOR, new GlyphStyle[]{
                new GlyphStyle("⌸", UITheme.TILE_DOOR)
        });
        tileRegistry.put(TILE.GRASS, new GlyphStyle[] {
                new GlyphStyle("🌾", UITheme.TILE_GRASS),
        });
        tileRegistry.put(TILE.WATER, new GlyphStyle[] {
                new GlyphStyle("░", UITheme.TILE_WATER),
        });
        tileRegistry.put(TILE.SOLID_OBSTACLE, new GlyphStyle[] {
                new GlyphStyle("⛩", UITheme.TILE_SOLID_OBSTACLE),
                new GlyphStyle("🖾", UITheme.TILE_SOLID_OBSTACLE),
                new GlyphStyle("┬", UITheme.TILE_SOLID_OBSTACLE),
        });
        tileRegistry.put(TILE.PASSABLE_OBSTACLE, new GlyphStyle[] {
                new GlyphStyle("▒", UITheme.TILE_WATER.darker())
        });
        tileRegistry.put(TILE.BOOKSHELF, new GlyphStyle[] {
                new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
        });
        tileRegistry.put(TILE.CARPET, new GlyphStyle[] {
                new GlyphStyle("🏽", UITheme.TILE_CARPET),
        });
        tileRegistry.put(TILE.IRON_BAR, new GlyphStyle[] {
                new GlyphStyle("⛓", UITheme.TILE_IRON_BAR),
        });
        tileRegistry.put(TILE.SKELETON, new GlyphStyle[] {
                new GlyphStyle("☠", UITheme.TILE_SKELETON),
        });
        tileRegistry.put(TILE.BRIDGE, new GlyphStyle[] {
                new GlyphStyle("☰", UITheme.TILE_BRIDGE),
        });

        // --- INTERACTABLE TILE REGISTRY ---
        registerInteractableTile(Web.class, "🕸", UITheme.ITILE_WEB);
        registerInteractableTile(Coin.class, "©", UITheme.ITILE_COIN);
        registerInteractableTile(NormalChest.class, "💼", UITheme.ITILE_NORMAL_CHEST);
        registerInteractableTile(TreasureChest.class, "💼", UITheme.ITILE_TREASURE_CHEST);
        registerInteractableTile(DroppedItem.class, "&", UITheme.ITILE_DROPPED_ITEM);
        registerInteractableTile(Heart.class, "♥", UITheme.ITILE_HEART);
        registerInteractableTile(Pot.class, "⚱", UITheme.ITITLE_POT);
        registerInteractableTile(ShatteredPot.class, "%", UITheme.ITILE_SHATTERED_POT);
        registerInteractableTile(Fire.class, "🔥", UITheme.ITILE_FIRE);
        registerInteractableTile(LockedDoor.class, "⌸", UITheme.ITILE_LOCKED_DOOR);
        registerInteractableTile(OpenedDoor.class, "⌸", UITheme.ITILE_OPENED_DOOR);
        registerInteractableTile(Staircase.class, "目", UITheme.ITILE_STAIRCASE);
        registerInteractableTile(SpikeTrap.class, "♒", UITheme.ITILE_SPIKE_TRAP);
        registerInteractableTile(PressurePlateTrap.class, "~", UITheme.ITILE_PRESSURE_PLATE_TRAP);
        registerInteractableTile(WoodenDoor.class, "⌸", UITheme.ITILE_WOODEN_DOOR);
        registerInteractableTile(BreakableWall.class, "▒", UITheme.TILE_WALL);

        // --- MONSTER ENTITY REGISTRY ---
        registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
        registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
        registerEntity(GiantSpider.class, "🕷", UITheme.ENTITY_SPIDER);
        registerEntity(FlareWitch.class, "༒", UITheme.ENTITY_FLARE_WITCH);
        registerEntity(Rat.class, "🐁", UITheme.ENTITY_RAT);
        registerEntity(Bat.class, "🦇", UITheme.ENTITY_BAT);
        registerEntity(Kobold.class, "🦎", UITheme.ENTITY_KOBOLD);
        registerEntity(GoblinArcher.class, "g", UITheme.ENTITY_GOBLIN);


        // --- PROJECTILE ENTITY REGISTRY ---
        registerEntity(Fireball.class, "🔥", UITheme.PROJECTILE_FIREBALL);
        registerEntity(WeaponProjectile.class, "?", Color.PINK);
    }

    public static GlyphRegistry getInstance() { return instance; }

    public void clearTileRegistry() {
        tileRegistry.clear();
    }
    public void clearInteractableTileRegistry() {
        interactableTileRegistry.clear();
    }
    public void clearEntityRegistry() {
        entityRegistry.clear();
    }

    public void registerTiles(TILE tile, GlyphStyle[] glyphs) {
        tileRegistry.put(tile, glyphs);
    }

    public void registerInteractableTile(Class<? extends InteractableTile> clazz, String glyph, Color color) {
        interactableTileRegistry.put(clazz, new GlyphStyle(glyph, color));
    }

    public void registerEntity(Class<? extends Entity> clazz, String glyph, Color color) {
        entityRegistry.put(clazz, new GlyphStyle(glyph, color));
    }

    public GlyphStyle getStyle(TILE tile, int roomX, int roomY, int roomId) {
        GlyphStyle[] options = tileRegistry.get(tile);
        if (options == null || options.length == 0) return voidStyle;

        int hash = (roomX * 31) ^ (roomY * 17) ^ (roomId * 13);
        return options[Math.abs(hash) % options.length];
    }

    public GlyphStyle getStyle(TILE tile) {
        GlyphStyle[] options = tileRegistry.get(tile);
        if (options == null || options.length == 0) return voidStyle;

        return options[Math.abs(new Random().nextInt(5)) % options.length];
    }

    public GlyphStyle getStyle(InteractableTile tile) { return interactableTileRegistry.getOrDefault(tile.getClass(), defaultStyle); }
    public GlyphStyle getStyle(Entity entity) { return entityRegistry.getOrDefault(entity.getClass(), defaultStyle); }
    public GlyphStyle getVoidStyle() { return voidStyle; }
}
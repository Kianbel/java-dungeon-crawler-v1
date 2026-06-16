package gui;

import entity.*;
import entity.boss.FlareWitch;
import entity.monster.GiantSpider;
import entity.monster.Zombie;
import entity.projectile.Fireball;
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
        // --- MULTI-TEXTURE STRUCTURAL REGISTRY ---
        // Pulls color values seamlessly from UITheme roles
        tileRegistry.put(TILE.WALL, new GlyphStyle[]{
                new GlyphStyle("▒", UITheme.TILE_WALL),
                new GlyphStyle("▒", UITheme.TILE_WALL),
                new GlyphStyle("▓", UITheme.TILE_WALL)
        });

        tileRegistry.put(TILE.FLOOR, new GlyphStyle[]{
                new GlyphStyle("·", UITheme.TILE_FLOOR),
                new GlyphStyle(".", UITheme.TILE_FLOOR),
                new GlyphStyle("░", UITheme.TILE_FLOOR)
        });

        tileRegistry.put(TILE.DOOR, new GlyphStyle[]{
                new GlyphStyle("⌸", UITheme.TILE_DOOR)
        });
        tileRegistry.put(TILE.GRASS, new GlyphStyle[] {
                new GlyphStyle("w", UITheme.TILE_GRASS),
                new GlyphStyle("w", UITheme.TILE_GRASS),
                new GlyphStyle("w", UITheme.TILE_GRASS),
                new GlyphStyle("w", UITheme.TILE_GRASS),
                new GlyphStyle("w", UITheme.TILE_GRASS),
                new GlyphStyle("\"", UITheme.TILE_GRASS),
                new GlyphStyle("'", UITheme.TILE_GRASS),
                new GlyphStyle("⚘", UITheme.TILE_GRASS),
                new GlyphStyle(".", UITheme.TILE_GRASS),
                new GlyphStyle("*", UITheme.TILE_GRASS),
                new GlyphStyle("⊹", UITheme.TILE_GRASS),
        });
        tileRegistry.put(TILE.WATER, new GlyphStyle[] {
                new GlyphStyle("▓", UITheme.TILE_WATER),
                new GlyphStyle("▓", UITheme.TILE_WATER),
                new GlyphStyle("▓", UITheme.TILE_WATER),
        });
        tileRegistry.put(TILE.SOLID_OBSTACLE, new GlyphStyle[] {
                new GlyphStyle("⛩", UITheme.TILE_SOLID_OBSTACLE),
        });
        tileRegistry.put(TILE.PASSABLE_OBSTACLE, new GlyphStyle[] {
                new GlyphStyle("ノ", UITheme.TILE_PASSABLE_OBSTACLE),
                new GlyphStyle("ᵕ", UITheme.TILE_PASSABLE_OBSTACLE),
                new GlyphStyle("ノ", UITheme.TILE_PASSABLE_OBSTACLE),
                new GlyphStyle("ノ", UITheme.TILE_PASSABLE_OBSTACLE),
                new GlyphStyle("⚔", UITheme.TILE_PASSABLE_OBSTACLE),
                new GlyphStyle("\uD83D\uDDE1", UITheme.TILE_PASSABLE_OBSTACLE), // sword
                new GlyphStyle("ᵕ", UITheme.TILE_PASSABLE_OBSTACLE),
        });
        tileRegistry.put(TILE.BOOKSHELF, new GlyphStyle[] {
                new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
                new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
                new GlyphStyle("目", UITheme.TILE_BOOKSHELF),
        });

        // --- INTERACTABLE TILE REGISTRY ---
        registerInteractableTile(Web.class, "#", UITheme.ITILE_WEB);
        registerInteractableTile(Coin.class, "$", UITheme.ITILE_COIN);
        registerInteractableTile(Chest.class, "C", UITheme.ITILE_CHEST);
        registerInteractableTile(DroppedWeapon.class, "/", UITheme.ITILE_DROPPED_WEAPON);
        registerInteractableTile(Heart.class, "♥", UITheme.ITILE_HEART);
        registerInteractableTile(Box.class, "⮽", UITheme.ITILE_BOX);
        registerInteractableTile(BrokenBox.class, "%", UITheme.ITILE_BROKEN_BOX);
        registerInteractableTile(Fire.class, "\uD83D\uDD25", UITheme.ITILE_FIRE);

        // --- MONSTER ENTITY REGISTRY ---
        registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
        registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
        registerEntity(GiantSpider.class, "S", UITheme.ENTITY_SPIDER);
        registerEntity(FlareWitch.class, "༒", UITheme.ENTITY_FLARE_WITCH);

        // --- PROJECTILE ENTITY REGISTRY ---
        registerEntity(Fireball.class, "\uD83D\uDD25", UITheme.PROJECTILE_FIREBALL);
    }

    public static GlyphRegistry getInstance() { return instance; }

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
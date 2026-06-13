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
                new GlyphStyle("▒", UITheme.WORLD_WALL),
                new GlyphStyle("▒", UITheme.WORLD_WALL),
                new GlyphStyle("▓", UITheme.WORLD_WALL_ALT)
        });

        tileRegistry.put(TILE.FLOOR, new GlyphStyle[]{
                new GlyphStyle("·", UITheme.WORLD_FLOOR_DIM),
                new GlyphStyle(".", UITheme.WORLD_FLOOR_DIM),
                new GlyphStyle("░", UITheme.WORLD_FLOOR_LIT)
        });

        tileRegistry.put(TILE.DOOR, new GlyphStyle[]{
                new GlyphStyle("⌸", UITheme.WORLD_DOOR)
        });
        tileRegistry.put(TILE.GRASS, new GlyphStyle[] {
                new GlyphStyle("w", Color.GREEN),
                new GlyphStyle("w", Color.DARKSEAGREEN),
                new GlyphStyle("w", Color.DARKOLIVEGREEN),
                new GlyphStyle("w", Color.FORESTGREEN),
                new GlyphStyle("w", Color.DARKGREEN),
                new GlyphStyle("\"", Color.DARKGREEN),
                new GlyphStyle("'", Color.DARKGREEN),
                new GlyphStyle("⚘", Color.DARKGREEN),
                new GlyphStyle(".", Color.DARKGREEN),
                new GlyphStyle("*", Color.DARKGREEN),
                new GlyphStyle("⊹", Color.DARKGREEN),
        });
        tileRegistry.put(TILE.WATER, new GlyphStyle[] {
                new GlyphStyle("▓", Color.DARKBLUE),
                new GlyphStyle("▓", Color.DARKBLUE.brighter()),
                new GlyphStyle("▓", Color.DARKBLUE.darker()),
        });
        tileRegistry.put(TILE.SOLID_OBSTACLE, new GlyphStyle[] {
                new GlyphStyle("⮽", Color.SADDLEBROWN),
                new GlyphStyle("⮽", Color.SADDLEBROWN.darker()),
                new GlyphStyle("⮽", Color.SADDLEBROWN.darker().darker()),
                new GlyphStyle("⮽", Color.SADDLEBROWN),
                new GlyphStyle("⮽", Color.SADDLEBROWN.darker()),
                new GlyphStyle("⮽", Color.SADDLEBROWN.darker().darker()),
                new GlyphStyle("⛩", Color.SADDLEBROWN.darker()),
        });
        tileRegistry.put(TILE.PASSABLE_OBSTACLE, new GlyphStyle[] {
                new GlyphStyle("ノ", UITheme.WORLD_FLOOR_DIM),
                new GlyphStyle("ᵕ", UITheme.WORLD_FLOOR_DIM),
                new GlyphStyle("ノ", Color.GRAY.darker()),
                new GlyphStyle("ノ", Color.GRAY),
                new GlyphStyle("⚔", UITheme.WORLD_FLOOR_DIM.brighter()),
                new GlyphStyle("\uD83D\uDDE1", UITheme.WORLD_FLOOR_DIM), // sword
                new GlyphStyle("ᵕ", Color.GRAY.darker()),
        });
        tileRegistry.put(TILE.BOOKSHELF, new GlyphStyle[] {
                new GlyphStyle("目", Color.PURPLE.darker()),
                new GlyphStyle("目", Color.PURPLE.darker().darker()),
                new GlyphStyle("目", Color.DARKVIOLET),
        });

        // --- INTERACTABLE TILE REGISTRY ---
        registerInteractableTile(Web.class, "#", UITheme.LOOT_WEB);
        registerInteractableTile(Coin.class, "$", UITheme.STAT_GOLD);
        registerInteractableTile(Chest.class, "C", UITheme.LOOT_CHEST);
        registerInteractableTile(DroppedWeapon.class, "/", UITheme.LOOT_WEAPON);
        registerInteractableTile(Heart.class, "♥", UITheme.STAT_HEALTH);
        registerInteractableTile(Box.class, "⮽", Color.SADDLEBROWN);
        registerInteractableTile(BrokenBox.class, "%", Color.SADDLEBROWN);

        // --- MONSTER ENTITY REGISTRY ---
        registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
        registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
        registerEntity(GiantSpider.class, "S", UITheme.ENTITY_SPIDER);
        registerEntity(FlareWitch.class, "༒", Color.ORANGE);

        // --- PROJECTILE ENTITY REGISTRY ---
        registerEntity(Fireball.class, "\uD83D\uDD25", Color.ORANGE);
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
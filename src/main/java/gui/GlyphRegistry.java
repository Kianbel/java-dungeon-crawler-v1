package gui;

import entity.*;
import javafx.scene.paint.Color;
import util.TILE;
import world.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class GlyphRegistry {
    private static final GlyphRegistry instance = new GlyphRegistry();

    private final Map<TILE, GlyphStyle[]> tileRegistry = new EnumMap<>(TILE.class);
    private final Map<Class<? extends InteractableTile>, GlyphStyle> interactableTileRegistry = new HashMap<>();
    private final Map<Class<? extends Entity>, GlyphStyle> entityRegistry = new HashMap<>();

    private final GlyphStyle voidStyle = new GlyphStyle(" ", UITheme.CANVAS_VOID);
    private final GlyphStyle defaultStyle = new GlyphStyle("?", Color.MAGENTA);

    public static class GlyphStyle {
        public final String glyph;
        public final Color color;

        public GlyphStyle(String glyph, Color color) {
            this.glyph = glyph;
            this.color = color;
        }
    }

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

        // --- INTERACTABLE TILE REGISTRY ---
        registerInteractableTile(Web.class, "#", UITheme.LOOT_WEB);
        registerInteractableTile(Coin.class, "$", UITheme.STAT_GOLD);
        registerInteractableTile(Chest.class, "C", UITheme.LOOT_CHEST);
        registerInteractableTile(DroppedWeapon.class, "/", UITheme.LOOT_WEAPON);

        // --- CHARACTER ENTITY REGISTRY ---
        registerEntity(Player.class, "@", UITheme.ENTITY_PLAYER);
        registerEntity(Zombie.class, "Z", UITheme.ENTITY_ZOMBIE);
        registerEntity(GiantSpider.class, "S", UITheme.ENTITY_SPIDER);
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

    public GlyphStyle getStyle(InteractableTile tile) { return interactableTileRegistry.getOrDefault(tile.getClass(), defaultStyle); }
    public GlyphStyle getStyle(Entity entity) { return entityRegistry.getOrDefault(entity.getClass(), defaultStyle); }
    public GlyphStyle getVoidStyle() { return voidStyle; }
}
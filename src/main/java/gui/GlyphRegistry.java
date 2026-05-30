package gui;

import entity.*;
import javafx.scene.paint.Color;
import util.TILE;
import world.InteractableTile;
import world.Web;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GlyphRegistry {
    private static final GlyphRegistry instance = new GlyphRegistry();
    private final Random random = new Random();

    private final Map<TILE, GlyphStyle[]> tileRegistry = new EnumMap<>(TILE.class);
    private final Map<Class<? extends InteractableTile>, GlyphStyle> interactableTileRegistry = new HashMap<>();
    private final Map<Class<? extends Entity>, GlyphStyle> entityRegistry = new HashMap<>();

    // High-fidelity fallback styles
    private final GlyphStyle voidStyle = new GlyphStyle(" ", Color.BLACK);
    private final GlyphStyle defaultEntityStyle = new GlyphStyle("?", Color.web("#D500F9"));
    private final GlyphStyle defaultInteractableTileStyle = new GlyphStyle("X", Color.web("#D500F9"));

    public static class GlyphStyle {
        public final String glyph;
        public final Color color;

        public GlyphStyle(String glyph, Color color) {
            this.glyph = glyph;
            this.color = color;
        }
    }

    private GlyphRegistry() {
        // --- COLOR PALETTE DEFINITION (Gothic Catacomb Theme) ---
        Color wallColor    = Color.web("#4A515B"); // Muted slate gray
        Color floorColorDim = Color.web("#1A261F"); // Dim background moss green
        Color floorColorLit = Color.web("#23362B"); // Slightly lighter accent green
        Color doorColor     = Color.web("#CD853F"); // Warm Peruvian brown/amber

        Color webColor = Color.SILVER;

        Color playerColor   = Color.web("#FFD700"); // Rich radiant gold
        Color zombieColor  = Color.DARKOLIVEGREEN;
        Color giantSpiderColor = Color.ROSYBROWN;

        // --- MULTI-TEXTURE STRUCTURAL REGISTRY ---
        // Registering arrays allows the engine to pull random variants, preventing repetitive grid patterns
        tileRegistry.put(TILE.WALL, new GlyphStyle[]{
                new GlyphStyle("▒", wallColor), // 60% standard textured masonry block
                new GlyphStyle("▒", wallColor),
                new GlyphStyle("▒", wallColor),
                new GlyphStyle("▓", wallColor.brighter()), // 20% heavy prominent brick accent
                new GlyphStyle("░", wallColor.darker())    // 20% cracked/eroded brick accent
        });

        tileRegistry.put(TILE.FLOOR, new GlyphStyle[]{
                new GlyphStyle("·", floorColorDim), // 70% standard clean gravel floor point
                new GlyphStyle("·", floorColorDim),
                new GlyphStyle(".", floorColorDim), // 20% tiny low-profile floor speck
                new GlyphStyle("░", floorColorLit)  // 10% patch of tall surface texture/moss
        });

        tileRegistry.put(TILE.DOOR, new GlyphStyle[]{
                new GlyphStyle("⌸", doorColor) // Heavy architectural iron-reinforced gate symbol
        });

        // --- INTERACTABLE TILE REGISTRY ---
        registerInteractableTile(Web.class, "#", webColor);

        // --- CHARACTER ENTITY REGISTRY ---
        registerEntity(Player.class, "@", playerColor);
        registerEntity(Zombie.class, "Z", zombieColor);
        registerEntity(GiantSpider.class, "S", giantSpiderColor);
    }

    public static GlyphRegistry getInstance() { return instance; }

    // Overloaded setup tool to make manual hot-swapping simple
    public void registerTile(TILE tile, String glyph, Color color) {
        tileRegistry.put(tile, new GlyphStyle[]{ new GlyphStyle(glyph, color) });
    }

    public void registerInteractableTile(Class<? extends InteractableTile> clazz, String glyph, Color color) {
        interactableTileRegistry.put(clazz, new GlyphStyle(glyph, color));
    }

    public void registerEntity(Class<? extends Entity> clazz, String glyph, Color color) {
        entityRegistry.put(clazz, new GlyphStyle(glyph, color));
    }

    /**
     * Resolves a persistent style variant for a specific tile based on coordinates
     * and the specific room's unique signature ID.
     */
    public GlyphStyle getStyle(TILE tile, int roomX, int roomY, int roomId) {
        GlyphStyle[] options = tileRegistry.get(tile);
        if (options == null || options.length == 0) return voidStyle;

        if (options.length > 1) {
            // Mix the unique Room ID into the coordinate hashing formula
            // This ensures the structural patterns shift completely between different rooms
            int hash = (roomX * 31) ^ (roomY * 17) ^ (roomId * 13);
            int persistentIndex = Math.abs(hash) % options.length;

            return options[persistentIndex];
        }
        return options[0];
    }
    public GlyphStyle getStyle(InteractableTile tile) {
        return interactableTileRegistry.getOrDefault(tile.getClass(), defaultInteractableTileStyle);
    }

    public GlyphStyle getStyle(Entity entity) {
        return entityRegistry.getOrDefault(entity.getClass(), defaultEntityStyle);
    }

    public GlyphStyle getVoidStyle() { return voidStyle; }
}
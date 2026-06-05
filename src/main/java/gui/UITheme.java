package gui;

import javafx.scene.paint.Color;

/**
 * Centered Color & Typography Palette Configuration.
 * Tweak these variables to instantly change how the entire game looks and reads.
 */
public class UITheme {

    // --- TYPOGRAPHY ENGINE CONFIGURATIONS ---
    public static final String FONT_FAMILY      = "Courier New";
    public static final String CSS_FONT_FAMILY  = "-fx-font-family: '" + FONT_FAMILY + "';";

    // Dynamic Sizing Rules
    public static final double DEFAULT_ZOOM    = 18.0;
    public static final double MIN_ZOOM        = 10.0;
    public static final double MAX_ZOOM        = 45.0;

    // Fixed Sidebar Sizing Rules
    public static final double FONT_SIZE_HEADER = 13.0;
    public static final double FONT_SIZE_TEXT   = 13.0;
    public static final double FONT_SIZE_LOG    = 12.0;
    public static final double FONT_SIZE_CTRL   = 12.0;

    // Pre-composed Font Style Mixins for Quick FXML/Label Injection
    public static final String STYLE_HEADER = CSS_FONT_FAMILY + " -fx-font-size: " + FONT_SIZE_HEADER + "px; -fx-font-weight: bold;";
    public static final String STYLE_TEXT   = CSS_FONT_FAMILY + " -fx-font-size: " + FONT_SIZE_TEXT + "px;";
    public static final String STYLE_LOG    = CSS_FONT_FAMILY + " -fx-font-size: " + FONT_SIZE_LOG + "px; -fx-font-weight: bold;";
    public static final String STYLE_CTRL   = CSS_FONT_FAMILY + " -fx-font-size: " + FONT_SIZE_CTRL + "px;";

    // --- STRUCTURAL SIDEBAR INTERFACE LAYOUTS (HEX Strings) ---
    public static final String BG_ROOT         = "#000000";
    public static final String BG_CARD         = "#000000";
    public static final String BORDER_NORMAL   = "#333333";
    public static final String BORDER_HIGHLIGHT= "#FFFFFF";

    // --- SYSTEM CANVAS GENERAL COLORS ---
    public static final Color CANVAS_VOID      = Color.BLACK;
    public static final Color TEXT_PARCHMENT   = Color.WHITE;
    public static final Color TEXT_MUTED       = Color.DARKGRAY;

    // --- MAP GRID STRUCTURAL ASSETS ---
    public static final Color WORLD_WALL       = Color.web("#4A515B");
    public static final Color WORLD_WALL_ALT   = Color.web("#636B75");
    public static final Color WORLD_FLOOR_DIM  = Color.web("#1A261F");
    public static final Color WORLD_FLOOR_LIT  = Color.web("#23362B");
    public static final Color WORLD_DOOR       = Color.web("#CD853F");

    // --- INTERACTABLE OBJECT LOOT PALETTE ---
    public static final Color LOOT_WEB         = Color.SILVER;
    public static final Color LOOT_CHEST       = Color.SADDLEBROWN;
    public static final Color LOOT_WEAPON      = Color.LIGHTCYAN;

    // --- CHARACTER ENTITIES PALETTE ---
    public static final Color ENTITY_PLAYER    = Color.web("#FFD700");
    public static final Color ENTITY_ZOMBIE    = Color.DARKOLIVEGREEN;
    public static final Color ENTITY_SPIDER    = Color.ROSYBROWN;

    // --- STATS HUD & LOG CHANNELS ---
    public static final Color STAT_HEALTH      = Color.RED;
    public static final Color STAT_HUNGER      = Color.CHOCOLATE;
    public static final Color STAT_ARMOR       = Color.LIGHTSTEELBLUE;
    public static final Color STAT_WEAPON      = Color.LIGHTGRAY;
    public static final Color STAT_GOLD        = Color.GOLD;
    public static final Color STAT_POTION      = Color.AQUAMARINE;

    public static final Color OVERLAY_RETICLE  = Color.AQUA;
    public static final Color OVERLAY_MODAL    = Color.GOLD;

    public static final Color LOG_WORLD        = Color.LIGHTBLUE;
    public static final Color LOG_PLAYER_ACTION= Color.YELLOW;
    public static final Color LOG_CRITICAL     = Color.RED;
    public static final Color LOG_MONSTER_ACTION     = Color.RED;
    public static final Color LOG_PLAYER_KILLS     = Color.GREEN;
}
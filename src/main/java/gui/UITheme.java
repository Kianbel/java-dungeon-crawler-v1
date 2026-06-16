package gui;

import javafx.scene.paint.Color;

/**
 * Centered Color & Typography Palette Configuration.
 * Tweak these variables to instantly change how the entire game looks and reads.
 */
public class UITheme {

    // --- TYPOGRAPHY ENGINE CONFIGURATIONS ---
    public static final String GENERAL_FONT_FAMILY = "Courier New";
    public static final String CSS_FONT_FAMILY  = "-fx-font-family: '" + GENERAL_FONT_FAMILY + "';";
    public static final String TEXT_POPUP_FONT_FAMILY = "Inter";

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

    /* https://lospec.com/palette-list/micro-roguelike */
    // --- TILES ---
//    public static final Color TILE_WALL = Color.web("#43434f");
//    public static final Color TILE_DOOR = Color.web("#ba6156");
//    public static final Color TILE_FLOOR = Color.web("#222323");
//    public static final Color TILE_GRASS = Color.web("#3ca370");
//    public static final Color TILE_WATER = Color.web("#6476e8");
//    public static final Color TILE_BOOKSHELF = Color.web("#8c3f5d");
//    public static final Color TILE_PASSABLE_OBSTACLE = Color.web("#43434f");
//    public static final Color TILE_SOLID_OBSTACLE = Color.PINK;

    public static final Color TILE_WALL = Color.web("#e36433");
    public static final Color TILE_DOOR = Color.web("#cf492c");
    public static final Color TILE_FLOOR = Color.web("#731144");
    public static final Color TILE_GRASS = Color.web("#e3884e");
    public static final Color TILE_WATER = Color.web("#ecb55f");
    public static final Color TILE_BOOKSHELF = Color.web("#8c3f5d");
    public static final Color TILE_PASSABLE_OBSTACLE = Color.web("#43434f");
    public static final Color TILE_SOLID_OBSTACLE = Color.PINK;

    // --- INTERACTABLE TILES ---
    public static final Color ITILE_BOX = Color.web("#ba6156");
    public static final Color ITILE_BROKEN_BOX = Color.web("#ba6156");
    public static final Color ITILE_DROPPED_WEAPON = Color.web("#ffffeb");
    public static final Color ITILE_COIN = Color.web("#ffe478");
    public static final Color ITILE_HEART = Color.web("#eb564b");
    public static final Color ITILE_FIRE = Color.web("#f2a65e");
    public static final Color ITILE_WEB = Color.web("#c2c2d1");
    public static final Color ITILE_CHEST = Color.web("#8c3f5d");

    // --- ENTITIES PALETTE ---
    public static final Color ENTITY_PLAYER = Color.web("#ffe478");
    public static final Color ENTITY_ZOMBIE = Color.web("#3ca370");
    public static final Color ENTITY_SPIDER = Color.web("#8c3f5d");
    public static final Color ENTITY_FLARE_WITCH = Color.web("#eb564b");

    // --- PROJECTILES
    public static final Color PROJECTILE_FIREBALL = Color.web("#f2a65e");

    // --- STATS HUD & LOG CHANNELS ---
    public static final Color STAT_HEALTH = Color.RED;
    public static final Color STAT_HUNGER = Color.CHOCOLATE;
    public static final Color STAT_ARMOR = Color.LIGHTSTEELBLUE;
    public static final Color STAT_WEAPON = Color.LIGHTGRAY;
    public static final Color STAT_GOLD = Color.GOLD;
    public static final Color STAT_POTION = Color.AQUAMARINE;

    public static final Color LOG_WORLD = Color.WHITE;
    public static final Color LOG_PLAYER_ACTION = Color.YELLOW;
    public static final Color LOG_CRITICAL = Color.RED;
    public static final Color LOG_MONSTER_ACTION = Color.RED;
    public static final Color LOG_DEV = Color.HOTPINK;

    // --- ATTACK DAMAGE INDICATORS ---
    public static final Color PLAYER_TAKE_DAMAGE = Color.RED;
    public static final Color NORMAL_DAMAGE = Color.WHITE;
    public static final Color CRITICAL_DAMAGE = Color.DARKRED;
    public static final Color MISS = Color.YELLOW;
}
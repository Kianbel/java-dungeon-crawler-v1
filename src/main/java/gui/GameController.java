package gui;

import entity.Entity;
import entity.Player;
import core.DungeonManager;
import core.EntityRoomManager;
import core.room.Room;
import util.Position;
import util.TILE;
import weapon.Weapon;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    @FXML private HBox rootContainer;
    @FXML private StackPane canvasContainer;
    @FXML private Canvas renderCanvas;
    @FXML private VBox logContainer;
    @FXML private VBox statsPanel, logsPanel, controlsPanel, controlsBox;
    @FXML private Label statsHeader, logsHeader, controlsHeader;
    @FXML private Label lblHealth, lblHunger, lblCoins, lblPotions;
    @FXML private Label healthBarText, healthValText;
    @FXML private Label hungerBarText, hungerValText;
    @FXML private Label armorText, weaponText, coinsText, potionsText;

    private GameCanvas monitorTerminal;
    private Viewport cameraFrame;
    private final List<OverlayComponent> runningOverlays = new ArrayList<>();

    private TargetReticle targetSelector;
    private MenuModal confirmationPrompt;

    // --- TILES ---
    private double currentTileSize = 44.0;
    private final double MIN_TILE_SIZE = 6.0;
    private final double MAX_TILE_SIZE = 60.0;
    private final double TILE_SIZE_CHANGE_AMOUNT = 2.0;

    // --- LOGS ---
    private final int MAX_LOG_LINES = 15;

    @FXML
    public void initialize() {
        // 1. Unmanage the Canvas to protect against layout scaling loops
        renderCanvas.setManaged(false);

        // 2. Build Base Layout Frameworks
        monitorTerminal = new GameCanvas(renderCanvas, currentTileSize);
        cameraFrame = new Viewport(monitorTerminal.getGridColumns(), monitorTerminal.getGridRows(), 6);

        targetSelector = new TargetReticle(monitorTerminal.getGridColumns(), monitorTerminal.getGridRows());
        confirmationPrompt = new MenuModal("PROCEED INTO THE DARKNESS?");

        runningOverlays.add(confirmationPrompt);
        runningOverlays.add(targetSelector);

        GUIManager.getInstance().registerController(this);
        DungeonManager.getInstance().generateDungeon();

        // 3. Apply Unified Dynamic Theme Injection across all HUD Containers
        applyInterfaceTheme();

        // 4. Bind window resize handlers
        canvasContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            renderCanvas.setWidth(newVal.doubleValue());
            handleWindowResize();
        });
        canvasContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            renderCanvas.setHeight(newVal.doubleValue());
            handleWindowResize();
        });

        attachKeyboardHandlers();
    }

    /**
     * Programmatically styles layout cards to ensure UI settings remain centralized.
     */
    private void applyInterfaceTheme() {
        // Root Container Node Context
        rootContainer.setStyle("-fx-background-color: " + UITheme.BG_ROOT + "; " + UITheme.CSS_FONT_FAMILY);
        canvasContainer.setStyle("-fx-border-color: " + UITheme.BORDER_HIGHLIGHT + "; -fx-border-width: 2; -fx-background-color: #000000;");

        // Sidebar Dashboard Panel Containers
        String subPanelStyle = "-fx-border-color: " + UITheme.BORDER_NORMAL + "; -fx-border-width: 2; -fx-background-color: " + UITheme.BG_CARD + ";";
        statsPanel.setStyle(subPanelStyle);
        logsPanel.setStyle(subPanelStyle);
        controlsPanel.setStyle(subPanelStyle);

        // Header Captions Style bindings using centralized font definitions
        String headerStyle = UITheme.STYLE_HEADER + " -fx-text-fill: " + toHexWebColor(UITheme.OVERLAY_MODAL) + ";";
        statsHeader.setStyle(headerStyle);
        logsHeader.setStyle(headerStyle);
        controlsHeader.setStyle(headerStyle);

        // Descriptive Static Label Elements Font Paints
        String labelStaticStyle = UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";";
        lblHealth.setStyle(labelStaticStyle);
        lblHunger.setStyle(labelStaticStyle);
        lblCoins.setStyle(labelStaticStyle);
        lblPotions.setStyle(labelStaticStyle);

        // Dynamic Text Metric Value Fields Style updates
        String activeMetricStyle = UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_PARCHMENT) + "; -fx-font-weight: bold;";
        healthValText.setStyle(activeMetricStyle);
        hungerValText.setStyle(activeMetricStyle);
        coinsText.setStyle(activeMetricStyle);
        potionsText.setStyle(activeMetricStyle);

        // Custom Visual Colors for specific UI Components
        healthBarText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HEALTH) + ";");
        hungerBarText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HUNGER) + ";");
        armorText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_ARMOR) + "; -fx-font-weight: bold;");
        weaponText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_WEAPON) + "; -fx-font-weight: bold;");

        buildControlsReferenceHud();
    }

    private void buildControlsReferenceHud() {
        controlsBox.getChildren().clear();
        String[] mappings = {
                "[WASD]  Move Explorer", "[SPACE] Rest / Skip Turn",
                "[E]     Interact Structure", "[M]     Toggle Target Scope",
                "[P]     Prompt Choice Menu", "[+ / -] Adjust Camera Zoom"
        };
        for (String item : mappings) {
            Label element = new Label(item);
            // Apply centralized style layout variables
            element.setStyle(UITheme.STYLE_CTRL + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";");
            controlsBox.getChildren().add(element);
        }
    }

    private void handleWindowResize() {
        if (monitorTerminal == null) return;
        monitorTerminal.updateFontSize(currentTileSize);
        int cols = monitorTerminal.getGridColumns();
        int rows = monitorTerminal.getGridRows();
        cameraFrame.updateScreenDimensions(cols, rows);
        targetSelector.updateBounds(cols, rows);
        confirmationPrompt.updateScreenDimensions(cols, rows);
        updateRenderingPipeline();
    }

    public void adjustTileSize(double delta) {
        double newSize = currentTileSize + delta;
        if (newSize < MIN_TILE_SIZE || newSize > MAX_TILE_SIZE) return;
        this.currentTileSize = newSize;
        handleWindowResize();
    }

    public void updateRenderingPipeline() {
        Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
        if (activeRoom == null) return;

        Player player = (Player) EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom)
                .stream().filter(e -> e instanceof Player).findFirst().orElse(null);

        TILE[][] layout = activeRoom.getLayout();
        int worldH = layout.length;
        int worldW = (worldH > 0) ? layout[0].length : 0;

        if (player != null) {
            cameraFrame.updateCameraFocus(player.position, worldW, worldH);
        }

        monitorTerminal.clearCanvas();
        GlyphRegistry glyphs = GlyphRegistry.getInstance();

        List<Entity> entityList = EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom);
        var itemTiles = activeRoom.getInteractableTiles();

        for (int sy = 0; sy < cameraFrame.getScreenHeight(); sy++) {
            for (int sx = 0; sx < cameraFrame.getScreenWidth(); sx++) {

                Position worldLoc = cameraFrame.toWorldSpace(sx, sy);
                String activeChar = glyphs.getVoidStyle().glyph;
                Color activeColor = UITheme.CANVAS_VOID;

                if (worldLoc.x >= 0 && worldLoc.x < worldW && worldLoc.y >= 0 && worldLoc.y < worldH) {
                    // Layer 1: Base Floor/Wall Structures
                    TILE structuralTile = layout[worldLoc.y][worldLoc.x];
                    if (structuralTile != null) {
                        var style = glyphs.getStyle(structuralTile, worldLoc.x, worldLoc.y, activeRoom.id);
                        activeChar = style.glyph;
                        activeColor = style.color; // Pulls directly from your registry config
                    }

                    // Layer 2: Interactable Map Loot Objects
                    for (var item : itemTiles) {
                        if (item.roomLayoutPosition.x == worldLoc.x && item.roomLayoutPosition.y == worldLoc.y) {
                            var style = glyphs.getStyle(item);
                            activeChar = style.glyph;
                            activeColor = style.color;
                            break;
                        }
                    }

                    // Layer 3: Living Entities (Player & Monsters)
                    for (Entity ent : entityList) {
                        if (ent.position.x == worldLoc.x && ent.position.y == worldLoc.y) {
                            var style = glyphs.getStyle(ent);
                            activeChar = style.glyph;
                            activeColor = style.color; // No dynamic dimming transformations, pure colors
                            break;
                        }
                    }
                }

                // Layer 4: Screen Interface Overlays (Menus / Cursors)
                for (OverlayComponent overlay : runningOverlays) {
                    if (overlay.isComponentActive() && overlay.interceptCellRendering(sx, sy)) {
                        activeChar = overlay.getCustomGlyph(sx, sy, activeChar);
                        activeColor = overlay.getCustomColor(sx, sy, activeColor);
                    }
                }

                monitorTerminal.drawCharacter(sx, sy, activeChar, activeColor);
            }
        }
    }

    private void attachKeyboardHandlers() {
        renderCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    KeyCode code = e.getCode();

                    for (OverlayComponent overlay : runningOverlays) {
                        if (overlay.isComponentActive()) {
                            if (overlay.interpretKeystroke(code)) {
                                updateRenderingPipeline();
                                return;
                            }
                        }
                    }

                    if (code == KeyCode.EQUALS) { adjustTileSize(TILE_SIZE_CHANGE_AMOUNT); return; }
                    if (code == KeyCode.MINUS) { adjustTileSize(-TILE_SIZE_CHANGE_AMOUNT); return; }

                    if (code == KeyCode.M) { targetSelector.toggleState(); updateRenderingPipeline(); return; }
                    if (code == KeyCode.P) {
                        confirmationPrompt.invokePrompt(choice -> addLog((choice == 0 ? "EXOLORER VENTURES FORWARD" : "EXPLORER HESITATES"), UITheme.LOG_PLAYER_ACTION));
                        updateRenderingPipeline();
                        return;
                    }

                    Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
                    Player player = (Player) EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom)
                            .stream().filter(ent -> ent instanceof Player).findFirst().orElse(null);

                    if (player == null) return;
                    Position movementVector = new Position(0, 0);
                    boolean isTickAction = true;

                    switch (code) {
                        case W -> movementVector.y--;
                        case A -> movementVector.x--;
                        case S -> movementVector.y++;
                        case D -> movementVector.x++;
                        case SPACE -> movementVector = new Position(0,0);
                        default -> isTickAction = false;
                    }

                    if (isTickAction) {
                        logContainer.getChildren().forEach(n -> ((Label) n).setStyle("-fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";"));
                        player.handleMove(movementVector);

                        EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom).stream()
                                .filter(ent -> ent instanceof entity.Monster)
                                .forEach(m -> ((entity.Monster)m).makeMove());

                        updateRenderingPipeline();
                    }
                });
            }
        });
    }

    public void updateHealth(int hp) { healthValText.setText(hp + "/100"); healthBarText.setText(buildBarMeter(hp)); }
    public void updateHunger(int hg) { hungerValText.setText(hg + "/100"); hungerBarText.setText(buildBarMeter(hg)); }
    public void updateArmor(int arm) { armorText.setText("Armor: " + arm + "/10"); }
    public void updateWeapon(Weapon w) { weaponText.setText("Weapon: " + w.name); }
    public void updateCoins(int count) { coinsText.setText(String.valueOf(count)); }
    public void updatePotions(int count) { potionsText.setText(String.valueOf(count)); }

    private String buildBarMeter(int val) {
        int fill = (int) Math.round((Math.max(0, Math.min(100, val)) / 100.0) * 15);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 15; i++) sb.append(i < fill ? "■" : "·");
        return sb.append("]").toString();
    }

    public void addLog(String txt, Color col) {
        Label element = new Label(txt);
        // Inject font parameters safely straight from theme file definitions
        element.setStyle(UITheme.STYLE_LOG + " -fx-text-fill: " + toHexWebColor(col) + ";");
        element.setWrapText(true);

        if (logContainer.getChildren().size() >= MAX_LOG_LINES) logContainer.getChildren().removeFirst();
        logContainer.getChildren().add(element);
    }

    public void clearLogContainer() { logContainer.getChildren().clear(); }
    public void flashScreenEffect(Color col) { updateRenderingPipeline(); }

    private String toHexWebColor(Color c) {
        return String.format("#%02X%02X%02X", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }
}
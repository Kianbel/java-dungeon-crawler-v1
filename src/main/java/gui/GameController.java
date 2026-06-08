package gui;

import entity.Entity;
import entity.Monster;
import entity.Player;
import core.DungeonManager;
import core.EntityRoomManager;
import core.room.type.Room;
import javafx.animation.FadeTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
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
import world.InteractableTile;

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

    private GameCanvas gameCanvas;
    private Viewport viewport;

    // --- TILES ---
    private double currentTileSize = 44.0;
    private final double MIN_TILE_SIZE = 6.0;
    private final double MAX_TILE_SIZE = 60.0;
    private final double TILE_SIZE_CHANGE_AMOUNT = 2.0;

    // --- LOGS ---
    private final int MAX_LOG_LINES = 8;

    // --- ENEMY ATTACK SLIDE OFFSET ANIMATION ---
    // Tracks temporary visual displacements for animating entities
    private final java.util.Map<Entity, RenderOffset> transientOffsets = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        // 1. Unmanage the Canvas to protect against layout scaling loops
        renderCanvas.setManaged(false);

        // 2. Build Base Layout Frameworks
        gameCanvas = new GameCanvas(renderCanvas, currentTileSize);
        viewport = new Viewport(gameCanvas.getGridColumns(), gameCanvas.getGridRows(), 6);

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
                "[WASD]  Move Explorer",
                "[SPACE] Rest / Skip Turn",
                "[E]     Interact Structure",
                "[+ / -] Adjust Camera Zoom"
        };
        for (String item : mappings) {
            Label element = new Label(item);
            // Apply centralized style layout variables
            element.setStyle(UITheme.STYLE_CTRL + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";");
            controlsBox.getChildren().add(element);
        }
    }

    private void handleWindowResize() {
        if (gameCanvas == null) return;
        gameCanvas.updateFontSize(currentTileSize);
        int cols = gameCanvas.getGridColumns();
        int rows = gameCanvas.getGridRows();
        viewport.updateScreenDimensions(cols, rows);

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
                .stream()
                .filter(entity -> entity instanceof Player)
                .findFirst()
                .orElse(null);

        TILE[][] roomLayout = activeRoom.getLayout();
        int roomHeight = roomLayout.length;
        int roomWidth = (roomHeight > 0) ? roomLayout[0].length : 0;

        if (player != null) {
            viewport.updateCameraFocus(player.position, roomWidth, roomHeight);
        }

        gameCanvas.clearCanvas();
        GlyphRegistry glyphs = GlyphRegistry.getInstance();

        List<Entity> entitiesInRoom = EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom);
        List<InteractableTile> interactableTiles = activeRoom.getInteractableTiles();

        // Iterate through every cell on the visible screen viewport
        for (int screenY = 0; screenY < viewport.getScreenHeight(); screenY++) {
            for (int screenX = 0; screenX < viewport.getScreenWidth(); screenX++) {

                Position worldPosition = viewport.toWorldSpace(screenX, screenY);

                boolean isWithinRoomBounds = worldPosition.x >= 0 && worldPosition.x < roomWidth && worldPosition.y >= 0 && worldPosition.y < roomHeight;
                if (!isWithinRoomBounds) {
                    // draw void character
                    gameCanvas.drawCharacter(screenX, screenY, glyphs.getVoidStyle().glyph, UITheme.CANVAS_VOID, 0.0, 0.0);
                    continue;
                }

                // --- INLINE LAYERING PIPELINE ---
                String activeGlyph = glyphs.getVoidStyle().glyph;
                Color activeColor = UITheme.CANVAS_VOID;

                // Layer 1: Base Floor/Wall Structures
                TILE structuralTile = roomLayout[worldPosition.y][worldPosition.x];
                if (structuralTile != null) {
                    GlyphRegistry.GlyphStyle structuralStyle;
                    if (structuralTile == TILE.WATER) {
                        structuralStyle = glyphs.getStyle(structuralTile);
                    } else {
                        structuralStyle = glyphs.getStyle(structuralTile, worldPosition.x, worldPosition.y, activeRoom.id);
                    }
                    activeGlyph = structuralStyle.glyph;
                    activeColor = structuralStyle.color;
                }

                // Layer 2: Interactable Map Loot Objects
                for (InteractableTile item : interactableTiles) {
                    if (item.roomLayoutPosition.x == worldPosition.x && item.roomLayoutPosition.y == worldPosition.y) {
                        GlyphRegistry.GlyphStyle itemStyle = glyphs.getStyle(item);
                        activeGlyph = itemStyle.glyph;
                        activeColor = itemStyle.color;
                        break;
                    }
                }

                // Layer 3: Living Entities
                Monster damagedMonsterOverlayTarget = null;
                double entityPixelOffsetX = 0.0;
                double entityPixelOffsetY = 0.0;

                for (Entity entity : entitiesInRoom) {
                    if (entity.position.x == worldPosition.x && entity.position.y == worldPosition.y) {
                        GlyphRegistry.GlyphStyle entityStyle = glyphs.getStyle(entity);
                        activeGlyph = entityStyle.glyph;

                        // Fetch dynamic presentation offsets from the UI mapping
                        if (transientOffsets.containsKey(entity)) {
                            RenderOffset animationOffset = transientOffsets.get(entity);
                            entityPixelOffsetX = animationOffset.x;
                            entityPixelOffsetY = animationOffset.y;
                        }

                        if (entity instanceof Player) {
                            activeColor = entityStyle.color;
                        } else {
                            // Generate unique color variations dynamically based on Monster identity hashes
                            double hue = entityStyle.color.getHue();
                            double saturation = Math.abs((double) (entity.id * 13 % 7 * 19 % 50) / 100) + 0.5;
                            double brightness = Math.abs((double) (entity.id * 19 % 13 * 23 % 50) / 100) + 0.5;
                            activeColor = Color.hsb(hue, saturation, brightness);

                            if (entity instanceof Monster && entity.health > 0 && entity.health < entity.maxHealth) {
                                damagedMonsterOverlayTarget = (Monster) entity;
                            }
                        }
                        break;
                    }
                }

                // Final rendering execution pass
                gameCanvas.drawCharacter(screenX, screenY, activeGlyph, activeColor, entityPixelOffsetX, entityPixelOffsetY);

                if (damagedMonsterOverlayTarget != null) {
                    double healthPercent = (double) damagedMonsterOverlayTarget.health / damagedMonsterOverlayTarget.maxHealth;
                    gameCanvas.drawHealthBar(screenX, screenY, healthPercent, entityPixelOffsetX, entityPixelOffsetY);
                }
            }
        }
    }

    private void attachKeyboardHandlers() {
        renderCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    KeyCode code = e.getCode();

                    if (code == KeyCode.EQUALS) { adjustTileSize(TILE_SIZE_CHANGE_AMOUNT); return; }
                    if (code == KeyCode.MINUS) { adjustTileSize(-TILE_SIZE_CHANGE_AMOUNT); return; }

                    Player player = (Player) EntityRoomManager.getInstance().getPlayer();

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
//                        logContainer.getChildren().forEach(n -> ((Label) n).setStyle("-fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";"));
                        player.handleMove(movementVector);

                        Room currentRoom = EntityRoomManager.getInstance().getPlayerRoom();
                        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
                        for(Entity entity : entities) {
                            if(entity instanceof Monster m) {
                                m.makeMove();
                            }
                        }

                        updateRenderingPipeline();
                    }
                });
            }
        });
    }

    public void updateHealth(int hp) { healthValText.setText(hp + "/100"); healthBarText.setText(buildBarMeter(hp)); }
    public void updateHunger(int hg) { hungerValText.setText(hg + "/100"); hungerBarText.setText(buildBarMeter(hg)); }
    public void updateArmor(int arm) { armorText.setText("Armor: " + arm + "/10"); }
    public void updateWeapon(Weapon w) { weaponText.setText("Weapon: " + w); }
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
    public void flashScreenEffect(Color color, int durationInMilis) {
        Rectangle flashOverlay = new Rectangle();
        flashOverlay.widthProperty().bind(canvasContainer.widthProperty());
        flashOverlay.heightProperty().bind(canvasContainer.heightProperty());
        flashOverlay.setFill(color);
        flashOverlay.setOpacity(1);
        flashOverlay.setMouseTransparent(true);

        canvasContainer.getChildren().add(flashOverlay);

        FadeTransition transition = new FadeTransition(Duration.millis(durationInMilis), flashOverlay);
        transition.setFromValue(1);
        transition.setToValue(0);

        transition.setOnFinished(event -> {
            canvasContainer.getChildren().remove(flashOverlay);
            updateRenderingPipeline(); // Final screen refresh
        });
        flashOverlay.setManaged(false);

        transition.play();
    }

    public void triggerAttackAnimation(Entity attacker, Entity target) {
        int dx = target.position.x - attacker.position.x;
        int dy = target.position.y - attacker.position.y;

        double targetPixelX = dx * (gameCanvas.getGridColumns() > 0 ? (renderCanvas.getWidth() / gameCanvas.getGridColumns()) : currentTileSize) * 0.5;
        double targetPixelY = dy * currentTileSize * 0.5;

        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        int totalFrames = 10;
        double durationMs = 120.0;

        for (int i = 0; i <= totalFrames; i++) {
            final int frame = i;
            javafx.animation.KeyFrame keyFrame = new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis((durationMs / totalFrames) * frame),
                    event -> {
                        double progress = (double) frame / totalFrames;
                        double scale = 1.0 - Math.abs(2.0 * progress - 1.0); // Curve math

                        double currentX = targetPixelX * scale;
                        double currentY = targetPixelY * scale;

                        // Update or clear the transient UI state mapping
                        if (frame == totalFrames) {
                            transientOffsets.remove(attacker); // Animation finished, clear memory!
                        } else {
                            transientOffsets.put(attacker, new RenderOffset(currentX, currentY));
                        }

                        updateRenderingPipeline();
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
    }

    private String toHexWebColor(Color c) {
        return String.format("#%02X%02X%02X", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }
}
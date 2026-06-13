package gui;

import entity.Entity;
import entity.monster.Monster;
import entity.Player;
import core.DungeonManager;
import core.EntityRoomManager;
import core.room.type.Room;
import entity.projectile.Projectile;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.ANIMATION_CURVE;
import util.MAP;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {

    @FXML private HBox rootContainer;
    @FXML private StackPane canvasContainer;
    @FXML private Canvas canvas;
    @FXML private VBox logContainer;
    @FXML private VBox statsPanel, logsPanel, controlsPanel, controlsBox;
    @FXML private Label statsHeader, logsHeader, controlsHeader;
    @FXML private Label lblHealth, lblHunger, lblCoins, lblPotions;
    @FXML private Label healthBarText, healthValText;
    @FXML private Label hungerBarText, hungerValText;
    @FXML private Label armorText, weaponText, coinsText, potionsText;

    private GameCanvas gameCanvas;
    private Viewport viewport;

    // --- BOOLEANS ---
    private boolean isMapOpen = false;

    // --- TILES ---
    private double currentTileSize = 50;
    private final double MIN_TILE_SIZE = 6.0;
    private final double MAX_TILE_SIZE = 70.0;
    private final double TILE_SIZE_CHANGE_AMOUNT = 2.0;

    // --- DARKNESS ---
    private enum LIGHT_LEVEL {
        ILLUMINATED,
        DIM,
        PURE_DARKNESS,
    }

    private final double DARKNESS_DISTANCE = 4; // default: 4
    private final double TOTAL_DARKNESS_DISTANCE_MULTIPLIER = 1.5;

    // --- LOGS ---
    private final int MAX_LOG_LINES = 8;

    // --- ENEMY ATTACK SLIDE OFFSET ANIMATION ---
    private final Map<Entity, RenderOffset> entityAnimationPixelDrawOffsets = new HashMap<>();

    @FXML
    public void initialize() {
        // 1. Unmanage the Canvas to protect against layout scaling loops
        canvas.setManaged(false);

        // 2. Build Base Layout Frameworks
        gameCanvas = new GameCanvas(canvas, currentTileSize);
        viewport = new Viewport(gameCanvas.getGridColumns(), gameCanvas.getGridRows(), 6);

        GUIManager.getInstance().registerController(this);
        DungeonManager.getInstance().generateDungeon();

        // 3. Apply Unified Dynamic Theme Injection across all HUD Containers
        applyInterfaceTheme();

        // 4. Bind window resize handlers
        canvasContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            handleWindowResize();
        });
        canvasContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
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
        final Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
        if (activeRoom == null) return;

        final Player player = (Player) EntityRoomManager.getInstance().getPlayer();

        final TILE[][] roomLayout = activeRoom.getLayout();

        final int ROOM_HEIGHT = roomLayout.length;
        final int ROOM_WIDTH = (ROOM_HEIGHT > 0) ? roomLayout[0].length : 0;
        if (player != null) {
            viewport.updateCameraFocus(player.position, ROOM_WIDTH, ROOM_HEIGHT);
        }

        gameCanvas.clearCanvas();
        GlyphRegistry glyphRegistry = GlyphRegistry.getInstance();


        for (int screenY = 0; screenY < viewport.getScreenHeight(); screenY++) {
            for (int screenX = 0; screenX < viewport.getScreenWidth(); screenX++) {

                Position worldPosition = viewport.toWorldPosition(screenX, screenY);

                boolean isWithinRoomBounds = worldPosition.x >= 0 && worldPosition.x < ROOM_WIDTH && worldPosition.y >= 0 && worldPosition.y < ROOM_HEIGHT;
                if (!isWithinRoomBounds) { // draw void character
                    gameCanvas.drawCharacter(screenX, screenY, glyphRegistry.getVoidStyle().glyph(), UITheme.CANVAS_VOID, 0.0, 0.0);
                    continue;
                }

                // --- INLINE LAYERING PIPELINE ---
                String activeGlyph = glyphRegistry.getVoidStyle().glyph();
                Color activeColor = UITheme.CANVAS_VOID;

                // Layer 1: Base Floor/Wall Structures
                TILE tile = roomLayout[worldPosition.y][worldPosition.x];
                if (tile != null) {
                    GlyphStyle tileStyle;
                    if (tile == TILE.WATER) {
                        tileStyle = glyphRegistry.getStyle(tile);
                    } else {
                        tileStyle = glyphRegistry.getStyle(tile, worldPosition.x, worldPosition.y, activeRoom.id);
                    }
                    activeGlyph = tileStyle.glyph();
                    activeColor = tileStyle.color();
                }

                // Layer 2: Interactable Map Loot Objects
                List<InteractableTile> interactableTiles = activeRoom.getInteractableTiles();
                for (InteractableTile interactableTile : interactableTiles) {
                    if (interactableTile.roomLayoutPosition.equals(worldPosition)) {
                        GlyphStyle interactableTileStyle = glyphRegistry.getStyle(interactableTile);
                        activeGlyph = interactableTileStyle.glyph();
                        activeColor = interactableTileStyle.color();
                        break;
                    }
                }

                // Layer 3: Living Entities
                Monster damagedMonsterOverlayTarget = null;
                double entityPixelOffsetX = 0.0;
                double entityPixelOffsetY = 0.0;
                List<Entity> entitiesInRoom = EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom);

                for (Entity entity : entitiesInRoom) {
                    if (entity.position.equals(worldPosition)) {
                        GlyphStyle entityStyle = glyphRegistry.getStyle(entity);
                        activeGlyph = entityStyle.glyph();

                        // Fetch dynamic presentation offsets from the UI mapping
                        if (entityAnimationPixelDrawOffsets.containsKey(entity)) {
                            RenderOffset animationOffset = entityAnimationPixelDrawOffsets.get(entity);
                            entityPixelOffsetX = animationOffset.x;
                            entityPixelOffsetY = animationOffset.y;
                        }

                        if (entity instanceof Player) {
                            activeColor = (entity.color != null) ? entity.color : entityStyle.color();
                        }
                        else {
                            if(entity.color != null) {
                                activeColor = entity.color;
                            }
                            else {
                                // Generate unique color variations dynamically based on Monster identity hashes
                                double hue = entityStyle.color().getHue();
                                double saturation = Math.abs((double) (entity.id * 13 % 7 * 19 % 50) / 100) + 0.5;
                                double brightness = Math.abs((double) (entity.id * 19 % 13 * 23 % 50) / 100) + 0.5;
                                activeColor = Color.hsb(hue, saturation, brightness);
                            }

                            if (entity instanceof Monster && entity.health > 0 && entity.health < entity.maxHealth) {
                                damagedMonsterOverlayTarget = (Monster) entity;
                            }
                        }
                        break;
                    }
                }

                // --- INLINE LAYERING PIPELINE CONTINUED ---

                // Run our single-source-of-truth illumination level calculation
                LIGHT_LEVEL lightLevel = getPositionIlluminationLevel(worldPosition, player, entitiesInRoom);

                // Determine if player has previously explored this specific area
                boolean isTravelled = false;
                if (player != null && player.illuminationData != null) {
                    List<Position> previousTravelledPositions = activeRoom.getPlayerTravelledPositions();
                    // If a position was within their baseline fully-lit range before, it's remembered
                    double memoryThreshold = player.illuminationData.illuminationRange;

                    for (Position previousTravelledPos : previousTravelledPositions) {
                        if (worldPosition.getDistanceTo(previousTravelledPos) <= memoryThreshold) {
                            isTravelled = true;
                            break;
                        }
                    }
                }

                // Apply the calculated lighting/fog-of-war states to the active color
                switch (lightLevel) {
                    case ILLUMINATED -> {
                        // Fully illuminated: Leave activeColor exactly as its base Layer 1/2/3 color
                    }
                    case DIM -> {
                        // Dimly lit: Tint or dim down the base color slightly
                        activeColor = activeColor.darker();
                    }
                    default -> {
                        // Pure Black Zone (lightLevel == 0)
                        // If we've seen it before, show it as a uniform grey shadow shroud. Otherwise, pitch black.
                        activeColor = isTravelled ? Color.BLACK.brighter().brighter() : Color.BLACK;
                    }
                }

                gameCanvas.drawCharacter(screenX, screenY, activeGlyph, activeColor, entityPixelOffsetX, entityPixelOffsetY);

                if (damagedMonsterOverlayTarget != null) {
                    double healthPercent = (double) damagedMonsterOverlayTarget.health / damagedMonsterOverlayTarget.maxHealth;
                    gameCanvas.drawHealthBar(screenX, screenY, healthPercent, entityPixelOffsetX, entityPixelOffsetY);
                }
            }
        }
    }

    private void attachKeyboardHandlers() {
        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
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
                        case T -> {
                            player.toggleGodMode();
                            isTickAction = false;
                        }
                        case SPACE -> movementVector = new Position(0,0);
                        case M -> {
                            if(!isMapOpen) isTickAction = false;
                            isMapOpen = !isMapOpen;
                        }
                        case F11 -> {
                            Stage stage = (Stage) rootContainer.getScene().getWindow();
                            stage.setFullScreen(!stage.isFullScreen());
                            isTickAction = false;
                        }
                        default -> isTickAction = false;
                    }
                    if(isMapOpen) {
                        openMap();
                    }
                    if (isTickAction) {
                        isMapOpen = false;
                        player.handleMove(movementVector);

                        Room currentRoom = EntityRoomManager.getInstance().getPlayerRoom();
                        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
                        for(int i = 0; i < entities.size(); i++) {
                            Entity entity = entities.get(i);
                            if(entity instanceof Monster m) {
                                m.makeMove();
                            }
                            if(entity instanceof Projectile p) {
                                p.makeMove();
                            }
                        }

                        updateRenderingPipeline();
                    }
                });
            }
        });
    }

    private void openMap() {
        final Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
        if(activeRoom == null) return;

        final MAP[][] mapLayout = DungeonManager.getInstance().getMapLayout();

        final int MAP_HEIGHT = mapLayout.length;
        final int MAP_LENGTH = mapLayout[0].length;
        viewport.updateCameraFocus(activeRoom.minimapPosition, MAP_LENGTH, MAP_HEIGHT);

        gameCanvas.clearCanvas();

        for(int screenY = 0; screenY < viewport.getScreenHeight(); screenY++) {
            for(int screenX = 0; screenX < viewport.getScreenWidth(); screenX++) {
                Position worldPosition = viewport.toWorldPosition(screenX, screenY);

                boolean isWithinRoomBounds = worldPosition.x >= 0 && worldPosition.x < MAP_LENGTH && worldPosition.y >= 0 && worldPosition.y < MAP_HEIGHT;
                if (!isWithinRoomBounds) {
                    gameCanvas.drawCharacter(screenX, screenY, " ", Color.BLACK, 0.0, 0.0);
                    continue;
                }

                MAP mapTile = mapLayout[worldPosition.y][worldPosition.x];
                String c = "?";
                Color color = Color.GRAY;
                if(activeRoom.minimapPosition.equals(worldPosition)) color = Color.YELLOW;
                if(mapTile != null) {
                    switch(mapTile) {
                        case SPAWN, CLEAR, INFESTED, TREASURE -> c = "□";
                        case BOSS -> {
                            c = "□";
                            color = Color.RED;
                        }
                        case VCORRIDOR -> c = "|";
                        case HCORRIDOR -> c = "-";
                    }
                }
                else color = Color.BLACK;

                gameCanvas.drawCharacter(screenX, screenY, c, color, 0,0);
            }
        }
    }

    private LIGHT_LEVEL getPositionIlluminationLevel(Position targetPos, Player player, List<Entity> entitiesInRoom) {
        LIGHT_LEVEL lightLevel = LIGHT_LEVEL.PURE_DARKNESS; // Default to pure black
        final int dimRange = 2;       // Your buffer variable for the dim outer ring (adjust as needed)

        // 1. Evaluate Player Light Source
        if (player != null && player.illuminationData != null && player.illuminationData.isIlluminated) {
            double distance = targetPos.getDistanceTo(player.position);
            double brightRange = player.illuminationData.illuminationRange;

            if (distance <= brightRange) {
                return LIGHT_LEVEL.ILLUMINATED; // Maximum brightness achieved, short-circuit immediately
            } else if (distance <= brightRange + dimRange) {
                lightLevel = LIGHT_LEVEL.DIM; // Mark as dim
            }
        }

        // 2. Evaluate Dynamic Entity Light Sources (Fireballs, Glowing Monsters, etc.)
        for (int i = 0; i < entitiesInRoom.size(); i++) {
            Entity entity = entitiesInRoom.get(i);
            if (entity.position == null || entity.illuminationData == null || !entity.illuminationData.isIlluminated) {
                continue;
            }

            double distance = targetPos.getDistanceTo(entity.position);
            double brightRange = entity.illuminationData.illuminationRange;

            if (distance <= brightRange) {
                return LIGHT_LEVEL.ILLUMINATED; // Maximum brightness achieved, short-circuit immediately
            } else if (distance <= brightRange + dimRange) {
                lightLevel = LIGHT_LEVEL.DIM; // Mark as dim
            }
        }

        // 3. Hook for Future Tile/Structure Light Sources (e.g., Torches, Braziers)
        // if (activeRoom.getTileAt(targetPos) == TILE.TORCH) {
        //     double distance = targetPos.getDistanceTo(torchPos);
        //     if (distance <= 3) return 2;
        //     else if (distance <= 3 + N) maxLightLevel = Math.max(maxLightLevel, 1);
        // }

        return lightLevel;
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

    public void flashScreenEffect(Color color, int durationInMilis, double fromOpacity, double toOpacity) {
        Rectangle flashOverlay = new Rectangle();
        flashOverlay.widthProperty().bind(canvasContainer.widthProperty());
        flashOverlay.heightProperty().bind(canvasContainer.heightProperty());
        flashOverlay.setFill(color);
        flashOverlay.setOpacity(1);
        flashOverlay.setMouseTransparent(true);

        canvasContainer.getChildren().add(flashOverlay);

        FadeTransition transition = new FadeTransition(Duration.millis(durationInMilis), flashOverlay);
        transition.setFromValue(fromOpacity);
        transition.setToValue(toOpacity);

        transition.setOnFinished(event -> {
            canvasContainer.getChildren().remove(flashOverlay);
            updateRenderingPipeline(); // Final screen refresh
        });
        flashOverlay.setManaged(false);

        transition.play();
    }
    public void triggerEntitySlideReverse(Entity entity, Position targetPosition, double slidePixelMultiplier, double animationDurationMs, ANIMATION_CURVE animationCurve) {
        triggerEntitySlideAnimation(entity, targetPosition, slidePixelMultiplier, animationDurationMs, animationCurve, true);
    }
    public void triggerEntitySlide(Entity entity, Position targetPosition, double slidePixelMultiplier, double animationDurationMs, ANIMATION_CURVE animationCurve) {
        triggerEntitySlideAnimation(entity, targetPosition, slidePixelMultiplier, animationDurationMs, animationCurve, false);
    }
    private void triggerEntitySlideAnimation(Entity entity, Position targetPosition, double slidePixelMultiplier, double animationDurationMs, ANIMATION_CURVE animationCurve, boolean isReverse) {
        int dx = targetPosition.x - entity.position.x;
        int dy = targetPosition.y - entity.position.y;

        double targetPixelX;
        if (gameCanvas.getGridColumns() > 0)
            targetPixelX = dx * (canvas.getWidth() / gameCanvas.getGridColumns()) * slidePixelMultiplier;
        else targetPixelX = dx * currentTileSize * slidePixelMultiplier;
        double targetPixelY = dy * currentTileSize * slidePixelMultiplier;

        Timeline timeline = new Timeline();

        final int TOTAL_FRAMES = 10;

        for(int i = 0; i <= TOTAL_FRAMES; i++) {
            final int frame = i; // needs to be final for lambda expressions
            KeyFrame keyframe = new KeyFrame(
                    Duration.millis(animationDurationMs / TOTAL_FRAMES * frame),
                    event -> {
                        double progress = (double) frame / TOTAL_FRAMES;
                        double curve;
                        switch(animationCurve) {
                            case TRIANGLE -> curve = 1.0 - Math.abs(2.0 * progress - 1.0);
                            case EASE_OUT -> curve = 1.0 - (1 - progress) * (1 - progress);
                            default -> curve = progress;
                        }
                        if(isReverse) curve = Math.abs(curve - 1.0);

                        double entityAnimationPosX = targetPixelX * curve;
                        double entityAnimationPosY = targetPixelY * curve;

                        if(frame == TOTAL_FRAMES) {
                            entityAnimationPixelDrawOffsets.remove(entity);
                        }
                        else {
                            entityAnimationPixelDrawOffsets.put(entity, new RenderOffset(entityAnimationPosX, entityAnimationPosY));
                        }

                        updateRenderingPipeline();
                    }
            );
            timeline.getKeyFrames().add(keyframe);
        }
        timeline.play();
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
    private String toHexWebColor(Color c) {
        return String.format("#%02X%02X%02X", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }
}
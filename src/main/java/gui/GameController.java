package gui;

import entity.Entity;
import entity.monster.Monster;
import entity.Player;
import core.DungeonManager;
import core.EntityRoomManager;
import core.room.type.Room;
import javafx.animation.AnimationTimer;
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
import item.weapon.Weapon;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import world.*;

import java.util.*;

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
    @FXML private VBox inventoryPanel, inventoryBox;
    @FXML private Label inventoryHeader;

    private GameCanvas gameCanvas;
    private Viewport viewport;
    private GameFSM gameFSM;

    // --- TILES ---
    private double currentTileSize = 40;
    private final double MIN_TILE_SIZE = 6.0;
    private final double MAX_TILE_SIZE = 70.0;
    private final double TILE_SIZE_CHANGE_AMOUNT = 2.0;

    // --- DARKNESS ---
    private enum LIGHT_LEVEL {
        ILLUMINATED,
        DIM,
        PURE_DARKNESS,
    }

    // --- PERSISTENT ZERO-ALLOCATION SPATIAL CACHES ---
    private InteractableTile[][] interactableGridCache;
    private Entity[][] entityGridCache;
    private LIGHT_LEVEL[][] lightGridCache;

    // --- LOGS ---
    private final int MAX_LOG_LINES = 8;

    // --- ENEMY ATTACK SLIDE OFFSET ANIMATION ---
    private final Map<Entity, RenderOffset> entityAnimationPixelDrawOffsets = new HashMap<>();

    // --- TEXT POP UPS ---
    private final List<TextPopupData> textPopupDataList = new ArrayList<>();

    @FXML
    public void initialize() {
        canvas.setManaged(false);

        gameCanvas = new GameCanvas(canvas, currentTileSize);
        viewport = new Viewport(gameCanvas.getGridColumns(), gameCanvas.getGridRows(), 6);
        gameFSM = new GameFSM(this);

        GUIManager.getInstance().registerController(this);
        DungeonManager.getInstance().generateDungeon();

        applyInterfaceTheme();

        canvasContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            handleWindowResize();
        });
        canvasContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            handleWindowResize();
        });

        gameFSM.runGame();
        attachKeyboardHandlers();
    }

    private void applyInterfaceTheme() {
        rootContainer.setStyle("-fx-background-color: " + UITheme.BG_ROOT + "; " + UITheme.CSS_FONT_FAMILY);
        canvasContainer.setStyle("-fx-border-color: " + UITheme.BORDER_HIGHLIGHT + "; -fx-border-width: 2; -fx-background-color: #000000;");

        String subPanelStyle = "-fx-border-color: " + UITheme.BORDER_NORMAL + "; -fx-border-width: 2; -fx-background-color: " + UITheme.BG_CARD + ";";
        statsPanel.setStyle(subPanelStyle);
        logsPanel.setStyle(subPanelStyle);
        controlsPanel.setStyle(subPanelStyle);

        String headerStyle = UITheme.STYLE_HEADER + " -fx-text-fill: #ede6c8;";
        statsHeader.setStyle(headerStyle);
        logsHeader.setStyle(headerStyle);
        controlsHeader.setStyle(headerStyle);

        String labelStaticStyle = UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";";
        lblHealth.setStyle(labelStaticStyle);
        lblHunger.setStyle(labelStaticStyle);
        lblCoins.setStyle(labelStaticStyle);

        String activeMetricStyle = UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_PARCHMENT) + "; -fx-font-weight: bold;";
        healthValText.setStyle(activeMetricStyle);
        hungerValText.setStyle(activeMetricStyle);
        coinsText.setStyle(activeMetricStyle);

        healthBarText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HEALTH) + ";");
        hungerBarText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HUNGER) + ";");
        armorText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_ARMOR) + "; -fx-font-weight: bold;");
        weaponText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_WEAPON) + "; -fx-font-weight: bold;");

        // Add this inside applyInterfaceTheme() alongside the other panels
        inventoryPanel.setStyle(subPanelStyle);
        inventoryHeader.setStyle(headerStyle);

        buildControlsReferenceHud();
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

    /**
     * Prepares and resets memory spaces natively to completely isolate heap allocations.
     * Fixed to ensure brand-new cache matrices are fully populated with baseline values.
     */
    private void synchronizeSpatialCaches(int width, int height) {
        // 1. Allocate arrays if they are null or if room dimensions changed
        if (interactableGridCache == null || interactableGridCache.length != height || interactableGridCache[0].length != width) {
            interactableGridCache = new InteractableTile[height][width];
            entityGridCache = new Entity[height][width];
            lightGridCache = new LIGHT_LEVEL[height][width];
        }

        // 2. Clear or reset all indices to prevent NullPointerExceptions across frames
        for (int row = 0; row < height; row++) {
            Arrays.fill(interactableGridCache[row], null);
            Arrays.fill(entityGridCache[row], null);
            Arrays.fill(lightGridCache[row], LIGHT_LEVEL.PURE_DARKNESS);
        }
    }

    public void updateRenderingPipeline() {
        final Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
        if (activeRoom == null) return;
        final Player player = (Player) EntityRoomManager.getInstance().getPlayer();

        final TILE[][] roomLayout = activeRoom.getLayout();
        final int roomHeight = roomLayout.length;
        final int roomWidth = (roomHeight > 0) ? roomLayout[0].length : 0;

        if (player != null) {
            viewport.updateCameraFocus(player.position, roomWidth, roomHeight);
        }

        gameCanvas.clearCanvas();
        GlyphRegistry glyphRegistry = GlyphRegistry.getInstance();

        // Initialize or wipe persistent matrices without spawning fresh objects
        synchronizeSpatialCaches(roomWidth, roomHeight);

        // Map interactable layers instantly
        List<InteractableTile> interactableTiles = activeRoom.getInteractableTiles();
        if (interactableTiles != null) {
            for (int i = 0; i < interactableTiles.size(); i++) {
                InteractableTile interactable = interactableTiles.get(i);
                Position pos = interactable.roomLayoutPosition;
                if (pos.y >= 0 && pos.y < roomHeight && pos.x >= 0 && pos.x < roomWidth) {
                    interactableGridCache[pos.y][pos.x] = interactable;
                }
            }
        }

        // Map moving entities into matrix coordinates
        List<Entity> entitiesInRoom = EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom);
        if (entitiesInRoom != null) {
            for (int i = 0; i < entitiesInRoom.size(); i++) {
                Entity entity = entitiesInRoom.get(i);
                if (entity.position != null && entity.position.y >= 0 && entity.position.y < roomHeight && entity.position.x >= 0 && entity.position.x < roomWidth) {
                    entityGridCache[entity.position.y][entity.position.x] = entity;
                }
            }
        }

        // --- FORWARD BLITTING LIGHT ENGINE: Project light outwards from emitters ---
        if (player != null && player.isIlluminated()) {
            blitLightSource(player.position.x, player.position.y, player.getIlluminationRange(), roomLayout, roomWidth, roomHeight);
        }

        if (entitiesInRoom != null) {
            for (int i = 0; i < entitiesInRoom.size(); i++) {
                Entity entity = entitiesInRoom.get(i);
                if (entity.position != null && entity.isIlluminated()) {
                    blitLightSource(entity.position.x, entity.position.y, entity.getIlluminationRange(), roomLayout, roomWidth, roomHeight);
                }
            }
        }

        for (int row = 0; row < roomHeight; row++) {
            for (int col = 0; col < roomWidth; col++) {
                if (roomLayout[row][col] == TILE.TORCH) {
                    blitLightSource(col, row, 1, roomLayout, roomWidth, roomHeight);
                }
                InteractableTile interactable = interactableGridCache[row][col];
                if (interactable instanceof Fire) {
                    blitLightSource(col, row, 1, roomLayout, roomWidth, roomHeight);
                }
            }
        }

        List<Position> travelledPositions = activeRoom.getPlayerTravelledPositions();
        double memoryThreshold = (player != null) ? player.getIlluminationRange() : 0.0;

        // --- VIEWPORT RENDERING CYCLE: Direct grid rendering using O(1) lookups ---
        for (int screenY = 0; screenY < viewport.getScreenHeight(); screenY++) {
            for (int screenX = 0; screenX < viewport.getScreenWidth(); screenX++) {

                Position worldPosition = viewport.toWorldPosition(screenX, screenY);

                boolean isWithinRoomBounds = worldPosition.x >= 0 && worldPosition.x < roomWidth && worldPosition.y >= 0 && worldPosition.y < roomHeight;
                if (!isWithinRoomBounds) {
                    gameCanvas.drawCharacter(screenX, screenY, glyphRegistry.getVoidStyle().glyph(), UITheme.CANVAS_VOID, 0.0, 0.0);
                    continue;
                }

                String activeGlyph = glyphRegistry.getVoidStyle().glyph();
                Color activeColor = UITheme.CANVAS_VOID;

                // Layer 1: Layout structure maps
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

                // Layer 2: Interactable objects
                InteractableTile currentInteractable = interactableGridCache[worldPosition.y][worldPosition.x];
                if (currentInteractable != null) {
                    GlyphStyle interactableTileStyle = glyphRegistry.getStyle(currentInteractable);
                    activeGlyph = interactableTileStyle.glyph();
                    activeColor = interactableTileStyle.color();
                }

                // Layer 3: Living entities
                Monster damagedMonsterOverlayTarget = null;
                double entityPixelOffsetX = 0.0;
                double entityPixelOffsetY = 0.0;

                Entity entity = entityGridCache[worldPosition.y][worldPosition.x];
                if (entity != null) {
                    GlyphStyle entityStyle = glyphRegistry.getStyle(entity);
                    activeGlyph = entityStyle.glyph();

                    if (entityAnimationPixelDrawOffsets.containsKey(entity)) {
                        RenderOffset animationOffset = entityAnimationPixelDrawOffsets.get(entity);
                        entityPixelOffsetX = animationOffset.x;
                        entityPixelOffsetY = animationOffset.y;
                    }

                    if (entity instanceof Player) {
                        activeColor = (entity.getColor() != null) ? entity.getColor() : entityStyle.color();
                    } else {
                        if (entity.getColor() != null) {
                            activeColor = entity.getColor();
                        } else {
                            double hue = entityStyle.color().getHue();
                            double saturation = Math.abs((double) (entity.id * 13 % 7 * 19 % 50) / 100) + 0.5;
                            double brightness = Math.abs((double) (entity.id * 19 % 13 * 23 % 50) / 100) + 0.5;
                            activeColor = Color.hsb(hue, saturation, brightness);
                        }

                        if (entity instanceof Monster && entity.health > 0 && entity.health < entity.maxHealth) {
                            damagedMonsterOverlayTarget = (Monster) entity;
                        }
                    }
                }

                // Gather illumination from our pre-blitted matrix
                LIGHT_LEVEL lightLevel = lightGridCache[worldPosition.y][worldPosition.x];

                // Parse map exploration traces
                boolean isTravelled = false;
                if (player != null && travelledPositions != null) {
                    for (int i = 0; i < travelledPositions.size(); i++) {
                        Position previousTravelledPos = travelledPositions.get(i);
                        double dx = worldPosition.x - previousTravelledPos.x;
                        double dy = worldPosition.y - previousTravelledPos.y;
                        if ((dx * dx + dy * dy) <= (memoryThreshold * memoryThreshold) &&
                                isPathClear(previousTravelledPos.x, previousTravelledPos.y, worldPosition.x, worldPosition.y, roomLayout)) {
                            isTravelled = true;
                            break;
                        }
                    }
                }

                switch (lightLevel) {
                    case ILLUMINATED -> {}
                    case DIM -> activeColor = activeColor.darker();
                    default -> activeColor = isTravelled ? Color.BLACK.brighter().brighter() : Color.BLACK;
                }

                gameCanvas.drawCharacter(screenX, screenY, activeGlyph, activeColor, entityPixelOffsetX, entityPixelOffsetY);

                if (damagedMonsterOverlayTarget != null) {
                    double healthPercent = (double) damagedMonsterOverlayTarget.health / damagedMonsterOverlayTarget.maxHealth;
                    gameCanvas.drawHealthBar(screenX, screenY, healthPercent, entityPixelOffsetX, entityPixelOffsetY);
                }
            }
        }

        // SCREEN OVERLAY RENDERING
        for (int i = 0; i < textPopupDataList.size(); i++) {
            TextPopupData textPopup = textPopupDataList.get(i);
            Position screenPos = viewport.toScreenPosition(textPopup.position.x, textPopup.position.y);

            if (screenPos != null) {
                Color blendedPopupColor = new Color(
                        textPopup.color.getRed(),
                        textPopup.color.getGreen(),
                        textPopup.color.getBlue(),
                        textPopup.opacity
                );

                final double offsetX = (double) (textPopup.hashCode() * 17 % 101) % 20;
                gameCanvas.drawString(screenPos.x, screenPos.y - 1, textPopup.text, 22, blendedPopupColor, offsetX, textPopup.pixelOffsetY);
            }
        }
    }

    /**
     * Blits light outwards onto our lighting cache inside an emitter's localized bounding box.
     */
    private void blitLightSource(int sourceX, int sourceY, int brightRange, TILE[][] roomLayout, int roomWidth, int roomHeight) {
        final int dimRange = 2;
        final int maximumRange = brightRange + dimRange;

        int startX = Math.max(0, sourceX - maximumRange);
        int endX = Math.min(roomWidth - 1, sourceX + maximumRange);
        int startY = Math.max(0, sourceY - maximumRange);
        int endY = Math.min(roomHeight - 1, sourceY + maximumRange);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                // If a tile is already directly illuminated, bypass calculation steps
                if (lightGridCache[y][x] == LIGHT_LEVEL.ILLUMINATED) continue;

                int dx = x - sourceX;
                int dy = y - sourceY;
                double distanceSquared = (dx * dx) + (dy * dy);

                if (distanceSquared <= (maximumRange * maximumRange)) {
                    if (isPathClear(sourceX, sourceY, x, y, roomLayout)) {
                        LIGHT_LEVEL generatedLevel = (distanceSquared <= (brightRange * brightRange)) ? LIGHT_LEVEL.ILLUMINATED : LIGHT_LEVEL.DIM;

                        if (generatedLevel == LIGHT_LEVEL.ILLUMINATED || lightGridCache[y][x] == LIGHT_LEVEL.PURE_DARKNESS) {
                            lightGridCache[y][x] = generatedLevel;
                        }
                    }
                }
            }
        }
    }

    private void attachKeyboardHandlers() {
        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    KeyCode code = e.getCode();

                    switch (code) {
                        case EQUALS -> { adjustTileSize(TILE_SIZE_CHANGE_AMOUNT); return; }
                        case MINUS -> { adjustTileSize(-TILE_SIZE_CHANGE_AMOUNT); return; }
                        case F11 -> {
                            Stage stage = (Stage) rootContainer.getScene().getWindow();
                            stage.setFullScreen(!stage.isFullScreen());
                        }
                    }

                    gameFSM.update(code);
                    System.out.println("-------------");
                });
            }
        });
    }

    public void resetGame() {
        textPopupDataList.clear();
        entityAnimationPixelDrawOffsets.clear();
        clearLogContainer();
        EntityRoomManager.getInstance().clear();

        DungeonManager.getInstance().generateDungeon();

        Player player = (Player) EntityRoomManager.getInstance().getPlayer();
        Room currentRoom = EntityRoomManager.getInstance().getPlayerRoom();
        System.out.println(player);
        System.out.println(currentRoom);
        final int roomWidth = currentRoom.length;
        final int roomHeight = currentRoom.height;

        viewport.updateCameraFocus(player.position, roomWidth, roomHeight);

        gameFSM.runGame();
    }

    public void openMap() {
        final Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
        if(activeRoom == null) return;

        final MAP[][] mapLayout = DungeonManager.getInstance().getMapLayout();

        final int mapHeight = mapLayout.length;
        final int mapLength = mapLayout[0].length;
        viewport.updateCameraFocus(activeRoom.minimapPosition, mapLength, mapHeight);

        gameCanvas.clearCanvas();

        for(int screenY = 0; screenY < viewport.getScreenHeight(); screenY++) {
            for(int screenX = 0; screenX < viewport.getScreenWidth(); screenX++) {
                Position worldPosition = viewport.toWorldPosition(screenX, screenY);

                boolean isWithinRoomBounds = worldPosition.x >= 0 && worldPosition.x < mapLength && worldPosition.y >= 0 && worldPosition.y < mapHeight;
                if (!isWithinRoomBounds) {
                    gameCanvas.drawCharacter(screenX, screenY, " ", Color.BLACK, 0.0, 0.0);
                    continue;
                }

                MAP mapTile = mapLayout[worldPosition.y][worldPosition.x];
                String minimapGlyph = "?";
                Color tileColor = Color.GRAY;
                if(activeRoom.minimapPosition.equals(worldPosition)) tileColor = Color.YELLOW;
                if(mapTile != null) {
                    switch(mapTile) {
                        case SPAWN, NORMAL, TREASURE -> minimapGlyph = "□";
                        case BOSS -> {
                            minimapGlyph = "□";
                            tileColor = Color.RED;
                        }
                        case VCORRIDOR -> minimapGlyph = "|";
                        case HCORRIDOR -> minimapGlyph = "-";
                    }
                }
                else tileColor = Color.BLACK;

                gameCanvas.drawCharacter(screenX, screenY, minimapGlyph, tileColor, 0,0);
            }
        }
    }

    /**
     * Bresenham's Line-Of-Sight Implementation.
     * Evaluated using primitive integer parameters to completely eliminate heap object generation overhead.
     */
    private boolean isPathClear(int startX, int startY, int targetX, int targetY, TILE[][] roomLayout) {
        if (startX == targetX && startY == targetY) return true;

        int currentX = startX;
        int currentY = startY;

        final int deltaX = Math.abs(targetX - currentX);
        final int deltaY = Math.abs(targetY - currentY);
        final int stepX = currentX < targetX ? 1 : -1;
        final int stepY = currentY < targetY ? 1 : -1;
        int errorValue = deltaX - deltaY;

        while (true) {
            if (currentX != startX || currentY != startY) {
                if (currentX == targetX && currentY == targetY) {
                    break;
                }

                if (currentY >= 0 && currentY < roomLayout.length && currentX >= 0 && currentX < roomLayout[currentY].length) {

                    TILE tile = roomLayout[currentY][currentX];
                    if (tile == TILE.WALL || tile == TILE.BOOKSHELF) {
                        return false;
                    }

                    InteractableTile interactable = interactableGridCache[currentY][currentX];
                    if (interactable instanceof Web
                            || interactable instanceof Box
                            || interactable instanceof LockedDoor) {
                        return false;
                    }
                }
            }

            if (currentX == targetX && currentY == targetY) break;

            int errorDoubleAdjustment = 2 * errorValue;
            if (errorDoubleAdjustment > -deltaY) {
                errorValue -= deltaY;
                currentX += stepX;
            }
            if (errorDoubleAdjustment < deltaX) {
                errorValue += deltaX;
                currentY += stepY;
            }
        }
        return true;
    }

    public void addLog(String txt, Color col) {
        Label element = new Label(txt);
        element.setStyle(UITheme.STYLE_LOG + " -fx-text-fill: " + toHexWebColor(col) + ";");
        element.setWrapText(true);

        if (logContainer.getChildren().size() >= MAX_LOG_LINES) logContainer.getChildren().removeFirst();
        logContainer.getChildren().add(element);
    }

    public void clearLogContainer() { logContainer.getChildren().clear(); }

    public void triggerTextPopup(TextPopupData textPopupData, double durationMs) {
        textPopupDataList.add(textPopupData);

        final double MAX_FLOAT_DISTANCE_PIXELS = -30.0;

        new AnimationTimer() {
            private long startTime = -1;

            @Override
            public void handle(long now) {
                if (startTime < 0) {
                    startTime = now;
                }

                double elapsedMs = (now - startTime) / 1_000_000.0;
                double progress = Math.min(1.0, elapsedMs / durationMs);

                double curve = 1.0 - progress;
                textPopupData.opacity = Math.clamp(curve, 0.0, 1.0);
                textPopupData.pixelOffsetY = progress * MAX_FLOAT_DISTANCE_PIXELS;

                updateRenderingPipeline();

                if (progress >= 1.0) {
                    textPopupDataList.remove(textPopupData);
                    updateRenderingPipeline();
                    this.stop();
                }
            }
        }.start();
    }

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
            updateRenderingPipeline();
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
        if (gameCanvas.getGridColumns() > 0) {
            targetPixelX = dx * (canvas.getWidth() / gameCanvas.getGridColumns()) * slidePixelMultiplier;
        }
        else targetPixelX = dx * currentTileSize * slidePixelMultiplier;
        double targetPixelY = dy * currentTileSize * slidePixelMultiplier;

        Timeline timeline = new Timeline();

        final int TOTAL_FRAMES = 10;

        for(int i = 0; i <= TOTAL_FRAMES; i++) {
            final int frame = i;
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

    public void updateHealth(int health) { healthValText.setText(health + "/100"); healthBarText.setText(buildBarMeter(health)); }
    public void updateHunger(int hunger) { hungerValText.setText(hunger + "/100"); hungerBarText.setText(buildBarMeter(hunger)); }
    public void updateArmor(int armor) { armorText.setText("Armor: " + armor + "/10"); }
    public void updateWeapon(Weapon weapon) { weaponText.setText("Weapon: " + weapon); }
    public void updateCoins(int count) { coinsText.setText(String.valueOf(count)); }
    public void updateInventory(List<item.Item> inventory) {
        inventoryBox.getChildren().clear();

        if (inventory == null || inventory.isEmpty()) {
            Label emptyLabel = new Label("Empty");
            emptyLabel.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";");
            inventoryBox.getChildren().add(emptyLabel);
            return;
        }

        // Group matching items and count their quantities
        Map<String, Integer> itemCounts = new LinkedHashMap<>();
        for (item.Item item : inventory) {
            String itemName = item.name;
            itemCounts.put(itemName, itemCounts.getOrDefault(itemName, 0) + 1);
        }

        // Populate the inventory UI list
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            Label itemLabel = new Label(entry.getKey() + " x" + entry.getValue());
            itemLabel.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_PARCHMENT) + ";");
            inventoryBox.getChildren().add(itemLabel);
        }
    }


    private void buildControlsReferenceHud() {
        controlsBox.getChildren().clear();
        String[] mappings = {
                "[WASD]  Move Explorer",
                "[SPACE] Rest / Skip Turn",
                "[M] Open Map",
                "[+ / -] Adjust Camera Zoom"
        };
        for (String item : mappings) {
            Label element = new Label(item);
            element.setStyle(UITheme.STYLE_CTRL + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";");
            controlsBox.getChildren().add(element);
        }
    }

    private String buildBarMeter(int value) {
        int fill = (int) Math.round((Math.max(0, Math.min(100, value)) / 100.0) * 15);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 15; i++) sb.append(i < fill ? "■" : "·");
        return sb.append("]").toString();
    }

    private String toHexWebColor(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
    }
}
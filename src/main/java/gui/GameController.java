package gui;

import entity.Entity;
import entity.monster.Monster;
import entity.Player;
import core.DungeonManager;
import core.EntityRoomManager;
import core.room.type.Room;
import entity.projectile.Projectile;
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
import weapon.Weapon;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import world.Box;
import world.Fire;
import world.InteractableTile;
import world.Web;

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
        lblPotions.setStyle(labelStaticStyle);

        String activeMetricStyle = UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_PARCHMENT) + "; -fx-font-weight: bold;";
        healthValText.setStyle(activeMetricStyle);
        hungerValText.setStyle(activeMetricStyle);
        coinsText.setStyle(activeMetricStyle);
        potionsText.setStyle(activeMetricStyle);

        healthBarText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HEALTH) + ";");
        hungerBarText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HUNGER) + ";");
        armorText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_ARMOR) + "; -fx-font-weight: bold;");
        weaponText.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_WEAPON) + "; -fx-font-weight: bold;");

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

                        if (entityAnimationPixelDrawOffsets.containsKey(entity)) {
                            RenderOffset animationOffset = entityAnimationPixelDrawOffsets.get(entity);
                            entityPixelOffsetX = animationOffset.x;
                            entityPixelOffsetY = animationOffset.y;
                        }

                        if (entity instanceof Player) {
                            activeColor = (entity.getColor() != null) ? entity.getColor() : entityStyle.color();
                        } else {
                            if(entity.getColor() != null) {
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
                        break;
                    }
                }

                // Calculate illumination level using our upgraded occlusion system
                LIGHT_LEVEL lightLevel = getPositionIlluminationLevel(worldPosition, player, entitiesInRoom, activeRoom);

                // Determine if player has previously explored this specific area
                boolean isTravelled = false;
                if (player != null) {
                    List<Position> previousTravelledPositions = activeRoom.getPlayerTravelledPositions();
                    double memoryThreshold = player.getIlluminationRange();

                    for (Position previousTravelledPos : previousTravelledPositions) {
                        // For explored maps memory (Fog of War), we check if a clear sight path existed
                        if (worldPosition.getDistanceTo(previousTravelledPos) <= memoryThreshold && isPathClear(previousTravelledPos, worldPosition, activeRoom)) {
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
        for (TextPopupData textPopup : textPopupDataList) {
            Position screenPos = viewport.toScreenPosition(textPopup.position.x, textPopup.position.y);

            if (screenPos != null) {
                Color blendedPopupColor = new Color(
                        textPopup.color.getRed(),
                        textPopup.color.getGreen(),
                        textPopup.color.getBlue(),
                        textPopup.opacity
                );

                final double offsetX = (double) (textPopup.hashCode() * 17 % 101) % 20;
                gameCanvas.drawString(screenPos.x, screenPos.y-1, textPopup.text, 22, blendedPopupColor, offsetX, textPopup.pixelOffsetY);
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
                        case W, UP -> movementVector.y--;
                        case A, LEFT -> movementVector.x--;
                        case S, DOWN -> movementVector.y++;
                        case D, RIGHT -> movementVector.x++;
                        case T -> {
                            player.toggleGodMode();
                            isTickAction = false;
                        }
                        case SPACE -> {
                            movementVector = new Position(0,0);
                            GUIManager.getInstance().triggerTextPopup("wait", Color.WHITE, player.position);
                        }
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
                    if(isMapOpen) openMap();

                    if (isTickAction) {
                        isMapOpen = false;
                        if(!player.isDead) {
                            player.handleMove(movementVector);

                            Room currentRoom = EntityRoomManager.getInstance().getPlayerRoom();
                            List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
                            for(int i = 0; i < entities.size(); i++) {
                                Entity entity = entities.get(i);
                                if(entity instanceof Monster monster) {
                                    monster.makeMove();
                                }
                                if(entity instanceof Projectile projectile) {
                                    projectile.makeMove();
                                }
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
     * Calculates the explicit light level for a target position with complete Raycast Occlusion parsing.
     */
    private LIGHT_LEVEL getPositionIlluminationLevel(Position targetPos, Player player, List<Entity> entitiesInRoom, Room activeRoom) {
        LIGHT_LEVEL finalLight = LIGHT_LEVEL.PURE_DARKNESS;
        final int dimRange = 2;

        if (activeRoom == null) return finalLight;
        TILE[][] roomLayout = activeRoom.getLayout();

        // 1. Evaluate Player Light Source
        if (player != null && player.isIlluminated()) {
            double distance = targetPos.getDistanceTo(player.position);
            double brightRange = player.getIlluminationRange();

            if (distance <= brightRange + dimRange && isPathClear(player.position, targetPos, activeRoom)) {
                if (distance <= brightRange) return LIGHT_LEVEL.ILLUMINATED;
                finalLight = LIGHT_LEVEL.DIM;
            }
        }

        // 2. Evaluate Dynamic Entity Light Sources
        for (int i = 0; i < entitiesInRoom.size(); i++) {
            Entity entity = entitiesInRoom.get(i);
            if (entity.position == null || !entity.isIlluminated()) continue;

            double distance = targetPos.getDistanceTo(entity.position);
            double brightRange = entity.getIlluminationRange();

            if (distance <= brightRange + dimRange && isPathClear(entity.position, targetPos, activeRoom)) {
                if (distance <= brightRange) return LIGHT_LEVEL.ILLUMINATED;
                finalLight = LIGHT_LEVEL.DIM;
            }
        }

        // 3. Stationary Light Sources (Torches)
        for (int y = 0; y < roomLayout.length; y++) {
            for (int x = 0; x < roomLayout[y].length; x++) {
                if (roomLayout[y][x] == TILE.TORCH) {
                    Position torchPos = new Position(x, y);
                    double distance = targetPos.getDistanceTo(torchPos);
                    double brightRange = 1.0;

                    if (distance <= brightRange + dimRange && isPathClear(torchPos, targetPos, activeRoom)) {
                        if (distance <= brightRange) return LIGHT_LEVEL.ILLUMINATED;
                        finalLight = LIGHT_LEVEL.DIM;
                    }
                }
            }
        }

        // 4. Interactable Light Sources
        List<InteractableTile> interactableTiles = activeRoom.getInteractableTiles();
        if (interactableTiles != null) {
            for (InteractableTile interactable : interactableTiles) {
                if (interactable instanceof Fire) {
                    Position firePos = interactable.roomLayoutPosition;
                    double distance = targetPos.getDistanceTo(firePos);
                    double brightRange = 1.0;

                    if (distance <= brightRange + dimRange && isPathClear(firePos, targetPos, activeRoom)) {
                        if (distance <= brightRange) return LIGHT_LEVEL.ILLUMINATED;
                        finalLight = LIGHT_LEVEL.DIM;
                    }
                }
            }
        }

        return finalLight;
    }

    /**
     * Bresenham's Line-Of-Sight Implementation.
     * Checks if a line ray between two points encounters any light-blocking elements (e.g. Walls).
     */
    private boolean isPathClear(Position start, Position end, Room activeRoom) {
        if (start.equals(end) || activeRoom == null) return true;

        TILE[][] roomLayout = activeRoom.getLayout();
        int x0 = start.x;
        int y0 = start.y;
        int x1 = end.x;
        int y1 = end.y;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        // 👇 1. CREATE A TEMPORARY GRID CACHE OF INTERACTABLES FOR INSTANT LOOKUPS
        // This turns a slow list-search into an instant coordinate check.
        InteractableTile[][] interactableGrid = new InteractableTile[roomLayout.length][roomLayout[0].length];
        List<InteractableTile> interactables = activeRoom.getInteractableTiles();
        if (interactables != null) {
            for (InteractableTile interactable : interactables) {
                Position p = interactable.roomLayoutPosition;
                if (p.y >= 0 && p.y < interactableGrid.length && p.x >= 0 && p.x < interactableGrid[0].length) {
                    interactableGrid[p.y][p.x] = interactable;
                }
            }
        }

        while (true) {
            if (x0 != start.x || y0 != start.y) {
                if (x0 == end.x && y0 == end.y) {
                    break;
                }

                // Check room dimensions safety boundaries
                if (y0 >= 0 && y0 < roomLayout.length && x0 >= 0 && x0 < roomLayout[y0].length) {

                    // 2. Check standard layout tiles (Walls)
                    TILE tile = roomLayout[y0][x0];
                    if (tile == TILE.WALL || tile == TILE.BOOKSHELF) {
                        return false;
                    }

                    // 👇 3. REPLACED THE SLOW FOR-LOOP WITH AN INSTANT GRID CHECK
                    InteractableTile interactable = interactableGrid[y0][x0];
                    if (interactable instanceof Web || interactable instanceof Box) {
                        return false; // Path blocked instantly!
                    }
                }
            }

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
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
    public void updatePotions(int count) { potionsText.setText(String.valueOf(count)); }

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
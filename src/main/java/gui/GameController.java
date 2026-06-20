package gui;

import core.Game;
import entity.Entity;
import entity.monster.Monster;
import entity.Player;
import core.DungeonManager;
import core.EntityRoomManager;
import core.room.type.Room;
import gui.dataclass.GlyphStyle;
import gui.dataclass.RenderOffset;
import gui.dataclass.TextPopupData;
import gui.dataclass.UITheme;
import item.weapon.Weapon;
import javafx.stage.Stage;
import util.ANIMATION_CURVE;
import util.MAP;
import util.Position;
import util.TILE;

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

    @FXML HBox rootContainer;
    @FXML StackPane canvasContainer;
    @FXML Canvas canvas;
    @FXML VBox logContainer;
    @FXML VBox statsPanel, logsPanel, controlsPanel, controlsBox;
    @FXML Label statsHeader, logsHeader, controlsHeader;
    @FXML Label lblHealth, lblHunger, lblCoins, lblArmor, lblWeapon;
    @FXML Label healthBar, healthValText;
    @FXML Label hungerBar, hungerValText;
    @FXML Label armorText, weaponText, coinsText;
    @FXML VBox inventoryPanel, inventoryBox;
    @FXML Label inventoryHeader;

    private GameCanvas gameCanvas;
    private Viewport viewport;
    private GameFSM gameFSM;

    // --- DELEGATED SUBSYSTEM MANAGERS ---
    private LightingEngine lightingEngine;
    private AnimationManager animationManager;
    private HUDManager hudManager;

    private double currentTileSize = 40;
    private final double MIN_TILE_SIZE = 6.0;
    private final double MAX_TILE_SIZE = 70.0;
    private final double TILE_SIZE_CHANGE_AMOUNT = 2.0;

    private InteractableTile[][] interactableGridCache;
    private Entity[][] entityGridCache;

    @FXML
    public void initialize() {
        canvas.setManaged(false);
        gameCanvas = new GameCanvas(canvas, currentTileSize);
        viewport = new Viewport(gameCanvas.getGridColumns(), gameCanvas.getGridRows(), 6);
        gameFSM = new GameFSM(this);

        // Subsystem Initializations
        lightingEngine = new LightingEngine();
        animationManager = new AnimationManager(canvasContainer, canvas, this::updateRenderingPipeline);
        hudManager = new HUDManager(this);

        Game.initialize(this);
        GUIManager.getInstance().registerController(this);

        hudManager.applyInterfaceTheme();
        setup();

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

    private void setup() {
        animationManager.clearAllAnimations();
        hudManager.clearLogContainer();
        EntityRoomManager.getInstance().clear();
        Game.getInstance().reset();

        DungeonManager.getInstance().generateDungeon();

        Player player = (Player) EntityRoomManager.getInstance().getPlayer();
        Room currentRoom = EntityRoomManager.getInstance().getPlayerRoom();

        viewport.updateCameraFocus(player.position, currentRoom.length, currentRoom.height);
    }

    private void handleWindowResize() {
        if (gameCanvas == null) return;
        gameCanvas.updateFontSize(currentTileSize);
        viewport.updateScreenDimensions(gameCanvas.getGridColumns(), gameCanvas.getGridRows());
        updateRenderingPipeline();
    }

    public void adjustTileSize(double delta) {
        double newSize = currentTileSize + delta;
        if (newSize < MIN_TILE_SIZE || newSize > MAX_TILE_SIZE) return;
        this.currentTileSize = newSize;
        handleWindowResize();
    }

    private void synchronizeSpatialCaches(int width, int height) {
        if (interactableGridCache == null || interactableGridCache.length != height || interactableGridCache[0].length != width) {
            interactableGridCache = new InteractableTile[height][width];
            entityGridCache = new Entity[height][width];
        }
        for (int row = 0; row < height; row++) {
            Arrays.fill(interactableGridCache[row], null);
            Arrays.fill(entityGridCache[row], null);
        }
        lightingEngine.synchronizeCache(width, height);
    }

    public void updateRenderingPipeline() {
        final Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
        if (activeRoom == null) return;
        final Player player = (Player) EntityRoomManager.getInstance().getPlayer();

        final TILE[][] roomLayout = activeRoom.getLayout();
        final int roomHeight = roomLayout.length;
        final int roomWidth = (roomHeight > 0) ? roomLayout[0].length : 0;

        // This now instantly recalibrates and centers the focus offset matrix on every call
        if (player != null) {
            viewport.updateCameraFocus(player.position, roomWidth, roomHeight);
        }

        gameCanvas.clearCanvas();
        GlyphRegistry glyphRegistry = GlyphRegistry.getInstance();
        synchronizeSpatialCaches(roomWidth, roomHeight);

        // Map interactables
        List<InteractableTile> interactables = activeRoom.getInteractableTiles();
        if (interactables != null) {
            for (InteractableTile interactable : interactables) {
                Position pos = interactable.roomLayoutPosition;
                if (pos.y >= 0 && pos.y < roomHeight && pos.x >= 0 && pos.x < roomWidth) {
                    interactableGridCache[pos.y][pos.x] = interactable;
                }
            }
        }

        // Map moving entities
        List<Entity> entitiesInRoom = EntityRoomManager.getInstance().getEntitiesInRoom(activeRoom);
        if (entitiesInRoom != null) {
            for (Entity entity : entitiesInRoom) {
                if (entity.position != null && entity.position.y >= 0 && entity.position.y < roomHeight && entity.position.x >= 0 && entity.position.x < roomWidth) {
                    entityGridCache[entity.position.y][entity.position.x] = entity;
                }
            }
        }

        // Process lighting calculations
        if (player != null && player.isIlluminated()) {
            lightingEngine.blitLightSource(player.position.x, player.position.y, player.getIlluminationRange(), roomLayout, roomWidth, roomHeight, interactableGridCache);
        }
        if (entitiesInRoom != null) {
            for (Entity entity : entitiesInRoom) {
                if (entity.position != null && entity.isIlluminated()) {
                    lightingEngine.blitLightSource(entity.position.x, entity.position.y, entity.getIlluminationRange(), roomLayout, roomWidth, roomHeight, interactableGridCache);
                }
            }
        }
        for (int row = 0; row < roomHeight; row++) {
            for (int col = 0; col < roomWidth; col++) {
                if (interactableGridCache[row][col] instanceof Fire) {
                    lightingEngine.blitLightSource(col, row, 1, roomLayout, roomWidth, roomHeight, interactableGridCache);
                }
            }
        }

        List<Position> travelledPositions = activeRoom.getPlayerTravelledPositions();
        double memoryThreshold = (player != null) ? player.getIlluminationRange() : 0.0;
        Map<Entity, RenderOffset> animationOffsets = animationManager.getEntityOffsets();

        // Core Screen Space Render Loop
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

                TILE tile = roomLayout[worldPosition.y][worldPosition.x];
                if (tile != null) {
                    GlyphStyle tileStyle = (tile == TILE.WATER) ? glyphRegistry.getStyle(tile) : glyphRegistry.getStyle(tile, worldPosition.x, worldPosition.y, activeRoom.id);
                    activeGlyph = tileStyle.glyph();
                    activeColor = tileStyle.color();
                }

                InteractableTile currentInteractable = interactableGridCache[worldPosition.y][worldPosition.x];
                if (currentInteractable != null) {
                    GlyphStyle interactableStyle = glyphRegistry.getStyle(currentInteractable);
                    activeGlyph = interactableStyle.glyph();
                    activeColor = (currentInteractable.getColor() != null) ? currentInteractable.getColor() : interactableStyle.color();
                }

                Monster damagedMonsterOverlayTarget = null;
                double entityPixelOffsetX = 0.0;
                double entityPixelOffsetY = 0.0;

                Entity entity = entityGridCache[worldPosition.y][worldPosition.x];
                if (entity != null) {
                    GlyphStyle entityStyle = glyphRegistry.getStyle(entity);
                    activeGlyph = entityStyle.glyph();

                    if (animationOffsets.containsKey(entity)) {
                        RenderOffset animationOffset = animationOffsets.get(entity);
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

                LightingEngine.LIGHT_LEVEL lightLevel = lightingEngine.getLightLevel(worldPosition.x, worldPosition.y);
                boolean isTravelled = false;
                if (player != null && travelledPositions != null) {
                    for (Position previousTravelledPos : travelledPositions) {
                        double dx = worldPosition.x - previousTravelledPos.x;
                        double dy = worldPosition.y - previousTravelledPos.y;
                        if ((dx * dx + dy * dy) <= (memoryThreshold * memoryThreshold) &&
                                lightingEngine.isPathClear(previousTravelledPos.x, previousTravelledPos.y, worldPosition.x, worldPosition.y, roomLayout, interactableGridCache)) {
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

        // Text popups rendering remains uniform
        List<TextPopupData> popups = animationManager.getTextPopups();
        for (TextPopupData textPopup : popups) {
            Position screenPos = viewport.toScreenPosition(textPopup.position.x, textPopup.position.y);
            if (screenPos != null) {
                Color blendedPopupColor = new Color(textPopup.color.getRed(), textPopup.color.getGreen(), textPopup.color.getBlue(), textPopup.opacity);
                final double offsetX = (double) (textPopup.hashCode() * 17 % 101) % 20;
                gameCanvas.drawString(screenPos.x, screenPos.y - 1, textPopup.text, 22, blendedPopupColor, offsetX, textPopup.pixelOffsetY);
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
                });
            }
        });
    }

    public void resetGame() { setup(); gameFSM.runGame(); }

    public void loadNextLevel() {
        animationManager.clearAllAnimations();
        EntityRoomManager.getInstance().clear();

        Room spawnRoom = DungeonManager.getInstance().generateDungeon();
        Player player = Game.getInstance().getPlayer();
        player.position.x = spawnRoom.length / 2;
        player.position.y = spawnRoom.height / 2;
        EntityRoomManager.getInstance().addEntityToRoom(player, spawnRoom);

        viewport.updateCameraFocus(player.position, spawnRoom.length, spawnRoom.height);
        updateRenderingPipeline();
    }

    public void openMap() {
        final Room activeRoom = EntityRoomManager.getInstance().getPlayerRoom();
        if (activeRoom == null) return;

        final MAP[][] mapLayout = DungeonManager.getInstance().getMapLayout();
        viewport.updateCameraFocus(activeRoom.minimapPosition, mapLayout[0].length, mapLayout.length);
        gameCanvas.clearCanvas();

        for (int screenY = 0; screenY < viewport.getScreenHeight(); screenY++) {
            for (int screenX = 0; screenX < viewport.getScreenWidth(); screenX++) {
                Position worldPosition = viewport.toWorldPosition(screenX, screenY);

                if (!(worldPosition.x >= 0 && worldPosition.x < mapLayout[0].length && worldPosition.y >= 0 && worldPosition.y < mapLayout.length)) {
                    gameCanvas.drawCharacter(screenX, screenY, " ", Color.BLACK, 0.0, 0.0);
                    continue;
                }

                MAP mapTile = mapLayout[worldPosition.y][worldPosition.x];
                String minimapGlyph = "?"; Color tileColor = activeRoom.minimapPosition.equals(worldPosition) ? Color.YELLOW : Color.GRAY;

                if (mapTile != null) {
                    switch (mapTile) {
                        case SPAWN, NORMAL, TREASURE -> minimapGlyph = "□";
                        case BOSS -> { minimapGlyph = "□"; tileColor = Color.RED; }
                        case VCORRIDOR -> minimapGlyph = "|";
                        case HCORRIDOR -> minimapGlyph = "-";
                    }
                } else tileColor = Color.BLACK;

                gameCanvas.drawCharacter(screenX, screenY, minimapGlyph, tileColor, 0, 0);
            }
        }
    }

    // --- DELEGATION SHIMS FOR BACKWARD COMPATIBILITY ---
    public void addLog(String txt, Color col) { hudManager.addLog(txt, col); }
    public void clearLogContainer() { hudManager.clearLogContainer(); }
    public void updateHealth(int health) { hudManager.updateHealth(health); }
    public void updateHunger(int hunger) { hudManager.updateHunger(hunger); }
    public void updateArmor(int armor) { hudManager.updateArmor(armor); }
    public void updateWeapon(Weapon weapon) { hudManager.updateWeapon(weapon); }
    public void updateCoins(int count) { hudManager.updateCoins(count); }
    public void updateInventory(List<item.Item> inventory) { hudManager.updateInventory(inventory); }

    public void triggerTextPopup(TextPopupData textPopupData, double durationMs) {
        animationManager.triggerTextPopup(textPopupData, durationMs);
    }
    public void triggerScreenFadeSequence(Color color, double fadeInMs, double holdMs, double fadeOutMs, Runnable mid, Runnable post) {
        animationManager.triggerScreenFadeSequence(color, fadeInMs, holdMs, fadeOutMs, mid, post);
    }
    public void clearScreenEffect() { animationManager.clearScreenEffect(); }

    public void triggerEntitySlideReverse(Entity entity, Position target, double mult, double ms, ANIMATION_CURVE curve) {
        animationManager.triggerEntitySlide(entity, target, mult, ms, curve, currentTileSize, gameCanvas.getGridColumns(), true);
    }
    public void triggerEntitySlide(Entity entity, Position target, double mult, double ms, ANIMATION_CURVE curve) {
        animationManager.triggerEntitySlide(entity, target, mult, ms, curve, currentTileSize, gameCanvas.getGridColumns(), false);
    }

    public void triggerScreenShake(double intensity, double durationMs) {
        animationManager.triggerScreenShake(intensity, durationMs);
    }
}
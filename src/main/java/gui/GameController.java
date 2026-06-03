package gui;

import core.EntityRoomManager;
import entity.Entity;
import entity.Monster;
import entity.Player;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import util.Position;
import util.TILE;
import core.DungeonManager;
import core.Room;
import world.InteractableTile;

import java.util.List;

public class GameController {

    @FXML private ScrollPane mapScrollPane;
    @FXML private GridPane dungeonGrid;
    @FXML private VBox logContainer;

    @FXML private Text healthBarText;
    @FXML private Text healthValText;
    @FXML private Text hungerBarText;
    @FXML private Text hungerValText;
    @FXML private Text armorText;
    @FXML private Text weaponText;
    @FXML private Text coinsText;
    @FXML private Text potionsText;

    private final int MAP_WIDTH = 118;
    private final int MAP_HEIGHT = 30;
    private final int MAX_LOG_LINES = 5;
    private double mapFontSize;
    private final String DEFAULT_FONT_STYLE = "Courier New";
    private final int DEFAULT_FONT_SIZE = 22;

    private final int SCREEN_FLASH_DURATION_MS = 100;
    private final String ROOM_TRANSFER_TRANSITION_COLOR = "#000000";
    private final int ROOM_TRANSFER_TRANSITION_DURATION_MS = 600;

    private boolean isControlsEnabled = true;

    private Entity player = null;
    private final Text[][] gridNodes = new Text[MAP_HEIGHT][MAP_WIDTH];

    private Room lastRenderedRoom = null;

    @FXML
    public void initialize() {
        Font gameFont = Font.font(DEFAULT_FONT_STYLE, mapFontSize);

        // 1. Clear out any previous layout constraints to reset cleanly
        dungeonGrid.getColumnConstraints().clear();

        // 2. Populate the base matrix canvas nodes as usual
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Text cell = new Text(" ");
                cell.setFont(gameFont);
                cell.setFill(Color.BLACK);
                gridNodes[y][x] = cell;
                dungeonGrid.add(cell, x, y);
            }
        }

        // Connect this view container to our clean global business model manager
        GUIManager.getInstance().registerController(this);

        // System Startup
        DungeonManager.getInstance().generateDungeon();
        Room playerRoom = EntityRoomManager.getInstance().getPlayerRoom();

        drawToScreen(playerRoom);
        setMapFontSize(DEFAULT_FONT_SIZE);

        handleControls();
    }

    public void drawToScreen(Room playerRoom) {
        if (playerRoom == null) return;

        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(playerRoom);
        List<InteractableTile> interactableTiles = playerRoom.getInteractableTiles();
        TILE[][] layout = playerRoom.getLayout();

        int roomHeight = layout.length;
        int roomWidth = (roomHeight > 0) ? layout[0].length : 0;

        int offsetX = (MAP_WIDTH - roomWidth) / 2;
        int offsetY = (MAP_HEIGHT - roomHeight) / 2;

        GlyphRegistry glyphs = GlyphRegistry.getInstance();

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                int roomX = x - offsetX;
                int roomY = y - offsetY;

                if (roomX >= 0 && roomX < roomWidth && roomY >= 0 && roomY < roomHeight) {
                    // 1. Draw Entities
                    Entity occupyingEntity = null;
                    for (Entity e : entities) {
                        if (e.position.x == roomX && e.position.y == roomY) {
                            occupyingEntity = e;
                            break;
                        }
                    }

                    if (occupyingEntity != null) {
                        GlyphRegistry.GlyphStyle style = glyphs.getStyle(occupyingEntity);
                        if (occupyingEntity instanceof Player) {
                            player = occupyingEntity;
                            updateCell(x, y, style.glyph, style.color);
                        }
                        else {
                            double outputSaturation = ((occupyingEntity.id * 13 + 17) % 113) % 100;
                            Color outputColor = Color.hsb(style.color.getHue(), Math.abs(outputSaturation)/100, style.color.getBrightness());
                            updateCell(x, y, style.glyph, outputColor);
                        }
                        continue;
                    }

                    // 2. Draw Interactable Tiles
                    InteractableTile interactableTile = null;
                    for(InteractableTile it : interactableTiles) {
                        if(it.roomLayoutPosition.x == roomX && it.roomLayoutPosition.y == roomY) {
                            interactableTile = it;
                            break;
                        }
                    }
                    if(interactableTile != null) {
                        GlyphRegistry.GlyphStyle style = glyphs.getStyle(interactableTile);
                        updateCell(x, y, style.glyph, style.color);
                        continue;
                    }

                    // 3. Draw Room Tiles
                    TILE tile = layout[roomY][roomX];
                    GlyphRegistry.GlyphStyle style = (tile != null) ? glyphs.getStyle(tile, roomX, roomY, playerRoom.id) : glyphs.getVoidStyle();
                    updateCell(x, y, style.glyph, style.color);
                } else {
                    updateCell(x, y, glyphs.getVoidStyle().glyph, glyphs.getVoidStyle().color);
                }
            }
        }
    }

    private void updateCell(int x, int y, String character, Color color) {
        if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
            Text cell = gridNodes[y][x];
            cell.setText(character);
            cell.setFill(color);
        }
    }

    public void setMapFontSize(double newSize) {
        if (newSize < 6.0 || newSize > 30.0) return;
        this.mapFontSize = newSize;
        Font updatedFont = Font.font(DEFAULT_FONT_STYLE, mapFontSize);

        // Recalculate and update the column width constraint rules dynamically
        double lockedColumnWidth = mapFontSize * 0.62;
        for (javafx.scene.layout.ColumnConstraints colRule : dungeonGrid.getColumnConstraints()) {
            colRule.setPrefWidth(lockedColumnWidth);
            colRule.setMinWidth(lockedColumnWidth);
            colRule.setMaxWidth(lockedColumnWidth);
        }

        // Update the entire matrix font profile
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gridNodes[y][x].setFont(updatedFont);
            }
        }

        dungeonGrid.requestLayout();
        javafx.application.Platform.runLater(() -> {
            mapScrollPane.setHvalue(0.5);
            mapScrollPane.setVvalue(0.5);
        });
    }

    public void adjustMapFontSize(double delta) { setMapFontSize(this.mapFontSize + delta); }

    /**
     * Smart rendering pipeline that automatically detects room transitions
     * and applies the appropriate visual effects.
     */
    public void updateRenderingPipeline() {
        Room currentRoom = EntityRoomManager.getInstance().getPlayerRoom();

        if (lastRenderedRoom == null) {
            lastRenderedRoom = currentRoom;
            drawToScreen(currentRoom);
            return;
        }
        if (lastRenderedRoom != currentRoom) {
            lastRenderedRoom = currentRoom;
            roomTransferFlashScreenEffect(Color.web(ROOM_TRANSFER_TRANSITION_COLOR), currentRoom);
            return;
        }
        drawToScreen(currentRoom);
    }

    /**
     * Triggers a momentary screen overlay flash, then renders the destination room.
     */
    public void roomTransferFlashScreenEffect(Color flashColor, Room targetRoom) {
        isControlsEnabled = false;
        // Fill the grid with the flash color
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gridNodes[y][x].setFill(flashColor);
            }
        }

        FadeTransition flashDelay = new FadeTransition(Duration.millis(ROOM_TRANSFER_TRANSITION_DURATION_MS), dungeonGrid);
        flashDelay.setOnFinished(event -> {
            drawToScreen(targetRoom);
            isControlsEnabled = true;
        });
        flashDelay.play();
    }

    public void flashScreenEffect(Color flashColor) {
        Color[][] originalColors = new Color[MAP_HEIGHT][MAP_WIDTH];
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                originalColors[y][x] = (Color) gridNodes[y][x].getFill();
                gridNodes[y][x].setFill(flashColor);
            }
        }
        FadeTransition flashDelay = new FadeTransition(Duration.millis(SCREEN_FLASH_DURATION_MS), dungeonGrid);
        flashDelay.setOnFinished(event -> drawToScreen(EntityRoomManager.getInstance().getPlayerRoom()));
        flashDelay.play();
    }

    // --- HUD RENDERERS ---
    public void updateHealth(int health) {
        final int MAX_HP = 100;
        healthValText.setText(health + "/" + MAX_HP);
        healthBarText.setText(generateAsciiMeter(health, MAX_HP));
    }

    public void updateHunger(int hunger) {
        final int MAX_HUNGER = 100;
        hungerValText.setText(hunger + "/" + MAX_HUNGER);
        hungerBarText.setText(generateAsciiMeter(hunger, MAX_HUNGER));
    }

    public void updateArmor(int armor) {
        final int MAX_ARMOR = 10;
        armorText.setText(armor + "/" + MAX_ARMOR);
    }
    public void updateWeapon(String weaponName) { weaponText.setText(weaponName); }
    public void updateCoins(int amount) { coinsText.setText(String.valueOf(amount)); }
    public void updatePotions(int amount) { potionsText.setText(String.valueOf(amount)); }

    private String generateAsciiMeter(int current, int max) {
        int totalBlocks = 15;
        int filledCount = (int) Math.round(Math.min(1.0, Math.max(0.0, (double) current / max)) * totalBlocks);
        StringBuilder meter = new StringBuilder("[");
        for (int i = 0; i < totalBlocks; i++) meter.append(i < filledCount ? "■" : "·");
        return meter.append("]").toString();
    }

    // --- LOGGING RENDERERS ---
    public void addLog(String message, Color color) {
        Text logEntry = new Text(message);
        logEntry.setFont(Font.font(DEFAULT_FONT_STYLE, 14));
        logEntry.setFill(color);
        logEntry.setWrappingWidth(580);
        if (logContainer.getChildren().size() >= MAX_LOG_LINES) logContainer.getChildren().removeFirst();
        logContainer.getChildren().add(logEntry);
    }

    public void clearLogContainer() { logContainer.getChildren().clear(); }

    private void grayOutOldLogs() {
        for(int i = 0; i < logContainer.getChildren().size(); i++) {
            Node prevLog = logContainer.getChildren().get(i);
            ((Text)prevLog).setFill(Color.GRAY);
        }
    }

    // --- PLAYER CONTROL HANDLER ---
    private void handleControls() {
        dungeonGrid.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if(!isControlsEnabled) return;

                    Position unitPos = new Position(0,0);
                    switch (e.getCode()) {
                        case KeyCode.W -> unitPos.y--;
                        case KeyCode.A -> unitPos.x--;
                        case KeyCode.S -> unitPos.y++;
                        case KeyCode.D -> unitPos.x++;
                        case KeyCode.EQUALS -> adjustMapFontSize(1.0);
                        case KeyCode.MINUS  -> adjustMapFontSize(-1.0);
                        default -> { return; }
                    }
                    if ((unitPos.x != 0 || unitPos.y != 0) && player != null) {
                        grayOutOldLogs();

                        ((Player)player).handleMove(unitPos);
                        handleMonstersMove();

                        updateRenderingPipeline();
                    }
                });
            }
        });
    }

    // --- MONSTER MOVEMENT HANDLER ---
    private void handleMonstersMove() {
        Room playerRoom = EntityRoomManager.getInstance().getPlayerRoom();
        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(playerRoom);

        for(Entity e : entities) {
            if(e instanceof Monster m) {
                m.makeMove();
            }
        }
    }
}
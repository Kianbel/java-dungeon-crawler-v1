package gui;

import core.EntityRoomManager;
import entity.Entity;
import entity.Player;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import util.Position;
import world.DungeonManager;
import world.Room;
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
    private final int MAX_LOG_LINES = 6;
    private double mapFontSize = 22.0;

    private final int SCREEN_FLASH_DURATION_MS = 100;

    private final String ROOM_TRANSFER_TRANSITION_COLOR = "#000000";
    private final int ROOM_TRANSFER_TRANSITION_DURATION_MS = 600;

    private Entity player = null;
    private final Text[][] gridNodes = new Text[MAP_HEIGHT][MAP_WIDTH];

    // Track the active room state on the screen
    private Room lastRenderedRoom = null;

    @FXML
    public void initialize() {
        Font gameFont = Font.font("Courier New", mapFontSize);

        // 1. Clear out any previous layout constraints to reset cleanly
        dungeonGrid.getColumnConstraints().clear();

        // 2. Set an absolute, unyielding pixel width for every column
        // For Courier New at size 14, each character is roughly 8 to 9 pixels wide
        double lockedColumnWidth = mapFontSize * 0.62;

        javafx.scene.layout.ColumnConstraints colRule = new javafx.scene.layout.ColumnConstraints();
        colRule.setPrefWidth(lockedColumnWidth);
        colRule.setMinWidth(lockedColumnWidth);
        colRule.setMaxWidth(lockedColumnWidth);

        // Apply this exact rule to every single column in your map width
        for (int i = 0; i < MAP_WIDTH; i++) {
            dungeonGrid.getColumnConstraints().add(colRule);
        }

        // 3. Populate the base matrix canvas nodes as usual
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Text cell = new Text(" ");
                cell.setFont(gameFont); // Explicit code configuration bypasses FXML font leak bugs
                cell.setFill(Color.BLACK);
                gridNodes[y][x] = cell;
                dungeonGrid.add(cell, x, y);
            }
        }

        // Connect this view container to our clean global business model manager
        GUIManager.getInstance().registerController(this);

        // System Startup
        world.DungeonManager.getInstance().generateDungeon();
        world.Room playerRoom = core.EntityRoomManager.getInstance().getPlayerRoom();

        drawToScreen(playerRoom);
        handleControls();

        setMapFontSize(22);
    }

    public void drawToScreen(Room playerRoom) {
        if (playerRoom == null) return;

        // Grab a stable, unique integer footprint signature for this specific room instance
        int roomId = playerRoom.hashCode();

        List<Entity> entities = core.EntityRoomManager.getInstance().getEntitiesInRoom(playerRoom);
        util.TILE[][] layout = playerRoom.getLayout();

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
                    // 1. Process Entities
                    Entity occupyingEntity = null;
                    for (Entity e : entities) {
                        if (e.position.x == roomX && e.position.y == roomY) {
                            occupyingEntity = e;
                            break;
                        }
                    }

                    if (occupyingEntity != null) {
                        if (occupyingEntity instanceof Player) player = occupyingEntity;
                        GlyphRegistry.GlyphStyle style = glyphs.getStyle(occupyingEntity);
                        updateCell(x, y, style.glyph, style.color);
                        continue;
                    }

                    // 2. Process Tiles (Now passing the unique roomId identifier!)
                    util.TILE tile = layout[roomY][roomX];
                    GlyphRegistry.GlyphStyle style = (tile != null) ? glyphs.getStyle(tile, roomX, roomY, roomId) : glyphs.getVoidStyle();
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
        if (newSize < 6.0 || newSize > 24.0) return;
        this.mapFontSize = newSize;
        Font updatedFont = Font.font("Courier New", mapFontSize);

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
        Room currentRoom = core.EntityRoomManager.getInstance().getPlayerRoom();

        // 1. Initial boot check: If nothing has been drawn yet, draw it instantly
        if (lastRenderedRoom == null) {
            lastRenderedRoom = currentRoom;
            drawToScreen(currentRoom);
            return;
        }

        // 2. Transition Check: If the player shifted into a different room instance
        if (lastRenderedRoom != currentRoom) {
            // Update our tracking reference immediately so we don't loop the flash
            lastRenderedRoom = currentRoom;

            // Execute the flash effect, passing the new room to be drawn on finish
            roomTransferFlashScreenEffect(Color.web(ROOM_TRANSFER_TRANSITION_COLOR), currentRoom);
        }
        // 3. Normal Movement Check: Player is moving around inside the same room
        else {
            drawToScreen(currentRoom);
        }
    }

    /**
     * Triggers a momentary screen overlay flash, then renders the destination room.
     */
    public void roomTransferFlashScreenEffect(Color flashColor, Room targetRoom) {
        // Fill the grid with the flash color
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gridNodes[y][x].setFill(flashColor);
            }
        }

        FadeTransition flashDelay = new FadeTransition(Duration.millis(ROOM_TRANSFER_TRANSITION_DURATION_MS), dungeonGrid);
        flashDelay.setOnFinished(event -> {
            drawToScreen(targetRoom);
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
    public void updateHealth(int current, int max) {
        healthValText.setText(current + "/" + max);
        healthBarText.setText(generateAsciiMeter(current, max));
    }

    public void updateHunger(int current, int max) {
        hungerValText.setText(current + "/" + max);
        hungerBarText.setText(generateAsciiMeter(current, max));
    }

    public void updateArmor(int current, int max) { armorText.setText(current + "/" + max); }
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
        logEntry.setFont(Font.font("Courier New", 14));
        logEntry.setFill(color);
        logEntry.setWrappingWidth(580);
        if (logContainer.getChildren().size() >= MAX_LOG_LINES) logContainer.getChildren().remove(0);
        logContainer.getChildren().add(logEntry);
    }

    public void clearLogContainer() { logContainer.getChildren().clear(); }

    private void handleControls() {
        dungeonGrid.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
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
                        player.walk(unitPos);
                        updateRenderingPipeline();
                    }
                });
            }
        });
    }
}
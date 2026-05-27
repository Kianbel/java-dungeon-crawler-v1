package gui;

import core.EntityRoomManager;
import entity.Entity;
import entity.Monster;
import entity.Player;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import util.DIRECTION;
import util.Position;
import util.TILE;
import world.DungeonManager;
import world.Room;

import java.util.List;

public class GameController {
    @FXML
    private Text textOutput;

    private Entity player = null;

    @FXML
    private void initialize() {
        DungeonManager.getInstance().generateDungeon();

        Room playerRoom = EntityRoomManager.getInstance().getPlayerRoom();
        drawToScreen(playerRoom);

        handleControls();
    }

    private void drawToScreen(Room playerRoom) {
        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(playerRoom);
        TILE[][] layout = playerRoom.getLayout();

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < layout.length; i++) {
            for(int j = 0; j < layout[0].length; j++) {
                if(layout[i][j] == null) {
                    stringBuilder.append('.');
                    continue;
                }
                boolean entityDrawFlag = false;
                for(Entity e : entities) {
                    if(e.position.x == j && e.position.y == i) {
                        if(e instanceof Player) {
                            stringBuilder.append('@');
                            player = e;
                        }
                        else if(e instanceof Monster) stringBuilder.append('M');
                        entityDrawFlag = true;
                    }
                }

                if(entityDrawFlag) continue;
                switch (layout[i][j]) {
                    case WALL -> stringBuilder.append('#');
                    case DOOR -> stringBuilder.append('+');
                    case FLOOR -> stringBuilder.append(' ');
                }
            }
            stringBuilder.append('\n');
        }

        textOutput.setText(stringBuilder.toString());
    }

    private void handleControls() {
        textOutput.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    Position unitPos = new Position(0,0);

                    switch (e.getCode()) {
                        case KeyCode.W -> unitPos.y--;
                        case KeyCode.A -> unitPos.x--;
                        case KeyCode.S -> unitPos.y++;
                        case KeyCode.D -> unitPos.x++;
                    }

                    if(unitPos.x != 0 || unitPos.y != 0) {
                        player.walk(unitPos);
                        drawToScreen(EntityRoomManager.getInstance().getPlayerRoom());
                    }
                });
            }
        });
    }
}

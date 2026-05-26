package gui;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import util.TILE;
import world.DungeonManager;

public class GameController {
    @FXML
    private Text textOutput;

    @FXML
    private void initialize() {

        DungeonManager.getInstance().generateDungeon();
        TILE[][] minimap = DungeonManager.getInstance().getMinimapOverviewLayout();
        drawToScreen(minimap);

        handleControls();
    }

    private void drawToScreen(TILE[][] layout) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < layout.length; i++) {
            for(int j = 0; j < layout[0].length; j++) {
                if(layout[i][j] == null) {
                    stringBuilder.append('.');
                    continue;
                }
                switch (layout[i][j]) {
                    case TILE.ROOM -> stringBuilder.append('%');
                    case TILE.HCORRIDOR -> stringBuilder.append('-');
                    case TILE.VCORRIDOR -> stringBuilder.append('|');
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
                    if (e.getCode() == KeyCode.SPACE) {
                        DungeonManager.getInstance().generateDungeon();
                        drawToScreen(DungeonManager.getInstance().getMinimapOverviewLayout());
                    }
                });
            }
        });
    }
}

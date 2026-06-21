package core;

import entity.Player;
import gui.GameController;

public class GameManager {
    private static GameController gameController;
    private int START_FLOOR = 1;
    private int currentFloor = START_FLOOR;
    private Player player;

    private static final GameManager instance = new GameManager();
    private GameManager() {}
    public static void initialize(GameController gameController) {
        GameManager.gameController = gameController;
    }
    public static GameManager getInstance() {
        assert gameController != null : "Game controller not yet initialized";
        return instance;
    }

    public void reset() {
        player = null;
        currentFloor = START_FLOOR;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void nextFloor() {
        currentFloor++;

        gameController.loadNextLevel();
    }
}

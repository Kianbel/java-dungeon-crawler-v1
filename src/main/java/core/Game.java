package core;

import entity.Player;
import gui.GameController;

public class Game {
    private static GameController gameController;
    private int currentFloor = 1;
    private Player player;

    private static final Game instance = new Game();
    private Game() {}
    public static void initialize(GameController gameController) {
        Game.gameController = gameController;
    }
    public static Game getInstance() {
        assert gameController != null : "Game controller not yet initialized";
        return instance;
    }

    public void reset() {
        player = null;
        currentFloor = 1;
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

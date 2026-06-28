package core;

import entity.Player;
import gui.GameController;
import util.LEVEL_THEME;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private static GameController gameController;
    private final int START_FLOOR = 1;
    private int currentFloor = START_FLOOR;
    private Player player;
    private final Random random = new Random();

    private static final GameManager instance = new GameManager();
    public static GameManager getInstance() {
        assert gameController != null : "Game controller not yet initialized";
        return instance;
    }


    private List<LEVEL_THEME> availableLevelThemes;
    private LEVEL_THEME currentLevelTheme;
    private GameManager() {
        currentLevelTheme = LEVEL_THEME.DUNGEON;
        availableLevelThemes = new ArrayList<>(List.of(
                LEVEL_THEME.SANDSTONE
        ));
    }

    // ----- METHODS ------------
    public static void initialize(GameController gameController) {
        GameManager.gameController = gameController;
    }

    public LEVEL_THEME getRandomLevelTheme() {
        if(availableLevelThemes.isEmpty()) {
            System.out.println("NO MORE AVAILABLE LEVEL THEMES, DEFAULTING TO DUNGEON THEME");
            return LEVEL_THEME.DUNGEON;
        }

        return availableLevelThemes.remove(random.nextInt(availableLevelThemes.size()));
    }

    public void reset() {
        player = null;
        currentFloor = START_FLOOR;
        currentLevelTheme = LEVEL_THEME.DUNGEON;

        availableLevelThemes = new ArrayList<>(List.of(
                LEVEL_THEME.SANDSTONE
        ));
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

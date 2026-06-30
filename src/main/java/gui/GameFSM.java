package gui;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import entity.Monster;
import entity.projectile.Projectile;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import util.Position;
import world.InteractableTile;
import world.SpikeTrap;

import java.util.ArrayList;
import java.util.List;

public class GameFSM {
    public enum STATE {
        RUNNING,
        MAP,
        PAUSE,
        RESET,
        TITLE,
        GAME_OVER,
        CONTROLS,
    }

    private boolean fromPause;
    private boolean fromTitle;

    private final GameController gameController;
    private STATE currentState;

    public GameFSM(GameController gameController) {
        this.gameController = gameController;
        this.currentState = STATE.TITLE;
    }

    public void runGame() {
        this.currentState = STATE.RUNNING;
        renderCurrentState();
    }

    public void update(KeyCode key) {
        if (currentState == null) return;

        switch (currentState) {
            case RUNNING -> handleRunning(key);
            case MAP     -> handleMap(key);
            case PAUSE   -> handlePause(key);
            case RESET   -> handleReset(key);
            case TITLE -> handleTitle(key);
            case GAME_OVER -> handleGameOver(key);
            case CONTROLS -> handleControls(key);
        }
    }

    public void switchState(STATE newState) {
        if (currentState == newState) return;
        System.out.println("State changing from " + currentState + " to " + newState);
        currentState = newState;
        renderCurrentState();
    }

    public STATE getCurrentState() {
        return currentState;
    }

    private void renderCurrentState() {
        switch (currentState) {
            case RUNNING, TITLE, GAME_OVER, PAUSE, CONTROLS -> gameController.updateRenderingPipeline();
            case MAP -> gameController.openMap();
        }
    }

    private void handleGameOver(KeyCode key) {
        if(key == KeyCode.SPACE) {
            gameController.resetGame();
            switchState(STATE.RUNNING);
        }
        else if(key == KeyCode.BACK_SPACE) {
            switchState(STATE.TITLE);
        }
    }

    private void handleTitle(KeyCode key) {
        if(key == KeyCode.SPACE) {
            gameController.resetGame();
            switchState(STATE.RUNNING);
        }
        else if(key == KeyCode.H) {
            switchState(STATE.CONTROLS);
            fromTitle = true;
        }
        else if(key == KeyCode.ESCAPE) {
            System.exit(0);
        }
    }

    private void handlePause(KeyCode key) {
        if (key == KeyCode.ESCAPE) {
            switchState(STATE.RUNNING);
        }
        else if(key == KeyCode.BACK_SPACE) {
            switchState(STATE.TITLE);
        }
        else if(key == KeyCode.H) {
            switchState(STATE.CONTROLS);
            fromPause = true;
        }
    }

    private void handleControls(KeyCode key) {
        if(key == KeyCode.H) {
            if(fromPause) switchState(STATE.PAUSE);
            else if(fromTitle) switchState(STATE.TITLE);

            fromTitle = false;
            fromPause = false;
        }
    }

    private void handleRunning(KeyCode key) {
        final Player player = (Player) EntityRoomManager.getInstance().getPlayer();

        // Safety check if player died out-of-turn
        if (player == null || player.isDead) {
            switchState(STATE.GAME_OVER);
            return;
        }

        Position movementVector = new Position(0,0);
        boolean isTickAction = false;

        switch (key) {
            case W, UP    -> { movementVector.y--; isTickAction = true; }
            case A, LEFT  -> { movementVector.x--; isTickAction = true; }
            case S, DOWN  -> { movementVector.y++; isTickAction = true; }
            case D, RIGHT -> { movementVector.x++; isTickAction = true; }
            case T        -> player.toggleGodMode();
            case F -> { player.eat(); isTickAction = true; }
            case H -> { player.heal(); isTickAction = true; }
            case SPACE    -> {
                GUIManager.getInstance().triggerTextPopup("wait", Color.WHITE, player.position);
                isTickAction = true;
            }
            case M -> {
                switchState(STATE.MAP);
                return;
            }
            case ESCAPE -> {
                switchState(STATE.PAUSE);
                return;
            }
            case R -> {
                switchState(STATE.RESET);
                return;
            }
        }

        if (isTickAction) {
            player.handleMove(movementVector);

            Room currentRoom = EntityRoomManager.getInstance().getPlayerRoom();

            // Defensively copy the lists to prevent ConcurrentModification/Index Exceptions
            List<Entity> entities = new ArrayList<>(EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom));
            for (Entity entity : entities) {
                if (entity instanceof Monster monster) monster.makeMove();
                if (entity instanceof Projectile projectile) projectile.makeMove();
            }

            List<InteractableTile> interactableTiles = new ArrayList<>(currentRoom.getInteractableTiles());
            for (InteractableTile tile : interactableTiles) {
                if (tile instanceof SpikeTrap spikeTrap) spikeTrap.makeMove();
            }

            // Check immediately if the player died during this tick action
            if (player.isDead) {
                switchState(STATE.GAME_OVER);
                return;
            }

            // Normal tick updates render here
            gameController.updateRenderingPipeline();
        }
    }

    private void handleMap(KeyCode key) {
        switchState(STATE.RUNNING);
    }

    private void handleReset(KeyCode key) {
        gameController.resetGame();
        switchState(STATE.RUNNING);
    }
}
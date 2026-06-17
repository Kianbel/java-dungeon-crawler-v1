package gui;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.Player;
import entity.monster.Monster;
import entity.projectile.Projectile;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import util.Position;

import java.util.List;

public class GameFSM {
    private enum STATE {
        RUNNING,
        MAP,
        DIED,
        EXIT,
        PAUSE,
        RESET,
    }

    private final GameController gameController;
    private STATE currentState;

    public GameFSM(GameController gameController) {
        this.gameController = gameController;
    }

    public void runGame() {
        currentState = STATE.RUNNING;
    }

    public void update(KeyCode key) {
        System.out.println("update");
        assert currentState != null : "Current state must be set up";

        switch(currentState) {
            case RUNNING -> handleRunning(key);
            case MAP -> handleMap(key);
            case DIED -> {
            }
            case EXIT -> {
            }
            case PAUSE -> {
            }
            case RESET -> handleReset(key);
        }
    }

    public void switchState(STATE newState) {
        System.out.println("switchstate");
        currentState = newState;

        switch(currentState) {
            case RUNNING -> {
            }
            case MAP -> {
            }
            case DIED -> {
            }
            case EXIT -> {
            }
            case PAUSE -> {
            }
            case RESET -> {
                gameController.resetGame();
                System.out.println("game reset");
            }
        }
        renderCurrentState();
    }

    public STATE getCurrentState() {
        return currentState;
    }






    private void renderCurrentState() {
        System.out.println("rendercurrentstate");
        switch (currentState) {
            case RUNNING -> {
                System.out.println("running");
                gameController.updateRenderingPipeline();
            }
            case DIED -> {
                System.out.println("died");
                gameController.updateRenderingPipeline();
                // TODO: display game over / restart message
            }
            case MAP -> {
                System.out.println("map");
                gameController.openMap();
            }
            case EXIT -> {
                System.out.println("exit");
                // TODO: quit game
            }
            case PAUSE -> {
                System.out.println("pause");
                // TODO: display pause message
            }
        }
    }

    private void handleRunning(KeyCode key) {
        System.out.println("handlerunning");

        final Player player = (Player) EntityRoomManager.getInstance().getPlayer();
        Position movementVector = new Position(0,0);
        boolean isTickAction = false;

        switch (key) {
            case W, UP -> {
                movementVector.y--;
                isTickAction = true;
            }
            case A, LEFT -> {
                movementVector.x--;
                isTickAction = true;
            }
            case S, DOWN -> {
                movementVector.y++;
                isTickAction = true;
            }
            case D, RIGHT -> {
                movementVector.x++;
                isTickAction = true;
            }
            case T -> {
                player.toggleGodMode();
            }
            case SPACE -> {
                movementVector = new Position(0,0);
                GUIManager.getInstance().triggerTextPopup("wait", Color.WHITE, player.position);
                isTickAction = true;
            }
            case M -> {
                switchState(STATE.MAP);
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
            List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
            for(int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                if(entity instanceof Monster monster) monster.makeMove();
                if(entity instanceof Projectile projectile) projectile.makeMove();
            }

            if(player.isDead) {
                switchState(STATE.DIED);
            }
            else renderCurrentState();
        }
    }

    private void handleMap(KeyCode key) {
        switchState(STATE.RUNNING);
    }

    private void handleReset(KeyCode key) {

    }
}

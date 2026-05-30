package gui;

import javafx.scene.paint.Color;
import world.Room;

public class GUIManager {
    private static final GUIManager instance = new GUIManager();
    private GameController controller;

    private GUIManager() {}

    public static GUIManager getInstance() { return instance; }

    /**
     * Connects the active FXML view controller instance to the manager pipeline.
     */
    public void registerController(GameController controller) {
        this.controller = controller;
    }

    public void refreshScreen(Room currentRoom) {
        if (controller != null) controller.drawToScreen(currentRoom);
    }

    public void printLog(String message, Color color) {
        if (controller != null) controller.addLog(message, color);
    }

    public void wipeLogs() {
        if (controller != null) controller.clearLogContainer();
    }

    public void triggerHurtFlash() {
        if (controller != null) controller.flashScreenEffect(Color.DARKRED);
    }

    public void triggerHealFlash() {
        if (controller != null) controller.flashScreenEffect(Color.web("#113311"));
    }

    // --- HUD Passthroughs ---
    public void setHP(int current) { if (controller != null) controller.updateHealth(current); }
    public void setHunger(int current) { if (controller != null) controller.updateHunger(current); }
    public void setArmor(int current) { if (controller != null) controller.updateArmor(current); }
    public void setWeapon(String name) { if (controller != null) controller.updateWeapon(name); }
    public void setCoins(int amount) { if (controller != null) controller.updateCoins(amount); }
    public void setPotions(int amount) { if (controller != null) controller.updatePotions(amount); }
}
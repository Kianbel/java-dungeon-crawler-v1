package gui;

import javafx.scene.paint.Color;
import core.room.Room;
import weapon.Weapon;

public class GUIManager {
    private static final GUIManager instance = new GUIManager();
    private GameController controller;

    private GUIManager() {}

    public static GUIManager getInstance() { return instance; }

    public void registerController(GameController controller) {
        this.controller = controller;
    }

    private boolean isPipelineOperational() {
        return controller != null;
    }

    public void refreshScreen(Room currentRoom) {
        if (isPipelineOperational()) controller.updateRenderingPipeline();
    }

    public void printLog(String message, Color color) {
        if (isPipelineOperational()) controller.addLog(message, color);
    }

    public void wipeLogs() {
        if (isPipelineOperational()) controller.clearLogContainer();
    }

    public void triggerHurtFlash() {
        if (isPipelineOperational()) controller.flashScreenEffect(Color.DARKRED);
    }

    public void triggerHealFlash() {
        if (isPipelineOperational()) controller.flashScreenEffect(Color.web("#113311"));
    }

    public void setHP(int current) { if (isPipelineOperational()) controller.updateHealth(current); }
    public void setHunger(int current) { if (isPipelineOperational()) controller.updateHunger(current); }
    public void setArmor(int current) { if (isPipelineOperational()) controller.updateArmor(current); }
    public void setWeapon(Weapon weapon) { if (isPipelineOperational()) controller.updateWeapon(weapon); }
    public void setCoins(int amount) { if (isPipelineOperational()) controller.updateCoins(amount); }
    public void setPotions(int amount) { if (isPipelineOperational()) controller.updatePotions(amount); }
}
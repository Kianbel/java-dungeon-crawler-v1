package gui;

import entity.Entity;
import item.Item;
import javafx.scene.paint.Color;
import core.room.type.Room;
import util.ANIMATION_CURVE;
import util.Position;
import item.weapon.Weapon;

import java.util.List;

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

    public void printDevLog(String message) {
        printLog(message, Color.PINK);
    }

    public void wipeLogs() {
        if (isPipelineOperational()) controller.clearLogContainer();
    }

    public void triggerHurtFlash() {
        if (isPipelineOperational()) controller.flashScreenEffect(Color.DARKRED, 50, 0.3, 0);
    }

    public void triggerHealFlash() {
        if (isPipelineOperational()) controller.flashScreenEffect(Color.GREEN, 200, 0.3, 0);
    }

    public void triggerColorFlash(Color color, int durationMilis) {
        if (isPipelineOperational()) controller.flashScreenEffect(color, durationMilis, 0.3, 0);
    }

    public void triggerColorFlash(Color color) {
        controller.flashScreenEffect(color, 60, 0.3, 0);
    }

    public void triggerRoomTransitionFlash() {
        if (isPipelineOperational()) controller.flashScreenEffect(Color.BLACK, 500, 0.8, 0);
    }

    public void triggerAttackAnimation(Entity attacker, Position targetPosition) {
        if (isPipelineOperational()) {
            controller.triggerEntitySlide(attacker, targetPosition, 0.5, 120, ANIMATION_CURVE.TRIANGLE);
        }
    }

    public void triggerTextPopup(String text, Color color, Position position) {
        triggerTextPopup(text, color, position, 800);
    }

    public void triggerTextPopup(String text, Color color, Position position, double durationMs) {
        if (isPipelineOperational()) {
            TextPopupData textPopupData = new TextPopupData(text, color, position);
            controller.triggerTextPopup(textPopupData, durationMs);
        }
    }


    public void triggerAttackAnimation(Entity attacker, Entity target) {
        triggerAttackAnimation(attacker, target.position);
    }

    public void triggerMoveAnimation(Entity entity, Position targetPosition) {
        if (isPipelineOperational()) {
            controller.triggerEntitySlideReverse(entity, targetPosition, 1, 100, ANIMATION_CURVE.EASE_OUT);
        }
    }

    public void setHP(int current) { if (isPipelineOperational()) controller.updateHealth(current); }
    public void setHunger(int current) { if (isPipelineOperational()) controller.updateHunger(current); }
    public void setArmor(int current) { if (isPipelineOperational()) controller.updateArmor(current); }
    public void setWeapon(Weapon weapon) { if (isPipelineOperational()) controller.updateWeapon(weapon); }
    public void setCoins(int amount) { if (isPipelineOperational()) controller.updateCoins(amount); }
    public void updateInventory(List<Item> inventory) {
        if (this.controller != null) {
            this.controller.updateInventory(inventory);
        }
    }
}
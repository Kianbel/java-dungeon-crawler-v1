package gui;

import gui.dataclass.UITheme;
import item.weapon.Weapon;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import java.util.*;

public class HUDManager {
    private final HBox rootContainer;
    private final VBox statsPanel, logsPanel, controlsPanel, controlsBox, logContainer, inventoryBox, inventoryPanel;
    private final Label statsHeader, logsHeader, controlsHeader, inventoryHeader;
    private final Label lblHealth, lblHunger, lblCoins, lblArmor, lblWeapon;
    private final Label healthBar, healthValText, hungerBar, hungerValText, armorText, weaponText, coinsText;

    private final int MAX_LOG_LINES = 6;

    public HUDManager(GameController controller) {
        // Reflection fields initialization using package-private visibility mapping or standard getters
        this.rootContainer = controller.rootContainer;
        this.statsPanel = controller.statsPanel;
        this.logsPanel = controller.logsPanel;
        this.controlsPanel = controller.controlsPanel;
        this.controlsBox = controller.controlsBox;
        this.logContainer = controller.logContainer;
        this.inventoryBox = controller.inventoryBox;
        this.inventoryPanel = controller.inventoryPanel;
        this.statsHeader = controller.statsHeader;
        this.logsHeader = controller.logsHeader;
        this.controlsHeader = controller.controlsHeader;
        this.inventoryHeader = controller.inventoryHeader;
        this.lblHealth = controller.lblHealth;
        this.lblHunger = controller.lblHunger;
        this.lblCoins = controller.lblCoins;
        this.lblArmor = controller.lblArmor;
        this.lblWeapon = controller.lblWeapon;
        this.healthBar = controller.healthBar;
        this.healthValText = controller.healthValText;
        this.hungerBar = controller.hungerBar;
        this.hungerValText = controller.hungerValText;
        this.armorText = controller.armorText;
        this.weaponText = controller.weaponText;
        this.coinsText = controller.coinsText;
    }

    public void applyInterfaceTheme() {
        rootContainer.setStyle("-fx-background-color: " + UITheme.BG_ROOT + "; " + UITheme.CSS_FONT_FAMILY);

        String subPanelStyle = "-fx-border-color: " + UITheme.BORDER_NORMAL + "; -fx-border-width: 2; -fx-background-color: " + UITheme.BG_CARD + ";";
        statsPanel.setStyle(subPanelStyle);
        logsPanel.setStyle(subPanelStyle);
        controlsPanel.setStyle(subPanelStyle);
        inventoryPanel.setStyle(subPanelStyle);

        String headerStyle = UITheme.STYLE_HEADER + " -fx-text-fill: #ede6c8;";
        statsHeader.setStyle(headerStyle);
        logsHeader.setStyle(headerStyle);
        controlsHeader.setStyle(headerStyle);
        inventoryHeader.setStyle(headerStyle);

        lblHealth.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HEALTH) + ";");
        lblHunger.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HUNGER) + ";");
        lblCoins.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_COIN) + ";");
        lblArmor.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_ARMOR) + ";");
        lblWeapon.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_WEAPON) + ";");

        String activeMetricStyle = UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_PARCHMENT) + "; -fx-font-weight: bold;";
        healthValText.setStyle(activeMetricStyle);
        hungerValText.setStyle(activeMetricStyle);
        coinsText.setStyle(activeMetricStyle);

        healthBar.setStyle(UITheme.STYLE_BAR + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HEALTH) + ";");
        hungerBar.setStyle(UITheme.STYLE_BAR + " -fx-text-fill: " + toHexWebColor(UITheme.STAT_HUNGER) + ";");
        armorText.setStyle(activeMetricStyle);
        weaponText.setStyle(activeMetricStyle);
        coinsText.setStyle(activeMetricStyle);

        buildControlsReferenceHud();
    }

    public void addLog(String txt, Color col) {
        Label element = new Label(txt);
        element.setStyle(UITheme.STYLE_LOG + " -fx-text-fill: " + toHexWebColor(col) + ";");
        element.setWrapText(true);

        if (logContainer.getChildren().size() >= MAX_LOG_LINES) logContainer.getChildren().removeFirst();
        logContainer.getChildren().add(element);
    }

    public void clearLogContainer() { logContainer.getChildren().clear(); }
    public void updateHealth(int health) { healthValText.setText(health + "/100"); healthBar.setText(buildBarMeter(health)); }
    public void updateHunger(int hunger) { hungerValText.setText(hunger + "/100"); hungerBar.setText(buildBarMeter(hunger)); }
    public void updateArmor(int armor) { armorText.setText(armor + "/10"); }
    public void updateWeapon(Weapon weapon) { weaponText.setText(weapon + ""); }
    public void updateCoins(int count) { coinsText.setText(String.valueOf(count)); }

    public void updateInventory(List<item.Item> inventory) {
        inventoryBox.getChildren().clear();
        if (inventory == null || inventory.isEmpty()) {
            Label emptyLabel = new Label("Empty");
            emptyLabel.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";");
            inventoryBox.getChildren().add(emptyLabel);
            return;
        }

        Map<String, Integer> itemCounts = new LinkedHashMap<>();
        for (item.Item item : inventory) {
            itemCounts.put(item.name, itemCounts.getOrDefault(item.name, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            Label itemLabel = new Label(String.format("- %s ( x%d )", entry.getKey(), entry.getValue()));
            itemLabel.setStyle(UITheme.STYLE_TEXT + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_PARCHMENT) + ";");
            inventoryBox.getChildren().add(itemLabel);
        }
    }

    private void buildControlsReferenceHud() {
        controlsBox.getChildren().clear();
        String[] mappings = {
                "[WASD]  Move Explorer          [M] Open Map",
                "[SPACE] Rest / Skip Turn       [+/-] Zoom",
        };
        for (String item : mappings) {
            Label element = new Label(item);
            element.setStyle(UITheme.STYLE_CTRL + " -fx-text-fill: " + toHexWebColor(UITheme.TEXT_MUTED) + ";");
            controlsBox.getChildren().add(element);
        }
    }

    private String buildBarMeter(int value) {
        final int BARS_AMOUNT = 25;
        int fill = (int) Math.round((Math.max(0, Math.min(100, value)) / 100.0) * BARS_AMOUNT);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < BARS_AMOUNT; i++) sb.append(i < fill ? "■" : "·");
        return sb.append("]").toString();
    }

    private String toHexWebColor(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
    }
}
package item;

import javafx.scene.paint.Color;

public class SellableItem extends Item {
    private int valueInCoins;

    public SellableItem(String name, int amount, int valueInCoins) {
        super(name, amount);
        this.valueInCoins = valueInCoins;
    }

    public int sell() {
        return 0;
    }

    public int getValueInCoins() {
        return valueInCoins;
    }

    public void setValueInCoins(int valueInCoins) {
        this.valueInCoins = valueInCoins;
    }
}

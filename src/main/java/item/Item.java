package item;

import javafx.scene.paint.Color;

public abstract class Item {
    public String name;
    public int amount;
    public int valueInCoins;

    public String character = null;
    public Color color = null;

    public Item(String name) {
        this.name = name;
        this.amount = 1;
    }

    public Item(String name, int amount) {
        this(name);
        this.amount = amount;
    }

    @Override
    public String toString() {
        return name;
    }

    public void overrideCharacter(String character) {
        if(character == null) return;
        this.character = character;
    }

    public void overrideColor(Color color) {
        if(color == null) return;
        this.color = color;
    }

    public void decreaseAmount() {
        if(amount <= 0) return;
        amount--;
    }
}

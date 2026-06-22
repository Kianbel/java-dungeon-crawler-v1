package item.weapon;

import item.Item;
import javafx.scene.paint.Color;

public abstract class Ammo extends Item {
    public String name;
    public int damage;
    private int amount;

    public Ammo(String name, int damage, int amount, Color color, String character) {
        super(name);
        this.damage = damage;
        this.amount = amount;
        overrideCharacter(character);
        overrideColor(color);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(amount, 0);
    }

    public void decreaseAmount() {
        if(amount < 0) return;
        this.amount -= 1;
    }
}

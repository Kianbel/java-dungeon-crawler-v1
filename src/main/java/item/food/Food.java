package item.food;

import item.Item;

public abstract class Food extends Item {
    public int hungerPoints;

    public Food(String name, int hungerPoints) {
        super(name);
        this.hungerPoints = hungerPoints;
    }

    public Food(String name, int hungerPoints, int amount) {
        this(name, hungerPoints);
        this.amount = amount;
    }
}

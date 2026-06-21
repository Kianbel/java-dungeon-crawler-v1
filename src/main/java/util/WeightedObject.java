package util;

import item.Item;
import world.DroppedItem;


public class WeightedObject {
    public Object object;
    public double weight;
    public Object attribute1;

    public WeightedObject(Object object, double weight) {
        if(object instanceof Item) throw new RuntimeException("Use the second constructor to make it a DroppedItem");
        this.object = object;
        this.weight = weight;
    }

    public WeightedObject(Item item, Position position, double weight) {
        this(new DroppedItem(position, item), weight);
    }

    public WeightedObject(Object object, Object attribute1, double weight) {
        this.object = object;
        this.weight = weight;
        this.attribute1 = attribute1;
    }
}

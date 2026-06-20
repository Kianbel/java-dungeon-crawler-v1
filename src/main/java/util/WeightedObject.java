package util;

import core.EntityRoomManager;
import core.room.type.Room;
import item.Item;
import world.DroppedItem;
import world.InteractableTile;

import java.util.List;
import java.util.Random;

public class WeightedObject {
    public Object object;
    public double weight;

    public WeightedObject(Object object, double weight) {
        if(object instanceof Item) throw new RuntimeException("Use the second constructor to make it a DroppedItem");
        this.object = object;
        this.weight = weight;
    }

    public WeightedObject(Item item, Position position, double weight) {
        this(new DroppedItem(position, item), weight);
    }
}

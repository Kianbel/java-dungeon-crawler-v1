package util;

import java.util.List;
import java.util.Random;

public class Randomizer {

    private static final Random random = new Random();

    @SafeVarargs
    public static <T> T pick(T... list) {
        return list[random.nextInt(list.length)];
    }

    public static Object rollWeightedObjects(List<WeightedObject> lootTable) {
        if(lootTable == null) throw new RuntimeException("Loot table is null");
        if(lootTable.isEmpty()) throw new RuntimeException("Loot table is empty");

        double totalWeight = 0;
        for(WeightedObject weightedObject : lootTable) {
            totalWeight += weightedObject.weight;
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;
        for(WeightedObject weightedObject : lootTable) {
            cumulativeWeight += weightedObject.weight;
            if(roll <= cumulativeWeight) {
                return weightedObject.object;
            }
        }
        return null;
    }
}

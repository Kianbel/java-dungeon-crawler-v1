package item.food;

import javafx.scene.paint.Color;

public class MonsterMeat extends Food {
    public MonsterMeat(int amount) {
        super("Monster Meat", 14, amount);
        overrideCharacter("🍖");
        overrideColor(Color.PURPLE.darker());
    }
}

package item.key;

import item.Item;
import javafx.scene.paint.Color;

public abstract class Key extends Item {
    public Key(String name) {
        super(name);
        overrideCharacter("🗝");
        overrideColor(Color.WHITE);
    }
}

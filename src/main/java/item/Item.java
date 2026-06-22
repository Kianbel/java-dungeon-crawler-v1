package item;

import javafx.scene.paint.Color;

public abstract class Item {
    public String name;
    public String character = null;
    public Color color = null;

    public Item(String name) {
        this.name = name;
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
}

package item.armor;

import javafx.scene.paint.Color;

public class IronMail extends Armor {
    public IronMail() {
        super("Iron Mail", 10);
        overrideCharacter("🥼"); // TODO: change character
        overrideColor(Color.WHITESMOKE);
    }
}

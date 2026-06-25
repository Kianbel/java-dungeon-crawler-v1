package item.armor;

import javafx.scene.paint.Color;

public class OdrilMail extends Armor {
    public OdrilMail() {
        super("Odril Mail", 17);
        overrideCharacter("🎽");
        overrideColor(Color.GREENYELLOW.darker().darker());
    }
}

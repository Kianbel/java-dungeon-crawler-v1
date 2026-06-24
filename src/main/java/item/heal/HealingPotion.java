package item.heal;

import gui.dataclass.UITheme;

public class HealingPotion extends Potion {
    public int healPoints;

    public HealingPotion(int amount) {
        super("Healing Potion", amount);
        healPoints = 10;
        overrideCharacter("𐃯"); // TODO: change character
        overrideColor(UITheme.STAT_HEALTH);
    }
}

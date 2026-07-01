package item.heal;

import gui.dataclass.UITheme;

public class HealingPotion extends Potion {
    public int healPoints;

    public HealingPotion(int amount) {
        super("Healing Potion +30HP", amount);
        healPoints = 30;
        overrideCharacter("▲");
        overrideColor(UITheme.STAT_HEALTH);
    }
}

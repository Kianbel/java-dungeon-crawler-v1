package core;

import entity.Player;
import entity.boss.FlareWitch;
import entity.monster.GiantSpider;
import entity.monster.Zombie;
import entity.projectile.Fireball;
import gui.GlyphRegistry;
import gui.GlyphStyle;
import gui.UITheme;
import javafx.scene.paint.Color;
import util.TILE;
import world.*;

public class GameData {
    private static final GameData instance = new GameData();
    private GameData() {}
    public static GameData getInstance() {return instance;}

    private int currentDungeonFloor = 1;
    private double monsterHealthMultiplier = 1.2;

    public void increaseDungeonFloor() {
        currentDungeonFloor++;}

    public int getCurrentDungeonFloor() {
        return currentDungeonFloor;
    }

    public double getMonsterHealthMultiplier() {
        return monsterHealthMultiplier;
    }

    public void loadColorPaletteBasedOnDungeonFloor() {
    }

    public void loadTileRegistryBasedOnDungeonFloor() {
    }
}

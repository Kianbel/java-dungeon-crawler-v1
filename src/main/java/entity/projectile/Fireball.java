package entity.projectile;

import entity.Entity;
import entity.Player;
import gui.GUIManager;
import gui.UITheme;
import javafx.scene.paint.Color;
import util.Position;

public class Fireball extends Projectile {
    public Fireball(Position moveDirectionInUnitPos, Position position) {
        super("Fireball", 8, moveDirectionInUnitPos, position);
        setIlluminated(true);
        setIlluminationRange(3);
        overrideColor(UITheme.PROJECTILE_FIREBALL);
    }

    @Override
    public void makeMove() {
        move();
    }

    @Override
    public void attack(Entity targetEntity) {
        if(targetEntity instanceof Fireball) return;
        super.attack(targetEntity);

        if(targetEntity instanceof Player) {
            GUIManager.getInstance().triggerColorFlash(UITheme.PROJECTILE_FIREBALL, 1000);
        }
    }
}

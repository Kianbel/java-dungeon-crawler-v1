package entity.projectile;

import entity.Entity;
import entity.Player;
import gui.GUIManager;
import javafx.scene.paint.Color;
import util.Position;

public class Fireball extends Projectile {
    public Fireball(Position moveDirectionInUnitPos, Position position) {
        super("Fireball", 8, moveDirectionInUnitPos, position);
        setIlluminated(true);
        setIlluminationRange(3);
        overrideColor(Color.ORANGE);
    }

    @Override
    public void makeMove() {
        move();
    }

    @Override
    public void attack(Entity targetEntity) {
        if(targetEntity instanceof Fireball || targetEntity.equals(this)) return;
        super.attack(targetEntity);

        if(targetEntity instanceof Player) {
            GUIManager.getInstance().triggerColorFlash(Color.ORANGE, 100);
        }
    }
}

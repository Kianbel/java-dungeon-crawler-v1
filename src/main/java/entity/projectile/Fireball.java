package entity.projectile;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
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
        super.attack(targetEntity);
        GUIManager.getInstance().triggerColorFlash(Color.ORANGE, 100);
    }
}

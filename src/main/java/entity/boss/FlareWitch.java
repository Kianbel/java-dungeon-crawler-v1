package entity.boss;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.monster.Monster;
import gui.GUIManager;
import gui.UITheme;
import item.key.LevelKey;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import util.Position;
import item.weapon.Fist;
import world.DroppedItem;
import world.InteractableTile;
import world.Staircase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlareWitch extends Boss {
    private final FlareWitchFSM stateMachine;


    public FlareWitch(Position position) {
        super("Flare Witch", 100, 2, new Fist(), position);
        stateMachine = new FlareWitchFSM(this);
        stateMachine.setupInitialState();
        setIlluminated(true);
        overrideColor(UITheme.ENTITY_FLARE_WITCH);
    }

    @Override
    protected void dropOnDeath(Map<InteractableTile, Double> map) {
        super.dropOnDeath(map);
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        super.hurt(damage, attacker);
        stateMachine.tryTeleport();
    }

    @Override
    public void makeMove() {
        stateMachine.update();
    }
}

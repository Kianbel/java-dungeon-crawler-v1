package entity.boss;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.monster.Monster;
import gui.UITheme;
import item.key.LevelKey;
import util.Position;
import item.weapon.Fist;
import world.DroppedItem;
import world.InteractableTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlareWitch extends Monster {
    private final FlareWitchFSM stateMachine;

    private final List<Entity> summonedEntities = new ArrayList<>();

    public FlareWitch(Position position) {
        super("Flare Witch", 100, 2, new Fist(), position);
        stateMachine = new FlareWitchFSM(this);
        stateMachine.setupInitialState();
        setIlluminated(true);
        overrideColor(UITheme.ENTITY_FLARE_WITCH);
    }

    @Override
    public void die() {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);

        super.die();
        for(Entity e : summonedEntities) {
            e.die();
        }
        summonedEntities.clear();

        currentRoom.addInteractableTile(new DroppedItem(position, new LevelKey()));
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

    public void addSummon(Entity summon) {
        summonedEntities.add(summon);
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        EntityRoomManager.getInstance().addEntityToRoom(summon, currentRoom);
    }
}

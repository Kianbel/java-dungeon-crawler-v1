package entity.monster;

import core.EntityRoomManager;
import core.GameManager;
import core.room.type.Room;
import entity.Entity;
import entity.Monster;
import entity.StandardMonsterFSM;
import entity.boss.FlareWitch;
import entity.boss.FlareWitchFSM;
import entity.projectile.Fireball;
import gui.GUIManager;
import javafx.scene.paint.Color;
import util.Position;
import util.Randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoblinWizardFSM extends StandardMonsterFSM<GoblinWizardFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
        BACKOFF,
        SUMMON,
    }

    private final Random random = new Random();

    public GoblinWizardFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

    @Override protected int getFollowRange() {return 12;}
    @Override protected double getFollowChance() {return 0.2;}
    @Override protected double getBackOffChance() {return 0.8;}

    @Override protected STATE getIdleState() {return STATE.IDLE;}
    @Override protected STATE getAngeredState() {return STATE.ANGERED;}
    @Override protected STATE getFollowState() {return STATE.FOLLOW;}
    @Override protected STATE getAttackState() {return STATE.ATTACK;}
    @Override protected STATE getBackOffState() {return STATE.BACKOFF;}
    @Override public void setupInitialState() {currentState = STATE.IDLE;}

    private int summonCooldown = 0;

    @Override
    public void update() {
        if(player == null) player = GameManager.getInstance().getPlayer();

        switch (currentState) {
            case IDLE -> handleIdle();
            case FOLLOW -> handleFollow();
            case ATTACK -> handleAttack();
            case ANGERED -> handleAngered();
            case BACKOFF -> handleBackOff();
            case SUMMON -> handleSummon();
        }

        if(summonCooldown > 0) summonCooldown--;
    }

    @Override
    public void switchState(STATE newState) {
        if(currentState == newState || newState == null) return;
        currentState = newState;
    }

    protected void handleAngered() {
        final double SUMMON_CHANCE = 0.3;
        if(Math.random() <= SUMMON_CHANCE && summonCooldown == 0) {
            switchState(STATE.SUMMON);
            GUIManager.getInstance().triggerTextPopup("chant", Color.DARKVIOLET, owner.position);
            return;
        }

        if (owner.getSquaredDistanceFromPlayer() <= getFollowRange()*getFollowRange()) {
            switchState(getFollowState());
            handleFollow();
        }
    }

    @Override
    protected void handleFollow() {
        Position moveVector = owner.pathfindToPlayerPosition();
        if(player.position.equals(owner.position.add(moveVector))) {
            switchState(getAttackState());
            handleAttack();
            return;
        }

        if(owner.isValidTargetPosition(owner.position.add(moveVector))) {
            if(Math.random() <= getFollowChance()) owner.walk(moveVector);
            else {
                switchState(STATE.ANGERED);
                handleAngered();
            }
        }
    }

    private void handleSummon() {
        final int SUMMON_COUNT = 3;
        final int SUMMON_DISTANCE = 5;

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(owner);
        List<Position> spawnablePositions = currentRoom.getSpawnablePositions();
        List<Position> summonPositions = new ArrayList<>((int) (Math.PI * SUMMON_DISTANCE * SUMMON_DISTANCE));
        for (int i = 0; i < spawnablePositions.size(); i++) {
            Position spawnablePos = spawnablePositions.get(i);
            if (spawnablePos.getDistanceTo(owner.position) <= SUMMON_DISTANCE) {
                summonPositions.add(spawnablePos);
            }
        }
        System.out.println(summonPositions.size());
        if (summonPositions.isEmpty()) return;

        for (int i = 0; i < SUMMON_COUNT; i++) {
            Position summonPosition = summonPositions.remove(random.nextInt(summonPositions.size()));
            Entity summon;
            double chance = Math.random();
            if(chance <= 0.1) summon = new GoblinTank(summonPosition);
            else if(chance <= 0.4) summon = new GoblinArcher(summonPosition);
            else summon = new GoblinSwordsman(summonPosition);

            summon.setIlluminated(true);
            summon.setIlluminationRange(1);

            EntityRoomManager.getInstance().addEntityToRoom(summon, currentRoom);
        }

        summonCooldown = 50;

        switchState(STATE.ANGERED);
        handleAngered();
    }
}

package entity.boss;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.monster.Monster;
import entity.projectile.Fireball;
import gui.GUIManager;
import util.Position;
import weapon.Fist;

import java.util.Random;



public class FlareWitch extends Monster {
    private final Random random = new Random();
    private Entity player;
    private Room currentRoom;

    private enum STATE {
        IDLE,
        ANGERED,
        FOLLOW,
        ADVANCE,
        CAST_SINGLE,
        CAST_BARRAGE,
        CAST_CIRCLE,
        SUMMON,
        ATTACK,
    }
    private STATE currentState;

    private int castCooldown = 0;
    private int summonCooldown = 0;
    private int barrageCount = 0;

    private final double WALK_CHANCE = 0.5;
    private final int FOLLOW_DISTANCE_THRESHOLD = 10;
    private final int MIN_CAST_COOLDOWN = 3;
    private final int MAX_CAST_COOLDOWN = 6;
    private final int MAX_BARRAGE_COUNT = 10;

    public FlareWitch(Position position) {
        super("Flare Witch", 100, 2, new Fist(), position);
        currentState = STATE.IDLE;
    }

    @Override
    public void makeMove() {
        if(player == null) player = EntityRoomManager.getInstance().getPlayer();
        if(currentRoom == null) currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);


        switch(currentState) {
            case IDLE -> {
                doIdle();
            }
            case ANGERED -> {
                doAngered();
            }
            case FOLLOW -> {
                doFollow();
            }
            case ADVANCE -> {
                doAdvance();
            }
            case CAST_SINGLE -> {
                doCastSingle();
            }
            case CAST_BARRAGE -> {
                doCastBarrage();
            }
            case CAST_CIRCLE -> {
//                doCastCircle();
            }
            case SUMMON -> {
//                doSummon();
            }
            case ATTACK -> {
                doAttack();
            }
        }

        if(castCooldown > 0) {
            castCooldown--;
            barrageCount = 0;
        }
        if(summonCooldown > 0) summonCooldown--;
    }

    private void doAttack() {
        GUIManager.getInstance().printDevLog(name + " attack");

        attack(player);
        currentState = STATE.ADVANCE;
    }

    private void doAdvance() {
        GUIManager.getInstance().printDevLog(name + " advance");

        if(Math.random() <= 0.3) {
            currentState = STATE.ANGERED;
            return;
        }
        if(getDistanceFromPlayer() == 1) {
            currentState = STATE.ATTACK;
            return;
        }

        Position unitPosToPlayer = pathfindToPlayerPosition();
        Position targetPosition = position.add(unitPosToPlayer);
        if(isValidTargetPosition(targetPosition)) {
            walk(unitPosToPlayer);
        }

    }

    private void doCastBarrage() {
        GUIManager.getInstance().printDevLog(name + " cast barrage");

        GUIManager.getInstance().printDevLog(barrageCount + "");

        if(barrageCount >= MAX_BARRAGE_COUNT) {
            castCooldown = MAX_CAST_COOLDOWN;
            currentState = STATE.ANGERED;
            return;
        }

        Position fireballSpawnPosition = position.add(pathfindToPlayerPosition());
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(pathfindToPlayerPosition(true), fireballSpawnPosition), currentRoom);
        barrageCount++;
    }

    private void doCastSingle() {
        GUIManager.getInstance().printDevLog(name + " cast single");

        Position fireballSpawnPosition = position.add(pathfindToPlayerPosition());
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(pathfindToPlayerPosition(true), fireballSpawnPosition), currentRoom);

        currentState = STATE.ANGERED;
        castCooldown = random.nextInt(MIN_CAST_COOLDOWN, MAX_CAST_COOLDOWN+1);
    }

    private void doAngered() {
        GUIManager.getInstance().printDevLog(name + " angered");

        int distanceFromPlayer = getDistanceFromPlayer();

        if(distanceFromPlayer >= FOLLOW_DISTANCE_THRESHOLD) {
            currentState = STATE.FOLLOW;
            return;
        }
        else if(Math.random() <= 0.3) {
            currentState = STATE.ADVANCE;
            return;
        }

        if(castCooldown <= 0) {
            if(health >= maxHealth/2) {
                currentState = STATE.CAST_SINGLE;
                return;
            }
            else {
                currentState = STATE.CAST_BARRAGE;
                return;
            }
        }
    }

    private void doFollow() {
        GUIManager.getInstance().printDevLog(name + " follow");

        if(getDistanceFromPlayer() < FOLLOW_DISTANCE_THRESHOLD) {
            currentState = STATE.ANGERED;
            return;
        }

        Position unitPosToPlayer = pathfindToPlayerPosition();
        Position targetPosition = position.add(unitPosToPlayer);
        if(isValidTargetPosition(targetPosition)) {
            walk(unitPosToPlayer);
        }
    }

    private void doIdle() {
        GUIManager.getInstance().printDevLog(name + " idle");
        if(getDistanceFromPlayer() < FOLLOW_DISTANCE_THRESHOLD) {
            currentState = STATE.ANGERED;
            return;
        }

        Position randomUnitPos = new Position(random.nextInt(-1,2), random.nextInt(-1,2));
        if(randomUnitPos.x == randomUnitPos.y) {
            if(Math.random() * 100 % 2 == 0) randomUnitPos.x = 0;
            else randomUnitPos.y = 0;
        }
        Position targetPosition = position.add(randomUnitPos);
        if(isValidTargetPosition(targetPosition)) {
            walk(randomUnitPos);
        }
    }
}

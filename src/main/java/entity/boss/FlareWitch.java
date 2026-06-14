package entity.boss;

import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.monster.GiantSpider;
import entity.monster.Monster;
import entity.monster.Zombie;
import entity.projectile.Fireball;
import gui.GUIManager;
import gui.Randomizer;
import javafx.scene.paint.Color;
import util.Position;
import weapon.Fist;

import java.util.ArrayList;
import java.util.List;
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
        TELEPORT,
    }
    private STATE currentState;

    private boolean isPhase2 = false;
    private int distanceToPlayer = 0;

    private int activeHintCooldown = 0;
    private int castCooldown = 0;
    private int summonCooldown = 0;
    private double teleportChance = 0.1;
    private int barrageCount = 0;


    private final int TELEPORT_DISTANCE_THRESHOLD = 10;
    private final int FOLLOW_DISTANCE_THRESHOLD = 10;
    private final int MIN_CAST_COOLDOWN = 3;
    private final int MAX_CAST_COOLDOWN = 6;
    private final int MAX_BARRAGE_COUNT = 10;
    private final int SUMMON_DISTANCE = 10;
    private final int MAX_SUMMON_COOLDOWN = 200;

    private List<Entity> summonedEntities = new ArrayList<>();

    public FlareWitch(Position position) {
        super("Flare Witch", 100, 2, new Fist(), position);
        changeState(STATE.IDLE);
        illuminationData.isIlluminated = true;
    }

    @Override
    public void die() {
        super.die();
        for(Entity e : summonedEntities) {
            e.die();
        }
    }

    @Override
    public void hurt(int damage, Entity attacker) {
        super.hurt(damage, attacker);
        if(Math.random() <= teleportChance && activeHintCooldown <= 0) {
            changeState(STATE.TELEPORT);
        }
    }

    @Override
    public void makeMove() {
        if(player == null) player = EntityRoomManager.getInstance().getPlayer();
        if(currentRoom == null) currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);

        if(health <= maxHealth/2 && !isPhase2) {
            isPhase2 = true;
            summonCooldown = 10;
            teleportChance = 0.2;
        }
        distanceToPlayer = (int) position.getDistanceTo(player.position);
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
                doCastCircle();
            }
            case SUMMON -> {
                doSummon();
            }
            case ATTACK -> {
                doAttack();
            }
            case TELEPORT -> {
                doTeleport();
            }
        }
        if(castCooldown > 0) {
            castCooldown--;
            if(castCooldown == 0) barrageCount = 0;
        }

        if(summonCooldown > 0) summonCooldown--;
        if(activeHintCooldown > 0) activeHintCooldown--;
    }

    private void doTeleport() {
        if(activeHintCooldown > 0) {
            GUIManager.getInstance().triggerTextPopup("chants", Color.DARKVIOLET, position);
            return;
        }

        List<Position> availablePositions = currentRoom.getSpawnablePositions();
        List<Position> teleportablePositions = new ArrayList<>((int) (Math.PI * TELEPORT_DISTANCE_THRESHOLD * TELEPORT_DISTANCE_THRESHOLD));
        for(Position p : availablePositions) {
            if(p.getDistanceTo(this.position) < TELEPORT_DISTANCE_THRESHOLD) {
                teleportablePositions.add(p);
            }
        }

        position = teleportablePositions.get(random.nextInt(teleportablePositions.size()));

        changeState(STATE.CAST_CIRCLE);
    }

    private void doSummon() {
        if(activeHintCooldown > 0) {
            GUIManager.getInstance().triggerTextPopup("chants", Color.DARKRED, position);
            return;
        }

        int summonCount = 5;
        if(isPhase2) summonCount = 3;

        List<Position> spawnablePositions = currentRoom.getSpawnablePositions();
        List<Position> summonPositions = new ArrayList<>((int) (Math.PI * SUMMON_DISTANCE * SUMMON_DISTANCE));
        for(int i = 0; i < spawnablePositions.size(); i++) {
            Position spawnablePos = spawnablePositions.get(i);
            if(spawnablePos.getDistanceTo(position) <= SUMMON_DISTANCE) {
                summonPositions.add(spawnablePos);
            }
        }

        if(summonPositions.isEmpty()) return;

        for(int i = 0; i < summonCount; i++) {
            Position summonPosition = summonPositions.remove(random.nextInt(summonPositions.size()));
            Entity summon = new Zombie(summonPosition);
            if(isPhase2) summon = new GiantSpider(summonPosition);

            summon.illuminationData.isIlluminated = true;
            summon.illuminationData.illuminationRange = 3;

            EntityRoomManager.getInstance().addEntityToRoom(summon, currentRoom);
            summonedEntities.add(summon);
        }

        changeState(STATE.ANGERED);
        summonCooldown = MAX_SUMMON_COOLDOWN;
    }

    private void doCastCircle() {
        Position fireballPos1 = position.add(pathfindToPlayerPosition());
        Position fireballPos2 = new Position(fireballPos1.x, fireballPos1.y-1);
        Position fireballPos3 = new Position(fireballPos1.x, fireballPos1.y+1);
        Position fireballPos4 = new Position(fireballPos1.x-1, fireballPos1.y);
        Position fireballPos5 = new Position(fireballPos1.x+1, fireballPos1.y);

        Position unitPosToPlayer = pathfindToPlayerPosition(true);

        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(unitPosToPlayer, fireballPos1), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(unitPosToPlayer, fireballPos2), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(unitPosToPlayer, fireballPos3), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(unitPosToPlayer, fireballPos4), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(unitPosToPlayer, fireballPos5), currentRoom);
        changeState(STATE.ANGERED);
        castCooldown = random.nextInt(MIN_CAST_COOLDOWN, MAX_CAST_COOLDOWN+1);
    }

    private void doAttack() {
        attack(player);
        changeState(STATE.ADVANCE);
    }

    private void doAdvance() {
        if(Math.random() <= 0.3) {
            changeState(STATE.ANGERED);
            return;
        }

        if(getDistanceFromPlayer() == 1) {
            changeState(STATE.ATTACK);
            return;
        }

        Position unitPosToPlayer = pathfindToPlayerPosition();
        Position targetPosition = position.add(unitPosToPlayer);
        if(isValidTargetPosition(targetPosition)) {
            walk(unitPosToPlayer);
        }
    }

    private void doCastBarrage() {
        Position fireballSpawnPosition = position.add(pathfindToPlayerPosition());
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(pathfindToPlayerPosition(true), fireballSpawnPosition), currentRoom);
        barrageCount++;

        if(barrageCount >= MAX_BARRAGE_COUNT) {
            castCooldown = MAX_CAST_COOLDOWN;
            changeState(STATE.ANGERED);
        }
    }

    private void doCastSingle() {
        Position fireballSpawnPosition = position.add(pathfindToPlayerPosition());
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(pathfindToPlayerPosition(true), fireballSpawnPosition), currentRoom);

        changeState(STATE.ANGERED);
        castCooldown = random.nextInt(MIN_CAST_COOLDOWN, MAX_CAST_COOLDOWN+1);
    }

    private void doAngered() {
        if(distanceToPlayer >= FOLLOW_DISTANCE_THRESHOLD) {
            changeState(STATE.FOLLOW);
            return;
        }
        else if(Math.random() <= 0.3) {
            changeState(STATE.ADVANCE);
            return;
        }

        if(castCooldown <= 0) {
            if(!isPhase2) {
                Randomizer randomizer = new Randomizer();
                switch(randomizer.pick(1, 2)) {
                    case 1 -> changeState(STATE.CAST_SINGLE);
                    case 2 -> {
                        if(summonCooldown <= 0) changeState(STATE.SUMMON);
                        else changeState(STATE.CAST_SINGLE);
                    }
                }
            }
            else {
                Randomizer randomizer = new Randomizer();
                switch(randomizer.pick(1, 2, 3)) {
                    case 1 -> changeState(STATE.CAST_BARRAGE);
                    case 2 -> changeState(STATE.CAST_CIRCLE);
                    case 3 -> {
                        if(summonCooldown <= 0) changeState(STATE.SUMMON);
                        else {
                            switch(randomizer.pick(1, 2)) {
                                case 1 -> changeState(STATE.CAST_BARRAGE);
                                case 2 -> changeState(STATE.CAST_CIRCLE);
                            }
                        }
                    }
                }
            }
        }
    }

    private void doFollow() {
        Position unitPosToPlayer = pathfindToPlayerPosition();
        Position targetPosition = position.add(unitPosToPlayer);
        if(isValidTargetPosition(targetPosition)) {
            walk(unitPosToPlayer);
        }

        if(distanceToPlayer < FOLLOW_DISTANCE_THRESHOLD) {
            changeState(STATE.ANGERED);
        }
    }

    private void doIdle() {
        Position randomUnitPos = new Position(random.nextInt(-1,2), random.nextInt(-1,2));
        if(randomUnitPos.x == randomUnitPos.y) {
            if(Math.random() * 100 % 2 == 0) randomUnitPos.x = 0;
            else randomUnitPos.y = 0;
        }
        Position targetPosition = position.add(randomUnitPos);
        if(isValidTargetPosition(targetPosition)) {
            walk(randomUnitPos);
        }

        if(distanceToPlayer < FOLLOW_DISTANCE_THRESHOLD) {
            changeState(STATE.ANGERED);
        }
    }

    private void changeState(STATE newState) {
        currentState = newState;

        switch(newState) {
            case IDLE -> {
            }
            case ANGERED -> {
                color = null;
            }
            case FOLLOW -> {
            }
            case ADVANCE -> {
            }
            case CAST_SINGLE -> {
            }
            case CAST_BARRAGE -> {
            }
            case CAST_CIRCLE -> {
            }
            case SUMMON -> {
                activeHintCooldown = 5;
                color = Color.DARKRED;
            }
            case ATTACK -> {
            }
            case TELEPORT -> {
                GUIManager.getInstance().printDevLog("teleport1");
                activeHintCooldown = 2;
                color = Color.DARKVIOLET;
            }
        }
    }
}

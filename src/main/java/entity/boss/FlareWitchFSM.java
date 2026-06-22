package entity.boss;

import core.GameManager;
import core.EntityRoomManager;
import core.room.type.Room;
import entity.Entity;
import entity.MonsterFSM;
import entity.monster.GiantSpider;
import entity.Monster;
import entity.monster.Zombie;
import entity.projectile.Fireball;
import gui.GUIManager;
import util.Randomizer;
import javafx.scene.paint.Color;
import util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlareWitchFSM extends MonsterFSM<FlareWitchFSM.STATE> {

    public enum STATE {
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

    private final Random random;
    private Room currentRoom;

    private boolean isPhase2 = false;
    private int distanceToPlayer = 0;

    private int activeHintCooldown = 0;
    private int castCooldown = 0;
    private int summonCooldown = 0;
    private int barrageCount = 0;

    private double teleportChance = 0.1;

    private final int TELEPORT_DISTANCE_THRESHOLD = 10;
    private final int FOLLOW_DISTANCE_THRESHOLD = 10;
    private final int MIN_CAST_COOLDOWN = 3;
    private final int MAX_CAST_COOLDOWN = 6;
    private final int MAX_BARRAGE_COUNT = 10;
    private final int SUMMON_DISTANCE = 10;
    private final int MAX_SUMMON_COOLDOWN = 200;

    public FlareWitchFSM(Monster owner) {
        super(owner);
        this.random = new Random();
    }

    @Override
    public void setupInitialState() {
        currentState = STATE.IDLE;
    }

    @Override
    public void update() {
        if (currentRoom == null) currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(owner);
        if (player == null) player = GameManager.getInstance().getPlayer();
        if (player == null) return;

        if (owner.health <= owner.maxHealth / 2 && !isPhase2) {
            isPhase2 = true;
            summonCooldown = 10;
            teleportChance = 0.2;
        }
        distanceToPlayer = (int) owner.position.getDistanceTo(player.position);

        if (castCooldown > 0) {
            castCooldown--;
            if (castCooldown == 0) barrageCount = 0;
        }
        if (summonCooldown > 0) summonCooldown--;

        switch (currentState) {
            case IDLE -> idle();
            case ANGERED -> angered();
            case FOLLOW -> follow();
            case ADVANCE -> advance();
            case CAST_SINGLE -> castSingle();
            case CAST_BARRAGE -> castBarrage();
            case CAST_CIRCLE -> castCircle();
            case SUMMON -> summon();
            case ATTACK -> attack();
            case TELEPORT -> teleport();
        }
    }

    @Override
    public void switchState(STATE newState) {
        currentState = newState;

        switch (newState) {
            case IDLE, FOLLOW, ADVANCE, CAST_SINGLE, CAST_BARRAGE, CAST_CIRCLE, ATTACK -> {}
            case ANGERED -> owner.resetColor();
            case SUMMON -> {
                GUIManager.getInstance().triggerTextPopup("chants", Color.DARKRED, owner.position);
                activeHintCooldown = 5;
                owner.overrideColor(Color.DARKRED);
            }
            case TELEPORT -> {
                GUIManager.getInstance().triggerTextPopup("chants", Color.DARKVIOLET, owner.position);
                activeHintCooldown = 2;
                owner.overrideColor(Color.DARKVIOLET);
            }
        }
    }

    public void tryTeleport() {
        if (currentState != STATE.TELEPORT && Math.random() <= teleportChance) {
            switchState(STATE.TELEPORT);
        }
    }

    private void teleport() {
        if (activeHintCooldown > 0) {
            activeHintCooldown--;
            return;
        }

        List<Position> availablePositions = currentRoom.getSpawnablePositions();
        List<Position> teleportablePositions = new ArrayList<>((int) (Math.PI * TELEPORT_DISTANCE_THRESHOLD * TELEPORT_DISTANCE_THRESHOLD));
        for (int i = 0; i < availablePositions.size(); i++) {
            Position p = availablePositions.get(i);
            if (p.getDistanceTo(owner.position) < TELEPORT_DISTANCE_THRESHOLD) {
                teleportablePositions.add(p);
            }
        }

        if (!teleportablePositions.isEmpty()) {
            owner.position = teleportablePositions.get(random.nextInt(teleportablePositions.size()));
        }

        switchState(STATE.CAST_CIRCLE);
    }

    private void summon() {
        if (activeHintCooldown > 0) {
            activeHintCooldown--;
            return;
        }

        int summonCount = isPhase2 ? 3 : 5;

        List<Position> spawnablePositions = currentRoom.getSpawnablePositions();
        List<Position> summonPositions = new ArrayList<>((int) (Math.PI * SUMMON_DISTANCE * SUMMON_DISTANCE));
        for (int i = 0; i < spawnablePositions.size(); i++) {
            Position spawnablePos = spawnablePositions.get(i);
            if (spawnablePos.getDistanceTo(owner.position) <= SUMMON_DISTANCE) {
                summonPositions.add(spawnablePos);
            }
        }

        if (summonPositions.isEmpty()) return;

        for (int i = 0; i < summonCount; i++) {
            if (summonPositions.isEmpty()) break;
            Position summonPosition = summonPositions.remove(random.nextInt(summonPositions.size()));
            Entity summon = isPhase2 ? new GiantSpider(summonPosition) : new Zombie(summonPosition);

            summon.setIlluminated(true);
            summon.setIlluminationRange(3);

            ((FlareWitch)owner).addSummon(summon);
        }

        switchState(STATE.ANGERED);
        summonCooldown = MAX_SUMMON_COOLDOWN;
    }

    /**
     * Helper calculation to find direct directional components without invoking A* paths.
     */
    private Position getDirectUnitVectorToPlayer() {
        if (player == null) return new Position(0, 0);
        int deltaX = player.position.x - owner.position.x;
        int deltaY = player.position.y - owner.position.y;
        return new Position(Integer.compare(deltaX, 0), Integer.compare(deltaY, 0));
    }

    private void castCircle() {
        Position shootingDirection = getDirectUnitVectorToPlayer();
        Position fireballPos1 = owner.position.add(shootingDirection);
        Position fireballPos2 = new Position(fireballPos1.x, fireballPos1.y - 1);
        Position fireballPos3 = new Position(fireballPos1.x, fireballPos1.y + 1);
        Position fireballPos4 = new Position(fireballPos1.x - 1, fireballPos1.y);
        Position fireballPos5 = new Position(fireballPos1.x + 1, fireballPos1.y);

        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballPos1), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballPos2), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballPos3), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballPos4), currentRoom);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballPos5), currentRoom);

        switchState(STATE.ANGERED);
        castCooldown = random.nextInt(MIN_CAST_COOLDOWN, MAX_CAST_COOLDOWN + 1);
    }

    private void attack() {
        owner.attack(player);
        switchState(STATE.ADVANCE);
    }

    private void advance() {
        if (Math.random() <= 0.3) {
            switchState(STATE.ANGERED);
            return;
        }

        if (owner.getDistanceFromPlayer() == 1) {
            switchState(STATE.ATTACK);
            return;
        }

        Position unitPosToPlayer = owner.pathfindToPlayerPosition();
        Position targetPosition = owner.position.add(unitPosToPlayer);
        if (owner.isValidTargetPosition(targetPosition)) {
            owner.walk(unitPosToPlayer);
        }
    }

    private void castBarrage() {
        Position shootingDirection = getDirectUnitVectorToPlayer();
        Position fireballSpawnPosition = owner.position.add(shootingDirection);

        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballSpawnPosition), currentRoom);
        barrageCount++;

        if (barrageCount >= MAX_BARRAGE_COUNT) {
            castCooldown = MAX_CAST_COOLDOWN;
            switchState(STATE.ANGERED);
        }
    }

    private void castSingle() {
        Position shootingDirection = getDirectUnitVectorToPlayer();
        Position fireballSpawnPosition = owner.position.add(shootingDirection);

        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballSpawnPosition), currentRoom);

        switchState(STATE.ANGERED);
        castCooldown = random.nextInt(MIN_CAST_COOLDOWN, MAX_CAST_COOLDOWN + 1);
    }

    private void angered() {
        if (distanceToPlayer >= FOLLOW_DISTANCE_THRESHOLD) {
            switchState(STATE.FOLLOW);
            return;
        } else if (Math.random() <= 0.3) {
            switchState(STATE.ADVANCE);
            return;
        }

        if (castCooldown <= 0) {
            if (!isPhase2) {
                switch (Randomizer.pick(1, 2)) {
                    case 1 -> switchState(STATE.CAST_SINGLE);
                    case 2 -> {
                        if (summonCooldown <= 0) switchState(STATE.SUMMON);
                        else switchState(STATE.CAST_SINGLE);
                    }
                }
            } else {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> switchState(STATE.CAST_BARRAGE);
                    case 2 -> switchState(STATE.CAST_CIRCLE);
                    case 3 -> {
                        if (summonCooldown <= 0) switchState(STATE.SUMMON);
                        else {
                            switch (Randomizer.pick(1, 2)) {
                                case 1 -> switchState(STATE.CAST_BARRAGE);
                                case 2 -> switchState(STATE.CAST_CIRCLE);
                            }
                        }
                    }
                }
            }
        }
    }

    private void follow() {
        Position unitPosToPlayer = owner.pathfindToPlayerPosition();
        Position targetPosition = owner.position.add(unitPosToPlayer);
        if (owner.isValidTargetPosition(targetPosition)) {
            owner.walk(unitPosToPlayer);
        }

        if (distanceToPlayer < FOLLOW_DISTANCE_THRESHOLD) {
            switchState(STATE.ANGERED);
        }
    }

    private void idle() {
        Position randomUnitPos = new Position(random.nextInt(-1, 2), random.nextInt(-1, 2));
        if (randomUnitPos.x == randomUnitPos.y) {
            if (random.nextBoolean()) randomUnitPos.x = 0;
            else randomUnitPos.y = 0;
        }
        Position targetPosition = owner.position.add(randomUnitPos);
        if (owner.isValidTargetPosition(targetPosition)) {
            owner.walk(randomUnitPos);
        }

        if (distanceToPlayer < FOLLOW_DISTANCE_THRESHOLD) {
            switchState(STATE.ANGERED);
        }
    }
}
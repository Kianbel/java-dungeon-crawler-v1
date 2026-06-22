package entity.monster;

import core.EntityRoomManager;
import core.GameManager;
import core.room.type.Room;
import entity.Monster;
import entity.StandardMonsterFSM;
import util.Position;
import world.InteractableTile;
import world.Web;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiantSpiderFSM extends StandardMonsterFSM<GiantSpiderFSM.STATE> {

    public enum STATE {
        IDLE, FOLLOW, ATTACK, WEBBING, BACKOFF
    }

    private final int WEBBING_DISTANCE_TO_PLAYER_THRESHOLD = 5;
    private final int AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD = 2;
    private final double WEBBING_CHANCE = 0.2;
    private final int WEB_RANGE = 1;
    private final int WEB_SHOOT_MOVE_COOLDOWN = 5;
    private int webShootCooldown = WEB_SHOOT_MOVE_COOLDOWN;
    private final double BACK_OFF_CHANCE = 0.9;
    private final double WALK_CHANCE = 0.6;

    public GiantSpiderFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

    @Override public void setupInitialState() { currentState = STATE.IDLE; }

    @Override protected int getFollowRange() { return WEBBING_DISTANCE_TO_PLAYER_THRESHOLD; }

    @Override protected double getFollowChance() {return 0;}
    @Override protected double getBackOffChance() {return 0;}

    @Override protected STATE getIdleState() { return STATE.IDLE; }
    @Override protected STATE getAngeredState() { return STATE.FOLLOW; } // Spiders skip ANGERED[cite: 5]
    @Override protected STATE getFollowState() { return STATE.FOLLOW; }
    @Override protected STATE getAttackState() { return STATE.ATTACK; }
    @Override protected STATE getBackOffState() { return STATE.BACKOFF; }

    @Override
    public void update() {
        if(player == null) player = GameManager.getInstance().getPlayer();
        if(webShootCooldown > 0) webShootCooldown--;

        switch (currentState) {
            case IDLE -> handleIdle(); // Inherited polymorphically!
            case FOLLOW -> handleFollow(); // Specialized spider pursuit logic
            case ATTACK -> handleAttack();
            case WEBBING -> handleWebbing();
            case BACKOFF -> handleBackOff(); // Inherited polymorphically!
        }
    }

    protected void handleFollow() {
        int distanceToPlayer = owner.getDistanceFromPlayer();

        // 1. Backoff trigger check[cite: 5]
        if (distanceToPlayer <= AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD && owner.health <= owner.maxHealth / 2) {
            if (Math.random() <= BACK_OFF_CHANCE) {
                switchState(STATE.BACKOFF);
                handleBackOff();
                return;
            }
        }
        // 2. Specialized web zoning check[cite: 5]
        else if (distanceToPlayer <= WEBBING_DISTANCE_TO_PLAYER_THRESHOLD && distanceToPlayer > AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD) {
            if (Math.random() <= WEBBING_CHANCE && webShootCooldown <= 0) {
                switchState(STATE.WEBBING);
                handleWebbing();
                return;
            }
        }

        // 3. Movement pursuit execution[cite: 5]
        Position moveVector = owner.pathfindToPlayerPosition();
        if(moveVector.x == 0 && moveVector.y == 0) return;

        Position targetPosition = owner.position.add(moveVector);

        if (player.position.equals(targetPosition)) {
            switchState(STATE.ATTACK);
            handleAttack();
            return;
        }

        if (owner.isValidTargetPosition(targetPosition) && Math.random() <= WALK_CHANCE) {
            owner.walk(moveVector);
        }
    }

    protected void handleAttack() {
        owner.attack(player);
        if (owner.getDistanceFromPlayer() <= AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD && owner.health <= owner.maxHealth / 2 && Math.random() <= BACK_OFF_CHANCE) {
            switchState(STATE.BACKOFF);
        } else {
            switchState(STATE.FOLLOW);
        }
    }

    private void handleWebbing() {
        if(webShootCooldown > 0) { switchState(STATE.FOLLOW); return; }

        List<Position> validWebPositions = new ArrayList<>();
        for(int y = player.position.y - WEB_RANGE; y < player.position.y + WEB_RANGE; y++) {
            for(int x = player.position.x - WEB_RANGE; x < player.position.x + WEB_RANGE; x++) {
                Position testPos = new Position(x,y);
                if(owner.isValidTargetPosition(testPos)) validWebPositions.add(testPos);
            }
        }

        if (!validWebPositions.isEmpty()) {
            Random random = new Random();
            Position webPosition = validWebPositions.get(random.nextInt(validWebPositions.size()));
            InteractableTile web = new Web(webPosition);
            Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(owner);
            webShootCooldown = WEB_SHOOT_MOVE_COOLDOWN;
            currentRoom.addInteractableTile(web);
        }
        switchState(STATE.FOLLOW);
    }

    @Override
    public void switchState(STATE newState) {
        if(currentState == newState || newState == null) return;
        currentState = newState;
    }
}
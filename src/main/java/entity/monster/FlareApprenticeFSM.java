package entity.monster;

import core.EntityRoomManager;
import core.GameManager;
import core.room.type.Room;
import entity.Monster;
import entity.StandardMonsterFSM;
import entity.projectile.Fireball;
import util.Position;

public class FlareApprenticeFSM extends StandardMonsterFSM<FlareApprenticeFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
        BACKOFF,
        CAST,
    }


    public FlareApprenticeFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

    @Override protected int getFollowRange() {return 12;}
    @Override protected double getBackOffChance() {return 0.8;}

    @Override protected STATE getIdleState() {return STATE.IDLE;}
    @Override protected STATE getAngeredState() {return STATE.ANGERED;}
    @Override protected STATE getFollowState() {return STATE.FOLLOW;}
    @Override protected STATE getAttackState() {return STATE.ATTACK;}
    @Override protected STATE getBackOffState() {return STATE.BACKOFF;}
    @Override public void setupInitialState() {currentState = STATE.IDLE;}

    @Override
    public void update() {
        if(player == null) player = GameManager.getInstance().getPlayer();

        switch (currentState) {
            case IDLE -> handleIdle();
            case FOLLOW -> handleFollow();
            case ATTACK -> handleAttack();
            case ANGERED -> handleAngered();
            case BACKOFF -> handleBackOff();
            case CAST -> handleCast();
        }
    }

    @Override
    public void switchState(STATE newState) {
        if(currentState == newState || newState == null) return;
        currentState = newState;
    }

    @Override
    protected void handleFollow() {
        Position moveVector = owner.pathfindToPlayerPosition();
        if(player.position.equals(owner.position.add(moveVector))) {
            switchState(getAttackState());
            handleAttack();
            return;
        }

        final double CAST_CHANCE = 0.4;
        if((Math.random() <= CAST_CHANCE) && owner.getSquaredDistanceFromPlayer() <= 5*5) {
            switchState(STATE.CAST);
            handleCast();
        }

        if(owner.isValidTargetPosition(owner.position.add(moveVector))) {
            if(Math.random() <= getFollowChance()) owner.walk(moveVector);
            else {
                switchState(STATE.CAST);
            }
        }
    }

    private void handleCast() {
        Position shootingDirection = getDirectUnitVectorToPlayer();
        Position fireballSpawnPosition = owner.position.add(shootingDirection);

        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(owner);
        EntityRoomManager.getInstance().addEntityToRoom(new Fireball(shootingDirection, fireballSpawnPosition), currentRoom);

        switchState(STATE.FOLLOW);
    }

    private Position getDirectUnitVectorToPlayer() {
        if (player == null) return new Position(0, 0);
        int deltaX = player.position.x - owner.position.x;
        int deltaY = player.position.y - owner.position.y;
        return new Position(Integer.compare(deltaX, 0), Integer.compare(deltaY, 0));
    }
}

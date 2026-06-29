package entity.monster;

import core.GameManager;
import entity.Monster;
import entity.StandardMonsterFSM;
import util.Position;
import util.Randomizer;

public class RoachFSM extends StandardMonsterFSM<RoachFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
        BACKOFF,
    }


    public RoachFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

    @Override protected int getFollowRange() {return 5;}
    @Override protected double getFollowChance() {return 0.9;}
    @Override protected double getBackOffChance() {return 0.9;}

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
        }
    }

    @Override
    public void switchState(STATE newState) {
        if(currentState == newState || newState == null) return;
        currentState = newState;
    }

    public void doAngered() {
        switchState(STATE.ANGERED);
        handleAngered();
    }

    @Override
    protected void handleIdle() {
        Position unitPos = new Position(0, 0);
        switch (Randomizer.pick(1, 2, 3, 4)) {
            case 1 -> unitPos.x = 1;
            case 2 -> unitPos.x = -1;
            case 3 -> unitPos.y = 1;
            case 4 -> unitPos.y = -1;
        }

        Position targetPos = owner.position.add(unitPos);
        if (owner.isValidTargetPosition(targetPos)) {
            owner.walk(unitPos);
        }
    }
}

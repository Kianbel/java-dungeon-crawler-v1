package entity.monster;

import core.GameManager;
import entity.Monster;
import entity.StandardMonsterFSM;

public class GiantCentipedeFSM extends StandardMonsterFSM<GiantCentipedeFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
        BACKOFF,
    }


    public GiantCentipedeFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

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
}

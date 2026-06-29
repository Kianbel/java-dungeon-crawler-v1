package entity.monster;

import core.GameManager;
import entity.Monster;
import entity.Player;
import entity.StandardMonsterFSM;
import util.Position;
import util.Randomizer;

public class RatFSM extends StandardMonsterFSM<RatFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
    }

    public RatFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

    @Override protected int getFollowRange() {return 10;}
    @Override protected STATE getIdleState() {return STATE.IDLE;}
    @Override protected STATE getAngeredState() {return STATE.ANGERED;}
    @Override protected STATE getFollowState() {return STATE.FOLLOW;}
    @Override protected STATE getAttackState() {return STATE.ATTACK;}
    @Override protected STATE getBackOffState() {return null;}

    @Override
    public void setupInitialState() {
        currentState = STATE.IDLE;
    }

    @Override
    public void update() {
        switch (currentState) {
            case IDLE -> handleIdle();
            case FOLLOW -> handleFollow();
            case ATTACK -> handleAttack();
            case ANGERED -> handleAngered();
        }
    }

    public void doAngered() {
        if(currentState == STATE.IDLE) switchState(STATE.ANGERED);
    }

    protected void handleIdle() {
        Position unitPos = new Position(0,0);
        switch(Randomizer.pick(1,2,3,4)) {
            case 1: unitPos.x = 1; break;
            case 2: unitPos.x = -1; break;
            case 3: unitPos.y = 1; break;
            case 4: unitPos.y = -1; break;
        }

        Position targetPos = owner.position.add(unitPos);
        if(owner.isValidTargetPosition(targetPos)) owner.walk(unitPos);
    }


    protected void handleFollow() {
        if(owner.getSquaredDistanceFromPlayer() == 1) {
            switchState(STATE.ATTACK);
            return;
        }

        Position moveVector = owner.pathfindToPlayerPosition();
        if(owner.isValidTargetPosition(owner.position.add(moveVector))) {
            owner.walk(moveVector);
        }
    }

    protected void handleAttack() {
        Player player = GameManager.getInstance().getPlayer();
        owner.attack(player);
        switchState(STATE.FOLLOW);
    }

    @Override
    public void switchState(STATE newState) {
        if(currentState == newState || newState == null) return;
        currentState = newState;
    }
}

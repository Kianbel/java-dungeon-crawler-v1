package entity.fsm;

import core.GameManager;
import entity.Player;
import entity.monster.Monster;
import entity.monster.Rat;
import util.Position;
import util.Randomizer;

public class RatFSM extends MonsterFSM<RatFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
    }

    private final Rat owner;
    final int FOLLOW_RANGE = 10;

    public RatFSM(Monster owner) {
        super(owner);
        this.owner = (Rat)owner;
        setupInitialState();
    }

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

    private void handleIdle() {
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

    private void handleAngered() {
        if(owner.getDistanceFromPlayer() <= FOLLOW_RANGE) {
            switchState(STATE.FOLLOW);
        }
        else switchState(STATE.ANGERED);
    }

    private void handleFollow() {
        if(owner.getDistanceFromPlayer() == 1) {
            switchState(STATE.ATTACK);
            return;
        }

        Position moveVector = owner.pathfindToPlayerPosition();
        if(owner.isValidTargetPosition(owner.position.add(moveVector))) {
            owner.walk(moveVector);
        }
    }

    private void handleAttack() {
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

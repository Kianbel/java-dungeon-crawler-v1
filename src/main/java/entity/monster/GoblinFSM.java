package entity.monster;

import core.GameManager;
import entity.Monster;
import entity.Player;
import entity.StandardMonsterFSM;
import util.Position;

public class GoblinFSM extends StandardMonsterFSM<GoblinFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
        BACKOFF,
    }

    int shootCooldown;

    public GoblinFSM(Monster owner) {
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

    protected void handleAttack() {
        if(shootCooldown > 0) {
            shootCooldown--;
            return;
        }

        if(owner.health <= owner.maxHealth * 0.4 && Math.random() <= getBackOffChance()) {
            switchState(STATE.BACKOFF);
            return;
        }

        Player player = GameManager.getInstance().getPlayer();
        owner.attack(player);
        shootCooldown = 2;

        Position moveVector = owner.pathfindToPlayerPosition();
        if(!player.position.equals(owner.position.add(moveVector))) {
            switchState(STATE.FOLLOW);
        }
    }


    @Override
    public void switchState(STATE newState) {
        if(currentState == newState || newState == null) return;
        currentState = newState;
    }
}

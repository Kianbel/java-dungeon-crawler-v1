package entity.monster;

import core.GameManager;
import entity.Monster;
import entity.Player;
import entity.StandardMonsterFSM;
import util.Position;
import util.Randomizer;

public class GoblinArcherFSM extends StandardMonsterFSM<GoblinArcherFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
        BACKOFF,
    }

    int shootCooldown;

    public GoblinArcherFSM(Monster owner) {
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

        System.out.println(shootCooldown);
    }

    protected void handleIdle() {
        if (player == null) player = GameManager.getInstance().getPlayer();

        if (owner.hasLineOfSight(owner.position, player.position) && owner.getSquaredDistanceFromPlayer() <= getFollowRange()*getFollowRange()) {
            switchState(getAngeredState());
            playAlertEffects();
            return;
        }

//        Position unitPos = new Position(0, 0);
//        switch (Randomizer.pick(1, 2, 3, 4)) {
//            case 1 -> unitPos.x = 1;
//            case 2 -> unitPos.x = -1;
//            case 3 -> unitPos.y = 1;
//            case 4 -> unitPos.y = -1;
//        }
//
//        Position targetPos = owner.position.add(unitPos);
//        if (owner.isValidTargetPosition(targetPos)) {
//            owner.walk(unitPos);
//        }
    }

    protected void handleAngered() {
        if (owner.getSquaredDistanceFromPlayer() <= getFollowRange()*getFollowRange()) {
            switchState(getFollowState());
        }
    }

    protected void handleBackOff() {
        if (player == null) player = GameManager.getInstance().getPlayer();

        int dx = player.position.x - owner.position.x;
        int dy = player.position.y - owner.position.y;
        Position unitPos = new Position(-Integer.compare(dx, 0), -Integer.compare(dy, 0));
        if(unitPos.x == unitPos.y) {
            switch (Randomizer.pick(1,2)) {
                case 1 -> unitPos.x = 0;
                case 2 -> unitPos.y = 0;
            }
        }

        Position targetPosition = owner.position.add(unitPos);
        if (owner.isValidTargetPosition(targetPosition)) {
            owner.walk(unitPos);
        } else {
            switchState(getFollowState());
        }
    }

    protected void handleFollow() {
        final double SHOOT_CHANCE = 0.4;
        if(owner.hasLineOfSight(owner.position, player.position) && Math.random() <= SHOOT_CHANCE) {
            switchState(STATE.ATTACK);
            handleAttack();
            return;
        }

        Position moveVector = owner.pathfindToPlayerPosition();
        if(player.position.equals(owner.position.add(moveVector))) {
            switchState(getAttackState());
            handleAttack();
            return;
        }

        if(owner.isValidTargetPosition(owner.position.add(moveVector))) {
            if(Math.random() <= getFollowChance()) owner.walk(moveVector);
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

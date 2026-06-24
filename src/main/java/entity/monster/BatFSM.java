package entity.monster;

import core.GameManager;
import entity.Monster;
import entity.StandardMonsterFSM;
import gui.AudioManager;
import gui.GUIManager;
import javafx.scene.paint.Color;

public class BatFSM extends StandardMonsterFSM<BatFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
        BACKOFF,
    }

    public BatFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

    @Override protected int getFollowRange() {return 6;}
    @Override protected double getFollowChance() {return 0.9;}

    @Override protected STATE getIdleState() {return STATE.IDLE;}
    @Override protected STATE getAngeredState() {return STATE.ANGERED;}
    @Override protected STATE getFollowState() {return STATE.FOLLOW;}
    @Override protected STATE getAttackState() {return STATE.ATTACK;}
    @Override protected STATE getBackOffState() {return STATE.BACKOFF;}

    @Override
    public void setupInitialState() {
        currentState = STATE.IDLE;
        owner.overrideColor(Color.BLACK.brighter());
    }

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
    protected void handleIdle() {
        if(owner.hasLineOfSight(owner.position, player.position) && owner.getDistanceFromPlayer() <= getFollowRange()) {
            switchState(STATE.ANGERED);
            GUIManager.getInstance().triggerTextPopup(owner.name + " found you", Color.WHITE, owner.position);
            GUIManager.getInstance().triggerTextPopup("!", Color.WHITE, owner.position);
            AudioManager.getInstance().playSFX("enemy_see_player");
            owner.resetColor();
        }
    }

    @Override
    public void switchState(STATE newState) {
        if(currentState == newState || newState == null) return;
        currentState = newState;
    }
}

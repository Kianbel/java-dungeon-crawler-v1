package entity.fsm;

import core.GameManager;
import entity.Entity;
import entity.Player;
import entity.monster.Kobold;
import entity.monster.Monster;
import gui.GUIManager;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
import util.Position;
import util.Randomizer;

public class KoboldFSM extends MonsterFSM<KoboldFSM.STATE> {
    public enum STATE {
        IDLE,
        FOLLOW,
        ATTACK,
        ANGERED,
    }

    final int FOLLOW_RANGE = 6;

    public KoboldFSM(Monster owner) {
        super(owner);
        setupInitialState();
    }

    @Override
    public void setupInitialState() {
        currentState = STATE.IDLE;
    }

    @Override
    public void update() {
        if(player == null) player = GameManager.getInstance().getPlayer();

        switch (currentState) {
            case IDLE -> handleIdle();
            case FOLLOW -> handleFollow();
            case ATTACK -> handleAttack();
            case ANGERED -> handleAngered();
        }
    }

    private void handleIdle() {
        if(owner.hasLineOfSight(owner.position, player.position)) {
            switchState(STATE.ANGERED);
            GUIManager.getInstance().printLog(owner.name + " found you.", UITheme.LOG_MONSTER_ACTION);
            GUIManager.getInstance().triggerTextPopup("!", Color.WHITE, owner.position);
            return;
        }

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
        Position moveVector = owner.pathfindToPlayerPosition();

        if(player.position.equals(owner.position.add(moveVector))) {
            switchState(STATE.ATTACK);
            return;
        }

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

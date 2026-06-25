package entity;

import core.GameManager;
import gui.AudioManager;
import gui.GUIManager;
import gui.dataclass.UITheme;
import javafx.scene.paint.Color;
import util.Position;
import util.Randomizer;

public abstract class StandardMonsterFSM<T extends Enum<T>> extends MonsterFSM<T> {

    public StandardMonsterFSM(Monster owner) {
        super(owner);
    }

    /**
     * @return default: 8
     */
    protected int getFollowRange() {return 8;}

    /**
     * @return default: 0.8
     */
    protected double getFollowChance() {return 0.8;}

    /**
     * @return default: 0.4
     */
    protected double getBackOffChance() {return 0.4;}

    protected abstract T getIdleState();
    protected abstract T getAngeredState();
    protected abstract T getFollowState();
    protected abstract T getAttackState();
    protected abstract T getBackOffState();

    /**
     * Optional hook if a monster has a custom sound/alert when finding the player.
     * Overriding this allows Bat vs Goblin vs Spider unique text/logs.
     */
    protected void playAlertEffects() {
        GUIManager.getInstance().printLog(owner.name + " found you.", UITheme.LOG_MONSTER_ACTION);
        GUIManager.getInstance().triggerTextPopup("!", Color.WHITE, owner.position);
        AudioManager.getInstance().playSFX("enemy_see_player");
    }

    protected void handleIdle() {
        if (player == null) player = GameManager.getInstance().getPlayer();

        if (owner.hasLineOfSight(owner.position, player.position) && owner.getDistanceFromPlayer() <= getFollowRange()) {
            switchState(getAngeredState());
            playAlertEffects();
            return;
        }

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

    protected void handleAngered() {
        if (owner.getDistanceFromPlayer() <= getFollowRange()) {
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
        if(owner.health <= owner.maxHealth * 0.4 && Math.random() <= getBackOffChance()) {
            switchState(getBackOffState());
            return;
        }

        Player player = GameManager.getInstance().getPlayer();
        owner.attack(player);

        Position moveVector = owner.pathfindToPlayerPosition();
        if(!player.position.equals(owner.position.add(moveVector))) {
            switchState(getFollowState());
        }
    }
}
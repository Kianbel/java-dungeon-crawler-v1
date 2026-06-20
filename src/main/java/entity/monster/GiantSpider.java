package entity.monster;

import core.EntityRoomManager;
import util.WeightedObject;
import entity.Entity;
import entity.RangeAttack;
import gui.GUIManager;
import javafx.scene.paint.Color;
import item.weapon.GiantSpiderFang;
import util.Position;
import core.room.type.Room;
import world.*;

import java.util.*;

public class GiantSpider extends Monster implements RangeAttack {
    private final int WEBBING_DISTANCE_TO_PLAYER_THRESHOLD = 5;
    private final int AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD = 2;

    private final double WEBBING_CHANCE = 0.2;
    private final int WEB_RANGE = 1;
    private int WEB_SHOOT_MOVE_COOLDOWN = 5;
    private int webShootCooldown = WEB_SHOOT_MOVE_COOLDOWN;

    private final double BACK_OFF_CHANCE = 0.9;
    private final int BACK_OFF_HEALTH_THRESHOLD = health/2;

    private final double WALK_CHANCE = 0.6;


    public GiantSpider(Position position) {
        super("Giant Spider", 20, 0, new GiantSpiderFang(), position);
    }

    @Override
    protected void makeSoundTextPopup() {
        GUIManager.getInstance().triggerTextPopup("hiss", Color.DARKRED, position);
    }

    @Override
    public void makeMove() {
        super.makeMove();
        if(webShootCooldown > 0) webShootCooldown--;

        Entity player = EntityRoomManager.getInstance().getPlayer();
        int dx = player.position.x - position.x;
        int dy = player.position.y - position.y;
        int distanceToPlayer = (int) Math.sqrt(dx*dx + dy*dy);

        if(distanceToPlayer <= 2) {
            if(health <= BACK_OFF_HEALTH_THRESHOLD) {
                if(Math.random() <= BACK_OFF_CHANCE) if(backOff()) return;
            }
        }
        if(distanceToPlayer <= WEBBING_DISTANCE_TO_PLAYER_THRESHOLD) {
            if(distanceToPlayer <= AGGRESSIVE_DISTANCE_TO_PLAYER_THRESHOLD) {
                handleWalk();
                return;
            }

            if(Math.random() <= WEBBING_CHANCE) {
                if(shoot(player.position, WEB_RANGE)) return;
            }
        }
        handleWalk();
    }

    @Override
    public void die() {
        List<WeightedObject> lootTable = new ArrayList<>(List.of(
                new WeightedObject(new GiantSpiderFang(), position, 1),
                new WeightedObject(new Heart(position, 5), 3),
                new WeightedObject(new Coin(position, 5), 3),
                new WeightedObject(null, 5)
        ));

        dropOnDeath(lootTable);
        super.die();
    }

    private void handleWalk() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        Position playerPosition = player.position;
        Position unitPos = pathfindToPlayerPosition();
        if(unitPos.x == 0 && unitPos.y == 0) return;

        Position targetPosition = new Position(position.x+unitPos.x, position.y+unitPos.y);

        if(playerPosition.x == targetPosition.x && playerPosition.y == targetPosition.y) {
            attack(player);
        }
        else if(isValidTargetPosition(targetPosition)){
            if(Math.random() <= WALK_CHANCE) walk(unitPos);
        }
    }

    @Override
    public void walk(Position unitPos) {
        super.walk(unitPos);
    }

    public boolean backOff() {
        Position playerPosition = EntityRoomManager.getInstance().getPlayer().position;

        int dx = playerPosition.x - position.x;
        int dy = playerPosition.y - position.y;
        int unitX = Integer.compare(dx, 0);
        int unitY = Integer.compare(dy, 0);

        Position targetPosition = new Position(position.x-unitX, position.y-unitY);
        if(isValidTargetPosition(targetPosition)) {
            walk(new Position(-unitX, -unitY));
            return true;
        }

        return false;
    }

    @Override
    public boolean shoot(Position targetPosition, int range) {
        if(webShootCooldown > 0) return false;

        List<Position> validWebPositions = new ArrayList<>();
        for(int y = targetPosition.y-range; y < targetPosition.y+range; y++) {
            for(int x = targetPosition.x-range; x < targetPosition.x+range; x++) {
                Position testPos = new Position(x,y);
                if(isValidTargetPosition(testPos)) validWebPositions.add(testPos);
            }
        }

        Random random = new Random();
        Position webPosition = validWebPositions.get(random.nextInt(validWebPositions.size()));
        InteractableTile web = new Web(webPosition);
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        webShootCooldown = WEB_SHOOT_MOVE_COOLDOWN;
        return currentRoom.addInteractableTile(web);
    }
}

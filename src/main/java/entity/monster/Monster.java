package entity.monster;

import core.EntityRoomManager;
import entity.Entity;
import entity.MoveAfterPlayer;
import entity.Player;
import javafx.geometry.Pos;
import util.DIRECTION;
import util.Randomizer;
import weapon.Weapon;
import util.Position;
import util.TILE;
import core.room.type.Room;
import world.InteractableTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Monster extends Entity implements MoveAfterPlayer {
    public Monster(String name, int health, int armor, Weapon weapon, Position position) {
        super(name, health, armor, weapon, position);
    }

    @Override
    public void makeMove() {
        if(Math.random() <= 0.1) makeSoundTextPopup();
    }

    public List<Position> getAStarPathPositions(Position fromPos, Position toPos) {
        final Node startNode = new Node(fromPos.x, fromPos.y);
        final Node goalNode = new Node(toPos.x, toPos.y);

        final Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        final TILE[][] roomLayout = currentRoom.getLayout();
        final int ROOM_HEIGHT = roomLayout.length;
        final int ROOM_LENGTH = roomLayout[0].length;

        List<Node> toSearch = new ArrayList<>();
        toSearch.add(startNode);
        List<Node> processed = new ArrayList<>();
        List<Node> neighbors = new ArrayList<>();

        while(!toSearch.isEmpty()) {
            Node currentNode = toSearch.getFirst();
            for(Node n : toSearch) {
                if(n.getF() < currentNode.getF() || n.getF() == currentNode.getF() && n.getH() < currentNode.getH()) {
                    currentNode = n;
                }
            }

            processed.add(currentNode);
            toSearch.remove(currentNode);

            if(currentNode.equals(goalNode)) {
                Node currentPathTile = currentNode;
                List<Position> pathList = new ArrayList<>();

                while(currentPathTile != startNode) {
                    pathList.addFirst(currentPathTile.getPosition());
                    currentPathTile = currentPathTile.getConnection();
                }

                return pathList;
            }

            int x = currentNode.x;
            int y = currentNode.y;
            if(y-1 >= 0 && roomLayout[y-1][x].isWalkable()) neighbors.add(new Node(x, y-1));
            if(y+1 < ROOM_HEIGHT && roomLayout[y+1][x].isWalkable()) neighbors.add(new Node(x,y+1));
            if(x-1 >= 0 && roomLayout[y][x-1].isWalkable()) neighbors.add(new Node(x-1, y));
            if(x+1 < ROOM_LENGTH && roomLayout[y][x+1].isWalkable()) neighbors.add(new Node(x+1,y));

            List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
            for(Entity e : entities) {
                if(e instanceof Player || e == this) continue;
                for(int i = 0; i < neighbors.size(); i++) {
                    Node neighbor = neighbors.get(i);
                    if(e.position.x == neighbor.x && e.position.y == neighbor.y) {
                        neighbors.remove(neighbor);
                        break;
                    }
                }
            }
            List<InteractableTile> interactableTiles = EntityRoomManager.getInstance().getInteractableTilesFromRoom(currentRoom);
            for(InteractableTile interactableTile : interactableTiles) {
                for(int i = 0; i < neighbors.size(); i++) {
                    Node neighbor = neighbors.get(i);
                    if(interactableTile.roomLayoutPosition.x == neighbor.x && interactableTile.roomLayoutPosition.y == neighbor.y) {
                        neighbors.remove(neighbor);
                        break;
                    }
                }
            }

            for(Node neighbor : neighbors) {
                if(processed.contains(neighbor)) continue;

                boolean inSearch = toSearch.contains(neighbor);
                double costToNeighbor = currentNode.getG() + currentNode.getPosition().getDistanceTo(neighbor.getPosition());

                if(!inSearch || costToNeighbor < neighbor.getG()) {
                    neighbor.setG(costToNeighbor);
                    neighbor.setConnection(currentNode);

                    if(!inSearch) {
                        neighbor.setH(neighbor.getPosition().getDistanceTo(toPos));
                        toSearch.add(neighbor);
                    }
                }
            }
            neighbors.clear();
        }
        return null;
    }


    /** Override to add sound when walking
     */
    protected void makeSoundTextPopup() {}

    /** Returns the unit vector position towards player position.
     * diagonal direction is disabled by default
     * @return Position(unitX, unitY)
     */
    public Position pathfindToPlayerPosition() {
        return pathfindToPlayerPosition(false);
    }
    /** Returns the unit vector position towards player position.
     * @return Position(unitX, unitY)
     */
    public Position pathfindToPlayerPosition(boolean allowDiagonal) {
        final Entity player = EntityRoomManager.getInstance().getPlayer();

        if(position.equals(player.position)) {
            return new Position(0,0);
        }

        if(allowDiagonal) {
            final Position playerPosition = player.position;
            final int dx = playerPosition.x - position.x;
            final int dy = playerPosition.y - position.y;
            final double distance = Math.sqrt(dx*dx + dy*dy);
            final double unitX = dx / distance;
            final double unitY = dy / distance;
            final double angleToPlayer = Math.abs(Math.atan2(unitY, unitX)) * (180.0 / Math.PI);
            final double diagonalAngle = angleToPlayer % 90;

            final int x = (int) Math.signum(dx);
            final int y = (int) Math.signum(dy);

            if(diagonalAngle >= 25 && diagonalAngle <= 65) {
                return new Position(x, y);
            }
            if(Math.abs(dx) > Math.abs(dy)) {
                return new Position(x, 0);
            }
            else if(Math.abs(dy) > Math.abs(dx)) {
                return new Position(0, y);
            }
        }
        else {
            final List<Position> pathPositions = getAStarPathPositions(this.position, player.position);
            if(pathPositions == null || pathPositions.isEmpty()) return new Position(0,0);

            final Position firstPathPosition = pathPositions.getFirst();
            return new Position(firstPathPosition.x-position.x, firstPathPosition.y-position.y);
        }
        return new Position(0,0);
    }

    public boolean isValidTargetPosition(Position targetPosition) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] roomLayout = currentRoom.getLayout();

        if(targetPosition.x < 0 || targetPosition.x >= roomLayout[0].length) return false;
        if(targetPosition.y < 0 || targetPosition.y >= roomLayout.length) return false;

        TILE tile = roomLayout[targetPosition.y][targetPosition.x];
        if(!tile.isWalkable()) return false;

        List<InteractableTile> interactableTiles = currentRoom.getInteractableTiles();
        for(InteractableTile interactableTile : interactableTiles) {
            if(interactableTile.roomLayoutPosition.equals(targetPosition) && interactableTile.isSolid) return false;
        }

        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
        for(Entity e : entities) {
            if(e == this) continue;
            if(e instanceof Player) continue;
            if(e.position.equals(targetPosition)) return false;
        }
        return true;
    }

    public int getDistanceFromPlayer() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        int dx = player.position.x - position.x;
        int dy = player.position.y - position.y;
        return (int) Math.sqrt(dx*dx + dy*dy);
    }
}

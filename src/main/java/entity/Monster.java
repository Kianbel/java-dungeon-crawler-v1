package entity;

import core.EntityRoomManager;
import gui.AudioManager;
import item.weapon.Weapon;
import util.Position;
import util.TILE;
import core.room.type.Room;
import world.InteractableTile;
import world.SpikeTrap;
import world.Trap;
import world.Web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Monster extends Entity implements MoveAfterPlayer {
    public Monster(String name, int health, int armor, Weapon weapon, Position position) {
        super(name, health, armor, weapon, position);
    }

    @Override
    public void die() {
        super.die();
        AudioManager.getInstance().playSFX("enemy_die");
    }

    @Override
    public void makeMove() {
        final int MAKE_SOUND_DISTANCE_THRESHOLD = 8;
        if(Math.random() <= 0.1 && getDistanceFromPlayer() <= MAKE_SOUND_DISTANCE_THRESHOLD) makeSoundTextPopup();
    }

    public boolean hasLineOfSight(Position start, Position end) {
        // Track the ray as it moves step-by-step across the grid
        int currentX = start.x;
        int currentY = start.y;

        int targetX = end.x;
        int targetY = end.y;

        // Calculate the total grid distance to travel on each axis
        int deltaX = Math.abs(targetX - currentX);
        int deltaY = Math.abs(targetY - currentY);

        // Determine the direction of the steps (+1 means right/down, -1 means left/up)
        int stepX = currentX < targetX ? 1 : -1;
        int stepY = currentY < targetY ? 1 : -1;

        // The error tracking variable determines when it's time to step diagonally
        // vs. stepping along a single axis to stay true to the visual ray.
        int lineError = deltaX - deltaY;

        final Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        final TILE[][] roomLayout = currentRoom.getLayout();

        final boolean[][] interactableTileCache = new boolean[currentRoom.height][currentRoom.length];
        List<InteractableTile> interactableTiles = currentRoom.getInteractableTiles();
        for(InteractableTile interactableTile : interactableTiles) {
            if(interactableTile instanceof Trap) continue;
            if(interactableTile instanceof Web) continue;

            interactableTileCache[interactableTile.roomLayoutPosition.y][interactableTile.roomLayoutPosition.x] = true;
        }

        while (true) {
            // If the ray safely reaches the target position without hitting a wall, sight is clear!
            if (currentX == targetX && currentY == targetY) {
                return true;
            }

            // Check for obstructions (ignoring the tile the monster is currently standing on)
            if (currentX != start.x || currentY != start.y) {
                if(!roomLayout[currentY][currentX].isWalkable()) return false;
                if(interactableTileCache[currentY][currentX]) return false;
            }

            // We double the error margin to perform integer-based math instead of floating-point math
            int doubleError = 2 * lineError;

            // Decide whether to step horizontally
            if (doubleError > -deltaY) {
                lineError -= deltaY;
                currentX += stepX;
            }

            // Decide whether to step vertically
            if (doubleError < deltaX) {
                lineError += deltaX;
                currentY += stepY;
            }
        }
    }

    private List<Position> getAStarPathPositions(Position fromPos, Position toPos) {
        final Node startNode = new Node(fromPos.x, fromPos.y);
        final Node goalNode = new Node(toPos.x, toPos.y);

        final Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        final TILE[][] roomLayout = currentRoom.getLayout();
        final int roomHeight = roomLayout.length;
        final int roomLength = roomLayout[0].length;

        // --- OPTIMIZATION CACHE: 2D Lookup Matrices for O(1) Performance ---
        boolean[][] isOccupied = new boolean[roomHeight][roomLength];
        boolean[][] openSet = new boolean[roomHeight][roomLength];
        boolean[][] closedSet = new boolean[roomHeight][roomLength];
        double[][] gCosts = new double[roomHeight][roomLength];

        for (double[] row : gCosts) {
            Arrays.fill(row, Double.MAX_VALUE);
        }

        // Cache living entities into our spatial matrix
        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                if (entity instanceof Player || entity == this) continue;
                if (entity.isInBounds(roomHeight, roomLength)) {
                    isOccupied[entity.position.y][entity.position.x] = true;
                }
            }
        }

        // Cache solid interactable tiles into our spatial matrix
        List<InteractableTile> interactableTiles = EntityRoomManager.getInstance().getInteractableTilesFromRoom(currentRoom);
        if (interactableTiles != null) {
            for (int i = 0; i < interactableTiles.size(); i++) {
                InteractableTile interactableTile = interactableTiles.get(i);
                if (interactableTile.isInBounds(roomHeight, roomLength)) {
                    if(interactableTile instanceof Trap trap){
                        if(trap instanceof SpikeTrap spikeTrap && spikeTrap.isActivated) {
                            isOccupied[interactableTile.roomLayoutPosition.y][interactableTile.roomLayoutPosition.x] = true;
                        }
                        else continue;
                    }
                    isOccupied[interactableTile.roomLayoutPosition.y][interactableTile.roomLayoutPosition.x] = true;
                }
            }
        }

        List<Node> toSearch = new ArrayList<>();
        toSearch.add(startNode);
        openSet[startNode.y][startNode.x] = true;
        gCosts[startNode.y][startNode.x] = 0;

        List<Node> neighbors = new ArrayList<>(4);

        while (!toSearch.isEmpty()) {
            Node currentNode = toSearch.get(0);
            for (int i = 1; i < toSearch.size(); i++) {
                Node nextNode = toSearch.get(i);
                if (nextNode.getF() < currentNode.getF() || (nextNode.getF() == currentNode.getF() && nextNode.getH() < currentNode.getH())) {
                    currentNode = nextNode;
                }
            }

            closedSet[currentNode.y][currentNode.x] = true;
            openSet[currentNode.y][currentNode.x] = false;
            toSearch.remove(currentNode);

            if (currentNode.equals(goalNode)) {
                Node currentPathTile = currentNode;
                List<Position> pathList = new ArrayList<>();

                while (currentPathTile != startNode) {
                    pathList.addFirst(currentPathTile.getPosition());
                    currentPathTile = currentPathTile.getConnection();
                }
                return pathList;
            }

            int x = currentNode.x;
            int y = currentNode.y;

            // Build valid adjacent node steps using our O(1) occupancy matrix checks
            if (y - 1 >= 0 && roomLayout[y - 1][x].isWalkable() && !isOccupied[y - 1][x]) neighbors.add(new Node(x, y - 1));
            if (y + 1 < roomHeight && roomLayout[y + 1][x].isWalkable() && !isOccupied[y + 1][x]) neighbors.add(new Node(x, y + 1));
            if (x - 1 >= 0 && roomLayout[y][x - 1].isWalkable() && !isOccupied[y][x - 1]) neighbors.add(new Node(x - 1, y));
            if (x + 1 < roomLength && roomLayout[y][x + 1].isWalkable() && !isOccupied[y][x + 1]) neighbors.add(new Node(x + 1, y));

            for (int i = 0; i < neighbors.size(); i++) {
                Node neighbor = neighbors.get(i);
                if (closedSet[neighbor.y][neighbor.x]) continue;

                boolean inSearch = openSet[neighbor.y][neighbor.x];
                double costToNeighbor = gCosts[currentNode.y][currentNode.x] + 1.0; // Uniform cardinal step weight

                if (!inSearch || costToNeighbor < gCosts[neighbor.y][neighbor.x]) {
                    gCosts[neighbor.y][neighbor.x] = costToNeighbor;
                    neighbor.setG(costToNeighbor);
                    neighbor.setConnection(currentNode);

                    if (!inSearch) {
                        neighbor.setH(neighbor.getPosition().getDistanceTo(toPos));
                        toSearch.add(neighbor);
                        openSet[neighbor.y][neighbor.x] = true;
                    }
                }
            }
            neighbors.clear();
        }
        return null;
    }

    protected abstract void makeSoundTextPopup();

    public Position pathfindToPlayerPosition() {
        return pathfindToPlayerPosition(false);
    }

    public Position pathfindToPlayerPosition(boolean allowDiagonal) {
        final Entity player = EntityRoomManager.getInstance().getPlayer();
        if (player == null || position.equals(player.position)) {
            return new Position(0, 0);
        }

        if (allowDiagonal) {
            final Position playerPosition = player.position;
            final int dx = playerPosition.x - position.x;
            final int dy = playerPosition.y - position.y;
            final double distance = Math.sqrt(dx * dx + dy * dy);
            final double unitX = dx / distance;
            final double unitY = dy / distance;
            final double angleToPlayer = Math.abs(Math.atan2(unitY, unitX)) * (180.0 / Math.PI);
            final double diagonalAngle = angleToPlayer % 90;

            final int x = (int) Math.signum(dx);
            final int y = (int) Math.signum(dy);

            if (diagonalAngle >= 25 && diagonalAngle <= 65) {
                return new Position(x, y);
            }
            if (Math.abs(dx) > Math.abs(dy)) {
                return new Position(x, 0);
            } else if (Math.abs(dy) > Math.abs(dx)) {
                return new Position(0, y);
            }
        } else {
            final List<Position> pathPositions = getAStarPathPositions(this.position, player.position);
            if (pathPositions == null || pathPositions.isEmpty()) return new Position(0, 0);

            final Position firstPathPosition = pathPositions.get(0);
            return new Position(firstPathPosition.x - position.x, firstPathPosition.y - position.y);
        }
        return new Position(0, 0);
    }

    public boolean isValidTargetPosition(Position targetPosition) {
        Room currentRoom = EntityRoomManager.getInstance().getRoomFromEntity(this);
        TILE[][] roomLayout = currentRoom.getLayout();

        if (targetPosition.x < 0 || targetPosition.x >= roomLayout[0].length) return false;
        if (targetPosition.y < 0 || targetPosition.y >= roomLayout.length) return false;

        TILE tile = roomLayout[targetPosition.y][targetPosition.x];
        if (!tile.isWalkable()) return false;

        List<InteractableTile> interactableTiles = currentRoom.getInteractableTiles();
        for (int i = 0; i < interactableTiles.size(); i++) {
            InteractableTile interactableTile = interactableTiles.get(i);
            if (interactableTile.roomLayoutPosition.equals(targetPosition)
                    && interactableTile.isSolid) return false;
        }

        List<Entity> entities = EntityRoomManager.getInstance().getEntitiesInRoom(currentRoom);
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e == this || e instanceof Player) continue;
            if (e.position.equals(targetPosition)) return false;
        }
        return true;
    }

    public int getDistanceFromPlayer() {
        Entity player = EntityRoomManager.getInstance().getPlayer();
        if (player == null) return 0;
        int dx = player.position.x - position.x;
        int dy = player.position.y - position.y;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
}
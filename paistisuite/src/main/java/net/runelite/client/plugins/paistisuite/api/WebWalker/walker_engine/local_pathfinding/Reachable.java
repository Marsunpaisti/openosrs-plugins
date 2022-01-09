package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

import java.util.*;

import static net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.real_time_collision.CollisionDataCollector.getCollisionData;

public class Reachable {

    private RSTile[][] map;

    /**
     * Generates reachable map from player position
     */
    public Reachable() {
        this(null);
    }

    private Reachable(RSTile homeTile) {
        map = generateMap(homeTile != null ? homeTile : new RSTile(PPlayer.location()));
    }

    public boolean canReach(RSTile position) {
        position = position.toWorldTile(PUtils.getClient());
        RSTile playerPosition = new RSTile(PPlayer.location());
        if (playerPosition.getX() == position.getX() && playerPosition.getY() == position.getY()) {
            return true;
        }
        return getParent(position.toLocalTile(PUtils.getClient())) != null;
    }

    public boolean canReach(WorldPoint position) {
        return canReach(new RSTile(position));
    }

    public boolean canReach(int x, int y) {
        RSTile playerPosition = new RSTile(PPlayer.location());
        if (playerPosition.getX() == x && playerPosition.getY() == y) {
            return true;
        }
        RSTile position = convertToLocal(x, y);
        return getParent(position) != null;
    }

    public RSTile closestTile(Collection<RSTile> tiles) {
        RSTile closest = null;
        double closestDistance = Integer.MAX_VALUE;
        RSTile playerPosition = new RSTile(PPlayer.location());
        for (RSTile positionable : tiles) {
            double distance = playerPosition.distanceToDouble(positionable);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = positionable;
            }
        }
        return closest;
    }

    /**
     * @param x
     * @param y
     * @return parent tile of x and y through BFS.
     */
    public RSTile getParent(int x, int y) {
        RSTile position = convertToLocal(x, y);
        return getParent(position);
    }

    public static WorldPoint getNearestReachableTile(WorldPoint target, int radius) {
        Reachable r = new Reachable();
        if (r.canReach(target)) return target;
        List<WorldPoint> reachableTiles = new ArrayList<WorldPoint>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                WorldPoint eval = target.dx(dx).dy(dy);
                if (!r.canReach(eval)) continue;
                reachableTiles.add(eval);
            }
        }

        return reachableTiles
                .stream()
                .sorted((a, b) -> {
                    // First sort by distance from target
                    int aObjDist = target.distanceTo(a);
                    int bObjDist = target.distanceTo(b);
                    if (aObjDist < bObjDist) return -1;
                    if (bObjDist > aObjDist) return 1;

                    // Then sort by distance from us
                    int aPlayerDist = r.getDistance(a);
                    int bPlayerDist = r.getDistance(b);
                    return aPlayerDist - bPlayerDist;
                })
                .findFirst()
                .orElse(null);
    }

    public static WorldPoint getNearestReachableTile(PTileObject to, int radius) {
        return getNearestReachableTile(to.getWorldLocation(), radius);
    }

    public RSTile getParent(RSTile tile) {
        if (tile.getType() != RSTile.TYPES.LOCAL) {
            tile = tile.toLocalTile(PUtils.getClient());
        }
        int x = tile.getX(), y = tile.getY();
        if (x < 0 || y < 0) {
            return null;
        }
        if (x >= 104 || y >= 104 || x >= map.length || y >= map[x].length) {
            return null;
        }
        return map[x][y];
    }

    /**
     * @param x
     * @param y
     * @return Distance to tile. Max integer value if unreachable. Does not account for positionable behind doors
     */
    public int getDistance(int x, int y) {
        RSTile position = convertToLocal(x, y);
        return getDistance(position);
    }

    /**
     * @param tile
     * @return path to tile. Does not account for positionable behind doors
     */
    public ArrayList<RSTile> getPath(RSTile tile) {
        RSTile position = convertToLocal(tile.getX(), tile.getY());
        int x = position.getX(), y = position.getY();
        return getPath(x, y);
    }

    /**
     * @param x
     * @param y
     * @return null if no path.
     */
    public ArrayList<RSTile> getPath(int x, int y) {
        ArrayList<RSTile> path = new ArrayList<>();
        RSTile playerPos = new RSTile(PPlayer.location()).toLocalTile(PUtils.getClient());
        if (x == playerPos.getX() && y == playerPos.getY()) {
            return path;
        }
        if (x < 0 || y < 0) {
            return null;
        }
        if (x >= 104 || y >= 104) {
            return null;
        }
        if (map[x][y] == null) {
            return null;
        }
        RSTile tile = new RSTile(x, y, PPlayer.location().getPlane(), RSTile.TYPES.LOCAL);
        while ((tile = map[tile.getX()][tile.getY()]) != null) {
            path.add(tile.toWorldTile(PUtils.getClient()));
        }
        Collections.reverse(path);
        return path;
    }

    public int getDistance(WorldPoint position) {
        return getDistance(new RSTile(position));
    }

    public int getDistance(RSTile localPos) {
        RSTile position = convertToLocal(localPos.getX(), localPos.getY());
        int x = position.getX(), y = position.getY();
        RSTile playerPos = new RSTile(PPlayer.location()).toLocalTile(PUtils.getClient());
        if (x == playerPos.getX() && y == playerPos.getY()) {
            return 0;
        }
        if (x < 0 || y < 0) {
            return Integer.MAX_VALUE;
        }
        if (x >= 104 || y >= 104) {
            return Integer.MAX_VALUE;
        }
        if (map[x][y] == null) {
            return Integer.MAX_VALUE;
        }
        int length = 0;
        RSTile tile = position;
        while ((tile = map[tile.getX()][tile.getY()]) != null) {
            length++;
        }
        return length;
    }

    private static RSTile convertToLocal(int x, int y) {
        RSTile position = new RSTile(x, y, PPlayer.location().getPlane(), x >= 104 || y >= 104 ? RSTile.TYPES.WORLD : RSTile.TYPES.LOCAL);
        if (position.getType() != RSTile.TYPES.LOCAL) {
            position = position.toLocalTile(PUtils.getClient());
        }
        return position;
    }

    public static RSTile getBestWalkableTile(RSTile target, Reachable reachable) {
        RSTile localPosition = target.toLocalTile(PUtils.getClient());
        HashSet<RSTile> building = new HashSet<>(); // TODO: BankHelper.getBuilding(positionable);
        boolean[][] traversed = new boolean[104][104];
        RSTile[][] parentMap = new RSTile[104][104];
        Queue<RSTile> queue = new LinkedList<>();
        int[][] collisionData = getCollisionData();
        if (collisionData == null)
            return null;

        queue.add(localPosition);
        try {
            traversed[localPosition.getX()][localPosition.getY()] = true;
            parentMap[localPosition.getX()][localPosition.getY()] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        while (!queue.isEmpty()) {
            RSTile currentLocal = queue.poll();
            int x = currentLocal.getX(), y = currentLocal.getY();

            int currentCollisionFlags = collisionData[x][y];
            if (AStarNode.isWalkable(currentCollisionFlags)) {
                if (reachable != null && !reachable.canReach(currentLocal.toWorldTile(PUtils.getClient()).getX(), currentLocal.toWorldTile(PUtils.getClient()).getY())) {
                    continue;
                }
                if (building != null && building.size() > 0) {
                    if (building.contains(currentLocal.toWorldTile(PUtils.getClient()))) {
                        return currentLocal.toWorldTile(PUtils.getClient());
                    }
                    continue; //Next tile because we are now outside of building.
                } else {
                    return currentLocal.toWorldTile(PUtils.getClient());
                }
            }

            for (Direction direction : Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                RSTile neighbor = direction.getPointingTile(currentLocal);
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();
                if (traversed[destinationX][destinationY]) {
                    continue; //Traversed already
                }
                traversed[destinationX][destinationY] = true;
                parentMap[destinationX][destinationY] = currentLocal;
                queue.add(neighbor);
            }

        }
        return null;
    }

    /**
     * @return gets collision map.
     */
    public static Reachable getMap() {
        return new Reachable(new RSTile(PPlayer.location()));
    }

    public static Reachable getMap(RSTile homeTile) {
        return new Reachable(homeTile);
    }

    /**
     * @return local reachable tiles
     */
    private static RSTile[][] generateMap(RSTile homeTile) {
        RSTile localPlayerPosition = homeTile.toLocalTile(PUtils.getClient());
        boolean[][] traversed = new boolean[104][104];
        RSTile[][] parentMap = new RSTile[104][104];
        Queue<RSTile> queue = new LinkedList<>();
        int[][] collisionData = getCollisionData();

        if (collisionData == null)
            return new RSTile[][]{};

        queue.add(localPlayerPosition);
        try {
            traversed[localPlayerPosition.getX()][localPlayerPosition.getY()] = true;
            parentMap[localPlayerPosition.getX()][localPlayerPosition.getY()] = null;
        } catch (Exception e) {
            return parentMap;
        }

        while (!queue.isEmpty()) {
            RSTile currentLocal = queue.poll();
            int x = currentLocal.getX(), y = currentLocal.getY();

            int currentCollisionFlags = collisionData[x][y];
            if (!AStarNode.isWalkable(currentCollisionFlags)) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                RSTile neighbor = direction.getPointingTile(currentLocal);
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();
                if (traversed[destinationX][destinationY]) {
                    continue; //Traversed already
                }
                traversed[destinationX][destinationY] = true;
                parentMap[destinationX][destinationY] = currentLocal;
                queue.add(neighbor);
            }

        }
        return parentMap;
    }

    public enum Direction {
        EAST(1, 0),
        NORTH(0, 1),
        WEST(-1, 0),
        SOUTH(0, -1),
        NORTH_EAST(1, 1),
        NORTH_WEST(-1, 1),
        SOUTH_EAST(1, -1),
        SOUTH_WEST(-1, -1),
        ;

        int x, y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public RSTile getPointingTile(RSTile tile) {
            return tile.translate(x, y);
        }

        public boolean isValidDirection(int x, int y, int[][] collisionData) {
            try {
                switch (this) {
                    case NORTH:
                        return !AStarNode.blockedNorth(collisionData[x][y]);
                    case EAST:
                        return !AStarNode.blockedEast(collisionData[x][y]);
                    case SOUTH:
                        return !AStarNode.blockedSouth(collisionData[x][y]);
                    case WEST:
                        return !AStarNode.blockedWest(collisionData[x][y]);
                    case NORTH_EAST:
                        if (AStarNode.blockedNorth(collisionData[x][y]) || AStarNode.blockedEast(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y + 1])) {
                            return false;
                        }
                        if (AStarNode.blockedNorth(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedEast(collisionData[x][y + 1])) {
                            return false;
                        }
                        return true;
                    case NORTH_WEST:
                        if (AStarNode.blockedNorth(collisionData[x][y]) || AStarNode.blockedWest(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y + 1])) {
                            return false;
                        }
                        if (AStarNode.blockedNorth(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedWest(collisionData[x][y + 1])) {
                            return false;
                        }
                        return true;
                    case SOUTH_EAST:
                        if (AStarNode.blockedSouth(collisionData[x][y]) || AStarNode.blockedEast(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y - 1])) {
                            return false;
                        }
                        if (AStarNode.blockedSouth(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedEast(collisionData[x][y - 1])) {
                            return false;
                        }
                        return true;
                    case SOUTH_WEST:
                        if (AStarNode.blockedSouth(collisionData[x][y]) || AStarNode.blockedWest(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y - 1])) {
                            return false;
                        }
                        if (AStarNode.blockedSouth(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedWest(collisionData[x][y - 1])) {
                            return false;
                        }
                        return true;
                    default:
                        return false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return false;
            }
        }
    }

}
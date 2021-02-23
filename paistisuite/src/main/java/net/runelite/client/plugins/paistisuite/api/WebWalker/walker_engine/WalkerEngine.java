package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PBanking;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.Teleport;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.PathFindingNode;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.bfs.BFS;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.PathObjectHandler;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.PathAnalyzer;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.Reachable;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.Charter;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.NavigationSpecialCase;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.ShipUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.real_time_collision.CollisionDataCollector;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.real_time_collision.RealTimeCollisionTile;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.AccurateMouse;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class WalkerEngine{
    private static WalkerEngine walkerEngine;
    private static Client client = PUtils.getClient();
    private int attemptsForAction;
    private int clickAfterSteps = (int)PUtils.randomNormal(5, 10);
    private final int failThreshold;
    private boolean navigating;
    private final ReentrantLock pathLock = new ReentrantLock();
    private final ReentrantLock furthestReachableTileLock = new ReentrantLock();
    private List<RSTile> currentPath;
    private PathAnalyzer.DestinationDetails debugFurthestReachable = null;

    public static WalkerEngine getInstance(){
        return walkerEngine != null ? walkerEngine : (walkerEngine = new WalkerEngine());
    }

    private WalkerEngine(){
        attemptsForAction = 0;
        failThreshold = 3;
        synchronized (pathLock){
            navigating = false;
            currentPath = null;
        }
    }

    public boolean walkPath(List<RSTile> path){
        return walkPath(path, null);
    }

    public List<RSTile> getCurrentPath() {
        synchronized (pathLock){
            return currentPath;
        }
    }

    public void setDebugFurthestReachable(PathAnalyzer.DestinationDetails val){
        synchronized (furthestReachableTileLock){
            debugFurthestReachable = val;
        }
    }

    public List<WorldPoint> getCurrentPathWorldPoints() {
        synchronized (pathLock){
            if (currentPath == null) return null;
            return currentPath.stream().map(RSTile::toWorldPoint).collect(Collectors.toList());
        }
    }

    public PathAnalyzer.DestinationDetails getDebugFurthestTile(){
        synchronized (furthestReachableTileLock){
            return this.debugFurthestReachable;
        }
    }

    /**
     *
     * @param path
     * @param walkingCondition
     * @return
     */
    public boolean walkPath(List<RSTile> path, WalkingCondition walkingCondition){
        if (path.size() == 0) {
            log.info("Path is empty");
            return false;
        }

        if (DaxWalker.getInstance().allowTeleports) {
            if (!handleTeleports(path)) {
                log.warn("Failed to handle teleports...");
                return false;
            }
        }

        synchronized (pathLock){
            navigating = true;
            currentPath = path;
        }
        try {
            PathAnalyzer.DestinationDetails destinationDetails;
            resetAttempts();

            while (true) {

                GameState gameState = client.getGameState();
                if (gameState != GameState.LOGGED_IN && gameState != GameState.LOADING) {
                    return false;
                }

                if (ShipUtils.isOnShip()) {
                    if (!ShipUtils.crossGangplank()) {
                        log.info("Failed to exit ship via gangplank.");
                        failedAttempt();
                    }
                    WaitFor.milliseconds(50, 250);
                    continue;
                }


                if (isFailedOverThreshhold()) {
                    log.info("Too many failed attempts");
                    return false;
                }

                destinationDetails = PathAnalyzer.furthestReachableTile(path);
                synchronized (furthestReachableTileLock){
                    debugFurthestReachable = destinationDetails;
                }
                if (destinationDetails == null) {
                    log.info("Could not grab destination details.");
                    failedAttempt();
                    continue;
                }

                RealTimeCollisionTile currentNode = destinationDetails.getDestination();
                RSTile assumedNext = destinationDetails.getAssumed();

                if (destinationDetails.getState() != PathAnalyzer.PathState.FURTHEST_CLICKABLE_TILE) {
                    log.info(destinationDetails.toString());
                }

                final RealTimeCollisionTile destination = currentNode;
                if (!destination.getRSTile().isInMinimap()) {
                    log.info("Closest tile in path is not in minimap: " + destination);
                    failedAttempt();
                    continue;
                }

                CustomConditionContainer conditionContainer = new CustomConditionContainer(walkingCondition);
                log.info(destinationDetails.toString());

                switch (conditionContainer.getResult()) {
                    case EXIT_OUT_WALKER_SUCCESS:
                        return true;
                    case EXIT_OUT_WALKER_FAIL:
                        return false;
                }

                switch (destinationDetails.getState()) {
                    case DISCONNECTED_PATH:
                        if (currentNode.getRSTile().toWorldPoint().distanceToHypotenuse(PPlayer.location()) > 10){
                            clickMinimap(currentNode);
                            WaitFor.milliseconds(1200, 3400);
                        }


                        NavigationSpecialCase.SpecialLocation specialLocation = NavigationSpecialCase.getLocation(currentNode.getRSTile());
                        NavigationSpecialCase.SpecialLocation specialLocationDestination = NavigationSpecialCase.getLocation(assumedNext);
                        if (specialLocation != null && specialLocationDestination != null) {
                            log.info("[SPECIAL LOCATION] We are at " + specialLocation + " and our destination is " + specialLocationDestination);
                            if (!NavigationSpecialCase.handle(specialLocationDestination)) {
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }


                        Charter.LocationProperty locationProperty = Charter.LocationProperty.getLocation(currentNode.getRSTile());
                        Charter.LocationProperty destinationProperty = Charter.LocationProperty.getLocation(assumedNext);
                        if (locationProperty != null && destinationProperty != null) {
                            log.info("Chartering to: " + destinationProperty);
                            if (!Charter.to(destinationProperty)) {
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }

                        //DO NOT BREAK OUT
                    case OBJECT_BLOCKING:
                        switch (conditionContainer.getResult()) {
                            case EXIT_OUT_WALKER_SUCCESS:
                                return true;
                            case EXIT_OUT_WALKER_FAIL:
                                return false;
                        }

                        RSTile walkingTile = Reachable.getBestWalkableTile(destination.getRSTile(), new Reachable());
                        if (isDestinationClose(destination) || (walkingTile != null ? AccurateMouse.walkTo(walkingTile) : clickMinimap(destination))) {
                            log.info("Handling Object...");
                            if (!PathObjectHandler.handle(destinationDetails, path)) {
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }
                        break;

                    case FURTHEST_CLICKABLE_TILE:
                        switch (conditionContainer.getResult()) {
                            case EXIT_OUT_WALKER_SUCCESS:
                                return true;
                            case EXIT_OUT_WALKER_FAIL:
                                return false;
                        }
                        if (clickMinimap(currentNode)) {
                            long offsetWalkingTimeout = System.currentTimeMillis() + PUtils.random(3000, 4500);
                            WaitFor.condition(10000, () -> {
                                switch (conditionContainer.trigger()) {
                                    case EXIT_OUT_WALKER_SUCCESS:
                                    case EXIT_OUT_WALKER_FAIL:
                                        return WaitFor.Return.SUCCESS;
                                }

                                PathAnalyzer.DestinationDetails furthestReachable = PathAnalyzer.furthestReachableTile(path);
                                synchronized (furthestReachableTileLock){
                                    debugFurthestReachable = furthestReachable;
                                }
                                PathFindingNode currentDestination = BFS.bfsClosestToPath(path, RealTimeCollisionTile.get(destination.getX(), destination.getY(), destination.getZ()));
                                if (currentDestination == null) {
                                    log.info("Could not walk to closest tile in path.");
                                    failedAttempt();
                                    return WaitFor.Return.FAIL;
                                }
                                int indexCurrentDestination = path.indexOf(currentDestination.getRSTile());

                                PathFindingNode closestToPlayer = PathAnalyzer.closestTileInPathToPlayer(path);
                                if (closestToPlayer == null) {
                                    log.info("Could not detect closest tile to player in path.");
                                    failedAttempt();
                                    return WaitFor.Return.FAIL;
                                }
                                int indexCurrentPosition = path.indexOf(closestToPlayer.getRSTile());
                                if (furthestReachable == null) {
                                    log.info("Furthest reachable is null/");
                                    return WaitFor.Return.FAIL;
                                }
                                int indexNextDestination = path.indexOf(furthestReachable.getDestination().getRSTile());
                                if (indexNextDestination - indexCurrentDestination > clickAfterSteps || indexCurrentDestination - indexCurrentPosition < 5) {
                                    clickAfterSteps = (int)PUtils.randomNormal(5, 10);
                                    return WaitFor.Return.SUCCESS;
                                }
                                if (System.currentTimeMillis() > offsetWalkingTimeout && !PPlayer.isMoving()){
                                    return WaitFor.Return.FAIL;
                                }
                                return WaitFor.milliseconds(100);
                            });
                        }
                        break;

                    case END_OF_PATH:
                        clickMinimap(destinationDetails.getDestination());
                        log.info("Reached end of path");
                        return true;
                }

                switch (conditionContainer.getResult()) {
                    case EXIT_OUT_WALKER_SUCCESS:
                        return true;
                    case EXIT_OUT_WALKER_FAIL:
                        return false;
                }

                WaitFor.milliseconds(50, 100);

            }
        } finally {
            synchronized(pathLock){
                navigating = false;
            }
        }
    }

    public boolean isNavigating() {
        synchronized ( pathLock ){
            return navigating;
        }
    }

    boolean isDestinationClose(PathFindingNode pathFindingNode){
        final RSTile playerPosition = new RSTile(PPlayer.location());
        return playerPosition.distanceToDouble(new RSTile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ())) <= 12
                && (BFS.isReachable(RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.getPlane()), RealTimeCollisionTile.get(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()), 200));
    }

    public boolean clickMinimap(PathFindingNode pathFindingNode){
        final RSTile playerPosition = new RSTile(PPlayer.location());
        if (playerPosition.distanceToDouble(pathFindingNode.getRSTile()) <= 1){
            return true;
        }
        PathFindingNode randomNearby = BFS.getRandomTileNearby(pathFindingNode);

        if (randomNearby == null){
            log.info("Unable to generate randomization.");
            return false;
        }

        //log.info("Randomize(" + pathFindingNode.getX() + "," + pathFindingNode.getY() + "," + pathFindingNode.getZ() + ") -> (" + randomNearby.getX() + "," + randomNearby.getY() + "," + randomNearby.getZ() + ")");
        return AccurateMouse.walkTo(new RSTile(randomNearby.getX(), randomNearby.getY(), randomNearby.getZ())) || AccurateMouse.walkTo(new RSTile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()));
    }

    private boolean resetAttempts(){
        return successfulAttempt();
    }

    private boolean successfulAttempt(){
        attemptsForAction = 0;
        return true;
    }

    private void failedAttempt(){
        log.info("Failed attempt on action.");
        WaitFor.milliseconds(450 * (attemptsForAction + 1), 850 * (attemptsForAction + 1));
        CollisionDataCollector.generateRealTimeCollision();
    }

    private boolean isFailedOverThreshhold(){
        return attemptsForAction >= failThreshold;
    }

    private class CustomConditionContainer {
        private WalkingCondition walkingCondition;
        private WalkingCondition.State result;
        CustomConditionContainer(WalkingCondition walkingCondition){
            this.walkingCondition = walkingCondition;
            this.result = WalkingCondition.State.CONTINUE_WALKER;
        }
        public WalkingCondition.State trigger(){
            result = (walkingCondition != null ? walkingCondition.action() : result);
            return result != null ? result : WalkingCondition.State.CONTINUE_WALKER;
        }
        public WalkingCondition.State getResult() {
            return result;
        }
    }

    private boolean handleTeleports(List<RSTile> path) {
        RSTile startPosition = path.get(0);
        RSTile playerPosition = new RSTile(PPlayer.location());
        if(startPosition.equals(playerPosition)) return true;
        if (PBanking.isBankOpen()){
            PBanking.closeBank();
            return WaitFor.condition(2000, () -> !PBanking.isBankOpen() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
        for (Teleport teleport : Teleport.values()) {
            if (!teleport.getRequirement().satisfies()) continue;
            if(teleport.isAtTeleportSpot(startPosition) && !teleport.isAtTeleportSpot(playerPosition)){
                log.info("Using teleport method: " + teleport);
                teleport.trigger();
                return WaitFor.condition(PUtils.random(3000, 20000),
                        () -> startPosition.distanceTo(new RSTile(PPlayer.location())) < 10 ?
                                WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            }
        }
        return true;
    }

}
package net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.Teleport;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.ShipUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class DaxWalker {
    @Getter
    private static Map<RSTile, Teleport> map;
    private static DaxWalker daxWalker;
    public boolean allowTeleports = false;

    public static DaxWalker getInstance() {
        return daxWalker != null ? daxWalker : (daxWalker = new DaxWalker());
    }

    public boolean useRun = true;

    private WalkingCondition globalWalkingCondition;

    private DaxWalker() {
        globalWalkingCondition = () -> WalkingCondition.State.CONTINUE_WALKER;

        map = new ConcurrentHashMap<>();
        for (Teleport teleport : Teleport.values()) {
            map.put(teleport.getLocation(), teleport);
        }
    }

    public static WalkingCondition getGlobalWalkingCondition() {
        return getInstance().globalWalkingCondition;
    }

    public void useLocalDevelopmentServer(boolean b) {
        WebWalkerServerApi.getInstance().setTestMode(b);
    }

    public static void setGlobalWalkingCondition(WalkingCondition walkingCondition) {
        getInstance().globalWalkingCondition = walkingCondition;
    }

    public static void setCredentials(DaxCredentialsProvider daxCredentialsProvider) {
        WebWalkerServerApi.getInstance().setDaxCredentialsProvider(daxCredentialsProvider);
    }

    public static boolean walkTo(RSTile destination) {
        return walkTo(destination, null);
    }

    public static boolean walkTo(RSTile destination, WalkingCondition walkingCondition) {

        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }

        Point3D start = new Point3D(PPlayer.location());
        if (start.equals(new Point3D(destination))) {
            return true;
        }

        List<PathRequestPair> pathRequestPairs = getInstance().allowTeleports ? getInstance().getPathTeleports(destination) : new ArrayList<>();
        pathRequestPairs.add(0, new PathRequestPair(start, new Point3D(destination)));

        //TODO need to add planted spirit trees if we ever use this method

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(PlayerDetails.generate(), pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);
        PathResult pathResult = getInstance().getBestPath(start, validPaths);
        if (pathResult == null) {
            log.warn("No valid path found");
            return false;
        }

        return WalkerEngine.getInstance().walkPath(pathResult.toRSTilePath(), getGlobalWalkingCondition().combine(walkingCondition));
    }

    public static boolean walkToBank() {
        return walkToBank(null, null);
    }

    public static boolean walkToBank(RunescapeBank bank) {
        return walkToBank(bank, null);
    }

    public static boolean walkToBank(WalkingCondition walkingCondition) {
        return walkToBank(null, walkingCondition);
    }

    public static boolean walkToBank(RunescapeBank bank, WalkingCondition walkingCondition) {
        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }

        if (bank != null)
            return walkTo(bank.getPosition(), walkingCondition);

        List<BankPathRequestPair> pathRequestPairs = getInstance().getBankPathTeleports();

        Point3D start = new Point3D(PPlayer.location());
        pathRequestPairs.add(0, new BankPathRequestPair(start, null));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getBankPaths(new BulkBankPathRequest(PlayerDetails.generate(), pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);
        PathResult pathResult = getInstance().getBestPath(start, validPaths);
        if (pathResult == null) {
            log.warn("No valid path found");
            return false;
        }
        return WalkerEngine.getInstance().walkPath(pathResult.toRSTilePath(), getGlobalWalkingCondition().combine(walkingCondition));
    }

    public List<PathRequestPair> getPathTeleports(RSTile destination) {
        return Teleport.getValidStartingRSTiles().stream()
                .map((RSTile t) -> new PathRequestPair(new Point3D(t),
                        new Point3D(destination)))
                .collect(Collectors.toList());
    }

    public List<BankPathRequestPair> getBankPathTeleports() {
        return Teleport.getValidStartingRSTiles().stream()
                .map((RSTile t) -> new BankPathRequestPair(new Point3D(t), null))
                .collect(Collectors.toList());
    }

    public List<PathResult> validPaths(List<PathResult> list) {
        List<PathResult> result = list.stream().filter(pathResult -> pathResult.getPathStatus() == PathStatus.SUCCESS).collect(
                Collectors.toList());
        if (!result.isEmpty()) {
            return result;
        }
        return Collections.emptyList();
    }

    public PathResult getBestPath(Point3D start, List<PathResult> list) {
        return list.stream().min(Comparator.comparingInt(pathResult -> getPathMoveCost(start, pathResult))).orElse(null);
    }

    public int getPathMoveCost(Point3D start, PathResult pathResult) {
        if (start.equals(pathResult.getPath().get(0))) return pathResult.getCost();
        Teleport teleport = map.get(new RSTile(pathResult.getPath().get(0)));
        if (teleport == null) return pathResult.getCost();
        return teleport.getMoveCost() + pathResult.getCost();
    }
}

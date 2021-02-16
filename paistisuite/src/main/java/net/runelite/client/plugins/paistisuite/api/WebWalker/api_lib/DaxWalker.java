package net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
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

        RSTile start = new RSTile(PPlayer.location());
        if (start.equals(destination)) {
            return true;
        }

        List<PathRequestPair> pathRequestPairs = getInstance().allowTeleports ? getInstance().getPathTeleports(destination) : new ArrayList<PathRequestPair>();

        pathRequestPairs.add(new PathRequestPair(new Point3D(start), new Point3D(destination)));

	    List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(PlayerDetails.generate(),pathRequestPairs));

	    List<PathResult> validPaths = getInstance().validPaths(pathResults);

	    PathResult pathResult = getInstance().getBestPath(validPaths);
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

        if(bank != null)
            return walkTo(bank.getPosition());

        List<BankPathRequestPair> pathRequestPairs = getInstance().getBankPathTeleports();

        pathRequestPairs.add(new BankPathRequestPair(new Point3D(PPlayer.location()),null));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getBankPaths(new BulkBankPathRequest(PlayerDetails.generate(),pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);
        PathResult pathResult = getInstance().getBestPath(validPaths);
        if (pathResult == null) {
            log.warn("No valid path found");
            return false;
        }
        return WalkerEngine.getInstance().walkPath(pathResult.toRSTilePath(), getGlobalWalkingCondition().combine(walkingCondition));
    }

    public List<PathRequestPair> getPathTeleports(RSTile start) {
        return Teleport.getValidStartingRSTiles().stream()
                .map((RSTile t) -> new PathRequestPair(new Point3D(t),
                        new Point3D(start)))
                .collect(Collectors.toList());
    }

    private List<BankPathRequestPair> getBankPathTeleports() {
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

    public PathResult getBestPath(List<PathResult> list) {
        return list.stream().min(Comparator.comparingInt(this::getPathMoveCost)).orElse(null);
    }

    private int getPathMoveCost(PathResult pathResult) {
        if (PPlayer.location().equals(pathResult.getPath().get(0).toWorldPoint())) return pathResult.getCost();
        Teleport teleport = map.get(new RSTile(pathResult.getPath().get(0).toWorldPoint()));
        if (teleport == null) return pathResult.getCost();
        return teleport.getMoveCost() + pathResult.getCost();
    }
}

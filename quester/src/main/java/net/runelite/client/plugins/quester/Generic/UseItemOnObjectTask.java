package net.runelite.client.plugins.quester.Generic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PathResult;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PathStatus;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PlayerDetails;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.Point3D;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.Reachable;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;

import java.util.function.BooleanSupplier;

@Slf4j
public class UseItemOnObjectTask implements Task {
    String objectName;
    String itemName;
    WorldPoint objectLoc;
    Quester plugin;
    BooleanSupplier successCondition;
    boolean isCompleted;
    boolean failed;
    int walkAttempts = 0;
    int interactAttempts = 0;
    int cachedDistance = -1;
    int cachedDistanceTick = -1;

    public UseItemOnObjectTask(Quester plugin, String itemName, String objectName, WorldPoint objectLoc, BooleanSupplier successCondition){
        this.objectName = objectName;
        this.objectLoc = objectLoc;
        this.plugin = plugin;
        this.itemName = itemName;
        this.successCondition = successCondition;
    }

    public String name() {
        return "Use " + this.itemName + " on " + this.objectName;
    }

    public WorldPoint location() {
        return this.objectLoc;
    }

    private PTileObject findTarget(){
        return PObjects.findObject(Filters.Objects.nameEquals(objectName)
                .and(tar -> tar.getWorldLocation().distanceTo(location()) < 10));
    }


    public boolean execute() {
        if (interactAttempts >= 5){
            log.info("Failed interact with object task. Too many attempts.");
            this.failed = true;
            return false;
        }

        PTileObject target = findTarget();
        WorldPoint nearestReachable = target != null ? Reachable.getNearestReachableTile(target, 1) : null;
        if (target == null || nearestReachable == null) {
            if (walkAttempts >= 5){
                this.failed = true;
                log.info("Unable to walk to object! Too many attempts!");
                return false;
            }
            walkAttempts++;
            if (plugin.webWalkTo(location())){
                PUtils.waitCondition(PUtils.random(2500, 3100), () -> !PPlayer.isMoving() && PPlayer.distanceTo(location()) <= 2);
                log.info("Walked to object");
            } else {
                log.info("Failed webwalk to object location! (Attempt " + walkAttempts + ")");
            }
            return true;
        }


        if (!PInteraction.useItemOnGameObject(PInventory.findItem(Filters.Items.nameEquals(itemName)), target)) {
            log.info("Unable to use " + itemName + " on " + objectName);
            this.failed = true;
            return false;
        }

        int distance = (int)Math.round(Reachable.getMap().getDistance(nearestReachable));
        if (distance > 1) PUtils.waitCondition(PUtils.random(800, 1400), PPlayer::isMoving);
        int multiplier = PPlayer.isRunEnabled() ? 300 : 600;
        int timeout = distance * multiplier + (int)PUtils.randomNormal(1900, 2800);
        PUtils.waitCondition(timeout, () -> !PPlayer.isMoving() || PPlayer.location().distanceTo(target.getWorldLocation()) <= 1);
        if (!PUtils.waitCondition(PUtils.random(6800, 8000), successCondition)){
            interactAttempts++;
            log.info("Timed out while waiting interaction success!");
            return true;
        }

        PUtils.sleepNormal(200, 600);
        this.isCompleted = true;
        return true;
    };

    public boolean condition() {
        return !isCompleted() && !isFailed() && PInventory.getCount(itemName) > 0;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isFailed(){
        return this.failed;
    }

    public int getDistance(){
        if (PUtils.getClient().getTickCount() <= cachedDistanceTick+30) return cachedDistance;
        WorldPoint playerLoc = PPlayer.getWorldLocation();
        Point3D playerLocPoint = new Point3D(playerLoc.getX(), playerLoc.getY(), playerLoc.getPlane());
        WorldPoint taskLoc = location();
        Point3D taskLocPoint = new Point3D(taskLoc.getX(), taskLoc.getY(), taskLoc.getPlane());
        PathResult path = WebWalkerServerApi.getInstance().getPath(playerLocPoint, taskLocPoint, PlayerDetails.generate());
        if (path.getPathStatus() == PathStatus.SUCCESS) {
            cachedDistance = path.getCost();
            cachedDistanceTick = PUtils.getClient().getTickCount();
            return cachedDistance;
        } else {
            cachedDistance = Integer.MAX_VALUE;
            cachedDistanceTick = PUtils.getClient().getTickCount();
            return cachedDistance;
        }
    };
}

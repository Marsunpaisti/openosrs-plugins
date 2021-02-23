package net.runelite.client.plugins.quester.Generic.ItemAcquisition;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.PShopping;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PathResult;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PathStatus;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PlayerDetails;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.Point3D;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.Reachable;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;

@Slf4j
public class BuyItemFromStoreTask implements Task {
    boolean failed;
    private String itemName;
    private Quester plugin;
    private WorldPoint location;
    private int price;
    private int quantity;
    private boolean isCompleted = false;
    int walkAttempts = 0;
    int tradeAttempts = 0;
    int hopAttempts = 0;
    int cachedDistance = -1;
    int cachedDistanceTick = -1;

    public BuyItemFromStoreTask(Quester plugin, String itemName, int quantity, int price, WorldPoint location){
        this.plugin = plugin;
        this.itemName = itemName;
        this.location = location;
        this.price = price;
        this.quantity = quantity;
    }

    public String name() {
        return "Buy " + this.itemName + " from shop.";
    }

    public WorldPoint location() {
        return this.location;
    }

    public NPC findTarget(){
        return PObjects.findNPC(Filters.NPCs.actionsContains("Trade")
        .and(n -> n.getWorldLocation().distanceTo(location()) < 10)
        .and(n -> Reachable.getNearestReachableTile(n.getWorldLocation(), 2) != null));
    }
    public boolean execute() {
        if (tradeAttempts >= 5){
            log.info("Failed talk to npc task. Too many attempts to talk to npc.");
            this.failed = true;
            return false;
        }
        NPC npc = findTarget();
        WorldPoint nearestReachable = npc != null ? Reachable.getNearestReachableTile(npc.getWorldLocation(), 2) : null;
        if (npc == null || nearestReachable == null) {
            if (walkAttempts >= 5 || hopAttempts > 8){
                this.failed = true;
                log.info("Unable to walk to npc! Too many attempts!");
                return false;
            }
            walkAttempts++;
            if (plugin.webWalkTo(location())){
                PUtils.waitCondition(PUtils.random(2500, 3100), () -> !PPlayer.isMoving() && PPlayer.distanceTo(location()) <= 2);
                log.info("Walked to npc");
            } else {
                log.info("Failed webwalk to npc location! (Attempt " + walkAttempts + ")");
            }
            return true;
        }

        if (!PInteraction.npc(npc, "Trade")) {
            log.info("Unable to trade with NPC!");
            this.failed = true;
            return false;
        }

        int distance = (int)Math.round(Reachable.getMap().getDistance(nearestReachable));
        if (distance > 1) PUtils.waitCondition(PUtils.random(800, 1400), PPlayer::isMoving);
        int multiplier = PPlayer.isRunEnabled() ? 300 : 600;
        int timeout = distance * multiplier + (int)PUtils.randomNormal(1900, 2800);
        PUtils.waitCondition(timeout, () -> !PPlayer.isMoving() || PPlayer.location().distanceTo(npc.getWorldLocation()) <= 1);

        if (!PUtils.waitCondition(PUtils.random(1300, 1900), PShopping::isShopOpen)){
            tradeAttempts++;
            log.info("Timed out while waiting for trade window!");
            return true;
        }

        int currentCount = PInventory.getCount(itemName);
        int bought = PShopping.buyItemFromShop(itemName, quantity - currentCount);
        if (bought != 0 ) {
            walkAttempts = 0;
            tradeAttempts = 0;
            return true;
        }
        if (bought < quantity - currentCount){
            PWorldHopper.hop();
            hopAttempts++;
            PUtils.sleepNormal(2000, 3000);
            return true;
        }

        this.isCompleted = true;
        return true;
    };

    public boolean condition() {
        int money = PInventory.getCount("Coins");
        int currentCount = PInventory.getCount(itemName);
        return !isCompleted() && !isFailed() && money > (quantity - currentCount) * price;
    }

    public boolean isCompleted() {
        return this.isCompleted;
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


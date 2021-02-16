package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports;


import kotlin.Pair;
import net.runelite.api.ItemDefinition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportConstants;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportLimit;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportScrolls;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.Requirement;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public enum Teleport {
    ;
    /*TODO:
        ADD TELES
     */
    private int moveCost;
    private RSTile location;
    private Requirement requirement;
    private Action action;
    private TeleportLimit teleportLimit;

    private boolean canUse = true;

    private int failedAttempts = 0;

    Teleport(int moveCost, RSTile location, Requirement requirement, Action action) {
        this.moveCost = moveCost;
        this.location = location;
        this.requirement = requirement;
        this.action = action;
        this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
    }

    Teleport(int moveCost, RSTile location, Requirement requirement, Action action, TeleportLimit limit) {
        this.moveCost = moveCost;
        this.location = location;
        this.requirement = requirement;
        this.action = action;
        this.teleportLimit = limit;
    }

    Teleport(int movecost, TeleportScrolls scroll){
        this.moveCost = movecost;
        this.location = scroll.getLocation();
        this.requirement = () -> inMembersWorld() && scroll.canUse();
        this.action = () -> scroll.teleportTo(false);
        this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
    }


    public int getMoveCost() {
        return moveCost;
    }

    public void setMoveCost(int cost){
        if(this.moveCost == 0)
            return;
        this.moveCost = cost;
    }

    public RSTile getLocation() {
        return location;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public boolean trigger() {
        boolean value = this.action.trigger();
        if(!value){
            failedAttempts++;
            if(failedAttempts > 3){
                canUse = false;
            }
        }
        return value;
    }

    public boolean isAtTeleportSpot(RSTile tile) {
        return tile.distanceTo(location) < 10;
    }

    public static void setMoveCosts(int moveCost){
        Arrays.stream(values()).forEach(t -> t.setMoveCost(moveCost));
    }

    private static List<Teleport> blacklist = new ArrayList<>();

    public static void blacklistTeleports(Teleport... teleports){
        blacklist.addAll(Arrays.asList(teleports));
    }

    public static void clearTeleportBlacklist(){
        blacklist.clear();
    }

    public static List<RSTile> getValidStartingRSTiles() {
        List<RSTile> RSTiles = new ArrayList<>();
        for (Teleport teleport : values()) {

            if (blacklist.contains(teleport) || !teleport.teleportLimit.canCast() ||
                    !teleport.canUse || !teleport.requirement.satisfies()) continue;
            RSTiles.add(teleport.location);
        }
        return RSTiles;
    }

    private interface Action {
        boolean trigger();
    }
    private static boolean value = false;

    private static int lastWorldChecked = -1;

    private static boolean inMembersWorld() {
        return PUtils.isMembersWorld();
    }

    private static Predicate<Pair<WidgetItem, ItemDefinition>> notNotedFilter() {
        throw new NotImplementedException();
    }

    private static boolean itemAction(String name, String... actions) {
        List<Pair<WidgetItem, ItemDefinition>> items = PInventory.getAllItemsWithDefs();
        if (items.size() == 0) {
            return false;
        }

        return PInteraction.item(items.get(0), actions);
    }



    private static boolean teleportWithScrollInterface(Predicate<Pair<WidgetItem, ItemDefinition>> itemFilter, String regex){
        throw new NotImplementedException();
    }

    private static boolean handleScrollInterface(String regex){
        throw new NotImplementedException();
    }

    private static boolean selectSpell(String spellName, String action){
        throw new NotImplementedException();
    }
}
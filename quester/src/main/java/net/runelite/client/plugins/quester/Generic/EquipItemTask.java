package net.runelite.client.plugins.quester.Generic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PathResult;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PathStatus;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.PlayerDetails;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.Point3D;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.Reachable;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;

@Slf4j
public class EquipItemTask implements Task {
    boolean failed;
    private String itemName;
    private Quester plugin;

    public EquipItemTask(Quester plugin, String itemName){
        this.plugin = plugin;
        this.itemName = itemName;
    }

    public String name() {
        return "Equip item " + this.itemName;
    }

    public WorldPoint location() {
        return PPlayer.location();
    }

    public boolean execute() {
        if (PInventory.findEquipmentItem(Filters.Items.nameContains(itemName)) != null) {
            return true;
        }

        PItem item = PInventory.findItem(Filters.Items.nameContains(itemName));
        if (item != null){
            if (!PInteraction.item(item, "Wear", "Wield")){
                log.info("Unable to select wear/wield on item: " + itemName);
                this.failed = true;
                return false;
            }

            if (PUtils.waitCondition((int)PUtils.randomNormal(1300, 1900), () -> PInventory.findEquipmentItem(Filters.Items.nameContains(itemName)) != null)){
                log.info("Successfully equipped " + itemName);
                PUtils.sleepNormal(200, 600);
                return true;
            }
        } else {
            log.info("Unable to find item to equip: " + itemName);
            this.failed = true;
            return false;
        }


        return false;
    };

    public boolean condition() {
        return !isCompleted() && !isFailed() && PInventory.findItem(Filters.Items.nameContains(itemName)) != null;
    }

    public boolean isCompleted() {
        return PInventory.findEquipmentItem(Filters.Items.nameContains(itemName)) != null;
    }

    public boolean isFailed(){
        return this.failed;
    }

    public int getDistance(){
        return 0;
    };
}

package net.runelite.client.plugins.quester.Generic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;

@Slf4j
public class DropAllItemsTask implements Task {
    boolean failed;
    private String itemName;
    private Quester plugin;

    public DropAllItemsTask(Quester plugin, String itemName){
        this.plugin = plugin;
        this.itemName = itemName;
    }

    public String name() {
        return "Drop " + this.itemName;
    }

    public WorldPoint location() {
        return PPlayer.location();
    }

    public boolean execute() {
        PItem item = PInventory.findItem(Filters.Items.nameContains(itemName));
        if (item != null){
            if (!PInteraction.item(item, "Drop")){
                log.info("Unable to select drop on item: " + itemName);
                this.failed = true;
                return false;
            }
            PUtils.sleepNormal(400, 800);
        }
        return false;
    };

    public boolean condition() {
        return !isCompleted() && !isFailed() && PInventory.getCount(itemName) > 0;
    }

    public boolean isCompleted() {
        return PInventory.getCount(itemName) == 0;
    }

    public boolean isFailed(){
        return this.failed;
    }

    public int getDistance(){
        return 0;
    };
}

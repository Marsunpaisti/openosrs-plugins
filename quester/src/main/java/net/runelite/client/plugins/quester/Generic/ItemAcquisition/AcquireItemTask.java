package net.runelite.client.plugins.quester.Generic.ItemAcquisition;

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
import net.runelite.client.plugins.quester.TaskContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AcquireItemTask implements Task, TaskContainer {
    boolean failed;
    private Task handler;
    List<Task> handlers;
    String itemName;
    int quantity;
    boolean isCompleted = false;

    public AcquireItemTask(Quester plugin, String itemName, int quantity){
        this.itemName = itemName;
        this.quantity = quantity;
        this.handlers = new ArrayList<Task>();
        List<Task> generatedHandlers = AcquisitionTasks.getHandlers(plugin, itemName, quantity);
        if (generatedHandlers == null || generatedHandlers.size() == 0) {
            log.error("No handlers were generated for AcquireItemTask: " + itemName);
        } else {
            this.handlers.addAll(generatedHandlers);
        }
    }

    public String name() {
        if (this.handler == null) return "Acquire item: " + itemName;
        return this.handler.name();
    }

    public WorldPoint location() {
        if (this.handler == null) return null;
        return getHandler().location();
    }

    public boolean execute() {
        if (!isCompleted() && getHandler() == null) {
            this.failed = true;
            return false;
        }

        boolean res = getHandler().execute();
        if (getHandler().isCompleted()) this.isCompleted = true;
        return res;
    };

    public Task getHandler(){
        if (this.handler == null || this.handler.isFailed()) {
            Task get = getTask();
            if (get != null) this.handler = get;
        }
        return this.handler;
    }

    public boolean condition() {
        return !isCompleted()
                && !isFailed()
                && getHandler() != null
                && PInventory.getCount(itemName) < quantity;
    }

    public boolean isCompleted() {
        return this.isCompleted || PInventory.getCount(itemName) >= quantity;
    }

    public boolean isFailed(){
        return this.failed;
    }

    public int getDistance(){
        return getHandler().getDistance();
    };

    @Override
    public Task getTask() {
        Task ret = handlers.stream()
                .filter(t -> !t.isFailed() && !t.isCompleted() && t.condition())
                .min((a, b) -> a.getDistance() - b.getDistance())
                .orElse(null);
        return ret;
    }

    @Override
    public void addTask(Task t) {
        handlers.add(t);
    }
}

package net.runelite.client.plugins.quester.Generic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.quester.Task;
import net.runelite.client.plugins.quester.TaskContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CompositeTask implements Task, TaskContainer {
    List<Task> tasks;
    public boolean isCompleted;

    public CompositeTask(Task ...tasks){
        this.tasks = new ArrayList<Task>();
        Collections.addAll(this.tasks, tasks);
    }

    @Override
    public String name() {
        if (getTask() == null) return "CompositeTask (" + this.tasks.stream().map(t -> t.name()).collect(Collectors.joining(", ")) + ")";
        return getTask().name();
    }

    @Override
    public WorldPoint location() {
        if (getTask() == null) return null;
        return getTask().location();
    }

    @Override
    public boolean execute() {
        if (getTask() == null) return false;
        return getTask().execute();
    }

    @Override
    public boolean condition() {
        return getTask() != null && getTask().condition();
    }

    @Override
    public boolean isCompleted() {
        return this.isCompleted || tasks.stream().allMatch(t -> t.isCompleted());
    }

    @Override
    public boolean isFailed() {
        return tasks.stream().anyMatch(t -> t.isFailed());
    }

    public void addTask(Task t){
        this.tasks.add(t);
    }

    @Override
    public int getDistance() {
        // Return first nonzero distance
        // (equip tasks are 0 distance for example)
        for (Task t : tasks){
            int distance = t.getDistance();
            if (distance > 0) return distance;
        }
        return 0;
    }

    @Override
    public Task getTask() {
        Task ret = null;
        for (Task t : tasks){
            if (!t.isFailed() && !t.isCompleted()){
                if (!t.condition()) {
                    log.info("Next task in CompositeTask cannot be accomplished!");
                    log.info("Next task would have been " + t.name());
                    return null;
                }
                ret = t;
                break;
            }
        }

        return ret;
    }

}

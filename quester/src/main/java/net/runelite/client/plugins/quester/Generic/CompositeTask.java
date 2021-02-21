package net.runelite.client.plugins.quester.Generic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.quester.Task;
import net.runelite.client.plugins.quester.TaskContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CompositeTask implements Task, TaskContainer {
    List<Task> tasks;

    public CompositeTask(Task ...tasks){
        this.tasks = new ArrayList<Task>();
        Collections.addAll(this.tasks, tasks);
    }

    @Override
    public String name() {
        return getTask().name();
    }

    @Override
    public WorldPoint location() {
        return getTask().location();
    }

    @Override
    public boolean execute() {
        return getTask().execute();
    }

    @Override
    public boolean condition() {
        return getTask().condition();
    }

    @Override
    public boolean isCompleted() {
        return tasks.stream().allMatch(t -> t.isCompleted());
    }

    @Override
    public boolean isFailed() {
        return tasks.stream().anyMatch(t -> t.isFailed());
    }

    @Override
    public int getDistance() {
        // Return first nonzero distance
        // (equip tasks are 0 distance for example)
        for (Task t : tasks){
            int distance = t.getDistance();
            if (distance != 0) return distance;
        }
        return 0;
    }

    @Override
    public Task getTask() {
        Task ret = null;
        for (Task t : tasks){
            if (!t.isFailed() && !t.isCompleted() && t.condition()){
                ret = t;
                break;
            }
        }

        return ret;
    }

}

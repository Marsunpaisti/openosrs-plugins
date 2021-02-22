package net.runelite.client.plugins.quester;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class Quest implements TaskContainer {
    List<Task> tasks;

    public Quest(Task ...tasks){
        this.tasks = new ArrayList<Task>();
        Collections.addAll(this.tasks, tasks);
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

        if (!isComplete() && !isFailed() && ret == null){
            log.info("Warning: Quest " + getName() + " is not completed/failed, but getTask() returned null");
            Task next = tasks.stream().filter(t -> !t.isFailed() && !t.isCompleted() && !t.condition()).findFirst().orElse(null);
            log.info("Next task would have been: " + (next != null ? next.name() : "NULL"));
        }
        return ret;
    }

    public abstract String getName();

    public boolean isFailed(){
        return this.tasks.stream().anyMatch(Task::isFailed);
    }

    public boolean isComplete(){
        return this.tasks.get(tasks.size() - 1).isCompleted();
    }

    public void addTask(Task t){
        this.tasks.add(t);
    }

    public int currentDistance(){
        if (getTask() == null) return Integer.MAX_VALUE;
        return getTask().getDistance();
    }
}

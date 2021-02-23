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
public class AnyOrderTask implements Task, TaskContainer {
    List<Task> tasks;

    public AnyOrderTask(Task ...tasks){
        this.tasks = new ArrayList<Task>();
        Collections.addAll(this.tasks, tasks);
    }

    @Override
    public String name() {
        if (getTask() == null) return "AnyOrderTask (" + this.tasks.stream().map(t -> t.name()).collect(Collectors.joining(", ")) + ")";
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
        return tasks.stream().allMatch(t -> t.isCompleted());
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
        Task t = getTask();
        if (t == null) return Integer.MAX_VALUE;
        return t.getDistance();
    }

    @Override
    public Task getTask() {
        Task ret = tasks.stream()
                .filter(t -> !t.isFailed() && !t.isCompleted() && t.condition())
                .sorted((a,b) -> a.getDistance() - b.getDistance())
                .findFirst()
                .orElse(null);
        return ret;
    }
}

package net.runelite.client.plugins.quester;

import lombok.extern.slf4j.Slf4j;

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
            if (!t.isFailed() && !t.isCompleted()){
                if (!t.condition()) {
                    log.info("Next task in Quest cannot be accomplished!");
                    log.info("Next task would have been " + t.name());
                    return null;
                }
                ret = t;
                break;
            }
        }

        return ret;
    }

    public abstract String getName();

    public boolean isFailed(){
        return this.tasks.stream().anyMatch(Task::isFailed);
    }

    public boolean isCompleted(){
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

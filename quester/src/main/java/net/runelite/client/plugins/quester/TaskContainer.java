package net.runelite.client.plugins.quester;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TaskContainer {
    @Getter
    List<Task> tasks = new ArrayList<Task>();
    Task currentTask;

    public TaskContainer(Task ...tasks){
        this.tasks.addAll(Arrays.asList(tasks));
    }

    public Task getTask(){
        if (currentTask != null && !currentTask.isComplete() && currentTask.isSticky()) return currentTask;

        for (Task t : tasks){
            if (t.condition() && !t.isComplete() && !t.isFailed()) {
                currentTask = t;
                return t;
            }
        }
        return null;
    }

    public Task getLastTask(){
        if (tasks.size() == 0) return null;
        return tasks.get(tasks.size() - 1);
    }
}

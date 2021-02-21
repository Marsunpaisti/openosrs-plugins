package net.runelite.client.plugins.quester.RestlessGhost;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Task;
import net.runelite.client.plugins.quester.TaskContainer;

public class RestlessGhostTask extends Task {
    TaskContainer subTasks = new TaskContainer(
            new StartRestlessGhostTask(),
            new TalkToNpcTask("Prayer tutor", new WorldPoint(3241, 3206, 0), new String[]{"What is prayer useful for", "No"})
    );
    @Override
    public String name() {
        return "Restless Ghost: " + (subTasks.getTask() != null ? subTasks.getTask().name() : "NULL");
    }

    @Override
    public WorldPoint location() {
        return subTasks.getTask().location();
    }

    @Override
    public boolean execute() {
        return subTasks.getTask().execute();
    }

    public boolean condition() {
        return subTasks.getTask() != null;
    }

    @Override
    public boolean isComplete() {
        return subTasks.getLastTask().isComplete();
    }

    @Override
    public boolean isSticky(){
        return subTasks.getTask().isSticky();
    }

    @Override
    public boolean isFailed(){
        return subTasks.getTasks().stream().anyMatch(Task::isFailed);
    }
}

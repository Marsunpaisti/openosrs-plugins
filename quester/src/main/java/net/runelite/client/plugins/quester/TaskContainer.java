package net.runelite.client.plugins.quester;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public interface TaskContainer {
    public Task getTask();
    public void addTask(Task t);
}

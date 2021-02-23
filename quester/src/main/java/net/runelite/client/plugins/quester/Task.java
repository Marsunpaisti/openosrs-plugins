package net.runelite.client.plugins.quester;

import net.runelite.api.coords.WorldPoint;

public interface Task {
    String name();
    WorldPoint location();
    boolean execute();
    boolean condition();
    boolean isCompleted();
    boolean isFailed();
    int getDistance();
}

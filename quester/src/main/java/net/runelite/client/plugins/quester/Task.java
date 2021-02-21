package net.runelite.client.plugins.quester;

import net.runelite.api.coords.WorldPoint;

public interface Task {
    public abstract String name();
    public abstract WorldPoint location();
    public abstract boolean execute();
    public abstract boolean condition();
    public abstract boolean isComplete();
    public boolean isFailed();
    public int getDistance();
}

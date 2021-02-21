package net.runelite.client.plugins.quester;

import net.runelite.api.coords.WorldPoint;

public abstract class Task {
    public abstract String name();
    public abstract WorldPoint location();
    public abstract boolean execute();
    public abstract boolean condition();
    public abstract boolean isComplete();
    public boolean isSticky(){
        return false;
    }
    public boolean isFailed(){
        return false;
    }
}

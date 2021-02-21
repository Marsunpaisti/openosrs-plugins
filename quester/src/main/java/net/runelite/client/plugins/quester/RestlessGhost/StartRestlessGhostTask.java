package net.runelite.client.plugins.quester.RestlessGhost;

import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PVars;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Task;

public class StartRestlessGhostTask extends Task {
    TalkToNpcTask talkTask = new TalkToNpcTask("Father Aereck", new WorldPoint(3241, 3206, 0), new String[]{"a quest", "Yes"});

    @Override
    public String name() {
        return "Start quest";
    }

    @Override
    public WorldPoint location() {
        return this.talkTask.location();
    }

    @Override
    public boolean execute() {
        return this.talkTask.execute();
    }

    @Override
    public boolean condition() {
        return PVars.getVarp(VarPlayer.QUEST_THE_RESTLESS_GHOST) == 0;
    }

    @Override
    public boolean isComplete() {
        return PVars.getVarp(VarPlayer.QUEST_THE_RESTLESS_GHOST) == 1;
    }

    @Override
    public boolean isFailed(){
        return this.talkTask.isFailed();
    }
}

package net.runelite.client.plugins.quester.RestlessGhost;

import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PVars;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.*;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;

public class StartRestlessGhostTask implements Task {
    TalkToNpcTask talkTask;

    public StartRestlessGhostTask(Quester plugin){
        talkTask = new TalkToNpcTask(plugin, "Father Aereck", new WorldPoint(3241, 3206, 0), "Talk-to", new String[]{"a quest", "Yes"});
    }

    public String name() {
        return "Start quest";
    }

    public WorldPoint location() {
        return this.talkTask.location();
    }

    public boolean execute() {
        return this.talkTask.execute();
    }

    public boolean condition() {
        return PVars.getVarp(VarPlayer.QUEST_THE_RESTLESS_GHOST) == 0;
    }

    public boolean isComplete() {
        return PVars.getVarp(VarPlayer.QUEST_THE_RESTLESS_GHOST) == 1;
    }

    public boolean isFailed(){
        return this.talkTask.isFailed();
    }

    public int getDistance(){
        return talkTask.getDistance();
    };
}

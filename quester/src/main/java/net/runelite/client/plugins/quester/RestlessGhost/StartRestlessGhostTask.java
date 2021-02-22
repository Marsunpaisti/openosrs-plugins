package net.runelite.client.plugins.quester.RestlessGhost;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.PVars;
import net.runelite.client.plugins.paistisuite.api.PrayerMap;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;

import java.util.Arrays;
import java.util.Map;

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
        return !isFailed() && !isCompleted();
    }

    public boolean isCompleted() {
        return PVars.getVarp(107) >= 1;
    }

    public boolean isFailed(){
        return this.talkTask.isFailed();
    }

    public int getDistance(){
        return talkTask.getDistance();
    };
}

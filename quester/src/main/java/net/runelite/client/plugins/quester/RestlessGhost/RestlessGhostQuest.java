package net.runelite.client.plugins.quester.RestlessGhost;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.quester.Generic.EquipItemTask;
import net.runelite.client.plugins.quester.Generic.LinkedTask;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Quest;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;
import net.runelite.client.plugins.quester.TaskContainer;

public class RestlessGhostQuest extends Quest{

    public RestlessGhostQuest(Quester plugin){
        super(new StartRestlessGhostTask(plugin),
                new TalkToNpcTask(
                        plugin,
                        "Father Urhney",
                        new WorldPoint(3147, 3174, 0),
                        "Talk-to",
                        new String[]{"Father Aereck sent me", "ghost haunting"},
                        new String[]{"lost the Amulet"}),
                new LinkedTask(
                        new EquipItemTask(plugin, "Ghostspeak amulet")
                )
        );
    }

    @Override
    public String getName() {
        return "Restless Ghost";
    }
}

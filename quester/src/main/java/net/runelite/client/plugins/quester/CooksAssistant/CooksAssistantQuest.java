package net.runelite.client.plugins.quester.CooksAssistant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.Filters;
import net.runelite.client.plugins.paistisuite.api.PObjects;
import net.runelite.client.plugins.paistisuite.api.PVars;
import net.runelite.client.plugins.quester.Generic.*;
import net.runelite.client.plugins.quester.Generic.ItemAcquisition.AcquireItemTask;
import net.runelite.client.plugins.quester.Generic.ItemAcquisition.AcquisitionTasks;
import net.runelite.client.plugins.quester.Quest;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.RestlessGhost.StartRestlessGhostTask;

import java.util.Arrays;

@Slf4j
public class CooksAssistantQuest extends Quest{
    @Getter
    @AllArgsConstructor
    private enum CooksAssistantStage {
        NOT_STARTED(0),
        COMPLETED(1);
        private final int varp;
        public static CooksAssistantStage fromVarp(int varp) {
            return Arrays.stream(values()).filter(v -> v.getVarp() == varp).findFirst().orElse(null);
        }
    }

    public CooksAssistantStage getQuestStage(){
        int questVarp = PVars.getVarp(29);
        return CooksAssistantStage.fromVarp(questVarp);
    }


    public CooksAssistantQuest(Quester plugin){
        super();
        addTask(new AnyOrderTask(
                    new CompositeTask(
                            new AcquireItemTask(plugin, "Bucket", 1),
                            new AcquireItemTask(plugin, "Bucket of Milk", 1)
                    ),
                    new AcquireItemTask(plugin, "Pot of flour", 1),
                    new AcquireItemTask(plugin, "Egg", 1)
        ));
    }

    @Override
    public String getName() {
        return "Cook's Assistant";
    }
}

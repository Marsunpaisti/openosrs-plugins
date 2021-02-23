package net.runelite.client.plugins.quester.SheepShearer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PVars;
import net.runelite.client.plugins.quester.Generic.AnyOrderTask;
import net.runelite.client.plugins.quester.Generic.CompositeTask;
import net.runelite.client.plugins.quester.Generic.ItemAcquisition.AcquireItemTask;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Quest;
import net.runelite.client.plugins.quester.Quester;

import java.util.Arrays;

@Slf4j
public class SheepShearerQuest extends Quest{
    @Getter
    @AllArgsConstructor
    private enum SheepShearerStage {
        NOT_STARTED(0),
        IN_PROGRESS(1),
        COMPLETED(3);
        private final int varp;
        public static SheepShearerStage fromVarp(int varp) {
            return Arrays.stream(values()).filter(v -> v.getVarp() == varp).findFirst().orElse(null);
        }
    }

    public SheepShearerStage getQuestStage(){
        int questVarp = PVars.getVarp(179);
        if (questVarp == 0) return SheepShearerStage.NOT_STARTED;
        if (questVarp > 0 && questVarp < 21) return SheepShearerStage.IN_PROGRESS;
        if (questVarp >= 21) return SheepShearerStage.COMPLETED;
        return null;
    }

    public SheepShearerQuest(Quester plugin){
        super();
        addTask(
                new CompositeTask(
                        new AcquireItemTask(plugin, "Ball of wool", 20),
                        new TalkToNpcTask(plugin,
                                "Fred the Farmer",
                                new WorldPoint(3190,3273, 0),
                                "Talk-to",
                                new String[]{"quest", "Yes"},
                                null)
                )
        );
    }

    @Override
    public boolean isCompleted() {
        return getQuestStage().getVarp() == SheepShearerStage.COMPLETED.getVarp();
    }

    @Override
    public String getName() {
        return "Sheep Shearer";
    }
}

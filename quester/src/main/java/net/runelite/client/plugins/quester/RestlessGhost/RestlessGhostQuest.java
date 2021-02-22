package net.runelite.client.plugins.quester.RestlessGhost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.Filters;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PObjects;
import net.runelite.client.plugins.paistisuite.api.PVars;
import net.runelite.client.plugins.quester.Generic.EquipItemTask;
import net.runelite.client.plugins.quester.Generic.CompositeTask;
import net.runelite.client.plugins.quester.Generic.InteractWithObjectTask;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Quest;
import net.runelite.client.plugins.quester.Quester;

import java.util.Arrays;

@Slf4j
public class RestlessGhostQuest extends Quest{
    @Getter
    @AllArgsConstructor
    private enum RestlessGhostStage {
        NOT_STARTED(0),
        STARTED(1),
        HAVE_GHOSTSPEAK(2),
        TALKED_TO_GHOST(3),
        HAVE_SKULL(4),
        COMPLETED(5);
        private final int varp;
        public static RestlessGhostStage fromVarp(int varp) {
            return Arrays.stream(values()).filter(v -> v.getVarp() == varp).findFirst().orElse(null);
        }
    }

    public RestlessGhostStage getQuestStage(){
        int questVarp = PVars.getVarp(107);
        //VarBit 2130 - 0 no skull, 1 have skull
        if (questVarp == 4 && PVars.getVarbit(2130) == 0){
            return RestlessGhostStage.TALKED_TO_GHOST;
        }
        return RestlessGhostStage.fromVarp(questVarp);
    }


    public RestlessGhostQuest(Quester plugin){
        super();
        addTask(new StartRestlessGhostTask(plugin));
        addTask(new TalkToNpcTask(
                plugin,
                "Father Urhney",
                new WorldPoint(3147, 3174, 0),
                "Talk-to",
                new String[]{"Father Aereck sent me", "ghost haunting"},
                new String[]{"lost the Amulet"}){
            @Override
            public boolean isCompleted(){
                return getQuestStage().getVarp() >= 2;
            }
        });
        addTask(new CompositeTask(
                    new EquipItemTask(plugin, "Ghostspeak amulet"),
                    new InteractWithObjectTask(
                            plugin,
                            "Coffin",
                            new String[]{"Open", "Close"},
                            new WorldPoint(3248, 3194, 0),
                            () -> PObjects.findNPC(Filters.NPCs.nameContains("Restless ghost")) != null
                    ),
                    new TalkToNpcTask(
                            plugin,
                            "Restless ghost",
                            new WorldPoint(3248, 3194, 0),
                            "Talk-to",
                            new String[]{""},
                            new String[]{"tell me what the"}
                    )
        ){
            @Override
            public boolean isCompleted(){
                return getQuestStage().getVarp() >= 3;
            }
        });
        addTask(new InteractWithObjectTask(
                plugin,
                "Altar",
                new String[]{"Search"},
                new WorldPoint(3116, 9565, 0),
                () -> getQuestStage().getVarp() >= RestlessGhostStage.HAVE_SKULL.getVarp()
        ){
            @Override
            public boolean isCompleted(){
                return getQuestStage().getVarp() >= RestlessGhostStage.HAVE_SKULL.getVarp();
            }
        });
        addTask(new CompositeTask(
                    new EquipItemTask(plugin, "Ghostspeak amulet"),
                    new InteractWithObjectTask(
                            plugin,
                            "Coffin",
                            new String[]{"Open", "Close"},
                            new WorldPoint(3248, 3194, 0),
                            () -> PObjects.findNPC(Filters.NPCs.nameContains("Restless ghost")) != null
                    ),
                    new TalkToNpcTask(
                            plugin,
                            "Restless ghost",
                            new WorldPoint(3248, 3194, 0),
                            "Talk-to",
                            new String[]{""},
                            null
                    ),
                    new InteractWithObjectTask(
                            plugin,
                            "Coffin",
                            new String[]{"Open", "Close"},
                            new WorldPoint(3248, 3194, 0),
                            () -> getQuestStage().getVarp() >= RestlessGhostStage.COMPLETED.getVarp()
                    )
        ));
    }

    @Override
    public String getName() {
        return "Restless Ghost";
    }
}

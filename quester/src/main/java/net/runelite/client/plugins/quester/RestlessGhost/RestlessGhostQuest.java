package net.runelite.client.plugins.quester.RestlessGhost;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.Filters;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PObjects;
import net.runelite.client.plugins.quester.Generic.EquipItemTask;
import net.runelite.client.plugins.quester.Generic.CompositeTask;
import net.runelite.client.plugins.quester.Generic.InteractWithObjectTask;
import net.runelite.client.plugins.quester.Generic.TalkToNpcTask;
import net.runelite.client.plugins.quester.Quest;
import net.runelite.client.plugins.quester.Quester;

public class RestlessGhostQuest extends Quest{
    public RestlessGhostQuest(Quester plugin){
        /*
        openCoffinTask = new InteractWithObjectTask(
                null,
                "Coffin",
                new String[]{"Open", "Search"},
                new WorldPoint(3248, 3194, 0),
                () -> PObjects.findNPC(Filters.NPCs.nameContains("Restless Ghost")) != null
        );

        talkToGhostTask = new TalkToNpcTask(
                plugin,
                "Restless Ghost",
                new WorldPoint(3248, 3194, 0),
                "Talk-to",
                new String[]{"tell me what the"},
                new String[]{""}
        );

        talkToFatherUrnheyTask = new TalkToNpcTask(
                plugin,
                "Father Urhney",
                new WorldPoint(3147, 3174, 0),
                "Talk-to",
                new String[]{"Father Aereck sent me", "ghost haunting"},
                new String[]{"lost the Amulet"});
         */

        super(
                new StartRestlessGhostTask(plugin),
                new TalkToNpcTask(
                        plugin,
                        "Father Urhney",
                        new WorldPoint(3147, 3174, 0),
                        "Talk-to",
                        new String[]{"Father Aereck sent me", "ghost haunting"},
                        new String[]{"lost the Amulet"}){
                        @Override
                        public boolean isCompleted(){
                            return PInventory.findEquipmentItem(Filters.Items.nameContains("Ghostspeak"))  != null
                                    || PInventory.findItem(Filters.Items.nameContains("Ghostspeak")) != null;
                        }
                },
                new CompositeTask(
                        new EquipItemTask(plugin, "Ghostspeak amulet"),
                        new InteractWithObjectTask(
                                null,
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
                )
        );
    }

    @Override
    public String getName() {
        return "Restless Ghost";
    }
}

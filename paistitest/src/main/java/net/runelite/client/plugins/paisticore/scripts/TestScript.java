package net.runelite.client.plugins.paisticore.scripts;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.ItemDefinition;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paisticore.PaistiCore;
import net.runelite.client.plugins.paisticore.api.PInteraction;
import net.runelite.client.plugins.paisticore.api.PInventory;
import net.runelite.client.plugins.paisticore.api.PPlayer;
import net.runelite.client.plugins.paisticore.api.PUtils;
import net.runelite.client.plugins.paisticore.framework.PScript;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class TestScript extends PScript {
    @Override
    protected void loop() {
        log.info("Loop start");

        if (PPlayer.get().getAnimation() != -1) return;

        Collection<Pair<WidgetItem, ItemDefinition>> items = PInventory.getAllItemsWithDefs();
        Pair<WidgetItem, ItemDefinition> tinderbox = items
                .stream()
                .filter(item -> item.getSecond().getName().equalsIgnoreCase("Tinderbox"))
                .collect(Collectors.toList())
                .get(0);
        Pair<WidgetItem, ItemDefinition> logs = items
                .stream()
                .filter(item -> item.getSecond().getName().equalsIgnoreCase("Logs"))
                .collect(Collectors.toList())
                .get(0);

        if (tinderbox == null){
            PUtils.sendGameMessage("Tinderbox not found");
            return;
        }
        if (logs == null){
            PUtils.sendGameMessage("Logs not found");
            return;
        }
        PUtils.sendGameMessage("Found tinderbox and logs");

        PInteraction.useItemOnItem(tinderbox.getFirst(), logs.getFirst());


    }


    @Override
    protected void onStart() {
        log.info("TestScript started");
    }

    @Override
    protected void onStop() {
        log.info("TestScript stopped");
    }
}

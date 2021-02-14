package net.runelite.client.plugins.paistisuite.scripts.firemaker;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemDefinition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.PScript;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class Firemaker extends PScript {
    @Override
    protected void loop() {
        PUtils.sleepNormal(3000, 5000);
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

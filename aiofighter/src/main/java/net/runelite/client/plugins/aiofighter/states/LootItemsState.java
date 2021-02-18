package net.runelite.client.plugins.aiofighter.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.client.plugins.aiofighter.AIOFighter;
import net.runelite.client.plugins.paistisuite.api.PGroundItems;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.types.PGroundItem;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class LootItemsState extends State {
    public LootItemsState(AIOFighter plugin) {
        super(plugin);
    }

    @Override
    public boolean condition() {
        if (plugin.fightEnemiesState.inCombat()) {
            return false;
        }

        return getNextLootableItem() != null;
    }

    @Override
    public String getName() {
        return "LootItemsState";
    }

    @Override
    public void loop(){
        super.loop();

        PGroundItem target = getNextLootableItem();
        if (target != null){
            PUtils.sleepNormal(100, 700);
            log.info("Looting " + target.getName() + " price: " + target.getPricePerSlot());

            if (plugin.isStopRequested()) return;
            if (PInteraction.groundItem(target, "Take"))
            {
                if (plugin.isStopRequested()) return;
                if (PPlayer.location().distanceTo(target.getLocation()) >= 1){
                    PUtils.waitCondition(PUtils.random(1400, 2200), PPlayer::isMoving);
                }
                PUtils.waitCondition(PUtils.random(4000, 6000), () -> !PPlayer.isMoving());
                PUtils.sleepNormal(100, 700);
            }
        }
    }

    public Comparator<PGroundItem> lootPrioritySorter = (a, b) -> {
        return (int)Math.round(PPlayer.distanceTo(a.getLocation())) - (int)Math.round(PPlayer.distanceTo(b.getLocation()));
    };

    public PGroundItem getNextLootableItem(){
        return getLootableItems()
                .stream()
                .sorted(lootPrioritySorter)
                .findFirst()
                .orElse(null);
    }

    public List<PGroundItem> getLootableItems(){
        return PGroundItems.findGroundItems(plugin.validLootFilter);
    }
}

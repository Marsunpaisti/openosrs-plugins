package net.runelite.client.plugins.aiofighter.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.client.plugins.aiofighter.AIOFighter;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.types.PGroundItem;
import net.runelite.client.plugins.paistisuite.api.types.PItem;

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
        if (plugin.fightEnemiesState.getCurrentTarget() != null) {
            return false;
        }

        PGroundItem nextLoot = getNextLootableItem();
        return nextLoot != null && haveSpaceForItem(nextLoot);
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
            if (PInventory.getEmptySlots() == 0 && plugin.eatFoodForLoot){
                List<PItem> foodItems = PInventory.findAllItems(plugin.validFoodFilter);
                int quantityBefore = foodItems.size();
                if (quantityBefore == 0) return;
                if (PInteraction.item(foodItems.get(0), "Eat")){
                    log.info("Eating food to make space for loot");
                    PUtils.waitCondition(PUtils.random(700, 1300), () -> PInventory.findAllItems(plugin.validFoodFilter).size() < quantityBefore);
                }
            }
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

    public boolean haveSpaceForItem(PGroundItem item){
        if (PInventory.getEmptySlots() > 0){
            return true;
        }
        if (item.isStackable() && PInventory.findItem(Filters.Items.idEquals(item.getId())) != null){
            return true;
        }

        if (plugin.eatFoodForLoot && PInventory.findItem(plugin.validFoodFilter) != null) {
            return true;
        }

        return false;
    }

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

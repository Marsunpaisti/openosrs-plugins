package net.runelite.client.plugins.paistisuite.api;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.PaistiSuite;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class PInventory
{
    public static ItemDefinition getItemDef(WidgetItem item){
        if (item == null) return null;

        ItemDefinition def = null;
        try {
            if (!PUtils.getClient().isClientThread()) {
                def = PaistiSuite.getInstance().clientExecutor.scheduleAndWait(() ->  PaistiSuite.getInstance().itemManager.getItemDefinition(item.getId()), "getItemDef");
            } else {
                def =  PaistiSuite.getInstance().itemManager.getItemDefinition(item.getId());
            }
        } catch (Exception e) {
            log.error("Error in getItemDef: " + e);
        }

        return def;
    }

    private static Future<ItemDefinition> getFutureItemDef(WidgetItem item){
        if (item == null) return null;

        return PaistiSuite.getInstance().clientExecutor.schedule(() ->  PaistiSuite.getInstance().itemManager.getItemDefinition(item.getId()), "getItemDef");
    }

    public static boolean isFull()
    {
        return getEmptySlots() <= 0;
    }

    public static boolean isEmpty()
    {
        return getEmptySlots() >= 28;
    }

    public static int getEmptySlots()
    {
        Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget != null)
        {
            return 28 - inventoryWidget.getWidgetItems().size();
        }
        else
        {
            return -1;
        }
    }

    public static Collection<WidgetItem> getAllItems()
    {
        Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget != null)
        {
            return inventoryWidget.getWidgetItems();
        }
        return null;
    }

    public static List<Pair<WidgetItem, ItemDefinition>> getAllItemsWithDefs()
    {
        Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget != null)
        {
            Collection<WidgetItem> widgetItems = inventoryWidget.getWidgetItems();
            List<Pair<WidgetItem, ItemDefinition>> pItems = null;
            if (PUtils.getClient().isClientThread()) {
                pItems = widgetItems.stream().map(wi -> new Pair<WidgetItem, ItemDefinition>(wi, getItemDef(wi))).collect(Collectors.toList());
            } else {
                try {
                    List<Pair<WidgetItem, Future<ItemDefinition>>> futures = widgetItems
                            .stream()
                            .map(wi -> new Pair<WidgetItem, Future<ItemDefinition>>(wi, getFutureItemDef(wi)))
                            .collect(Collectors.toList());

                    pItems = futures
                            .stream()
                            .map(pair -> {
                                try {
                                    return new Pair<WidgetItem, ItemDefinition>(pair.component1(), pair.component2().get());
                                } catch (InterruptedException | ExecutionException e) {
                                    log.error(e.toString());
                                    e.printStackTrace();
                                }
                                return null;
                            })
                            .collect(Collectors.toList());

                } catch (Exception e){
                    log.error("Error in getPItems: " + e);
                }
            }

            return pItems;
        }
        return null;
    }

    public static Pair<WidgetItem, ItemDefinition> findItem(Predicate<Pair<WidgetItem, ItemDefinition>> filter){
        return getAllItemsWithDefs()
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }


    public static List<Pair<WidgetItem, ItemDefinition>> findAllItems(Predicate<Pair<WidgetItem, ItemDefinition>> filter){
        return getAllItemsWithDefs()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static List<Item> getEquippedItems(){
        List<Item> equipped = new ArrayList<>();
        Item[] items = null;
        if (PUtils.getClient().isClientThread()) {
            items = PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT).getItems();
        } else {
            try {
                items = PaistiSuite.getInstance().clientExecutor.scheduleAndWait(() -> {
                    return PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT).getItems();
                }, "getEquippedItems");
            } catch (Exception e){
                log.error("Error in getEquippedItems: " + e.toString());
            }
        }

        if (items == null) return equipped;
        for (Item item : items)
        {
            if (item.getId() == -1 || item.getId() == 0)
            {
                continue;
            }
            equipped.add(item);
        }
        return equipped;
    }

    public static int getEquippedCount(int equipmentId){
        int count = 0;
        List<Item> equipment = getEquippedItems();

        for (Item i : equipment){
            if (i.getId() == equipmentId) {
                count += i.getQuantity();
            }
        }

        return count;
    }
}

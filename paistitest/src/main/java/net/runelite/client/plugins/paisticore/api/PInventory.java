package net.runelite.client.plugins.paisticore.api;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.queries.InventoryItemQuery;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paisticore.PaistiCore;
import net.runelite.client.plugins.paisticore.api.PUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class PInventory
{
    public static ItemDefinition getItemDef(WidgetItem item){
        if (item == null) return null;

        ItemDefinition def = null;
        try {
            if (!PUtils.getClient().isClientThread()) {
                def = PaistiCore.getInstance().clientExecutor.scheduleAndWait(() ->  PaistiCore.getInstance().itemManager.getItemDefinition(item.getId()), "getItemDef");
            } else {
                def =  PaistiCore.getInstance().itemManager.getItemDefinition(item.getId());
            }
        } catch (Exception e) {
            log.error("Error in getItemDef: " + e);
        }

        return def;
    }

    public static Future<ItemDefinition> getFutureItemDef(WidgetItem item){
        if (item == null) return null;

        return PaistiCore.getInstance().clientExecutor.schedule(() ->  PaistiCore.getInstance().itemManager.getItemDefinition(item.getId()), "getItemDef");
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

    public static Collection<Pair<WidgetItem, ItemDefinition>> getAllItemsWithDefs()
    {
        Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget != null)
        {
            Collection<WidgetItem> widgetItems = inventoryWidget.getWidgetItems();
            Collection<Pair<WidgetItem, ItemDefinition>> pItems = null;
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

}

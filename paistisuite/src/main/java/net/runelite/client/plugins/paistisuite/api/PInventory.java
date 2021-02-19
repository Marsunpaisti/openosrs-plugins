package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.types.PItem;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class PInventory
{
    public static ItemDefinition getItemDef(WidgetItem item){
        return PUtils.clientOnly(() -> PaistiSuite.getInstance().itemManager.getItemDefinition(item.getId()), "getItemDef");
    }

    public static ItemDefinition getItemDef(Item item){
        return PUtils.clientOnly(() -> PaistiSuite.getInstance().itemManager.getItemDefinition(item.getId()), "getItemDef");
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

    public static Integer getEmptySlots()
    {
        return PUtils.clientOnly(() -> {
            Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
            if (inventoryWidget != null)
            {
                return 28 - inventoryWidget.getWidgetItems().size();
            }
            else
            {
                return -1;
            }
        }, "getEmptySlots");
    }
    /*
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
    */

    public static List<PItem> getAllItems(){
       return PUtils.clientOnly(() -> {
            Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
            if (inventoryWidget == null) return null;
            Collection<WidgetItem> widgetItems = inventoryWidget.getWidgetItems();
            List<PItem> pItems = widgetItems
                    .stream()
                    .map(PItem::new)
                    .collect(Collectors.toList());
            return pItems;
        }, "getAllPItems");
    }

    public static int getCount(String name){
        int count = 0;
        List<PItem> items = getAllItems();
        for (PItem i : items){
            if (i.getDefinition().getName().equals(name)) {
                count += i.getQuantity();
            }
        }
        return count;
    }

    public static List<PItem> findAllItems(Predicate<PItem> filter){
        return getAllItems()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static PItem findItem(Predicate<PItem> filter){
        return getAllItems()
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public static List<PItem> getEquipmentItems(){
        return PUtils.clientOnly(() -> {
            ItemContainer container = PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT);
            if (container == null) return null;
            Item[] eqitems = PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT).getItems();
            List<PItem> equippedPItems = new ArrayList<PItem>();
            int slot = 0;
            for (Item i : eqitems){
                if (i.getId() != -1) equippedPItems.add(new PItem(i, slot));
                slot++;
            }
            return equippedPItems;
        }, "getEquippedPItems");
    }

    public static List<PItem> findAllEquipmentItems(Predicate<PItem> filter){
        List<PItem> eq = getEquipmentItems();
        if (eq == null) return new ArrayList<PItem>();
        return eq
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static PItem findEquipmentItem(Predicate<PItem> filter){
        List<PItem> eq = getEquipmentItems();
        if (eq == null) return null;
        return eq
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public static List<Item> legacyGetEquipmentItems(){
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

    public static int getEquipmentCount(int equipmentId){
        int count = 0;
        List<Item> equipment = legacyGetEquipmentItems();

        for (Item i : equipment){
            if (i.getId() == equipmentId) {
                count += i.getQuantity();
            }
        }

        return count;
    }
}

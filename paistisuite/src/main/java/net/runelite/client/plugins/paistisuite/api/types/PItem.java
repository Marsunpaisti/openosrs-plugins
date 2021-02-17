package net.runelite.client.plugins.paistisuite.api.types;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemDefinition;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;

@Slf4j
public class PItem {
    public WidgetItem widgetItem;
    public Widget equipmentWidget;
    private Item item;
    public ItemDefinition itemDefinition;
    public boolean isEquipmentItem;
    private String slotName;
    private int quantity;
    private int id;

    public PItem(WidgetItem widgetItem, ItemDefinition itemDefinition){
        this.isEquipmentItem = false;
        this.widgetItem = widgetItem;
        this.itemDefinition = itemDefinition;
    }

    public PItem(WidgetItem widgetItem){
        this.isEquipmentItem = false;
        this.widgetItem = widgetItem;
        this.itemDefinition = PInventory.getItemDef(widgetItem);
    }

    public PItem(Item item, int slot) {
        this.isEquipmentItem = true;
        this.item = item;
        this.itemDefinition = PInventory.getItemDef(item);

        // THe enum is missing one index in the middle for some reason
        int index = slot >= 7 ? (slot-1) : slot;
        KitType k = KitType.values()[index];
        this.slotName = k.getName();
        this.widgetItem = null;
        this.equipmentWidget = PWidgets.get(k.getWidgetInfo());
        //log.info(getName() + " in slot " + slot + "(" + slotName + ")");
    }

    public String getSlotName(){
        if (this.isEquipmentItem) {
            return this.slotName;
        } else {
            return "Inventory";
        }
    }
    public int getId(){
        if (this.widgetItem != null) return widgetItem.getId();
        if (this.item != null) return item.getId();
        log.error("Couldnt get PItem id!");
        return -1;
    }

    public int getQuantity(){
        if (this.widgetItem != null) return widgetItem.getQuantity();
        if (this.item != null) return item.getQuantity();
        log.error("Couldnt get PItem quantity!");
        return -1;
    }

    public String getName(){
        return this.getDefinition().getName();
    }

    public String[] getActions(){
        return PUtils.clientOnly(() -> {
            if (isEquipmentItem){
                return equipmentWidget.getActions();
            }
            return getDefinition().getInventoryActions();
        }, "PItem.getActions");
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }
    public ItemDefinition getDefinition() {
        return itemDefinition;
    }

    public WidgetItem getWidgetItem(){
        return widgetItem;
    }

    public WidgetItem getFirst(){
        return widgetItem;
    }

    public ItemDefinition getSecond(){
        return itemDefinition;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof PItem){
            PItem other = ((PItem) o);
            if (other.widgetItem != null && widgetItem != null){
                return other.widgetItem.equals(widgetItem);
            }
        }

        return false;
    }
}

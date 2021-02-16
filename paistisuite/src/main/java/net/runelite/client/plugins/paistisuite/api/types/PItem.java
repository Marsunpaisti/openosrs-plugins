package net.runelite.client.plugins.paistisuite.api.types;

import net.runelite.api.Item;
import net.runelite.api.ItemDefinition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;

public class PItem {
    public WidgetItem widgetItem;
    public ItemDefinition itemDefinition;
    public boolean isEquipmentItem;

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


    public PItem(Item item) {
        this.isEquipmentItem = true;
        this.itemDefinition = PInventory.getItemDef(item);
        this.widgetItem = null;
    }

    public ItemDefinition getItemDefinition() {
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
}

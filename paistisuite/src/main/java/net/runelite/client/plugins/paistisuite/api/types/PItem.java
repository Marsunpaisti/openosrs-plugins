package net.runelite.client.plugins.paistisuite.api.types;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemDefinition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;

@Slf4j
public class PItem {
    public WidgetItem widgetItem;
    public Item item;
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
        this.item = item;
        this.itemDefinition = PInventory.getItemDef(item);
        this.widgetItem = null;
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
            if (other.isEquipmentItem && isEquipmentItem){
                return other.getDefinition().getId() == getDefinition().getId();
            }
            if (other.widgetItem != null && widgetItem != null){
                return other.widgetItem.equals(widgetItem);
            }
        }

        return false;
    }
}

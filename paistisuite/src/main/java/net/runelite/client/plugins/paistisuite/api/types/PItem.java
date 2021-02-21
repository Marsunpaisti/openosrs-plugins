package net.runelite.client.plugins.paistisuite.api.types;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.PlayerComposition;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;

@Slf4j
public class PItem {
    WidgetInfo[] slotToWidget = new WidgetInfo[]{
            WidgetInfo.EQUIPMENT_HELMET,
            WidgetInfo.EQUIPMENT_CAPE,
            WidgetInfo.EQUIPMENT_AMULET,
            WidgetInfo.EQUIPMENT_WEAPON,
            WidgetInfo.EQUIPMENT_BODY,
            WidgetInfo.EQUIPMENT_SHIELD,
            null,
            WidgetInfo.EQUIPMENT_LEGS,
            null,
            WidgetInfo.EQUIPMENT_GLOVES,
            WidgetInfo.EQUIPMENT_BOOTS,
            null,
            WidgetInfo.EQUIPMENT_RING,
            WidgetInfo.EQUIPMENT_AMMO
    };

    public WidgetItem widgetItem;
    public Widget equipmentWidget;
    private Item item;
    public ItemComposition ItemComposition;
    public boolean isEquipmentItem;
    private String slotName;
    private int quantity;
    private int id;

    public PItem(WidgetItem widgetItem, ItemComposition ItemComposition){
        this.isEquipmentItem = false;
        this.widgetItem = widgetItem;
        this.ItemComposition = ItemComposition;
    }

    public PItem(WidgetItem widgetItem){
        this.isEquipmentItem = false;
        this.widgetItem = widgetItem;
        this.ItemComposition = PInventory.getItemDef(widgetItem);
    }

    public PItem(Item item, int slot) {
        this.isEquipmentItem = true;
        this.item = item;
        this.ItemComposition = PInventory.getItemDef(item);
        KitType k = KitType.values()[slot];
        this.slotName = k.getName();
        this.widgetItem = null;
        PlayerComposition p;
        this.equipmentWidget = PWidgets.get(slotToWidget[slot]);
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

    public ItemComposition getItemComposition() {
        return ItemComposition;
    }
    public ItemComposition getDefinition() {
        return ItemComposition;
    }

    public WidgetItem getWidgetItem(){
        return widgetItem;
    }

    public WidgetItem getFirst(){
        return widgetItem;
    }

    public ItemComposition getSecond(){
        return ItemComposition;
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

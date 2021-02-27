package net.runelite.client.plugins.paistisuite.api.types;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.PlayerComposition;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.PShopping;
import net.runelite.client.plugins.paistisuite.api.PBanking;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;

@Slf4j
public class PItem {
    static WidgetInfo[] slotToWidget = new WidgetInfo[]{
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

    public enum PItemType {
        INVENTORY,
        EQUIPMENT,
        BANK,
        SHOP
    }

    public WidgetItem widgetItem;
    private Widget widget;
    private Item item;
    public ItemComposition ItemComposition;
    private String equipmentSlotName;
    private int slotIndex;
    public PItemType itemType;

    public static PItem fromShopItem(Item shopItem, int slot){
        PItem item = new PItem(null);
        item.item = shopItem;
        item.ItemComposition = PInventory.getItemDef(shopItem);
        item.slotIndex = slot;
        item.widgetItem = null;
        if (PWidgets.get(WidgetInfo.SHOP_ITEMS_CONTAINER) != null) item.widget = PWidgets.get(WidgetInfo.SHOP_ITEMS_CONTAINER).getChild(slot);;
        item.itemType = PItemType.SHOP;
        return item;
    }

    public static PItem fromBankItem(Item bankItem, int slot){
        PItem item = new PItem(null);
        item.item = bankItem;
        item.ItemComposition = PInventory.getItemDef(bankItem);
        item.slotIndex = slot;
        item.widgetItem = null;
        if (PWidgets.get(WidgetInfo.BANK_ITEM_CONTAINER) != null) item.widget = PWidgets.get(WidgetInfo.BANK_ITEM_CONTAINER).getChild(slot);;
        item.itemType = PItemType.BANK;
        return item;
    }

    public static PItem fromEquipmentItem(Item equipmentItem, int slot){
        PItem item = new PItem(null);
        item.itemType = PItemType.EQUIPMENT;
        item.item = equipmentItem;
        item.ItemComposition = PInventory.getItemDef(equipmentItem);
        KitType k = KitType.values()[slot];
        item.equipmentSlotName = k.getName();
        item.widget = PWidgets.get(slotToWidget[slot]);
        item.slotIndex = slot;
        item.widgetItem = null;
        return item;
    }

    public PItem(WidgetItem widgetItem, ItemComposition ItemComposition){
        this.itemType = PItemType.INVENTORY;
        this.widgetItem = widgetItem;
        this.ItemComposition = ItemComposition;
    }

    public PItem(WidgetItem widgetItem){
        this.itemType = PItemType.INVENTORY;
        this.widgetItem = widgetItem;
        if (widgetItem != null) this.ItemComposition = PInventory.getItemDef(widgetItem);
    }

    public String getSlotName(){
        switch (this.itemType){
            case INVENTORY:
                return "Inventory";
            case EQUIPMENT:
                return this.equipmentSlotName;
            case BANK:
                return "Bank";
            case SHOP:
                return "Shop";
            default:
                return null;
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

    public String[] getCurrentActions(){
        return PUtils.clientOnly(() -> {
            switch (this.itemType){
                case INVENTORY:
                    if (PBanking.isBankOpen() || PBanking.isDepositBoxOpen()){
                        return getDepositInventoryWidget().getActions();
                    } else if (PShopping.isShopOpen()){
                        return getShopInventoryWidget().getActions();
                    }
                    return getDefinition().getInventoryActions();
                case EQUIPMENT:
                    return getWidget().getActions();
                case BANK:
                    return getWidget().getActions();
                case SHOP:
                    return getWidget().getActions();
                default:
                    return null;
            }

        }, "PItem.getCurrentActions");
    }

    public String[] getInventoryActions(){
        return PUtils.clientOnly(() -> {
            return getDefinition().getInventoryActions();
        }, "PItem.getInventoryActions");
    }

    public int getInventorySlotIndex(){
        if (getWidgetItem() != null){
            return getWidgetItem().getIndex();
        }

        return -1;
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

    public Widget getWidget(){
        switch (this.itemType){
            case INVENTORY:
                if (PBanking.isBankOpen() || PBanking.isDepositBoxOpen()){
                    return getDepositInventoryWidget();
                } else if (PShopping.isShopOpen()){
                    return getShopInventoryWidget();
                }
                return widgetItem.getWidget();
            case EQUIPMENT:
                return this.widget != null ? this.widget : PWidgets.get(slotToWidget[slotIndex]);
            case BANK:
                return this.widget != null ? this.widget : PWidgets.get(WidgetInfo.BANK_ITEM_CONTAINER).getChild(slotIndex);
            case SHOP:
                return this.widget != null ? this.widget : PWidgets.get(WidgetInfo.SHOP_ITEMS_CONTAINER).getChild(slotIndex);
            default:
                return null;
    }

    }

    public WidgetItem getFirst(){
        return widgetItem;
    }

    public ItemComposition getSecond(){
        return ItemComposition;
    }

    public Widget getShopInventoryWidget(){
        if (PShopping.isShopOpen()) {
            return PWidgets.get(WidgetInfo.SHOP_INVENTORY_ITEMS_CONTAINER).getChild(getInventorySlotIndex());
        }
        return null;
    }

    public Widget getDepositInventoryWidget(){
        if (PBanking.isBankOpen()) {
            return PWidgets.get(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER).getChild(getInventorySlotIndex());
        }
        if (PBanking.isDepositBoxOpen()){
            return PWidgets.get(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER).getChild(getInventorySlotIndex());
        }
        return null;
    }

    @Override
    public String toString(){
        return getName() + " x " + getQuantity() + " Type: " + itemType;
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

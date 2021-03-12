package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Keyboard;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PBanking {
    public static Boolean isBankOpen(){
        return PUtils.clientOnly(() -> PUtils.getClient().getItemContainer(InventoryID.BANK) != null, "isBankOpen");
    }

    public static boolean closeBank(){
        if (!isBankOpen() && !isDepositBoxOpen()) return true;
        Widget parent = PWidgets.get(12, 2);
        Widget bankCloseWidget = null;
        if (parent != null) {
            bankCloseWidget = parent.getDynamicChildren()[11];
        }
        if (bankCloseWidget != null) {
            return PInteraction.widget(bankCloseWidget, "Close");
        }
        return false;
    }

    public static Boolean isDepositBoxOpen(){
        return PWidgets.get(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER) != null;
    }

    public static Boolean depositInventory(){
        if (PInventory.getEmptySlots() == 28) return true;

        if (isBankOpen()) {
            Widget button = PWidgets.get(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            return PInteraction.widget(button, "Deposit inventory");
        }

        if (isDepositBoxOpen()){
            Widget button = PWidgets.get(192,4);
            return PInteraction.widget(button, "Deposit inventory");
        }

        return false;
    }

    public static boolean depositEquipment(){
        if (PInventory.getEquipmentItems().size() == 0) return true;
        if (isBankOpen()) {
            Widget button = PWidgets.get(WidgetInfo.BANK_DEPOSIT_EQUIPMENT);
            return PInteraction.widget(button, "Deposit worn items");
        }

        if (isDepositBoxOpen()){
            Widget button = PWidgets.get(192,6);
            return PInteraction.widget(button, "Deposit worn items");
        }

        return false;
    }

    public static Boolean depositAll(Predicate<PItem> filter){
        return PUtils.clientOnly(() -> {
            List<PItem> items = PInventory.findAllItems(filter);
            if (items == null) return false;
            boolean didDeposit = false;
            for (PItem item : items){
                if (depositAll(item)) {
                    didDeposit = true;
                    PUtils.sleepNormal(200, 1000, 100, 400);
                }
            }

            return didDeposit;
        }, "depositAllFilter");
    }

    public static Boolean depositAll(PItem item){
        return PUtils.clientOnly(() -> {
            if (PInventory.getCount(item.getName()) == 1){
                return PInteraction.item(item, "Deposit-1");
            } else {
                return PInteraction.item(item, "Deposit-All");
            }
        }, "depositAll");
    }

    public static List<PItem> getBankItems(){
        return PUtils.clientOnly(() -> {
            ItemContainer bankItemContainer = PUtils.getClient().getItemContainer(InventoryID.BANK);
            if (bankItemContainer == null) return new ArrayList<PItem>();
            Item[] items = bankItemContainer.getItems();
            List<PItem> bankItems = new ArrayList<PItem>();
            int slot = 0;
            for (Item i : items){
                if (i.getId() != -1 && i.getQuantity() != 0) bankItems.add(PItem.fromBankItem(i, slot));
                slot++;
            }
            return bankItems;
        }, "getBankItems");
    }

    public static boolean openBank() {
        if (isBankOpen()) return true;
        PTileObject booth = PObjects.findObject(
                Filters.Objects.actionsContains("Bank")
                .and(b -> PPlayer.distanceTo(b) <= 20));
        PTileObject bankChest = PObjects.findObject(
                Filters.Objects.actionsContains("Use")
                .and(Filters.Objects.nameEquals("Bank chest"))
                .and(b -> PPlayer.distanceTo(b) <= 20));
        NPC banker = PObjects.findNPC(
                Filters.NPCs.actionsContains("Bank")
                .and(b -> PPlayer.distanceTo(b) <= 20));
        boolean didInteract = false;

        if (bankChest != null){
            didInteract = PInteraction.tileObject(booth, "Use");
            return true;
        }

        if (booth != null && banker != null){
            if (PUtils.random(1,4) == 1){
                didInteract = PInteraction.npc(banker, "Bank");
            } else {
                didInteract = PInteraction.tileObject(booth, "Bank");
            }
        } else if (booth != null){
            didInteract = PInteraction.tileObject(booth, "Bank");
        } else if (banker != null){
            didInteract = PInteraction.npc(banker, "Bank");
        }

        return didInteract;
    }

    public static PItem findBankItem(Predicate<PItem> filter){
        return getBankItems()
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public static boolean withdrawItem(String nameOrId, int quantity){
        if (!isBankOpen()) return false;
        PItem target = findBankItem(Filters.Items.nameContainsOrIdEquals(nameOrId));
        if (target == null) return false;

        if (target.getQuantity() < quantity) return false;

        int tens = (int) Math.floor(quantity / 10);
        int fives = (int) Math.floor((quantity - 10 * tens) / 5);
        int ones = (int) Math.floor((quantity - 10 * tens - 5 * fives));

        if (ones+tens+fives >= 3) {
            if (!PInteraction.item(target, "Withdraw-X")) return false;
            if (!PUtils.waitCondition(PUtils.random(2000, 3000), () -> PWidgets.isSubstantiated(WidgetInfo.CHATBOX_FULL_INPUT))) return false;
            PUtils.sleepNormal(250, 600);
            Keyboard.typeString(""+quantity);
            PUtils.sleepNormal(250, 600);
            Keyboard.typeKeysInt(KeyEvent.VK_ENTER);
            PUtils.sleepNormal(250, 600);
        } else {
            for (int i = 0; i < tens; i++){
                if (!PInteraction.item(target, "Withdraw-10")) return false;
                PUtils.sleepNormal(400, 800);
            }
            for (int i = 0; i < fives; i++){
                if (!PInteraction.item(target, "Withdraw-5")) return false;
                PUtils.sleepNormal(400, 800);
            }
            for (int i = 0; i < ones; i++){
                if (!PInteraction.item(target, "Withdraw-1")) return false;
                PUtils.sleepNormal(400, 800);
            }
        }
        return true;
    }
}

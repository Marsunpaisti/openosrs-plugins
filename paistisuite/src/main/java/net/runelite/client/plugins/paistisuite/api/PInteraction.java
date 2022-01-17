package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.types.PGroundItem;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;
import net.runelite.client.plugins.paistisuite.api.types.Spells;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class PInteraction {
    public static Boolean tileObject(PTileObject to, String... actions) {
        return PUtils.clientOnly(() -> {
            if (to == null) return false;
            if (to.getDef() == null) return false;

            String[] possibleActions = to.getDef().getActions();
            List<String> desiredActions = Arrays.asList(actions);
            int actionIndex = -1;
            String action = "";
            int i = 0;
            boolean found = false;
            for (String a : possibleActions) {
                if (desiredActions.contains(a)) {
                    action = a;
                    actionIndex = i;
                    found = true;
                    break;
                }
                i++;
            }
            if (!found) return false;
            MenuAction actionOp = null;
            switch (actionIndex) {
                case 0:
                    actionOp = MenuAction.GAME_OBJECT_FIRST_OPTION;
                    break;
                case 1:
                    actionOp = MenuAction.GAME_OBJECT_SECOND_OPTION;
                    break;
                case 2:
                    actionOp = MenuAction.GAME_OBJECT_THIRD_OPTION;
                    break;
                case 3:
                    actionOp = MenuAction.GAME_OBJECT_FOURTH_OPTION;
                    break;
                case 4:
                    actionOp = MenuAction.GAME_OBJECT_FIFTH_OPTION;
                    break;
                default:
                    return false;
            }
            MenuAction finalActionOp = actionOp;
            if (to.getFirst() instanceof GameObject){
                PUtils.getClient().invokeMenuAction(
                        "",
                        "PaistiSuite",
                        to.getFirst().getId(),
                        finalActionOp.getId(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getX(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getY());
            } else {
                PUtils.getClient().invokeMenuAction(
                        "",
                        "PaistiSuite",
                        to.getFirst().getId(),
                        finalActionOp.getId(),
                        (to.getFirst()).getWorldLocation().getX() - PUtils.getClient().getBaseX(),
                        (to.getFirst()).getWorldLocation().getY() - PUtils.getClient().getBaseY()
                );
            }
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "interact_tileObject");
    }

    public static Boolean groundItem(PGroundItem item, String ...actions){
        return PUtils.clientOnly(() -> {
            if (item == null) return false;
            if (item.getDef() == null) return false;
            if (!item.getLocation().isInScene(PUtils.getClient())) return false;
            String[] possibleActions = item.getActions();
            List<String> desiredActions = Arrays.asList(actions);
            int actionIndex = -1;
            String action = "";
            int i = 0;
            boolean found = false;
            for (String a : possibleActions) {
                if (desiredActions.contains(a)) {
                    action = a;
                    actionIndex = i;
                    found = true;
                    break;
                }
                i++;
            }
            if (!found) return false;
            MenuAction actionOp = null;
            switch (actionIndex) {
                case 0:
                    actionOp = MenuAction.GROUND_ITEM_FIRST_OPTION;
                    break;
                case 1:
                    actionOp = MenuAction.GROUND_ITEM_SECOND_OPTION;
                    break;
                case 2:
                    actionOp = MenuAction.GROUND_ITEM_THIRD_OPTION;
                    break;
                case 3:
                    actionOp = MenuAction.GROUND_ITEM_FOURTH_OPTION;
                    break;
                case 4:
                    actionOp = MenuAction.GROUND_ITEM_FIFTH_OPTION;
                    break;
                default:
                    return false;
            }

            MenuAction finalActionOp = actionOp;
            PUtils.getClient().invokeMenuAction(
                    "",
                    "PaistiSuite",
                    item.getId(),
                    finalActionOp.getId(),
                    item.getLocation().getX() - PUtils.getClient().getBaseX(),
                    item.getLocation().getY() - PUtils.getClient().getBaseY());
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "interact_GroundItem");
    }

    public static Boolean useItemOnItem(PItem item, PItem target){
        return PUtils.clientOnly(() -> {
            if (item == null || target == null || item.getWidgetItem() == null || target.getWidgetItem() == null) return false;
            if (item.equals(target)) return false;
            PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
            PUtils.getClient().setSelectedItemID(item.getWidgetItem().getId());
            PUtils.getClient().invokeMenuAction(
                    "",
                    "PaistiSuite",
                    target.getWidgetItem().getId(),
                    MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
                    target.getWidgetItem().getIndex(),
                    WidgetInfo.INVENTORY.getId());
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "interact_useItemOnItem");
    }

    public static Boolean useSpellOnItem(Spells spell, PItem target){
        return PUtils.clientOnly(() -> {
            if (spell == null || target == null || target.getWidgetItem() == null || target.itemType != PItem.PItemType.INVENTORY) return false;
            Widget spellWidget = PWidgets.get(spell.getInfo());
            if (spellWidget == null) return false;
            PUtils.getClient().setSelectedSpellWidget(spellWidget.getId());
            PUtils.getClient().setSelectedSpellChildIndex(-1);
            PUtils.getClient().invokeMenuAction(
                    "",
                    "PaistiSuite",
                    target.getWidgetItem().getId(),
                    MenuAction.ITEM_USE_ON_WIDGET.getId(),
                    target.getWidgetItem().getIndex(),
                    WidgetInfo.INVENTORY.getId());
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "interact_useSpellOnItem");
    }

    private static Boolean equippedItem(PItem item, String ...actions){
        if (item == null) return false;
        return PUtils.clientOnly(() -> {
            if (item.getWidget() == null) return false;
            return PInteraction.widget(item.getWidget(), actions);
        }, "interact_equippedItem");
    }

    private static Boolean bankedItem(PItem item, String ...actions){
        if (item == null) return false;
        return PUtils.clientOnly(() -> {
            if (item.getWidget() == null) return false;
            return PInteraction.widget(item.getWidget(), actions);
        }, "interact_bankItem");
    }

    private static Boolean shopItem(PItem item, String ...actions){
        if (item == null) return false;
        return PUtils.clientOnly(() -> {
            if (item.getWidget() == null) return false;
            return PInteraction.widget(item.getWidget(), actions);
        }, "interact_shopItem");
    }

    private static Boolean sellOrDepositItem(PItem item, String ...actions){
        if (item == null) return false;
        return PUtils.clientOnly(() -> {
            if (item.getWidget() == null) return false;
            return PInteraction.widget(item.getWidget(), actions);
        }, "interact_sellOrDepositItem");
    }

    private static Boolean regularInventoryItem(PItem item, String ...actions){
        if (item == null) return false;
        return PUtils.clientOnly(() -> {
            if (item.itemType != PItem.PItemType.INVENTORY) return false;
            String[] possibleActions = item.getDefinition().getInventoryActions();
            List<String> desiredActions = Arrays.asList(actions);
            int actionIndex = -1;
            String action = "";
            int i = 0;
            boolean found = false;
            for (String a : possibleActions) {
                if (desiredActions.contains(a)) {
                    action = a;
                    actionIndex = i;
                    found = true;
                    break;
                }
                i++;
            }

            if (!found) return false;

            MenuAction actionOp = null;
            switch (actionIndex) {
                case 0:
                    actionOp = MenuAction.ITEM_FIRST_OPTION;
                    break;
                case 1:
                    actionOp = MenuAction.ITEM_SECOND_OPTION;
                    break;
                case 2:
                    actionOp = MenuAction.ITEM_THIRD_OPTION;
                    break;
                case 3:
                    actionOp = MenuAction.ITEM_FOURTH_OPTION;
                    break;
                case 4:
                    actionOp = MenuAction.ITEM_FIFTH_OPTION;
                    break;
                default:
                    return false;
            }

            MenuAction finalActionOp = actionOp;
            PUtils.getClient().invokeMenuAction(
                    "",
                    "PaistiSuite",
                    item.getId(),
                    finalActionOp.getId(),
                    item.getWidgetItem().getIndex(),
                    WidgetInfo.INVENTORY.getId());
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "interact_regularInventoryItem");
    }

    public static Boolean item(PItem item, String ...actions){
        return PUtils.clientOnly(() -> {
            if (item == null) return false;
            if (item.itemType == PItem.PItemType.EQUIPMENT) return equippedItem(item, actions);
            if (item.itemType == PItem.PItemType.BANK) return bankedItem(item, actions);
            if (item.itemType == PItem.PItemType.SHOP) return shopItem(item, actions);
            if (PShopping.isShopOpen() || PBanking.isBankOpen()) return sellOrDepositItem(item, actions);
            return regularInventoryItem(item, actions);
        }, "interact_item");
    }

    public static Boolean clickItem(PItem item) {
        return PUtils.clientOnly(() -> {
            if (item == null) return false;
            if (item.itemType == PItem.PItemType.EQUIPMENT) return false;
            if (item.getWidgetItem() == null) return false;
            if (item.getWidgetItem().getWidget().isHidden()) return false;
            PMouse.clickShape(item.getWidgetItem().getCanvasBounds());
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "clickItem");
    }

    public static Boolean useItemOnTileObject(PItem item, PTileObject to){
        return PUtils.clientOnly(() -> {
            if (item == null || to == null || item.getWidgetItem() == null || to.tileObject == null) return false;

            if (to.getFirst() instanceof GameObject){
                PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
                PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
                PUtils.getClient().setSelectedItemID(item.getId());
                PUtils.getClient().invokeMenuAction(
                        "",
                        "PaistiSuite",
                        to.getFirst().getId(),
                        MenuAction.ITEM_USE_ON_GAME_OBJECT.getId(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getX(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getY());
            } else {
                PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
                PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
                PUtils.getClient().setSelectedItemID(item.getId());
                PUtils.getClient().invokeMenuAction(
                        "",
                        "PaistiSuite",
                        to.getFirst().getId(),
                        MenuAction.ITEM_USE_ON_GAME_OBJECT.getId(),
                        (to.getFirst()).getWorldLocation().getX() - PUtils.getClient().getBaseX(),
                        (to.getFirst()).getWorldLocation().getY() - PUtils.getClient().getBaseY());
            }
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "interact_useItemOnTileObject");
    }

    public static Boolean npc(NPC npc, String... actions) {
        return PUtils.clientOnly(() -> {
            if (npc == null) return false;
            if (npc.getTransformedComposition() == null) {
                log.error("Unable to get transformed def for NPC");
                return false;
            }
            String[] possibleActions = npc.getTransformedComposition().getActions();
            List<String> desiredActions = Arrays.asList(actions);
            int actionIndex = -1;
            String action = "";
            int i = 0;
            boolean found = false;
            for (String a : possibleActions) {
                if (desiredActions.contains(a)) {
                    action = a;
                    actionIndex = i;
                    found = true;
                    break;
                }
                i++;
            }

            if (!found) {
                log.error("Unable to find action: " + action + " on npc " + npc.getTransformedComposition().getName());
                return false;
            }

            MenuAction actionOp = null;
            switch (actionIndex) {
                case 0:
                    actionOp = MenuAction.NPC_FIRST_OPTION;
                    break;
                case 1:
                    actionOp = MenuAction.NPC_SECOND_OPTION;
                    break;
                case 2:
                    actionOp = MenuAction.NPC_THIRD_OPTION;
                    break;
                case 3:
                    actionOp = MenuAction.NPC_FOURTH_OPTION;
                    break;
                case 4:
                    actionOp = MenuAction.NPC_FIFTH_OPTION;
                    break;
                default:
                    return false;
            }

            MenuAction finalActionOp = actionOp;

            PUtils.getClient().invokeMenuAction(
                    "",
                    "PaistiSuite",
                    npc.getIndex(),
                    finalActionOp.getId(),
                    0,
                    0);
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;

        }, "interact_npc");
    }

    public static Boolean useItemOnNpc(PItem item, NPC npc) {
        return PUtils.clientOnly(() -> {
            if (npc == null || item == null || item.getWidgetItem() == null) return false;
            if (npc.getTransformedComposition() == null) {
                log.error("Unable to get transformed def for NPC");
                return false;
            }
            PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
            PUtils.getClient().setSelectedItemID(item.getWidgetItem().getId());
            PUtils.getClient().invokeMenuAction(
                    "",
                    "PaistiSuite",
                    npc.getIndex(),
                    MenuAction.ITEM_USE_ON_NPC.getId(),
                    0,
                    0);
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;

        }, "interact_use_item_on_npc");
    }

    /***
     * Just sends a regular click on the widgets area
     * @param widget
     * @return Successful or not
     */
    public static Boolean clickWidget(Widget widget) {
        return PUtils.clientOnly(() -> {
            if (widget == null) return false;
            PMouse.clickShape(widget.getBounds());
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "clickWidget");
    }

    public static Boolean widget(Widget widget, String... actions) {
        if (widget == null) return false;
        return PUtils.clientOnly(() -> {
            String[] possibleActions = widget.getActions();
            if (possibleActions == null) return false;
            List<String> desiredActions = Arrays.asList(actions);
            int actionIndex = -1;
            String action = "";
            int i = 0;
            boolean found = false;
            for (String a : possibleActions) {
                if (desiredActions.contains(a)) {
                    action = a;
                    actionIndex = i;
                    found = true;
                    break;
                }
                i++;
            }


            if (!found) return false;
            int finalActionIndex = actionIndex + 1;
            MenuAction finalActionOp = finalActionIndex > 5 ? MenuAction.CC_OP_LOW_PRIORITY : MenuAction.CC_OP;
            final int widgetId = widget.getId();
            int childIndex = -1;
            int searchIndex = 0;
            if (widget.getParent() != null && widget.getParent().getChildren() != null){
                for (Widget c : widget.getParent().getChildren()){
                    if (c.equals(widget)) {
                        childIndex = searchIndex;
                        break;
                    } else {
                        searchIndex++;
                    }
                }
            }
            PUtils.getClient().invokeMenuAction(
                    "",
                    "PaistiSuite",
                    finalActionIndex,
                    finalActionOp.getId(),
                    childIndex,
                    widgetId);
            PUtils.getClient().setMouseIdleTicks(0);
            PUtils.getClient().setKeyboardIdleTicks(0);
            return true;
        }, "interact_widget");
    }

}

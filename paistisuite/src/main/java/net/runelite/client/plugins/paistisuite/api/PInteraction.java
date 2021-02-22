package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.types.PGroundItem;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

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
                        "",
                        to.getFirst().getId(),
                        finalActionOp.getId(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getX(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getY());
            } else {
                PUtils.getClient().invokeMenuAction(
                        "",
                        "",
                        to.getFirst().getId(),
                        finalActionOp.getId(),
                        (to.getFirst()).getWorldLocation().getX() - PUtils.getClient().getBaseX(),
                        (to.getFirst()).getWorldLocation().getY() - PUtils.getClient().getBaseY()
                );
            }

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
                    "",
                    item.getId(),
                    finalActionOp.getId(),
                    item.getLocation().getX() - PUtils.getClient().getBaseX(),
                    item.getLocation().getY() - PUtils.getClient().getBaseY());

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
                    "",
                    target.getWidgetItem().getId(),
                    MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
                    target.getWidgetItem().getIndex(),
                    9764864);
            return true;
        }, "interact_useItemOnItem");
    }

    private static Boolean equippedItem(PItem item, String ...actions){
        return PUtils.clientOnly(() -> {
            if (item == null) return false;
            if (!item.isEquipmentItem) return false;
            if (item.equipmentWidget == null) return false;
            return PInteraction.widget(item.equipmentWidget, actions);
        }, "interact_equippedItem");
    }


    public static Boolean item(PItem item, String ...actions){
        return PUtils.clientOnly(() -> {
            if (item == null) return false;
            if (item.isEquipmentItem) return equippedItem(item, actions);
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
                    "",
                    item.getId(),
                    finalActionOp.getId(),
                    item.getWidgetItem().getIndex(),
                    9764864);
            return true;
        }, "interact_item");
    }

    public static Boolean clickItem(PItem item) {
        return PUtils.clientOnly(() -> {
            if (item == null) return false;
            if (item.isEquipmentItem) return false;
            if (item.getWidgetItem() == null) return false;
            PMouse.clickShape(item.getWidgetItem().getCanvasBounds());
            return true;
        }, "clickItem");
    }

    public static Boolean useItemOnGameObject(PItem item, PTileObject to){
        return PUtils.clientOnly(() -> {
            if (item == null || to == null || item.getWidgetItem() == null || to.tileObject == null) return false;

            if (to.getFirst() instanceof GameObject){
                PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
                PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
                PUtils.getClient().setSelectedItemID(item.getId());
                PUtils.getClient().invokeMenuAction(
                        "",
                        "",
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
                        "",
                        to.getFirst().getId(),
                        MenuAction.ITEM_USE_ON_GAME_OBJECT.getId(),
                        (to.getFirst()).getWorldLocation().getX() - PUtils.getClient().getBaseX(),
                        (to.getFirst()).getWorldLocation().getY() - PUtils.getClient().getBaseY());
            }
            return true;
        }, "interact_useItemOnItem");
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
                    "",
                    npc.getIndex(),
                    finalActionOp.getId(),
                    0,
                    0);
            return true;

        }, "interact_npc");
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
            return true;
        }, "clickWidget");
    }

    public static Boolean widget(Widget widget, String... actions) {
        if (widget == null) return false;
        return PUtils.clientOnly(() -> {
            String[] possibleActions = widget.getActions();
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
                    "",
                    finalActionIndex,
                    finalActionOp.getId(),
                    childIndex,
                    widgetId);
            return true;
        }, "interact_widget");
    }

}

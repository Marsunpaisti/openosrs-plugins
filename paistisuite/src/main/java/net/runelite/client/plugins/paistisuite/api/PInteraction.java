package net.runelite.client.plugins.paistisuite.api;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class PInteraction {
    public static boolean tileObject(PTileObject to, String... actions) {
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
        MenuOpcode actionOp = null;
        switch (actionIndex) {
            case 0:
                actionOp = MenuOpcode.GAME_OBJECT_FIRST_OPTION;
                break;
            case 1:
                actionOp = MenuOpcode.GAME_OBJECT_SECOND_OPTION;
                break;
            case 2:
                actionOp = MenuOpcode.GAME_OBJECT_THIRD_OPTION;
                break;
            case 3:
                actionOp = MenuOpcode.GAME_OBJECT_FOURTH_OPTION;
                break;
            case 4:
                actionOp = MenuOpcode.GAME_OBJECT_FIFTH_OPTION;
                break;
            default:
                return false;
        }

        if (to.getFirst() instanceof GameObject){
            MenuOpcode finalActionOp = actionOp;
            PaistiSuite.getInstance().clientExecutor.schedule(() -> {
                PUtils.getClient().invokeMenuAction(
                        "",
                        "",
                        to.getFirst().getId(),
                        finalActionOp.getId(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getX(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getY());
            }, "interact_gameObject");
        } else {
            MenuOpcode finalActionOp = actionOp;
            PaistiSuite.getInstance().clientExecutor.schedule(() -> {
                PUtils.getClient().invokeMenuAction(
                        "",
                        "",
                        to.getFirst().getId(),
                        finalActionOp.getId(),
                        (to.getFirst()).getWorldLocation().getX() - PUtils.getClient().getBaseX(),
                        (to.getFirst()).getWorldLocation().getY() - PUtils.getClient().getBaseY()
                );
            }, "interact_gameObject");
        }

        return true;
    }

    public static boolean useItemOnItem(PItem item, PItem target){
        if (item == null || target == null || item.getWidgetItem() == null || target.getWidgetItem() == null) return false;
        if (item.equals(target)) return false;

        PaistiSuite.getInstance().clientExecutor.schedule(() -> {
            PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
            PUtils.getClient().setSelectedItemID(item.getWidgetItem().getId());
            PUtils.getClient().invokeMenuAction(
                    "",
                    "",
                    target.getWidgetItem().getId(),
                    MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId(),
                    target.getWidgetItem().getIndex(),
                    9764864);
        }, "interact_useItemOnItem");


        return true;
    }

    public static boolean item(PItem item, String ...actions){
        if (item == null) return false;
        if (item.isEquipmentItem) return false;
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

        MenuOpcode actionOp = null;
        switch (actionIndex) {
            case 0:
                actionOp = MenuOpcode.ITEM_FIRST_OPTION;
                break;
            case 1:
                actionOp = MenuOpcode.ITEM_SECOND_OPTION;
                break;
            case 2:
                actionOp = MenuOpcode.ITEM_THIRD_OPTION;
                break;
            case 3:
                actionOp = MenuOpcode.ITEM_FOURTH_OPTION;
                break;
            case 4:
                actionOp = MenuOpcode.ITEM_FIFTH_OPTION;
                break;
            default:
                return false;
        }

        MenuOpcode finalActionOp = actionOp;
        PaistiSuite.getInstance().clientExecutor.schedule(() -> {
            PUtils.getClient().invokeMenuAction(
                    "",
                    "",
                    item.getId(),
                    finalActionOp.getId(),
                    item.getWidgetItem().getIndex(),
                    9764864);
        }, "interact_item");

        return true;
    }

    public static boolean clickItem(PItem item) {
        if (item == null) return false;
        if (item.isEquipmentItem) return false;
        if (item.getWidgetItem() == null) return false;
        PMouse.clickShape(item.getWidgetItem().getCanvasBounds());
        return true;
    }


    public static boolean useItemOnGameObject(PItem item, PTileObject to){
        if (item == null || to == null || item.getWidgetItem() == null || to.tileObject == null) return false;

        if (to.getFirst() instanceof GameObject){
            PaistiSuite.getInstance().clientExecutor.schedule(() -> {
                PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
                PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
                PUtils.getClient().setSelectedItemID(item.getId());
                PUtils.getClient().invokeMenuAction(
                        "",
                        "",
                        to.getFirst().getId(),
                        MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getX(),
                        ((GameObject)to.getFirst()).getSceneMinLocation().getY());
            }, "interact_useItemOnItem");
        } else {
            PaistiSuite.getInstance().clientExecutor.schedule(() -> {
                PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
                PUtils.getClient().setSelectedItemSlot(item.getWidgetItem().getIndex());
                PUtils.getClient().setSelectedItemID(item.getId());
                PUtils.getClient().invokeMenuAction(
                        "",
                        "",
                        to.getFirst().getId(),
                        MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId(),
                        (to.getFirst()).getWorldLocation().getX() - PUtils.getClient().getBaseX(),
                        (to.getFirst()).getWorldLocation().getY() - PUtils.getClient().getBaseY());
            }, "interact_useItemOnItem");
        }

        return true;
    }

    public static boolean npc(NPC npc, String... actions) {
        if (npc == null) return false;
        if (npc.getTransformedDefinition() == null) {
            log.error("Unable to get transformed def for NPC");
            return false;
        }
        String[] possibleActions = npc.getTransformedDefinition().getActions();
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
            log.error("Unable to find action: " + action + " on npc " + npc.getTransformedDefinition().getName());
            return false;
        }

        MenuOpcode actionOp = null;
        switch (actionIndex) {
            case 0:
                actionOp = MenuOpcode.NPC_FIRST_OPTION;
                break;
            case 1:
                actionOp = MenuOpcode.NPC_SECOND_OPTION;
                break;
            case 2:
                actionOp = MenuOpcode.NPC_THIRD_OPTION;
                break;
            case 3:
                actionOp = MenuOpcode.NPC_FOURTH_OPTION;
                break;
            case 4:
                actionOp = MenuOpcode.NPC_FIFTH_OPTION;
                break;
            default:
                return false;
        }

        MenuOpcode finalActionOp = actionOp;
        PaistiSuite.getInstance().clientExecutor.schedule(() -> {
            PUtils.getClient().invokeMenuAction(
                    "",
                    "",
                    npc.getIndex(),
                    finalActionOp.getId(),
                    0,
                    0);
        }, "interact_widget");

        return true;
    }

    /***
     * Just sends a regular click on the widgets area
     * @param widget
     * @return Successful or not
     */
    public static boolean clickWidget(Widget widget) {
        if (widget == null) return false;
        PMouse.clickShape(widget.getBounds());
        return true;
    }

    public static boolean widget(Widget widget, String... actions) {
        if (widget == null) return false;
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
        MenuOpcode finalActionOp = MenuOpcode.CC_OP;
        final int widgetId = widget.getId();
        PaistiSuite.getInstance().clientExecutor.schedule(() -> {
            PUtils.getClient().invokeMenuAction(
                    "",
                    "",
                    finalActionIndex,
                    finalActionOp.getId(),
                    -1,
                    widgetId);
        }, "interact_widget");

        return true;
    }

}

package net.runelite.client.plugins.paisticore.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paisticore.PaistiCore;
import net.runelite.client.plugins.paisticore.framework.MenuInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class PInteraction {

    public static ObjectDefinition getObjectDef(GameObject go) {
        if (go == null) return null;

        ObjectDefinition def = null;
        try {
            if (!PUtils.getClient().isClientThread()) {
                def = PaistiCore.getInstance().clientExecutor.scheduleAndWait(() -> PUtils.getClient().getObjectDefinition(go.getId()), "getObjectDef");
            } else {
                def = PUtils.getClient().getObjectDefinition(go.getId());
            }
        } catch (Exception e) {
            log.error("Error in getObjectDef: " + e);
        }

        return def;
    }

    public static boolean gameObject(GameObject go, String... actions) {
        if (go == null) return false;
        ObjectDefinition def = getObjectDef(go);
        if (def == null) return false;

        String[] possibleActions = def.getActions();
        List<String> desiredActions = Arrays.asList(actions);
        int actionIndex = -1;
        String action = "";
        int i = 0;
        for (String a : possibleActions) {
            if (desiredActions.contains(a)) {
                action = a;
                actionIndex = i;
                break;
            }
            i++;
        }
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

        /*
        MenuInterceptor.setNextEntry(
                new MenuEntry(
                        action,
                        "",
                        go.getId(),
                        actionOp.getId(),
                        go.getSceneMinLocation().getX(),
                        go.getSceneMinLocation().getY(),
                        false
                )
        );

        PMouse.clickShape(go.getConvexHull());

         */
        MenuOpcode finalActionOp = actionOp;
        PaistiCore.getInstance().clientExecutor.schedule(() -> {
            PUtils.getClient().invokeMenuAction(
                    "",
                    "",
                    go.getId(),
                    finalActionOp.getId(),
                    go.getSceneMinLocation().getX(),
                    go.getSceneMinLocation().getY());
        }, "interact_gameObject");

        return true;
    }

    public static boolean useItemOnItem(WidgetItem item, WidgetItem target){
        if (item.equals(target)) return false;

        PaistiCore.getInstance().clientExecutor.schedule(() -> {
            PUtils.getClient().setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            PUtils.getClient().setSelectedItemSlot(item.getIndex());
            PUtils.getClient().setSelectedItemID(item.getId());
            PUtils.getClient().invokeMenuAction(
                    "",
                    "",
                    target.getId(),
                    MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId(),
                    target.getIndex(),
                    9764864);
        }, "interact_useItemOnItem");


        return true;
    }
}

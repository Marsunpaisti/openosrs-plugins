package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling;

import kotlin.Pair;
import net.runelite.api.ItemDefinition;
import net.runelite.api.NPC;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;

public class InteractionHelper {
    public static boolean click(TileObject object, String... actions){
        return click(object, actions, null);
    }

    public static boolean click(TileObject object, String action, WaitFor.Condition condition){
        return click(object, new String[]{action}, condition);
    }

    /**
     * Interacts with nearby object and waits for {@code condition}.
     *
     * @param object GameObject to click
     * @param actions actions to click
     * @param condition condition to wait for after the click action
     * @return if {@code condition} is null, then return the outcome of condition.
     *          Otherwise, return the result of the click action.
     */
    public static boolean click(TileObject object, String[] actions, WaitFor.Condition condition){
        if (object == null){
            return false;
        }
        if (!PInteraction.tileObject(object, actions)){
            return false;
        }
        return condition == null || WaitFor.condition(PUtils.random(7000, 8500), condition) == WaitFor.Return.SUCCESS;
    }

    public static boolean clickDefPair(Pair<TileObject, ObjectDefinition> object, String[] actions, WaitFor.Condition condition){
        return click(object.getFirst(), actions, condition);
    }
    public static boolean clickDefPair(Pair<TileObject, ObjectDefinition> object, String action, WaitFor.Condition condition){
        return click(object.getFirst(), action, condition);
    }

    public static boolean clickDefPair(Pair<TileObject, ObjectDefinition> object, String ...actions){
        return click(object.getFirst(), actions, null);
    }

    public static boolean click(NPC npc, String... actions){
        return click(npc, actions, null);
    }

    public static boolean click(NPC npc, String action, WaitFor.Condition condition){
        return click(npc, new String[]{action}, condition);
    }

    /**
     * Interacts with nearby object and waits for {@code condition}.
     *
     * @param npc npc to click
     * @param actions actions to click
     * @param condition condition to wait for after the click action
     * @return if {@code condition} is null, then return the outcome of condition.
     *          Otherwise, return the result of the click action.
     */
    public static boolean click(NPC npc, String[] actions, WaitFor.Condition condition){
        if (npc == null){
            return false;
        }
        if (!PInteraction.npc(npc, actions)){
            return false;
        }
        return condition == null || WaitFor.condition(PUtils.random(7000, 8500), condition) == WaitFor.Return.SUCCESS;
    }

    public static boolean useItemOnObject(WidgetItem item, TileObject object){
        return PInteraction.useItemOnGameObject(item, object);
    }


    public static boolean click(Pair<WidgetItem, ItemDefinition> item, String[] actions, WaitFor.Condition condition){
        if (item == null){
            return false;
        }
        if (!PInteraction.item(item, actions)){
            return false;
        }
        return condition == null || WaitFor.condition(PUtils.random(7000, 8500), condition) == WaitFor.Return.SUCCESS;
    }

    public static boolean click(Pair<WidgetItem, ItemDefinition> item, String ...actions){
        return click(item, actions, null);
    }

}

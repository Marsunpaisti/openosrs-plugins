package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import kotlin.Pair;
import net.runelite.api.ItemDefinition;
import net.runelite.api.NPC;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PPlayer;


import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class Filters {
    public static class Objects {
        public static Predicate<Pair<TileObject, ObjectDefinition>> nameEquals(String name) {
            return (Pair<TileObject, ObjectDefinition> pair) -> pair.getSecond().getName().equalsIgnoreCase(name);
        }
        public static Predicate<Pair<TileObject, ObjectDefinition>> idEquals(int id) {
            return (Pair<TileObject, ObjectDefinition> pair) -> pair.getSecond().getId() == id;
        }
        public static Predicate<Pair<TileObject, ObjectDefinition>> actionsContains(String ...action) {
            return (Pair<TileObject, ObjectDefinition> pair) -> Arrays.stream(pair.getSecond().getActions())
                    .filter(java.util.Objects::nonNull)
                    .anyMatch(a -> Arrays.asList(action).contains(a));
        }
        public static Predicate<Pair<TileObject, ObjectDefinition>> actionsEquals(String ...actions) {
            return (Pair<TileObject, ObjectDefinition> pair) -> Arrays.stream(pair.getSecond().getActions())
                    .filter(java.util.Objects::nonNull)
                    .allMatch(a -> Arrays.asList(actions).contains(a));
        }
        public static Predicate<Pair<TileObject, ObjectDefinition>> nameContains(String name) {
            return (Pair<TileObject, ObjectDefinition> pair) -> pair.getSecond().getName().contains(name);
        }
        public static Predicate<Pair<TileObject, ObjectDefinition>> withinDistance(int distance) {
            return (Pair<TileObject, ObjectDefinition> pair) -> (pair.getFirst().getWorldLocation().distanceToHypotenuse(PPlayer.location()) < distance);
        }
    }
    public static class NPCs {
        public static Predicate<NPC> nameContains(String ...name) {
            return (NPC npc) -> Arrays.stream(name).anyMatch(n -> npc.getTransformedDefinition().getName().contains(n));
        }
        public static Predicate<NPC> nameEquals(String ...name) {
            return (NPC npc) ->  Arrays.stream(name).anyMatch(n -> npc.getTransformedDefinition().getName().equals(n));
        }
        public static Predicate<NPC> actionsEquals(String ...action) {
            return (NPC npc) -> Arrays.stream(npc.getTransformedDefinition().getActions())
                    .filter(java.util.Objects::nonNull)
                    .allMatch(a -> Arrays.asList(action).contains(a));
        }
        public static Predicate<NPC> actionsContains(String ...action) {
            return (NPC npc) -> Arrays.stream(npc.getTransformedDefinition().getActions())
                    .anyMatch(a -> Arrays.asList(action).contains(a));
        }
    }

    public static class Items {
        public static Predicate<Pair<WidgetItem, ItemDefinition>> idEquals(int id){
            return (Pair<WidgetItem, ItemDefinition> pair) -> pair.getSecond().getId() == id;
        }
        public static Predicate<Pair<WidgetItem, ItemDefinition>> nameEquals(String name){
            return (Pair<WidgetItem, ItemDefinition> pair) -> pair.getSecond().getName().equals(name);
        }
    }
}

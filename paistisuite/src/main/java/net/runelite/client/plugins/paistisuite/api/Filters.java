package net.runelite.client.plugins.paistisuite.api;

import kotlin.Pair;
import net.runelite.api.ItemDefinition;
import net.runelite.api.NPC;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;


import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class Filters {
    public static class Objects {
        public static Predicate<PTileObject> nameEquals(String name) {
            return (PTileObject pair) -> pair.getSecond().getName().equalsIgnoreCase(name);
        }
        public static Predicate<PTileObject> idEquals(int id) {
            return (PTileObject pair) -> pair.getSecond().getId() == id;
        }
        public static Predicate<PTileObject> actionsContains(String ...action) {
            return (PTileObject pair) -> Arrays.stream(pair.getSecond().getActions())
                    .filter(java.util.Objects::nonNull)
                    .anyMatch(a -> Arrays.asList(action).contains(a));
        }
        public static Predicate<PTileObject> actionsEquals(String ...actions) {
            return (PTileObject pair) -> Arrays.stream(pair.getSecond().getActions())
                    .filter(java.util.Objects::nonNull)
                    .allMatch(a -> Arrays.asList(actions).contains(a));
        }
        public static Predicate<PTileObject> nameContains(String name) {
            return (PTileObject pair) -> pair.getSecond().getName().contains(name);
        }
        public static Predicate<PTileObject> withinDistance(int distance) {
            return (PTileObject pair) -> (pair.getFirst().getWorldLocation().distanceToHypotenuse(PPlayer.location()) < distance);
        }
    }
    public static class NPCs {
        public static Predicate<NPC> nameContains(String ...name) {
            return (NPC npc) -> npc.getTransformedDefinition() != null && Arrays.stream(name).anyMatch(n -> npc.getTransformedDefinition().getName().contains(n));
        }
        public static Predicate<NPC> nameEquals(String ...name) {
            return (NPC npc) -> npc.getTransformedDefinition() != null && Arrays.stream(name).anyMatch(n -> npc.getTransformedDefinition().getName().equals(n));
        }
        public static Predicate<NPC> actionsEquals(String ...action) {
            return (NPC npc) -> npc.getTransformedDefinition() != null && Arrays.stream(npc.getTransformedDefinition().getActions())
                    .filter(java.util.Objects::nonNull)
                    .allMatch(a -> Arrays.asList(action).contains(a));
        }
        public static Predicate<NPC> actionsContains(String ...action) {
            return (NPC npc) -> npc.getTransformedDefinition() != null && Arrays.stream(npc.getTransformedDefinition().getActions())
                    .anyMatch(a -> Arrays.asList(action).contains(a));
        }
    }

    public static class Items {
        public static Predicate<PItem> idEquals(int id){
            return (PItem pair) -> pair.getSecond().getId() == id;
        }
        public static Predicate<PItem> nameEquals(String name){
            return (PItem pair) -> pair.getSecond().getName().equals(name);
        }

        public static Predicate<PItem> nameContains(String ...s) {
            return (PItem pair) -> Arrays.stream(s).anyMatch(str -> pair.getDefinition().getName().contains(str));
        }
    }
}

package net.runelite.client.plugins.paistisuite.api;

import kotlin.Pair;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.types.PGroundItem;
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
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(name).anyMatch(n -> npc.getTransformedComposition().getName().contains(n));
        }
        public static Predicate<NPC> nameEquals(String ...name) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(name).anyMatch(n -> npc.getTransformedComposition().getName().equalsIgnoreCase(n));
        }
        public static Predicate<NPC> actionsEquals(String ...action) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(npc.getTransformedComposition().getActions())
                    .filter(java.util.Objects::nonNull)
                    .allMatch(a -> Arrays.asList(action).contains(a));
        }
        public static Predicate<NPC> actionsContains(String ...action) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(npc.getTransformedComposition().getActions())
                    .anyMatch(a -> Arrays.asList(action).contains(a));
        }
        public static Predicate<NPC> nameOrIdEquals(String ...namesorids){
            return (NPC n) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (n.getTransformedComposition() != null && n.getTransformedComposition().getName().equalsIgnoreCase(str)) return true;
                try {
                    int id = Integer.parseInt(str);
                    return n.getTransformedComposition() != null && n.getTransformedComposition().getId() == id;
                } catch (NumberFormatException ignored){
                }
                return false;
            });
        }
    }

    public static class Items {
        public static Predicate<PItem> idEquals(int id){
            return (PItem pair) -> pair.getSecond().getId() == id;
        }
        public static Predicate<PItem> nameEquals(String ...names){
            return (PItem pair) -> Arrays.stream(names).anyMatch(str -> pair.getDefinition().getName().equalsIgnoreCase(str));
        }
        public static Predicate<PItem> nameOrIdEquals(String ...namesorids){
            return (PItem pair) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (pair.getDefinition().getName().equalsIgnoreCase(str)) return true;
                try {
                    int id = Integer.parseInt(str);
                    return pair.getDefinition().getId() == id;
                } catch (NumberFormatException ignored){
                }
                return false;
            });
        }
        public static Predicate<PItem> nameContainsOrIdEquals(String ...namesorids){
            return (PItem pair) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (pair.getDefinition().getName().contains(str)) return true;
                try {
                    int id = Integer.parseInt(str);
                    return pair.getDefinition().getId() == id;
                } catch (NumberFormatException ignored){
                }
                return false;
            });
        }
        public static Predicate<PItem> nameContains(String ...s) {
            return (PItem pair) -> Arrays.stream(s).anyMatch(str -> pair.getDefinition().getName().contains(str));
        }
    }


    public static class GroundItems {
        public static Predicate<PGroundItem> nameContainsOrIdEquals(String ...namesorids){
            return (PGroundItem item) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (item.getName().contains(str)) return true;
                try {
                    int id = Integer.parseInt(str);
                    return item.getId() == id;
                } catch (NumberFormatException ignored){
                }
                return false;
            });
        }
        public static Predicate<PGroundItem> SlotPriceAtLeast(int minValue){
            return (PGroundItem item) -> item.getPricePerSlot() >= minValue;
        }
    }
}

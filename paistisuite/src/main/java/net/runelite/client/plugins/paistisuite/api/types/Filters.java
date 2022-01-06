package net.runelite.client.plugins.paistisuite.api.types;

import net.runelite.api.NPC;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.paistisuite.api.PPlayer;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

public class Filters {
    public static class Objects {
        public static Predicate<PTileObject> nameEquals(String... names) {
            return (PTileObject pair) -> Arrays.stream(names).anyMatch(name -> pair.getSecond().getName().equalsIgnoreCase(name));
        }

        public static Predicate<PTileObject> idEquals(int... ids) {
            return (PTileObject pair) -> Arrays.stream(ids).anyMatch(id -> pair.getSecond().getId() == id);
        }

        public static Predicate<PTileObject> actionsContains(String... actions) {
            return (PTileObject pair) -> Arrays.stream(pair.getSecond().getActions())
                    .filter(java.util.Objects::nonNull)
                    .anyMatch(a -> Arrays.stream(actions)
                            .anyMatch(s -> a.toLowerCase().contains(s.toLowerCase())));
        }

        public static Predicate<PTileObject> actionsEquals(String... actions) {
            return (PTileObject pair) -> Arrays.stream(pair.getSecond().getActions())
                    .filter(java.util.Objects::nonNull)
                    .anyMatch(a -> Arrays.stream(actions)
                            .filter(java.util.Objects::nonNull)
                            .anyMatch(s -> s.equalsIgnoreCase(a)));
        }

        public static Predicate<PTileObject> nameContains(String name) {
            return (PTileObject pair) -> pair.getSecond().getName().toLowerCase().contains(name.toLowerCase());
        }

        public static Predicate<PTileObject> withinDistance(int distance) {
            return (PTileObject pair) -> (pair.getFirst().getWorldLocation().distanceTo2DHypotenuse(PPlayer.location()) < distance);
        }
    }

    public static class NPCs {
        public static Predicate<NPC> nameContains(String... name) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(name).anyMatch(n -> npc.getTransformedComposition().getName().toLowerCase().contains(n.toLowerCase()));
        }

        public static Predicate<NPC> nameEquals(String... name) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(name).anyMatch(n -> npc.getTransformedComposition().getName().equalsIgnoreCase(n));
        }

        public static Predicate<NPC> actionsEquals(String... action) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(npc.getTransformedComposition().getActions())
                    .filter(java.util.Objects::nonNull)
                    .map(String::toLowerCase)
                    .anyMatch(a -> Arrays.stream(action).anyMatch(m -> m.equalsIgnoreCase(a)));
        }

        public static Predicate<NPC> actionsContains(String... action) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(npc.getTransformedComposition().getActions())
                    .filter(java.util.Objects::nonNull)
                    .map(String::toLowerCase)
                    .anyMatch(a -> Arrays.stream(action).anyMatch(m -> a.contains(m.toLowerCase())));
        }

        public static Predicate<NPC> actionsDontContain(String... actions) {
            return (NPC npc) -> npc.getTransformedComposition() != null && Arrays.stream(npc.getTransformedComposition().getActions())
                    .filter(java.util.Objects::nonNull)
                    .map(String::toLowerCase)
                    .noneMatch(a -> Arrays.stream(actions).anyMatch(m -> a.contains(m.toLowerCase())));
        }

        public static Predicate<NPC> nameOrIdEquals(String... namesorids) {
            return (NPC n) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (n.getTransformedComposition() != null && n.getTransformedComposition().getName().equalsIgnoreCase(str))
                    return true;
                try {
                    int id = Integer.parseInt(str);
                    return n.getTransformedComposition() != null && n.getTransformedComposition().getId() == id;
                } catch (NumberFormatException ignored) {
                }
                return false;
            });
        }

        public static Predicate<NPC> nameContainsOrIdEquals(String... namesorids) {
            return (NPC n) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (n.getTransformedComposition() != null &&
                        n.getTransformedComposition().getName().toLowerCase().contains(str.toLowerCase())) return true;
                try {
                    int id = Integer.parseInt(str);
                    return n.getTransformedComposition() != null && n.getTransformedComposition().getId() == id;
                } catch (NumberFormatException ignored) {
                }
                return false;
            });
        }
    }

    public static class Items {
        public static Predicate<PItem> idEquals(Set<Integer> ids) {
            return (PItem pair) -> ids.contains(pair.getSecond().getId());
        }

        public static Predicate<PItem> idEquals(int id) {
            return (PItem pair) -> pair.getSecond().getId() == id;
        }

        public static Predicate<PItem> processedIdEquals(int id) {
            return (PItem pair) -> getProcessedID(pair.getSecond().getId()) == getProcessedID(id);
        }

        public static Predicate<PItem> nameEquals(String... names) {
            return (PItem pair) -> Arrays.stream(names).anyMatch(str -> pair.getDefinition().getName().equalsIgnoreCase(str));
        }

        public static Predicate<PItem> nameOrIdEquals(String... namesorids) {
            return (PItem pair) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (pair.getDefinition().getName().equalsIgnoreCase(str)) return true;
                try {
                    int id = Integer.parseInt(str);
                    return pair.getDefinition().getId() == id;
                } catch (NumberFormatException ignored) {
                }
                return false;
            });
        }

        public static Predicate<PItem> nameContainsOrIdEquals(String... namesorids) {
            return (PItem pair) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (pair.getDefinition().getName().toLowerCase().contains(str.toLowerCase())) return true;
                try {
                    int id = Integer.parseInt(str);
                    return pair.getDefinition().getId() == id;
                } catch (NumberFormatException ignored) {
                }
                return false;
            });
        }

        public static Predicate<PItem> nameContains(String... s) {
            return (PItem pair) -> Arrays.stream(s).anyMatch(str -> pair.getDefinition().getName().toLowerCase().contains(str.toLowerCase()));
        }

        public static int getProcessedID(int itemId) {
            return ItemVariationMapping.map(itemId);
        }
    }


    public static class GroundItems {
        public static Predicate<PGroundItem> nameContainsOrIdEquals(String... namesorids) {
            return (PGroundItem item) -> Arrays.stream(namesorids).anyMatch(str -> {
                if (item.getName().toLowerCase().contains(str.toLowerCase())) return true;
                try {
                    int id = Integer.parseInt(str);
                    return item.getId() == id;
                } catch (NumberFormatException ignored) {
                }
                return false;
            });
        }

        public static Predicate<PGroundItem> SlotPriceAtLeast(int minValue) {
            return (PGroundItem item) -> item.getPricePerSlot() >= minValue;
        }
    }
}

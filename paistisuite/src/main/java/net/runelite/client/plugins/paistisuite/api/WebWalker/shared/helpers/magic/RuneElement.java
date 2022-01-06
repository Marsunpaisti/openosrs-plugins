package net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic;


import net.runelite.api.ItemComposition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.Teleport;
import net.runelite.client.plugins.paistisuite.api.types.PItem;

public enum RuneElement {

    AIR("Air", "Smoke", "Mist", "Dust"),
    EARTH("Earth", "Lava", "Mud", "Dust"),
    FIRE("Fire", "Lava", "Smoke", "Steam"),
    WATER("Water", "Mud", "Steam", "Mist"),
    LAW("Law"),
    NATURE("Nature"),
    SOUL("Soul");

    private String[] alternativeNames;
    private boolean hasStaff;
    private int runeCount;

    RuneElement(String... alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String[] getAlternativeNames() {
        return alternativeNames;
    }

    public static void resetAllRuneElements() {
        for (RuneElement rune : RuneElement.values()) {
            rune.resetRuneElement();
        }
    }

    public void resetRuneElement() {
        hasStaff = false;
        runeCount = 0;
    }

    public void checkHasStaff(PItem item) {
        if (hasStaff) {
            return;
        }

        if (item.getDefinition().isMembers() && !Teleport.CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean()) {
            return;
        }

        String name = getItemName(item).toLowerCase();
        if (!name.contains("staff")) {
            return;
        }
        for (String alternativeName : alternativeNames) {
            if (name.contains(alternativeName.toLowerCase())) {
                hasStaff = true;
                return;
            }
        }
    }

    public void checkRuneCount(PItem item) {
        if (item.getDefinition().isMembers() && !Teleport.CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean()) {
            return;
        }

        String name = getItemName(item).toLowerCase();

        if (!name.contains("rune")) {
            return;
        }

        for (String alternativeName : alternativeNames) {
            if (name.startsWith(alternativeName.toLowerCase())) {
                runeCount += item.getFirst().getQuantity();
                return;
            }
        }
    }

    public int getRuneCount() {
        if (hasStaff) {
            return Integer.MAX_VALUE;
        }
        return runeCount + RunePouch.getQuantity(this);
    }

    /**
     * @param item
     * @return item name. Never null. "null" if no name.
     */
    private static String getItemName(PItem item) {
        ItemComposition definition = item.getDefinition();
        String name;
        return definition == null || (name = definition.getName()) == null ? "null" : name;
    }
}
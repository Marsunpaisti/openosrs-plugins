package net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic;


import kotlin.Pair;
import net.runelite.api.ItemDefinition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;

import java.util.Arrays;
import java.util.List;

public enum RuneElement {

    AIR("Air", "Smoke", "Mist", "Dust"),
    EARTH("Earth", "Lava", "Mud", "Dust"),
    FIRE("Fire", "Lava", "Smoke", "Steam"),
    WATER("Water", "Mud", "Steam", "Mist"),
    LAW("Law"),
    NATURE("Nature"),
    SOUL("Soul");

    private String[] alternativeNames;

    RuneElement(String... alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String[] getAlternativeNames() {
        return alternativeNames;
    }

    public int getCount() {
        if (haveStaff()) {
            return Integer.MAX_VALUE;
        }
        List<Pair<WidgetItem, ItemDefinition>> items = PInventory.findAllItems((Pair<WidgetItem, ItemDefinition> pair) -> {
            String name = pair.getSecond().getName().toLowerCase();

            if (!name.contains("rune")) {
                return false;
            }

            for (String alternativeName : alternativeNames) {
                if (name.startsWith(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });

        return items.stream().mapToInt(i -> i.getFirst().getQuantity()).sum() + RunePouch.getQuantity(this);
    }

    private boolean haveStaff() {
        /* TODO:
        return Equipment.find(new Filter<RSItem>() {
            @Override
            public boolean accept(RSItem rsItem) {
                String name = getItemName(rsItem).toLowerCase();
                if (!name.contains("staff")) {
                    return false;
                }
                for (String alternativeName : alternativeNames) {
                    if (name.contains(alternativeName.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }
        }).length > 0;

         */
        return true;
    }

    /**
     * @param item
     * @return item name. Never null. "null" if no name.
     */
    private static String getItemName(Pair<WidgetItem, ItemDefinition> item) {
        ItemDefinition definition = item.getSecond();
        String name;
        return definition == null || (name = definition.getName()) == null ? "null" : name;
    }


}
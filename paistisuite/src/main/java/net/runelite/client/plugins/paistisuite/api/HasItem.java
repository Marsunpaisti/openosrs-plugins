package net.runelite.client.plugins.paistisuite.api;

import net.runelite.client.plugins.paistisuite.api.types.PItem;

import java.util.function.Predicate;

public class HasItem {

    private boolean state = false;
    private final Predicate<PItem> FILTER;

    public HasItem(Predicate<PItem> filter) {
        this.FILTER = filter;
    }

    public void resetState() {
        state = false;
    }

    public void mapHasItem(PItem item) {
        if (!state) {
            state = FILTER.test(item);
        }
    }

    public boolean checkHasItem() {
        return state;
    }
}

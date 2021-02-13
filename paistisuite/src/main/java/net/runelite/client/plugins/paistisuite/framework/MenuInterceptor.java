package net.runelite.client.plugins.paistisuite.framework;

import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOptionClicked;

public class MenuInterceptor {
    private static MenuEntry nextEntry = null;

    public static void setNextEntry(MenuEntry entry){
        nextEntry = entry;
    }

    public static void onMenuOptionClicked(MenuOptionClicked event) {
        if (nextEntry != null) {
            event.setMenuEntry(nextEntry);
            nextEntry = null;
        }
    }
}

package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PMenu {
    public static void addEntry(MenuEntryAdded event, String option) {
        List<MenuEntry> entries = new LinkedList<>(Arrays.asList(PUtils.getClient().getMenuEntries()));

        if (entries.stream().anyMatch(e -> e.getOption().equals(option))) {
            return;
        }

        MenuEntry entry = new MenuEntry();
        entry.setOption(option);
        entry.setTarget(event.getTarget());
        entries.add(0, entry);

        PUtils.getClient().setMenuEntries(entries.toArray(new MenuEntry[0]));
    }

}

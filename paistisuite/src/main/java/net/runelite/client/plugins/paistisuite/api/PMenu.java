package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PMenu {
    public static void addEntry(MenuEntryAdded event, Client client, String option) {
        List<MenuEntry> entries = new LinkedList<>(Arrays.asList(client.getMenuEntries()));

        if (entries.stream().anyMatch(e -> e.getOption().equals(option))) {
            return;
        }

        client.createMenuEntry(-1).setOption(option)
                .setTarget(event.getTarget())
                .setIdentifier(0)
                .setParam1(0)
                .setParam1(0)
                .setType(MenuAction.RUNELITE);
    }
}

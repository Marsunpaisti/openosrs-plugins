package net.runelite.client.plugins.paistisuite.framework;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.plugins.paistisuite.api.PWalking;

@Slf4j
public class MenuInterceptor {
    private static MenuEntry nextEntry = null;

    public static void setNextEntry(MenuEntry entry){
        nextEntry = entry;
    }

    public static void onMenuOptionClicked(MenuOptionClicked event) {
        log.info("Menu: " + event.getMenuOption()
                + " TAR: " + event.getMenuTarget()
                + " ID: " + event.getId()
                + " OP: " + event.getMenuAction()
                + " P0: " + event.getActionParam()
                + " P1: " + event.getWidgetId());

        if (nextEntry == null) return;

        event.consume();
        if (nextEntry.getOption().equals("Walk here") && PWalking.walkAction)
        {
            //log.info("Walk action: {} {}", PWalking.coordX, PWalking.coordY);
            PWalking.setSelectedSceneTile(PWalking.coordX, PWalking.coordY);
            PWalking.walkAction = false;
            nextEntry = null;
            return;
        }

        event.setMenuEntry(nextEntry);
        nextEntry = null;
    }
}

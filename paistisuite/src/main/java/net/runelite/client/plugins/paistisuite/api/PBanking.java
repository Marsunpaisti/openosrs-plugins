package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.InventoryID;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class PBanking {
    public static Boolean isBankOpen(){
        return PUtils.clientOnly(() -> PUtils.getClient().getItemContainer(InventoryID.BANK) != null, "isBankOpen");
    }

    public static boolean closeBank(){
        if (!isBankOpen()) return true;
        Widget bankCloseWidget = PWidgets.get(WidgetInfo.BANK_PIN_EXIT_BUTTON);
        if (bankCloseWidget != null && !bankCloseWidget.isHidden()) {
            return PInteraction.clickWidget(bankCloseWidget);
        }
        return false;
    }

    public static Boolean isDepositBoxOpen(){
        return PUtils.clientOnly(() -> PUtils.getClient().getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER) != null, "isDepositBoxOpen");
    }
}

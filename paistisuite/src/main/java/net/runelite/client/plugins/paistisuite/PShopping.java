package net.runelite.client.plugins.paistisuite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;

import java.util.ArrayList;
import java.util.List;

public class PShopping {
    @AllArgsConstructor
    @Getter
    public static class ShopItem{
        Widget widget;
        String name;
        int quantity;
        int id;
        ItemComposition composition;

        @Override
        public String toString(){
            return name + " x " + quantity;
        }
    }

    public static boolean isShopOpen(){
        return PWidgets.isSubstantiated(300, 16);
    }

    public static boolean closeShop(){
        if (!isShopOpen()) return true;
        Widget parent = PWidgets.get(300, 1);
        if (parent == null) return false;
        PInteraction.widget(parent.getChild(11), "Close");
        return PUtils.waitCondition(1200, () -> !isShopOpen());
    }

    public static List<ShopItem> getShopItems(){
        List<ShopItem> items = new ArrayList<ShopItem>();
        if (!isShopOpen()) return items;
        Widget[] widgets = PWidgets.get(300, 16).getChildren();
        if (widgets == null) return items;

        for (Widget i : widgets){
            if (i.getName() == null || i.getName().length() == 0) continue;
            String strippedName = i.getName().strip().replaceAll("<(.*?)>", "");
            int quantity = i.getItemQuantity();
            int id = i.getId();
            ItemComposition comp = PInventory.getItemDef(id);

            items.add(new ShopItem(i, strippedName, quantity, id, comp));
        }

        return items;
    }

    public static int buyItemFromShop(String name, int quantity){
        if (!isShopOpen()) return 0;
        List<ShopItem> items = getShopItems();

        ShopItem target = null;
        for (ShopItem i : items){
            if (i.getName().equals(name)){
                target = i;
                break;
            }
        }

        if (target == null) return 0;

        if (target.getQuantity() <= 0){
            return 0;
        } else if (target.getQuantity() <= quantity){
            if (PInteraction.widget(target.widget, "Buy 50")){
                return target.getQuantity();
            }
        } else {
            int fiftys = (int) Math.floor(quantity / 50);
            int fives = (int) Math.floor((quantity - 50 * fiftys) / 5);
            int ones = (int) Math.floor((quantity - 50 * fiftys - 5 * fives));
            int bought = 0;
            for (int i = 0; i < fiftys; i++){
                if (PInteraction.widget(target.widget, "Buy 50")){
                    bought += 50;
                }
                PUtils.sleepNormal(400, 800);
            }
            for (int i = 0; i < fives; i++){
                if (PInteraction.widget(target.widget, "Buy 5")){
                    bought += 5;
                }
                PUtils.sleepNormal(400, 800);
            }
            for (int i = 0; i < ones; i++){
                if (PInteraction.widget(target.widget, "Buy 1")){
                    bought += 1;
                }
                PUtils.sleepNormal(400, 800);
            }

            return bought;
        }
        return 0;
    }
}

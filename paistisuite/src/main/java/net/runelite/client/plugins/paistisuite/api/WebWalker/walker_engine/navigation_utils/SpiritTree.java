package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.paistisuite.api.PObjects;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import net.runelite.client.plugins.paistisuite.api.Filters;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

@Slf4j
public class SpiritTree {

    private static final int SPIRIT_TREE_MASTER_INTERFACE = 187;
    private static final int SPIRIT_TREE_CHILD_INTERFACE = 3;

    public enum Location {
        SPIRIT_TREE_GRAND_EXCHANGE("Grand Exchange", 3183, 3508, 0),
        SPIRIT_TREE_STRONGHOLD("Gnome Stronghold", 2461, 3444, 0),
        SPIRIT_TREE_KHAZARD("Battlefield of Khazard", 2555, 3259, 0),
        SPIRIT_TREE_VILLAGE("Tree Gnome Village", 2542, 3170, 0);

        private int x, y, z;
        private String name;
        Location(String name, int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public RSTile getRSTile(){
            return new RSTile(x, y, z);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }

    public static boolean to(Location location){
        PTileObject tree = PObjects.findObject(Filters.Objects.nameEquals("Spirit tree").and(Filters.Objects.actionsContains("Travel")));
        if (tree == null) return false;

        if (!PWidgets.isValid(SPIRIT_TREE_MASTER_INTERFACE, SPIRIT_TREE_CHILD_INTERFACE)
                && !InteractionHelper.click(tree, "Travel", () -> PWidgets.isValid(SPIRIT_TREE_MASTER_INTERFACE, SPIRIT_TREE_CHILD_INTERFACE) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
            return false;
        }
        
        RSInterface option = InterfaceHelper.getAllChildren(187, 3)
                .stream()
                .filter(rsInterface -> {
                    String text = rsInterface.getText();
                    return (text != null && text.contains(location.getName()));
                 })
                .findFirst()
                .orElse(null);


        if (option == null){
            log.info("Option null");
            return false;
        }

        if (!option.click()){
            log.info("Failed to click option");
            return false;
        }

        if (WaitFor.condition(PUtils.random(5400, 6500), () -> location.getRSTile().toWorldPoint().distanceToHypotenuse(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(250, 500);
            return true;
        }
        return false;
    }

}
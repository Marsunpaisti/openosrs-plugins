package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.paistisuite.api.PObjects;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.Point3D;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SpiritTree {

    private static final int SPIRIT_TREE_MASTER_INTERFACE = 187;
    private static final int SPIRIT_TREE_CHILD_INTERFACE = 3;

    public enum Location {
        SPIRIT_TREE_GRAND_EXCHANGE("Grand Exchange", 3183, 3508, 0),
        SPIRIT_TREE_STRONGHOLD("Gnome Stronghold", 2461, 3444, 0),
        SPIRIT_TREE_KHAZARD("Battlefield of Khazard", 2555, 3259, 0),
        SPIRIT_TREE_VILLAGE("Tree Gnome Village", 2542, 3170, 0),
        SPIRIT_TREE_FELDIP("Feldip Hills", 2486, 2849, 0),
        SPIRIT_TREE_PRIFDDINAS("Prifddinas", 3274, 6123, 0),
        SPIRIT_TREE_SARIM("Port Sarim", 3059, 3256, 0, true),
        SPIRIT_TREE_ETCETERIA("Etceteria", 2611, 3857, 0, true),
        SPIRIT_TREE_BRIMHAVEN("Brimhaven", 2800, 3204, 0, true),
        SPIRIT_TREE_HOSIDIUS("Hosidius", 1692, 3540, 0, true),
        SPIRIT_TREE_GUILD("Farming Guild", 1252, 3752, 0, true);

        private final int x, y, z;
        private final String name;
        @Getter
        private boolean farming = false;

        Location(String name, int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }

        Location(String name, int x, int y, int z, boolean farming){
            this(name, x,y,z);
            this.farming = farming;
        }

        private static final Map<Point3D, Location> locationMap = new HashMap<>();

        static {
            for (Location location : Location.values()) {
                locationMap.put(new Point3D(location.getX(), location.getY(), location.getZ()), location);
            }
        }

        public static Location getSpiritTree(Point3D point) {
            return locationMap.get(point);
        }

        public String getName() {
            return name;
        }

        public RSTile getRSTile() {
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

        public Point3D getPoint3D() {
            return new Point3D(x, y, z);
        }
    }

    public static boolean to(Location location) {
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


        if (option == null) {
            log.info("Option null");
            return false;
        }

        if (!option.interact()) {
            log.info("Failed to click option");
            return false;
        }

        if (WaitFor.condition(PUtils.random(5400, 6500), () -> location.getRSTile().toWorldPoint().distanceToHypotenuse(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
            WaitFor.milliseconds(1200, 3000);
            return true;
        }
        return false;
    }

}
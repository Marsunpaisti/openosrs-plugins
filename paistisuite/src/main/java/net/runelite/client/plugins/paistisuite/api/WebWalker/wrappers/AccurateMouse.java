package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWalking;

public class AccurateMouse {

    public static boolean walkTo(RSTile tile){
        Client client = PUtils.getClient();
        if (tile == null) {
            return false;
        }

        if (!PWalking.minimapWalk(tile.toWorldPoint())) {
            return PWalking.sceneWalk(tile.toWorldPoint());
        }

        return true;
    }

    public static boolean walkTo(WorldPoint tile){
        if (tile == null) {
            return false;
        }

        if (!PWalking.minimapWalk(tile)) {
            return PWalking.sceneWalk(tile);
        }

        return true;
    }
}

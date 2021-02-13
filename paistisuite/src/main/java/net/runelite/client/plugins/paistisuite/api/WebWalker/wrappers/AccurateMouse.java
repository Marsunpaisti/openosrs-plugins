package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import net.runelite.api.Client;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWalking;

public class AccurateMouse {

    public static boolean clickMinimap(RSTile tile){
        Client client = PUtils.getClient();
        if (tile == null) {
            return false;
        }

        return PWalking.minimapWalk(tile.toWorldPoint());
    }
}

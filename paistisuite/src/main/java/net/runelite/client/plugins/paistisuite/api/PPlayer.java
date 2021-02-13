package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.PaistiSuite;

public class PPlayer {
    public static Player get() {
        return getPlayer();
    }

    public static Player getPlayer() {
        return PaistiSuite.getInstance().client.getLocalPlayer();
    }

    public static WorldPoint getWorldLocation() {
        if (getPlayer() == null) return null;
        return getPlayer().getWorldLocation();
    }

    public static WorldPoint location(){
        return getWorldLocation();
    }
}

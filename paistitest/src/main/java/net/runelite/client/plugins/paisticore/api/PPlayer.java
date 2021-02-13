package net.runelite.client.plugins.paisticore.api;

import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paisticore.PaistiCore;

public class PPlayer {
    public static Player get() {
        return getPlayer();
    }

    public static Player getPlayer() {
        return PaistiCore.getInstance().client.getLocalPlayer();
    }

    public static WorldPoint getWorldLocation() {
        if (getPlayer() == null) return null;
        return getPlayer().getWorldLocation();
    }

    public static WorldPoint location(){
        return getWorldLocation();
    }
}

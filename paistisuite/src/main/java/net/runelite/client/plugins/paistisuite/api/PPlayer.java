package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

public class PPlayer {
    public static Player get() {
        return getPlayer();
    }

    public static Player getPlayer() {
        if (PUtils.getClient() == null) return null;
        return PUtils.getClient().getLocalPlayer();
    }

    public static WorldPoint getWorldLocation() {
        if (getPlayer() == null) return null;
        return getPlayer().getWorldLocation();
    }

    public static boolean isMoving()
    {
        Player player = PPlayer.get();
        if (player == null)
        {
            return false;
        }
        return player.getIdlePoseAnimation() != player.getPoseAnimation();
    }

    public static double distanceTo(WorldPoint point){
        return PPlayer.getWorldLocation().distanceToHypotenuse((point));
    }

    public static double distanceTo(NPC npc){
        return PPlayer.getWorldLocation().distanceToHypotenuse(npc.getWorldLocation());
    }

    public static double distanceTo(PTileObject to){
        return PPlayer.getWorldLocation().distanceToHypotenuse(to.getWorldLocation());
    }

    public static boolean isRunEnabled()
    {
        return PUtils.getClient().getVarpValue(173) == 1;
    }

    public static WorldPoint location(){
        return getWorldLocation();
    }

    public static int getRunEnergy(){
        return PUtils.getClient().getEnergy();
    }
}

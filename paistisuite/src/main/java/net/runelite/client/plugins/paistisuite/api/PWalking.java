package net.runelite.client.plugins.paistisuite.api;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.framework.MenuInterceptor;
import net.runelite.rs.api.RSClient;

import java.io.IOException;

@Slf4j
public class PWalking {
    public static int coordX;
    public static int coordY;
    public static boolean walkAction;

    private class Path{
        RSTile start;
        RSTile end;
        Player player;
    }
    /**
     * Do not call directly
     **/
    public static void setSelectedSceneTile(int x, int y)
    {
        RSClient rsClient = (RSClient) PUtils.getClient();
        rsClient.setSelectedSceneTileX(x);
        rsClient.setSelectedSceneTileY(y);
        rsClient.setViewportWalking(true);
        rsClient.setCheckClick(false);
    }

    public static boolean minimapWalk(WorldPoint worldPoint) {
        if (worldPoint.distanceToHypotenuse(PPlayer.getWorldLocation()) > 18 || worldPoint.getPlane() != PUtils.getClient().getPlane()){
            log.error("Point given to minimapwalk is outside minimap");
            return false;
        }
        LocalPoint localPoint = LocalPoint.fromWorld(PUtils.getClient(), worldPoint);
        if (localPoint == null) {
            log.error("LocalPoint is null in scenewalk");
            return false;
        }


        Point minimapPoint = null;
        try {
            minimapPoint = PaistiSuite.getInstance().clientExecutor.scheduleAndWait(() -> Perspective.localToMinimap(PUtils.getClient(), localPoint), "LocalToMinimap");
        } catch (Exception e){
            log.error(e.toString());
            return false;
        }
        if (minimapPoint == null){
            log.error("MinimapPoint is null in scenewalk");
            return false;
        }

        coordX = localPoint.getSceneX();
        coordY = localPoint.getSceneY();
        PWalking.walkAction = true;
        MenuInterceptor.setNextEntry(new MenuEntry("Walk here", "", 0, MenuOpcode.WALK.getId(),
                0, 0, false));
        PMouse.clickPoint(minimapPoint);
        return true;
    }
}

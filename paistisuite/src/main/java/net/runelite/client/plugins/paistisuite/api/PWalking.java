package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.framework.MenuInterceptor;
import net.runelite.rs.api.RSClient;

@Slf4j
public class PWalking {
    public static int coordX;
    public static int coordY;
    public static boolean walkAction;

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
        if (worldPoint.distanceToHypotenuse(PPlayer.getWorldLocation()) > 19 || worldPoint.getPlane() != PUtils.getClient().getPlane()){
            log.error("Point given to minimapwalk is outside minimap");
            return false;
        }
        LocalPoint localPoint = LocalPoint.fromWorld(PUtils.getClient(), worldPoint);
        if (localPoint == null) {
            log.error("LocalPoint is null in scenewalk");
            return false;
        }


        Point minimapPoint = Perspective.localToMinimap(PUtils.getClient(), localPoint);
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

package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;

import kotlin.Pair;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

import java.util.Arrays;
import java.util.Objects;

public class ShipUtils {

    private static final RSTile[] SPECIAL_CASES = new RSTile[]{new RSTile(2663, 2676, 1)};

    public static boolean isOnShip() {
        for (RSTile specialCase : SPECIAL_CASES){
            if (PPlayer.location().distanceTo(specialCase.toWorldPoint()) <= 5) return true;
        }

        return getGangplank() != null
                && new RSTile(PPlayer.location()).getPlane() == 1
                && PObjects.getAllObjects()
                .stream()
                .filter(ob -> ob.getWorldLocation().distanceToHypotenuse(PPlayer.location()) <= 10)
                .anyMatch(ob ->
                        Arrays.asList("Ship's wheel", "Ship's ladder", "Anchor")
                        .stream()
                        .anyMatch(name -> ob.getSecond().getName().equalsIgnoreCase(name))
                );
    }

    public static boolean crossGangplank() {
        PTileObject gangplank = getGangplank();
        if (gangplank == null){
            return false;
        }
        if (!PInteraction.tileObject(gangplank, "Cross")) {
            return false;
        }
        return WaitFor.condition(PUtils.random(3500, 4500), () -> !ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static PTileObject getGangplank(){
        return PObjects.findObject(Filters.Objects.nameEquals("Gangplank").and(Filters.Objects.actionsContains("Cross")));
    }

}

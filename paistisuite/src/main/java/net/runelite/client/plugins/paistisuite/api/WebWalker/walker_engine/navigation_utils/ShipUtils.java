package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;

import kotlin.Pair;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PObjects;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

import java.lang.reflect.Array;
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
                && PObjects.getAllObjectsWithDefs()
                .stream()
                .filter(pair -> pair.getFirst().getWorldLocation().distanceToHypotenuse(PPlayer.location()) <= 10)
                .anyMatch(pair ->
                        Arrays.asList("Ship's wheel", "Ship's ladder", "Anchor")
                        .stream()
                        .anyMatch(name -> pair.getSecond().getName().equals(name))
                );
    }

    public static boolean crossGangplank() {
        TileObject gangplank = getGangplank();
        if (gangplank == null){
            return false;
        }
        if (!PInteraction.tileObject(gangplank, "Cross")) {
            return false;
        }
        return WaitFor.condition(PUtils.random(2500, 3000), () -> !ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static TileObject getGangplank(){
        var optional = PObjects.getAllObjectsWithDefs()
                .stream()
                .filter(pair -> pair.getSecond().getName().equalsIgnoreCase("Gangplank"))
                .filter(pair -> Arrays.stream(pair.getSecond().getActions())
                        .filter(Objects::nonNull)
                        .anyMatch(s -> s.equals("Cross")))
                .findFirst();
        return optional.map(Pair<TileObject, ObjectDefinition>::getFirst).orElse(null);
    }

}

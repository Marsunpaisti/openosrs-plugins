package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSVarBit;

public class TeleportConstants {
    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 30;

    public static final int
            GE_TELEPORT_VARBIT = 4585, SPELLBOOK_INTERFACE_MASTER = 218, SCROLL_INTERFACE_MASTER = 187;

    private static int getWildernessLevel() {
        return PUtils.getWildernessLevelFrom(PPlayer.getWorldLocation());
    }

    public static boolean isVarrockTeleportAtGE(){
        return RSVarBit.get(GE_TELEPORT_VARBIT).getValue() > 0;
    }
}
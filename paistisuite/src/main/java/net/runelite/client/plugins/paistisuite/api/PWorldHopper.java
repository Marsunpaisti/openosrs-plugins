package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.client.plugins.paistisuite.PShopping;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.util.WorldUtil;
import net.runelite.client.plugins.paistisuite.api.PUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

@Slf4j
public class PWorldHopper {
    private static World findWorld(List<World> worlds, EnumSet<WorldType> currentWorldTypes, int totalLevel, int currentLocation)
    {
        World world = worlds.get(new Random().nextInt(worlds.size()));

        EnumSet<WorldType> types = world.getTypes().clone();

        types.remove(WorldType.LAST_MAN_STANDING);

        if (types.contains(WorldType.SKILL_TOTAL))
        {
            try
            {
                int totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));

                if (totalLevel >= totalRequirement)
                {
                    types.remove(WorldType.SKILL_TOTAL);
                }
            }
            catch (NumberFormatException ex)
            {
                log.warn("Failed to parse total level requirement for target world", ex);
            }
        }

        if (currentWorldTypes.equals(types))
        {
            int worldLocation = world.getLocation();

            if (worldLocation == currentLocation)
            {
                return world;
            }
        }

        return null;
    }

    public static void hop()
    {
        WorldResult worldResult = PaistiSuite.getInstance().worldService.getWorlds();
        if (worldResult == null || PUtils.getClient().getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        World currentWorld = worldResult.findWorld(PUtils.getClient().getWorld());
        log.info("Current world: {}", currentWorld.getLocation());
        if (currentWorld == null)
        {
            return;
        }

        EnumSet<WorldType> currentWorldTypes = currentWorld.getTypes().clone();

        currentWorldTypes.remove(WorldType.PVP);
        currentWorldTypes.remove(WorldType.HIGH_RISK);
        currentWorldTypes.remove(WorldType.BOUNTY);
        currentWorldTypes.remove(WorldType.SKILL_TOTAL);
        currentWorldTypes.remove(WorldType.LAST_MAN_STANDING);

        List<World> worlds = worldResult.getWorlds();

        int totalLevel = PUtils.getClient().getTotalLevel();

        World world;
        do
        {
            world = findWorld(worlds, currentWorldTypes, totalLevel, currentWorld.getLocation());
        }
        while (world == null || world == currentWorld);

        hop(world.getId());
    }

    private static void hop(int worldId)
    {
        if (PBanking.isBankOpen()) PBanking.closeBank();
        if (PShopping.isShopOpen()) PShopping.closeShop();
        WorldResult worldResult = PaistiSuite.getInstance().worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        World world = worldResult.findWorld(worldId);
        if (world == null)
        {
            return;
        }

        final net.runelite.api.World rsWorld = PUtils.getClient().createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (PUtils.getClient().getGameState() == GameState.LOGIN_SCREEN)
        {
            PUtils.getClient().changeWorld(rsWorld);
            return;
        }
        log.info("Hopping to world: " + world.getId());

        if (PWidgets.get(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            PUtils.clientOnly(() -> {
                PUtils.getClient().openWorldHopper();
                return null;
            }, "openWorldHopper");
            PUtils.waitCondition(1900, () -> PWidgets.isSubstantiated(WidgetInfo.WORLD_SWITCHER_LIST));
            PUtils.sleepNormal(700, 1200);
        }

        PUtils.clientOnly(() -> {
            PUtils.getClient().hopToWorld(rsWorld);
            return null;
        }, "hopToWorld");

        PUtils.sleepNormal(900, 1800);
        PUtils.waitCondition(1900, () -> PUtils.getClient().getGameState() == GameState.LOGGED_IN);
        PUtils.sleepNormal(900, 1800);
    }

}

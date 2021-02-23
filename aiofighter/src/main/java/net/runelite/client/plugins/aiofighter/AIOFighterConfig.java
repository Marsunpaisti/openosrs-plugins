package net.runelite.client.plugins.aiofighter;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.*;

@ConfigGroup("AIOFighter")
public interface AIOFighterConfig extends Config
{
    @ConfigItem(
            keyName = "enableOverlay",
            name = "Enable overlay",
            description = "Enable drawing of the overlay",
            position = 10
    )
    default boolean enableOverlay()
    {
        return false;
    }

    @ConfigTitle(
            name = "Targeting",
            description = "Enter enemy names/ids to target",
            position = 15
    )
    String targetingTitle = "Targeting";

    @ConfigItem(
            keyName = "enemyNames",
            name = "",
            description = "",
            title = targetingTitle,
            position = 16
    )
    default String enemyNames()
    {
        return "Goblin, Cow";
    }

    @Range(
            min = 1,
            max = 20
    )
    @ConfigItem(
            keyName = "searchRadius",
            name = "Search radius",
            description = "The distance (in tiles) to search for targets.",
            title = targetingTitle,
            position = 17
    )
    default int searchRadius()
    {
        return 10;
    }

    @ConfigItem(
            keyName = "enablePathfind",
            name = "Check pathfinding",
            description = "Enable to also check that a valid path to target can be found. May impact performance.",
            title = targetingTitle,
            position = 18
    )
    default boolean enablePathfind()
    {
        return false;
    }

    @ConfigItem(
            keyName = "setFightAreaButton",
            name = "Set fighting area",
            description = "Set fighting area to where you are standing",
            title = targetingTitle,
            position = 19
    )
    default Button setFightAreaButton()
    {
        return new Button();
    }

    @ConfigTitle(
            name = "Eating",
            description = "Enter food names/ids to eat",
            position = 15
    )
    String eatingTitle = "Eating";

    @ConfigItem(
            keyName = "foodNames",
            name = "",
            description = "Food names or IDs to eat",
            title = eatingTitle,
            position = 21
    )
    default String foodNames()
    {
        return "Shrimps, Cabbage";
    }

    @ConfigItem(
            keyName = "minEatHP",
            name = "Minimum Eat HP",
            description = "Minimum HP to eat. Bot will always eat below this value.",
            title = eatingTitle,
            position = 25

    )
    default int minEatHP()
    {
        return 10;
    }

    @ConfigItem(
            keyName = "maxEatHP",
            name = "Maximum Eat HP",
            description = "Highest HP that bot sometimes eats at (random between min and max eat hp)",
            title = eatingTitle,
            position = 30
    )
    default int maxEatHP()
    {
        return 20;
    }

    @ConfigItem(
            keyName = "stopWhenOutOfFood",
            name = "Stop when out of food",
            description = "Stops and logs out when out of food",
            title = eatingTitle,
            position = 31
    )
    default boolean stopWhenOutOfFood()
    {
        return false;
    }

    @ConfigTitle(
            name = "Looting",
            description = "Enter loot names/ids to pick up",
            position = 15
    )
    String lootingTitle = "Looting";

    @ConfigItem(
            keyName = "lootNames",
            name = "",
            description = "",
            title = lootingTitle,
            position = 51
    )
    default String lootNames()
    {
        return "Clue, champion";
    }

    @ConfigItem(
            keyName = "lootGEValue",
            name = "Loot if value>X",
            description = "Loot items that are more valuable than X. 0 to disable",
            title = lootingTitle,
            position = 52
    )
    default int lootGEValue()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "lootOwnKills",
            name = "Only loot your kills",
            description = "Makes the bot ignore drops from other players.",
            title = lootingTitle,
            position = 53
    )
    default boolean lootOwnKills()
    {
        return false;
    }

    @ConfigItem(
            keyName = "eatForLoot",
            name = "Eat to make space for loot",
            description = "Makes the bot eat food to get more inventory space for loot.",
            title = lootingTitle,
            position = 54
    )
    default boolean eatForLoot()
    {
        return false;
    }

    @ConfigTitle(
            name = "Safe spotting",
            description = "Enter enemy names/ids to target",
            position = 15
    )
    String safespotTitle = "Safe spotting";

    @ConfigItem(
            keyName = "setSafeSpotButton",
            name = "Set safespot tile",
            description = "Set safespot tile where you are standing",
            title = safespotTitle,
            position = 71
    )
    default Button setSafeSpotButton()
    {
        return new Button();
    }
    @ConfigItem(
            keyName = "enableSafeSpot",
            name = "Use safespot for combat",
            description = "Run to safespot tile in combat",
            title = safespotTitle,
            position = 72
    )
    default boolean enableSafeSpot()
    {
        return false;
    }
    @ConfigItem(
            keyName = "exitInSafeSpot",
            name = "Safespot when stopping",
            description = "Run to safespot and logout when stopping script",
            title = safespotTitle,
            position = 73
    )
    default boolean exitInSafeSpot()
    {
        return false;
    }



    @ConfigItem(
            keyName = "startButton",
            name = "Start",
            description = "Start",
            position = 101
    )
    default Button startButton()
    {
        return new Button();
    }

    @ConfigItem(
            keyName = "stopButton",
            name = "Stop",
            description = "Stop",
            position = 102
    )
    default Button stopButton()
    {
        return new Button();
    }

    @ConfigItem (
            keyName = "storedFightTile",
            hidden = true,
            name = "Stored fight tile",
            description = "Used to save last used fight tile",
            position = 103
    )
    default WorldPoint storedFightTile()
    {
        return null;
    }

    @ConfigItem (
            keyName = "storedSafeSpotTile",
            hidden = true,
            name = "Stored safe spot tile",
            description = "Used to save last used safe spot tile",
            position = 104
    )
    default WorldPoint storedSafeSpotTile()
    {
        return null;
    }
}


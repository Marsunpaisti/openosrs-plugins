package net.runelite.client.plugins.aiofighter;

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

    @ConfigSection(
            keyName = "targetingTitle",
            name = "Targeting",
            description = "Enter enemy names or IDs to target",
            position = 15
    )
    default boolean targetingTitle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "enemyNames",
            name = "",
            description = "",
            section = "targetingTitle",
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
            section = "targetingTitle",
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
            section = "targetingTitle",
            position = 18
    )
    default boolean enablePathfind()
    {
        return false;
    }

    @ConfigSection(
            keyName = "eatingTitle",
            name = "Eating",
            description = "Eating options. Enter food names or IDs to eat.",
            position = 20
    )
    default boolean eatingTitle()
    {
        return false;
    }
    @ConfigItem(
            keyName = "foodNames",
            name = "",
            description = "Food names or IDs to eat",
            section = "eatingTitle",
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
            section = "eatingTitle",
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
            section = "eatingTitle",
            position = 30
    )
    default int maxEatHP()
    {
        return 20;
    }

    @ConfigSection(
            keyName = "lootingTitle",
            name = "Looting",
            description = "Always loot item names whose names contain",
            position = 50
    )
    default boolean lootingTitle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "lootNames",
            name = "",
            description = "",
            section = "lootingTitle",
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
            section = "lootingTitle",
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
            section = "lootingTitle",
            position = 53
    )
    default boolean lootOwnKills()
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


}


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
            description = "Enter enemy names to target",
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
            description = "Eating options",
            position = 20
    )
    default boolean eatingTitle()
    {
        return false;
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


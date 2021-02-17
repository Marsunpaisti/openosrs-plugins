package net.runelite.client.plugins.aiofighter;

import net.runelite.client.config.*;

@ConfigGroup("AIOFighter")
public interface AIOFighterConfig extends Config
{
    @ConfigSection(
            keyName = "instructionsTitle",
            name = "Instructions",
            description = "Instructions. Don't enter anything into this field",
            position = 15
    )
    default boolean instructionsTitle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions. Don't enter anything into this field",
            position = 18,
            section = "instructionsTitle"
    )
    default String instructions()
    {
        return "Instructions woop woop";
    }

    @ConfigItem(
            keyName = "enableOverlay",
            name = "Enable overlay",
            description = "Enable drawing of the overlay",
            position = 145
    )
    default boolean enableOverlay()
    {
        return false;
    }

    @ConfigSection(
            keyName = "enemyNamesTitle",
            name = "Enemy names",
            description = "Enter enemy names to target",
            position = 15
    )
    default boolean enemyNamesTitle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "enemyNames",
            name = "",
            description = "",
            position = 18,
            section = "enemyNamesTitle"
    )
    default String enemyNames()
    {
        return "Goblin, Cow";
    }

    @Range(
            min = 1,
            max = 64
    )
    @ConfigItem(
            keyName = "searchRadius",
            name = "Search radius",
            description = "The distance (in tiles) to search for targets.",
            position = 31,
            hide = "dropInventory",
            titleSection = "generalTitle"
    )
    default int searchRadius()
    {
        return 10;
    }

    @ConfigItem(
            keyName = "startButton",
            name = "Start",
            description = "Start fighter",
            position = 151
    )
    default Button startButton()
    {
        return new Button();
    }

    @ConfigItem(
            keyName = "stopButton",
            name = "Stop",
            description = "Stop",
            position = 151
    )
    default Button stopButton()
    {
        return new Button();
    }


}


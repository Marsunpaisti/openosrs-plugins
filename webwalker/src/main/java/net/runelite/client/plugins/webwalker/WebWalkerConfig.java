package net.runelite.client.plugins.webwalker;

import net.runelite.client.config.*;

@ConfigGroup("WebWalker")
public interface WebWalkerConfig extends Config
{
    @ConfigSection(
            keyName = "instructionsTitle",
            name = "Instructions",
            description = "Instructions Title",
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
        return "Select your location from the drop-down or enter a custom location using x,y,z format. Use Location/Tile Location in Developer Tools to obtain a custom coordinate.";
    }
}


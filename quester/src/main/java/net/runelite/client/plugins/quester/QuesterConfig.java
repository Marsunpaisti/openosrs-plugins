package net.runelite.client.plugins.quester;

import net.runelite.client.config.*;

@ConfigGroup("Quester")
public interface QuesterConfig extends Config
{
    @ConfigItem(
            keyName = "startButton",
            name = "Start",
            description = "Start quester",
            position = 151
    )
    default Button startButton()
    {
        return new Button();
    }

    @ConfigItem(
            keyName = "stopButton",
            name = "Stop",
            description = "Stop quester",
            position = 152
    )
    default Button stopButton()
    {
        return new Button();
    }
}


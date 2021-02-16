package net.runelite.client.plugins.paistisuite;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PaistiSuite")
public interface PaistiSuiteConfig extends Config {
    @ConfigItem(
            keyName = "daxApiKey",
            name = "Dax API Key",
            description = "Key to use for dax's webwalker requests",
            position = 110,
            hidden = false,
            unhide = "category",
            unhideValue = "CUSTOM"
    )
    default String daxApiKey()
    {
        return "sub_DPjXXzL5DeSiPf";
    }

    @ConfigItem(
            keyName = "daxSecretKey",
            name = "Dax API key secret",
            description = "Secret to use for dax's webwalker requests",
            position = 135,
            hidden = false,
            unhide = "category",
            unhideValue = "CUSTOM"
    )
    default String daxSecretKey()
    {
        return "PUBLIC-KEY";
    }
}

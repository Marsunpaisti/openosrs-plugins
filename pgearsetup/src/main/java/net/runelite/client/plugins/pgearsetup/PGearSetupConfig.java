package net.runelite.client.plugins.pgearsetup;

import net.runelite.client.config.*;

@ConfigGroup("PGearSetup")
public interface PGearSetupConfig extends Config {
    @ConfigItem(
            keyName = "resetSetups",
            name = "Reset gear setups",
            description = "Reset all gear setups",
            position = 98
    )
    default Button resetSetups()
    {
        return new Button();
    }

    @ConfigItem(
            keyName = "gearSetupsSerializedStore",
            name = "Gear setups",
            description = "Stored gear setups",
            position = 100,
            hidden = true
    )


    default String gearSetupsSerializedStore()
    {
        return null;
    }
}

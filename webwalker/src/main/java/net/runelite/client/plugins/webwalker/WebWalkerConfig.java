package net.runelite.client.plugins.webwalker;

import net.runelite.client.config.*;
import net.runelite.client.plugins.webwalker.farming.*;

@ConfigGroup("WebWalker")
public interface WebWalkerConfig extends Config
{
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

    default boolean notesTitle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "notepad",
            name = "",
            description = "Paste custom coords that you want to save for frequent use",
            section = "notesTitle",
            position = 30
    )
    default String notepad()
    {
        return "Paste custom co-ords that you want to save for frequent use";
    }

    @ConfigItem(
            keyName = "category",
            name = "Category",
            description = "Select the category of destinations",
            position = 100
    )
    default Category category()
    {
        return Category.NONE;
    }

    @ConfigItem(
            keyName = "catBanks",
            name = "Location",
            description = "Select the location to walk to",
            position = 101,
            hidden = true
    )
    default Banks catBanks()
    {
        return Banks.NONE;
    }

    @ConfigItem(
            keyName = "catBarcrawl",
            name = "Location",
            description = "Select the location to walk to",
            position = 102,
            hidden = true
    )
    default Barcrawl catBarcrawl()
    {
        return Barcrawl.NONE;
    }

    @ConfigItem(
            keyName = "catCities",
            name = "Location",
            description = "Select the location to walk to",
            position = 103,
            hidden = true
    )
    default Cities catCities()
    {
        return Cities.NONE;
    }

    @ConfigItem(
            keyName = "catFarming",
            name = "Patch Type",
            description = "Select the Farming category you want",
            position = 110,
            hidden = true
    )
    default Farming catFarming()
    {
        return Farming.NONE;
    }

    @ConfigItem(
            keyName = "catFarmAllotments",
            name = "Patch",
            description = "Select the location to walk to",
            position = 111,
            hidden = true
    )
    default Allotments catFarmAllotments()
    {
        return Allotments.NONE;
    }

    @ConfigItem(
            keyName = "catFarmBushes",
            name = "Patch",
            description = "Select the location to walk to",
            position = 112,
            hidden = true
    )
    default Bushes catFarmBushes()
    {
        return Bushes.NONE;
    }

    @ConfigItem(
            keyName = "catFarmFruitTrees",
            name = "Patch",
            description = "Select the location to walk to",
            position = 113,
            hidden = true
    )
    default FruitTrees catFarmFruitTrees()
    {
        return FruitTrees.NONE;
    }

    @ConfigItem(
            keyName = "catFarmHerbs",
            name = "Patch",
            description = "Select the location to walk to",
            position = 114,
            hidden = true
    )
    default Herbs catFarmHerbs()
    {
        return Herbs.NONE;
    }

    @ConfigItem(
            keyName = "catFarmHops",
            name = "Patch",
            description = "Select the location to walk to",
            position = 115,
            hidden = true
    )
    default Hops catFarmHops()
    {
        return Hops.NONE;
    }

    @ConfigItem(
            keyName = "catFarmTrees",
            name = "Patch",
            description = "Select the location to walk to",
            position = 116,
            hidden = true
    )
    default Trees catFarmTrees()
    {
        return Trees.NONE;
    }

    @ConfigItem(
            keyName = "catGuilds",
            name = "Location",
            description = "Select the location to walk to",
            position = 103,
            hidden = true
    )
    default Guilds catGuilds()
    {
        return Guilds.NONE;
    }

    @ConfigItem(
            keyName = "catSkilling",
            name = "Location",
            description = "Select the location to walk to",
            position = 104,
            hidden = true
    )
    default Skilling catSkilling()
    {
        return Skilling.NONE;
    }

    @ConfigItem(
            keyName = "catSlayer",
            name = "Location",
            description = "Select the location to walk to",
            position = 105,
            hidden = true
    )
    default Slayer catSlayer()
    {
        return Slayer.NONE;
    }

    @ConfigItem(
            keyName = "catMisc",
            name = "Location",
            description = "Select the location to walk to",
            position = 106,
            hidden = true
    )
    default Misc catMisc()
    {
        return Misc.NONE;
    }

    @ConfigItem(
            keyName = "customLocation",
            name = "Custom Location",
            description = "Enter a Coordinate to walk to. Co-ordinate format should be x,y,z. \nTurn on Location or Tile Location in Developer Tools to obtain coordinates.",
            position = 135,
            hidden = true
    )
    default String customLocation()
    {
        return "0,0,0";
    }

    @ConfigItem(
            keyName = "disableRun",
            name = "Disable Running",
            description = "Disable running to arrive at your destination with 100% energy.",
            position = 145
    )
    default boolean disableRun()
    {
        return false;
    }

    @ConfigItem(
            keyName = "allowTeleports",
            name = "Allow Teleporting",
            description = "Allows walker to use teleport items in your inventory to travel.",
            position = 145
    )
    default boolean allowTeleports()
    {
        return false;
    }


    @ConfigItem(
            keyName = "startButton",
            name = "Start",
            description = "Start walker",
            position = 151
    )
    default Button startButton()
    {
        return new Button();
    }

    @ConfigItem(
            keyName = "stopButton",
            name = "Stop",
            description = "Stop walker",
            position = 151
    )
    default Button stopButton()
    {
        return new Button();
    }

}


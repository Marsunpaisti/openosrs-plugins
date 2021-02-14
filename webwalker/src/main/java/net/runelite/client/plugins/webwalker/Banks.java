/*
 * Copyright (c) 2019-2020, ganom <https://github.com/Ganom>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package net.runelite.client.plugins.webwalker;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Banks
{
	NONE(""),
	AL_KHARID("Al kharid", 3269, 3167, 0),
	ARCEUUS("Arceuus", 1624, 3745, 0),
	ARDOUGNE_NORTH("Ardougne north", 2616, 3332, 0),
	ARGOUDNE_SOUTH("Ardougne south", 2655, 3283, 0),
	BARBARIAN_OUTPOST("Barbarian outpost", 2536, 3574, 0),
	BLAST_FURNACE_BANK("Blast furnace", 1948, 4957, 0),
	BLAST_MINE("Blast mine", 1502, 3856, 0),
	CAMELOT("Camelot", 2725, 3493, 0),
	CANIFIS("Canifis",3512, 3480, 0),
	CASTLE_WARS("Castle Wars", 2443, 3083, 0),
	CATHERBY("Catherby", 2808, 3441, 0),
	DIHN_BANK("Dihn", 1640, 3944, 0),
	DRAYNOR("Draynor", 3092, 3243, 0),
	DUEL_ARENA("Duel arena", 3381, 3268, 0),
	DWARF_MINE_BANK("Dwarf mine", 2837, 10207, 0),
	EDGEVILLE("Edgeville", 3094, 3492, 0),
	FALADOR_EAST("Falador east",3013, 3355, 0),
	FALADOR_WEST("Falador west", 2946, 3368, 0),
	FARMING_GUILD("Farming guild", 1253, 3741, 0),
	FEROX_ENCLAVE("Ferox enclave", 3130, 3631, 0),
	FISHING_GUILD("Fishing guild", 2586, 3420, 0),
	FOSSIL_ISLAND("Fossil island", 3739, 3804, 0),
	GNOME_BANK("Gnome bank",2445, 3425, 1),
	GNOME_TREE_BANK_SOUTH("Gnome tree south", 2449, 3482, 1),
	GNOME_TREE_BANK_WEST("Gnome tree west", 2442, 3488, 1),
	GRAND_EXCHANGE("Grand Exchange", 3164, 3487, 0),
	GREAT_KOUREND_CASTLE("Great Kourend castle", 1612, 3681, 2),
	HOSIDIUS("Hosidius", 1749, 3599, 0),
	HOSIDIUS_KITCHEN("Hosidius kitchen", 1676, 3617, 0),
	JATIZSO("Jatizso", 2416, 3801, 0),
	LANDS_END("Lands end", 1512, 3421, 0),
	LOVAKENGJ("Lovakengj", 1526, 3739, 0),
	LUMBRIDGE_BASEMENT("Lumbridge basement", 3218, 9623, 0),
	LUMBRIDGE_TOP("Lumbridge top", 3208, 3220, 2),
	LUNAR_ISLE("Lunar isle", 2099, 3919, 0),
	MOTHERLOAD("Motherlode", 3760, 5666, 0),
	MOUNT_KARUULM("Mount Karuulm", 1324, 3824, 0),
	NARDAH("Nardah", 3428, 2892, 0),
	NEITIZNOT("Neitiznot", 2337, 3807, 0),
	PEST_CONTROL("Pest control", 2667, 2653, 0),
	PISCARILIUS("Piscarilius", 1803, 3790, 0),
	PRIFDDINAS("Prifiddinas", 3257, 6106, 0),
	ROGUES_DEN("Rogues den", 3043, 4973, 1),
	SHANTY_PASS("Shantay pass", 3308, 3120, 0),
	SHAYZIEN("Shayzien", 1504, 3615, 0),
	SHAYZIEN_BANK("Shayzien bank", 1504, 3615, 0),
	SHILO_VILLAGE("Shilo village", 2852, 2954, 0),
	SOPHANEM("Sophanem", 2799, 5169, 0),
	SULPHUR_MINE("Sulphur mine", 1453, 3858, 0),
	TZHAAR("Tzhaar", 2446, 5178, 0),
	VARROCK_EAST("Varrock east",3253, 3420, 0),
	VARROCK_WEST("Varrock west", 3185, 3441, 0),
	VINERY("Vinery", 1808, 3570, 0),
	VINERY_BANK("Vinery bank", 1809, 3566, 0),
	WINTERTODT("Wintertodt", 1640, 3944, 0),
	WOODCUTTING_GUILD("Woodcutting guild", 1591, 3479, 0),
	YANILLE("Yanille", 2613, 3093, 0),
	ZANARIS("Zanaris", 2383, 4458, 0),
	ZEAH_SAND_BANK("Zeah sand bank", 1719, 3465, 0)
	;
	private final String name;
	private WorldPoint worldPoint;

	Banks(String name)
	{
		this.name = name;
	}

	Banks(String name, int x, int y, int plane)
	{
		this.name = name;
		this.worldPoint = new WorldPoint(x, y, plane);
	}
}

package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportConstants;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportLimit;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportScrolls;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.Requirement;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.RSItemHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic.RuneElement;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic.RunePouch;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic.Spell;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic.SpellBook;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Keyboard;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSVarBit;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.paistisuite.api.types.Spells;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
public enum Teleport {
    VARROCK_TELEPORT(
            TeleportType.TELEPORT_SPELL, new RSTile(3212, 3424, 0),
            Spell.VARROCK_TELEPORT::canUse,
            () -> castSpell("Varrock Teleport", "Cast")
    ),

    VARROCK_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(3212, 3424, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.VARROCK_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Varrock t.*", "Break")
    ),

    VARROCK_TELEPORT_GRAND_EXCHANGE(
            TeleportType.TELEPORT_SPELL, new RSTile(3161, 3478, 0),
            () -> Spell.VARROCK_TELEPORT.canUse() && TeleportConstants.isVarrockTeleportAtGE(),
            () -> castSpell("Varrock Teleport", "Grand Exchange")
    ),

    LUMBRIDGE_TELEPORT(
            TeleportType.TELEPORT_SPELL, new RSTile(3225, 3219, 0),
            Spell.LUMBRIDGE_TELEPORT::canUse,
            () -> castSpell("Lumbridge Teleport", "Cast")
    ),

    LUMBRIDGE_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(3225, 3219, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.LUMBRIDGE_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Lumbridge t.*", "Break")
    ),

    FALADOR_TELEPORT(
            TeleportType.TELEPORT_SPELL, new RSTile(2966, 3379, 0),
            Spell.FALADOR_TELEPORT::canUse,
            () -> castSpell("Falador Teleport", "Cast")
    ),

    FALADOR_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2966, 3379, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.FALADOR_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Falador t.*", "Break")
    ),

    CAMELOT_TELEPORT(
            TeleportType.TELEPORT_SPELL, new RSTile(2757, 3479, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && Spell.CAMELOT_TELEPORT.canUse(),
            () -> castSpell("Camelot Teleport", "Cast")

    ),

    CAMELOT_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2757, 3479, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CAMELOT_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Camelot t.*", "Break")
    ),

    SEERS_TELEPORT(
            TeleportType.TELEPORT_SPELL, new RSTile(2757, 3479, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && Spell.CAMELOT_TELEPORT.canUse() && RSVarBit.get(4560).getValue() == 1,
            () -> castSpell("Camelot Teleport", "Seers'")
    ),

    ARDOUGNE_TELEPORT(
            TeleportType.TELEPORT_SPELL, new RSTile(2661, 3300, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && Spell.ARDOUGNE_TELEPORT.canUse(),
            () -> castSpell("Ardougne Teleport", "Cast")

    ),

    ARDOUGNE_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2661, 3300, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.ARDOUGNE_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Ardougne t.*", "Break")
    ),

    NARDAH_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.NARDAH, HasItems.NARDAH
    ),
    DIGSITE_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.DIGSITE, HasItems.DIGSITE
    ),
    FELDIP_HILLS_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.FELDIP_HILLS, HasItems.FELDIP_HILLS
    ),
    LUNAR_ISLE_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.LUNAR_ISLE, HasItems.LUNAR_ISLE
    ),
    MORTTON_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.MORTTON, HasItems.MORTTON
    ),
    PEST_CONTROL_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.PEST_CONTROL, HasItems.PEST_CONTROL
    ),
    PISCATORIS_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.PISCATORIS, HasItems.PISCATORIS
    ),
    TAI_BWO_WANNAI_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.TAI_BWO_WANNAI, HasItems.TAI_BWO_WANNAI
    ),
    ELF_CAMP_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.ELF_CAMP, HasItems.ELF_CAMP
    ),
    MOS_LE_HARMLESS_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.MOS_LE_HARMLESS, HasItems.MOS_LE_HARMLESS
    ),
    LUMBERYARD_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.LUMBERYARD, HasItems.LUMBERYARD
    ),
    ZULANDRA_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.ZULANDRA, HasItems.ZULANDRA
    ),
    KEY_MASTER_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.KEY_MASTER, HasItems.KEY_MASTER
    ),
    REVENANT_CAVES_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.REVENANT_CAVES, HasItems.REVENANT_CAVES
    ),
    WATSON_TELEPORT(
            TeleportType.TELEPORT_SCROLL, TeleportScrolls.WATSON, HasItems.WATSON
    ),

    RING_OF_WEALTH_GRAND_EXCHANGE(
            TeleportType.RECHARGABLE_TELE, new RSTile(3161, 3478, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RING_OF_WEALTH_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)Grand Exchange"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    RING_OF_WEALTH_FALADOR(
            TeleportType.RECHARGABLE_TELE, new RSTile(2994, 3377, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RING_OF_WEALTH_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)falador.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    RING_OF_WEALTH_MISCELLANIA(
            TeleportType.RECHARGABLE_TELE, new RSTile(2535, 3861, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RING_OF_WEALTH_FILTER.getHasItem().checkHasItem() && PVars.getSetting(359) >= 100,
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)misc.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    RING_OF_DUELING_DUEL_ARENA(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3313, 3233, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RING_OF_DUELING_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*duel arena.*")
    ),

    RING_OF_DUELING_CASTLE_WARS(
            TeleportType.NONRECHARABLE_TELE, new RSTile(2440, 3090, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RING_OF_DUELING_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Castle Wars.*")
    ),

    RING_OF_DUELING_FEROX_ENCLAVE(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3150, 3635, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RING_OF_DUELING_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Ferox Enclave.*")
    ),

    NECKLACE_OF_PASSAGE_WIZARD_TOWER(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3113, 3179, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.NECKLACE_OF_PASSAGE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*wizard.+tower.*")
    ),

    NECKLACE_OF_PASSAGE_OUTPOST(
            TeleportType.NONRECHARABLE_TELE, new RSTile(2430, 3347, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.NECKLACE_OF_PASSAGE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*the.+outpost.*")
    ),

    NECKLACE_OF_PASSAGE_EYRIE(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3406, 3156, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.NECKLACE_OF_PASSAGE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*eagl.+eyrie.*")
    ),

    COMBAT_BRACE_WARRIORS_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(2882, 3550, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.COMBAT_BRACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*warrior.+guild.*")
    ),

    COMBAT_BRACE_CHAMPIONS_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(3190, 3366, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.COMBAT_BRACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*champion.+guild.*")
    ),

    COMBAT_BRACE_MONASTARY(
            TeleportType.RECHARGABLE_TELE, new RSTile(3053, 3486, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.COMBAT_BRACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*monastery.*")
    ),

    COMBAT_BRACE_RANGE_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(2656, 3442, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.COMBAT_BRACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*rang.+guild.*")
    ),

    GAMES_NECK_BURTHORPE(
            TeleportType.NONRECHARABLE_TELE, new RSTile(2897, 3551, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.GAMES_NECKLACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*burthorpe.*")
    ),

    GAMES_NECK_BARBARIAN_OUTPOST(
            TeleportType.NONRECHARABLE_TELE, new RSTile(2520, 3570, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.GAMES_NECKLACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*barbarian.*")
    ),

    GAMES_NECK_CORPOREAL(
            TeleportType.NONRECHARABLE_TELE, new RSTile(2965, 4382, 2),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.GAMES_NECKLACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*corporeal.*")
    ),

    GAMES_NECK_WINTERTODT(
            TeleportType.NONRECHARABLE_TELE, new RSTile(1623, 3937, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && hasBeenToZeah() && HasItems.GAMES_NECKLACE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*wintertodt.*")
    ),

    GLORY_EDGEVILLE(
            TeleportType.RECHARGABLE_TELE, new RSTile(3087, 3496, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.GLORY_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*edgeville.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    GLORY_KARAMJA(
            TeleportType.RECHARGABLE_TELE, new RSTile(2918, 3176, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.GLORY_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*karamja.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    GLORY_DRAYNOR(
            TeleportType.RECHARGABLE_TELE, new RSTile(3105, 3251, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.GLORY_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*draynor.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    GLORY_AL_KHARID(
            TeleportType.RECHARGABLE_TELE, new RSTile(3293, 3163, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.GLORY_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*al kharid.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    SKILLS_FISHING_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(2610, 3391, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SKILLS_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Fishing.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    SKILLS_MINING_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(3052, 9764, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SKILLS_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Mining.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    SKILLS_CRAFTING_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(2935, 3293, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SKILLS_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Craft.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    SKILLS_COOKING_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(3145, 3442, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SKILLS_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Cooking.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    SKILLS_WOODCUTTING_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(1663, 3507, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SKILLS_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Woodcutting.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),

    SKILLS_FARMING_GUILD(
            TeleportType.RECHARGABLE_TELE, new RSTile(1248, 3719, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SKILLS_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Farming.*"),
            () -> CachedBooleans.LEVEL_30_WILDERNESS_LIMIT.getCachedBoolean().getBoolean()
    ),
    BURNING_AMULET_CHAOS_TEMPLE(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3236, 3635, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.BURNING_AMULET_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Chaos.*|Okay, teleport to level.*)")
    ),

    BURNING_AMULET_BANDIT_CAMP(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3039, 3652, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.BURNING_AMULET_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Bandit.*|Okay, teleport to level.*)")
    ),

    BURNING_AMULET_LAVA_MAZE(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3029, 3843, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.BURNING_AMULET_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Lava.*|Okay, teleport to level.*)")
    ),

    DIGSITE_PENDANT_DIGSITE(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3346, 3445, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.DIGSITE_PENDANT_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.DIGSITE_PENDANT_FILTER, "Digsite")
    ),

    DIGSITE_PENDANT_FOSSIL_ISLAND(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3764, 3869, 1),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.DIGSITE_PENDANT_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.DIGSITE_PENDANT_FILTER, "Fossil Island")
    ),

    ECTOPHIAL(
            TeleportType.UNLIMITED_TELE, new RSTile(3660, 3524, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.ECTOPHIAL_FILTER.getHasItem().checkHasItem(),
            () -> RSItemHelper.click(Filters.Items.nameContains("Ectophial"), "Empty")
    ),

    LLETYA(
            TeleportType.RECHARGABLE_TELE, new RSTile(2330, 3172, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.TELEPORT_CRYSTAL_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER, "Lletya")
    ),

    XERICS_GLADE(
            TeleportType.RECHARGABLE_TELE, new RSTile(1753, 3565, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.XERICS_TALISMAN_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Glade")
    ),
    XERICS_INFERNO(
            TeleportType.RECHARGABLE_TELE, new RSTile(1505, 3809, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.XERICS_TALISMAN_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Inferno")
    ),
    XERICS_LOOKOUT(
            TeleportType.RECHARGABLE_TELE, new RSTile(1575, 3531, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.XERICS_TALISMAN_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Lookout")
    ),

    WEST_ARDOUGNE_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2500, 3290, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.WEST_ARDOUGNE_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("West ardougne t.*", "Break")
    ),

    RADAS_BLESSING_KOUREND_WOODLAND(
            TeleportType.UNLIMITED_TELE, new RSTile(1558, 3458, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RADAS_BLESSING_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Kourend .*")
    ),
    RADAS_BLESSING_MOUNT_KARUULM(
            TeleportType.UNLIMITED_TELE, new RSTile(1310, 3796, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RADAS_BLESSING_FILTER3.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Mount.*")
    ),

    CRAFTING_CAPE_TELEPORT(
            TeleportType.UNLIMITED_TELE, new RSTile(2931, 3286, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CRAFTING_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.CRAFTING_CAPE_FILTER, "Teleport")
    ),

    CABBAGE_PATCH_TELEPORT(
            TeleportType.UNLIMITED_TELE, new RSTile(3049, 3287, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.EXPLORERS_RING_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.EXPLORERS_RING_FILTER, "Teleport")
    ),

    LEGENDS_GUILD_TELEPORT(
            TeleportType.UNLIMITED_TELE, new RSTile(2729, 3348, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.QUEST_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.QUEST_CAPE_FILTER, "Teleport")
    ),

    KANDARIN_MONASTERY_TELEPORT(
            TeleportType.UNLIMITED_TELE, new RSTile(2606, 3221, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.ARDOUGNE_CLOAK_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER, ".*Monastery.*")
    ),

    RIMMINGTON_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2954, 3224, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RIMMINGTON_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Rimmington t.*", "Break")
    ),

    TAVERLEY_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2894, 3465, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.TAVERLEY_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Taverley t.*", "Break")
    ),

    RELLEKKA_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2668, 3631, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.RELLEKKA_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Rellekka t.*", "Break")
    ),

    BRIMHAVEN_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2758, 3178, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.BRIMHAVEN_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Brimhaven t.*", "Break")
    ),

    POLLNIVNEACH_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(3340, 3004, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.POLLNIVNEACH_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Pollnivneach t.*", "Break")
    ),

    YANILLE_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(2544, 3095, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.YANILLE_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Yanille t.*", "Break")
    ),

    HOSIDIUS_TELEPORT_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(1744, 3517, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.HOSIDIUS_TELEPORT_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Hosidius t.*", "Break")
    ),

    CONSTRUCTION_CAPE_RIMMINGTON(
            TeleportType.UNLIMITED_TELE, new RSTile(2954, 3224, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CONSTRUCTION_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, ".*Rimmington")
    ),

    CONSTRUCTION_CAPE_TAVERLEY(
            TeleportType.UNLIMITED_TELE, new RSTile(2894, 3465, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CONSTRUCTION_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, ".*Taverley")
    ),

    CONSTRUCTION_CAPE_RELLEKKA(
            TeleportType.UNLIMITED_TELE, new RSTile(2668, 3631, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CONSTRUCTION_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, ".*Rellekka")
    ),

    CONSTRUCTION_CAPE_BRIMHAVEN(
            TeleportType.UNLIMITED_TELE, new RSTile(2758, 3178, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CONSTRUCTION_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, ".*Brimhaven")
    ),

    CONSTRUCTION_CAPE_POLLNIVNEACH(
            TeleportType.UNLIMITED_TELE, new RSTile(3340, 3004, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CONSTRUCTION_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, ".*Pollnivneach")
    ),

    CONSTRUCTION_CAPE_YANILLE(
            TeleportType.UNLIMITED_TELE, new RSTile(2544, 3095, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CONSTRUCTION_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, ".*Yanille")
    ),

    CONSTRUCTION_CAPE_HOSIDIUS(
            TeleportType.UNLIMITED_TELE, new RSTile(1744, 3517, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.CONSTRUCTION_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, ".*Hosidius")
    ),

    SLAYER_RING_GNOME_STRONGHOLD(
            TeleportType.NONRECHARABLE_TELE, new RSTile(2433, 3424, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SLAYER_RING.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Stronghold")
    ),

    SLAYER_RING_MORYTANIA(
            TeleportType.NONRECHARABLE_TELE, new RSTile(3422, 3537, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SLAYER_RING.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Tower")
    ),

    SLAYER_RING_RELLEKKA_CAVE(
            TeleportType.NONRECHARABLE_TELE, new RSTile(2801, 9999, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SLAYER_RING.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Rellekka")
    ),

    SALVE_GRAVEYARD_TAB(
            TeleportType.TELEPORT_SPELL, new RSTile(3432, 3460, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.SALVE_GRAVEYARD_TAB.getHasItem().checkHasItem(),
            () -> RSItemHelper.click("Salve graveyard t.*", "Break")
    ),

    ENCHANTED_LYRE_RELLEKA(
            TeleportType.RECHARGABLE_TELE, new RSTile(2661, 3465, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.ENCHANTED_LYRE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.ENCHANTED_LYRE_FILTER, "Play|Rellekka.*")
    ),

    FARMING_CAPE(
            TeleportType.UNLIMITED_TELE, new RSTile(1248, 3726, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.FARMING_CAPE_FILTER.getHasItem().checkHasItem(),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.FARMING_CAPE_FILTER, "Teleport")
    ),

    ROYAL_SEED(
            TeleportType.UNLIMITED_TELE, new RSTile(2465, 3495, 0),
            () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && HasItems.ROYAL_SEED.getHasItem().checkHasItem(),
            () -> RSItemHelper.click(Filters.Items.idEquals(ItemID.ROYAL_SEED_POD), "Commune")
    );//TODO add missing teleports

    public enum TeleportType {
        TELEPORT_SPELL(50),
        TELEPORT_SCROLL(100),
        NONRECHARABLE_TELE(80),
        RECHARGABLE_TELE(50),
        UNLIMITED_TELE(10);

        private int movecost;

        TeleportType(int movecost) {
            this.movecost = movecost;
        }

        public void setMoveCost(int movecost) {
            this.movecost = movecost;
        }

        public int getMovecost() {
            return movecost;
        }
    }

    public enum CachedBooleans {
        IN_MEMBERS_WORLD(new CachedBoolean() {
            @Override
            public boolean checkState() {
                return inMembersWorld();
            }
        }),
        LEVEL_20_WILDERNESS_LIMIT(new CachedBoolean() {
            @Override
            public boolean checkState() {
                return TeleportConstants.LEVEL_20_WILDERNESS_LIMIT.canCast();
            }
        }),
        LEVEL_30_WILDERNESS_LIMIT(new CachedBoolean() {
            @Override
            public boolean checkState() {
                return TeleportConstants.LEVEL_30_WILDERNESS_LIMIT.canCast();
            }
        }),
        ;

        @Getter
        private final CachedBoolean cachedBoolean;

        CachedBooleans(CachedBoolean cachedBoolean) {
            this.cachedBoolean = cachedBoolean;
        }

        public static void resetCachedBooleans() {
            for (CachedBooleans bool : CachedBooleans.values()) {
                bool.getCachedBoolean().resetState();
            }
        }
    }

    public enum HasItems {
        ENCHANTED_LYRE_FILTER(new HasItem(WearableItemTeleport.ENCHANTED_LYRE_FILTER), false),
        SLAYER_RING(new HasItem(WearableItemTeleport.SLAYER_RING)),
        CONSTRUCTION_CAPE_FILTER(new HasItem(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER)),
        ARDOUGNE_CLOAK_FILTER(new HasItem(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER)),
        QUEST_CAPE_FILTER(new HasItem(WearableItemTeleport.QUEST_CAPE_FILTER)),
        EXPLORERS_RING_FILTER(new HasItem(WearableItemTeleport.EXPLORERS_RING_FILTER)),
        CRAFTING_CAPE_FILTER(new HasItem(WearableItemTeleport.CRAFTING_CAPE_FILTER)),
        FARMING_CAPE_FILTER(new HasItem(WearableItemTeleport.FARMING_CAPE_FILTER)),
        RADAS_BLESSING_FILTER3(new HasItem(WearableItemTeleport.RADAS_BLESSING_FILTER.and(Filters.Items.nameContains("3", "4")))),
        RADAS_BLESSING_FILTER(new HasItem(WearableItemTeleport.RADAS_BLESSING_FILTER)),
        XERICS_TALISMAN_FILTER(new HasItem(WearableItemTeleport.XERICS_TALISMAN_FILTER)),
        TELEPORT_CRYSTAL_FILTER(new HasItem(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER), false),
        ECTOPHIAL_FILTER(new HasItem(Filters.Items.idEquals(ItemID.ECTOPHIAL)), false),
        DIGSITE_PENDANT_FILTER(new HasItem(WearableItemTeleport.DIGSITE_PENDANT_FILTER)),
        BURNING_AMULET_FILTER(new HasItem(WearableItemTeleport.BURNING_AMULET_FILTER)),
        SKILLS_FILTER(new HasItem(WearableItemTeleport.SKILLS_FILTER)),
        GLORY_FILTER(new HasItem(WearableItemTeleport.GLORY_FILTER)),
        GAMES_NECKLACE_FILTER(new HasItem(WearableItemTeleport.GAMES_NECKLACE_FILTER)),
        COMBAT_BRACE_FILTER(new HasItem(WearableItemTeleport.COMBAT_BRACE_FILTER)),
        NECKLACE_OF_PASSAGE_FILTER(new HasItem(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER)),
        RING_OF_DUELING_FILTER(new HasItem(WearableItemTeleport.RING_OF_DUELING_FILTER)),
        RING_OF_WEALTH_FILTER(new HasItem(WearableItemTeleport.RING_OF_WEALTH_FILTER)),
        VARROCK_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.VARROCK_TELEPORT)), false),
        HOSIDIUS_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.HOSIDIUS_TELEPORT)), false),
        YANILLE_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.YANILLE_TELEPORT)), false),
        POLLNIVNEACH_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.POLLNIVNEACH_TELEPORT)), false),
        BRIMHAVEN_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.BRIMHAVEN_TELEPORT)), false),
        RELLEKKA_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.RELLEKKA_TELEPORT)), false),
        TAVERLEY_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.TAVERLEY_TELEPORT)), false),
        RIMMINGTON_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.RIMMINGTON_TELEPORT)), false),
        SALVE_GRAVEYARD_TAB(new HasItem(Filters.Items.idEquals(ItemID.SALVE_GRAVEYARD_TELEPORT)), false),
        WEST_ARDOUGNE_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.WEST_ARDOUGNE_TELEPORT)), false),
        ARDOUGNE_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.ARDOUGNE_TELEPORT)), false),
        CAMELOT_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.CAMELOT_TELEPORT)), false),
        FALADOR_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.FALADOR_TELEPORT)), false),
        LUMBRIDGE_TELEPORT_TAB(new HasItem(Filters.Items.idEquals(ItemID.LUMBRIDGE_TELEPORT)), false),
        MASTER_SCROLL_BOOK(new HasItem(Filters.Items.idEquals(ItemID.MASTER_SCROLL_BOOK)), false),
        NARDAH(new HasItem(Filters.Items.idEquals(ItemID.NARDAH_TELEPORT)), false),
        DIGSITE(new HasItem(Filters.Items.idEquals(ItemID.DIGSITE_TELEPORT)), false),
        FELDIP_HILLS(new HasItem(Filters.Items.idEquals(ItemID.FELDIP_HILLS_TELEPORT)), false),
        LUNAR_ISLE(new HasItem(Filters.Items.idEquals(ItemID.LUNAR_ISLE_TELEPORT)), false),
        MORTTON(new HasItem(Filters.Items.idEquals(ItemID.MORTTON_TELEPORT)), false),
        PEST_CONTROL(new HasItem(Filters.Items.idEquals(ItemID.PEST_CONTROL_TELEPORT)), false),
        PISCATORIS(new HasItem(Filters.Items.idEquals(ItemID.PISCATORIS_TELEPORT)), false),
        TAI_BWO_WANNAI(new HasItem(Filters.Items.idEquals(ItemID.TAI_BWO_WANNAI_TELEPORT)), false),
        ELF_CAMP(new HasItem(Filters.Items.idEquals(ItemID.IORWERTH_CAMP_TELEPORT)), false),
        MOS_LE_HARMLESS(new HasItem(Filters.Items.idEquals(ItemID.MOS_LEHARMLESS_TELEPORT)), false),
        LUMBERYARD(new HasItem(Filters.Items.idEquals(ItemID.LUMBERYARD_TELEPORT)), false),
        ZULANDRA(new HasItem(Filters.Items.idEquals(ItemID.ZULANDRA_TELEPORT)), false),
        KEY_MASTER(new HasItem(Filters.Items.idEquals(ItemID.KEY_MASTER_TELEPORT)), false),
        REVENANT_CAVES(new HasItem(Filters.Items.idEquals(ItemID.REVENANT_CAVE_TELEPORT)), false),
        WATSON(new HasItem(Filters.Items.idEquals(ItemID.WATSON_TELEPORT)), false),
        ROYAL_SEED(new HasItem(Filters.Items.idEquals(ItemID.ROYAL_SEED_POD)), false),
        ;

        @Getter
        private final HasItem hasItem;
        @Getter
        private final boolean equippable;

        HasItems(HasItem hasItem) {
            this.hasItem = hasItem;
            this.equippable = true;
        }

        HasItems(HasItem hasItem, boolean equippable) {
            this.hasItem = hasItem;
            this.equippable = equippable;
        }
    }

    private final TeleportType teleportType;
    private final RSTile location;
    private final Requirement requirement;
    private final Action action;
    private final TeleportLimit teleportLimit;

    private boolean canUse = true;

    private int failedAttempts = 0;

    Teleport(TeleportType teleportType, RSTile location, Requirement requirement, Action action) {
        this.teleportType = teleportType;
        this.location = location;
        this.requirement = requirement;
        this.action = action;
        this.teleportLimit = () -> CachedBooleans.LEVEL_20_WILDERNESS_LIMIT.getCachedBoolean().getBoolean();
    }

    Teleport(TeleportType teleportType, RSTile location, Requirement requirement, Action action, TeleportLimit limit) {
        this.teleportType = teleportType;
        this.location = location;
        this.requirement = requirement;
        this.action = action;
        this.teleportLimit = limit;
    }

    Teleport(TeleportType teleportType, TeleportScrolls scroll, HasItems hasItem) {
        this.teleportType = teleportType;
        this.location = scroll.getLocation();
        this.requirement = () -> CachedBooleans.IN_MEMBERS_WORLD.getCachedBoolean().getBoolean() && (hasItem.hasItem.checkHasItem() || scroll.canUse());
        this.action = () -> scroll.teleportTo(false);
        this.teleportLimit = () -> CachedBooleans.LEVEL_20_WILDERNESS_LIMIT.getCachedBoolean().getBoolean();
    }

    public int getMoveCost() {
        return teleportType.getMovecost();
    }

    public RSTile getLocation() {
        return location;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public TeleportLimit getTeleportLimit() {
        return teleportLimit;
    }

    public boolean trigger() {
        boolean value = this.action.trigger();
        if (!value) {
            failedAttempts++;
            if (failedAttempts > 3) {
                canUse = false;
            }
        }
        failedAttempts = 0;
        return value;
    }

    public boolean isAtTeleportSpot(RSTile tile) {
        return tile.distanceTo(location) < 10;
    }

    private static final Set<Teleport> blacklist = new HashSet<>();

    public static void blacklistTeleports(Teleport... teleports) {
        blacklist.addAll(Arrays.asList(teleports));
    }

    public static void clearTeleportBlacklist() {
        blacklist.clear();
    }

    public static void checkAllItems() {
        log.info("Start: " + System.currentTimeMillis());
        CachedBooleans.resetCachedBooleans();
        SpellBook.resetCurrenSpellBook();
        RunePouch.resetHasPouch();
        RuneElement.resetAllRuneElements();
        List<PItem> inventoryItems = PInventory.getAllItems();
        List<PItem> equippedItems = PInventory.getEquipmentItems();
        for (PItem invItem : inventoryItems) {
            RunePouch.checkIsRunePouch(invItem);
            for (RuneElement rune : RuneElement.values()) {
                rune.checkRuneCount(invItem);
            }
        }
        for (PItem equipItem : equippedItems) {
            for (RuneElement rune : RuneElement.values()) {
                rune.checkHasStaff(equipItem);
            }
        }
        for (HasItems items : HasItems.values()) {
            items.getHasItem().resetState();
            for (PItem invItem : inventoryItems) {
                items.getHasItem().mapHasItem(invItem);
            }
            for (PItem equipItem : equippedItems) {
                if (items.equippable) {
                    items.getHasItem().mapHasItem(equipItem);
                }
            }
        }
        log.info("Checked Items: " + System.currentTimeMillis());
    }

    public static List<RSTile> getValidStartingRSTiles() {
        checkAllItems();
        List<RSTile> RSTiles = new ArrayList<>();
        for (Teleport teleport : values()) {
            if (blacklist.contains(teleport) || !teleport.teleportLimit.canCast() ||
                    !teleport.canUse || !teleport.requirement.satisfies()) continue;
            //log.info("Teleport: " + teleport.name() + ", " + System.currentTimeMillis());
            RSTiles.add(teleport.location);
        }
        //log.info("Finish: " + System.currentTimeMillis());
        return RSTiles;
    }

    private interface Action {
        boolean trigger();
    }

    private static boolean inMembersWorld() {
        return PUtils.isMembersWorld();
    }

    private static Predicate<PItem> notNotedFilter() {
        return itm -> itm.getDefinition() != null && itm.getDefinition().getNote() == -1;
    }

    private static boolean itemAction(String name, String... actions) {
        PItem item = PInventory.findItem(Filters.Items.nameEquals(name));
        return PInteraction.item(item, actions);
    }

    private static boolean teleportWithScrollInterface(Predicate<PItem> itemFilter, String regex) {
        ArrayList<PItem> items = new ArrayList<>();
        items.addAll(PInventory.findAllItems(itemFilter));
        items.addAll(PInventory.findAllEquipmentItems(itemFilter));

        if (items.size() == 0) {
            return false;
        }

        if (!PWidgets.isSubstantiated(TeleportConstants.SCROLL_INTERFACE_MASTER)) {
            PItem teleportItem = items.get(0);
            if (!RSItemHelper.clickMatch(teleportItem, "(Rub|Teleport|" + regex + ")")
                    || WaitFor.condition(2500,
                    () -> PWidgets.isSubstantiated(TeleportConstants.SCROLL_INTERFACE_MASTER) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)
                    != WaitFor.Return.SUCCESS) {
                return false;
            }
        }

        return handleScrollInterface(regex);
    }

    private static boolean handleScrollInterface(String regex) {
        RSInterface box = new RSInterface(PWidgets.get(187, 3));
        if (box.getWidget() == null) return false;

        RSInterface[] children = box.getChildren();
        if (children == null)
            return false;
        for (RSInterface child : children) {
            String txt = child.getText();
            if (txt != null && txt.matches(regex)) {
                Keyboard.typeString(Text.removeTags(txt).substring(0, 1));
                return true;
            }
        }
        return false;
    }

    public static boolean castSpell(String spellName, String action) {
        WidgetInfo spellWidgetInfo = Spells.getWidget(spellName);
        if (PWidgets.isValid(spellWidgetInfo)) {
            return PInteraction.widget(PWidgets.get(spellWidgetInfo), action);
        }
        return false;
    }

    private static boolean hasBeenToZeah() {
        return RSVarBit.get(4897).getValue() > 0;
    }
}
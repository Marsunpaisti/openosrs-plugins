package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.RSItemHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.NPCInteraction;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PItem;

import java.util.ArrayList;
import java.util.function.Predicate;

@Slf4j
public class WearableItemTeleport {

    private static final Predicate<PItem> NOT_NOTED = itm -> itm.getDefinition() != null && itm.getDefinition().getNote() == -1;

    public static final Predicate<PItem> RING_OF_WEALTH_FILTER = Filters.Items.idEquals(ImmutableSet.of(ItemID.RING_OF_WEALTH_1, ItemID.RING_OF_WEALTH_2, ItemID.RING_OF_WEALTH_3, ItemID.RING_OF_WEALTH_4,
            ItemID.RING_OF_WEALTH_5, ItemID.RING_OF_WEALTH_I1, ItemID.RING_OF_WEALTH_I2, ItemID.RING_OF_WEALTH_I3, ItemID.RING_OF_WEALTH_I4, ItemID.RING_OF_WEALTH_I5));
    public static final Predicate<PItem> RING_OF_DUELING_FILTER = Filters.Items.processedIdEquals(ItemID.RING_OF_DUELING1);
    public static final Predicate<PItem> NECKLACE_OF_PASSAGE_FILTER = Filters.Items.processedIdEquals(ItemID.NECKLACE_OF_PASSAGE1);
    public static final Predicate<PItem> COMBAT_BRACE_FILTER = Filters.Items.idEquals(ImmutableSet.of(ItemID.COMBAT_BRACELET1, ItemID.COMBAT_BRACELET2, ItemID.COMBAT_BRACELET3, ItemID.COMBAT_BRACELET4,
            ItemID.COMBAT_BRACELET5, ItemID.COMBAT_BRACELET6));
    public static final Predicate<PItem> GAMES_NECKLACE_FILTER = Filters.Items.processedIdEquals(ItemID.GAMES_NECKLACE1);
    public static final Predicate<PItem> GLORY_FILTER = Filters.Items.idEquals(ImmutableSet.of(ItemID.AMULET_OF_GLORY1, ItemID.AMULET_OF_GLORY2, ItemID.AMULET_OF_GLORY3, ItemID.AMULET_OF_GLORY4,
            ItemID.AMULET_OF_GLORY5, ItemID.AMULET_OF_GLORY6, ItemID.AMULET_OF_GLORY_T1, ItemID.AMULET_OF_GLORY_T2, ItemID.AMULET_OF_GLORY_T3, ItemID.AMULET_OF_GLORY_T4,
            ItemID.AMULET_OF_GLORY_T5, ItemID.AMULET_OF_GLORY_T6, ItemID.AMULET_OF_ETERNAL_GLORY));
    public static final Predicate<PItem> SKILLS_FILTER = Filters.Items.idEquals(ImmutableSet.of(ItemID.SKILLS_NECKLACE1, ItemID.SKILLS_NECKLACE2, ItemID.SKILLS_NECKLACE3, ItemID.SKILLS_NECKLACE4,
            ItemID.SKILLS_NECKLACE5, ItemID.SKILLS_NECKLACE6));
    public static final Predicate<PItem> BURNING_AMULET_FILTER = Filters.Items.processedIdEquals(ItemID.BURNING_AMULET1);
    public static final Predicate<PItem> DIGSITE_PENDANT_FILTER = Filters.Items.processedIdEquals(ItemID.DIGSITE_PENDANT_1);
    public static final Predicate<PItem> TELEPORT_CRYSTAL_FILTER = Filters.Items.processedIdEquals(ItemID.TELEPORT_CRYSTAL_1).or(Filters.Items.idEquals(ItemID.ETERNAL_TELEPORT_CRYSTAL));
    public static final Predicate<PItem> XERICS_TALISMAN_FILTER = Filters.Items.idEquals(ItemID.XERICS_TALISMAN);
    public static final Predicate<PItem> RADAS_BLESSING_FILTER = Filters.Items.processedIdEquals(ItemID.RADAS_BLESSING_1);
    public static final Predicate<PItem> CRAFTING_CAPE_FILTER = Filters.Items.processedIdEquals(ItemID.CRAFTING_CAPE);
    public static final Predicate<PItem> FARMING_CAPE_FILTER = Filters.Items.processedIdEquals(ItemID.FARMING_CAPE);
    public static final Predicate<PItem> EXPLORERS_RING_FILTER = Filters.Items.idEquals(ImmutableSet.of(ItemID.EXPLORERS_RING_2, ItemID.EXPLORERS_RING_3, ItemID.EXPLORERS_RING_4));
    public static final Predicate<PItem> QUEST_CAPE_FILTER = Filters.Items.processedIdEquals(ItemID.QUEST_POINT_CAPE);
    public static final Predicate<PItem> ARDOUGNE_CLOAK_FILTER = Filters.Items.processedIdEquals(ItemID.ARDOUGNE_CLOAK_1);
    public static final Predicate<PItem> CONSTRUCTION_CAPE_FILTER = Filters.Items.processedIdEquals(ItemID.CONSTRUCT_CAPE);
    public static final Predicate<PItem> SLAYER_RING = Filters.Items.processedIdEquals(ItemID.SLAYER_RING_1);
    public static final Predicate<PItem> ENCHANTED_LYRE_FILTER = Filters.Items.processedIdEquals(ItemID.ENCHANTED_LYRE1).and(i -> i.getId() != ItemID.ENCHANTED_LYRE);


    private WearableItemTeleport() {

    }

    public static boolean has(Predicate<PItem> filter) {
        return PInventory.findItem(filter) != null || PInventory.findEquipmentItem(filter) != null;
    }

    public static boolean teleport(Predicate<PItem> filter, String action) {
        return teleportWithItem(filter, action);
    }


    private static boolean teleportWithItem(Predicate<PItem> itemFilter, String regex) {
        ArrayList<PItem> items = new ArrayList<>();
        items.addAll(PInventory.findAllItems(itemFilter));
        items.addAll(PInventory.findAllEquipmentItems(itemFilter));

        if (items.size() == 0) {
            return false;
        }

        PItem teleportItem = items.get(0);
        final WorldPoint startingPosition = PPlayer.location();

        boolean clickRes = RSItemHelper.clickMatch(teleportItem, "(Rub|Teleport|" + regex + ")");
        if (!clickRes) {
            log.info("Clicking tp item failed");
            return false;
        }
        boolean waitRes = WaitFor.condition(
                PUtils.random(3800, 4600), () -> {
                    NPCInteraction.handleConversationRegex(regex);
                    if (startingPosition.distanceToHypotenuse(PPlayer.location()) > 5) {
                        return WaitFor.Return.SUCCESS;
                    }
                    return WaitFor.Return.IGNORE;
                }) == WaitFor.Return.SUCCESS;

        if (!waitRes) {
            log.info("Waiting for teleport movement failed");
        }

        return waitRes;
    }

}
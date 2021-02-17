package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.plugins.paistisuite.api.Filters;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.RSItemHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.NPCInteraction;
import net.runelite.client.plugins.paistisuite.api.types.PItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

@Slf4j
public class WearableItemTeleport {

    private static final Predicate<PItem> NOT_NOTED = itm -> itm.getDefinition() != null && itm.getDefinition().getNote() == -1;

    public static final Predicate<PItem> RING_OF_WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth (").and(i -> i.getDefinition().getName().matches(".*[1-5]\\)")).and(NOT_NOTED);
    public static final Predicate<PItem> RING_OF_DUELING_FILTER = Filters.Items.nameContains("Ring of dueling").and(NOT_NOTED);
    public static final Predicate<PItem> NECKLACE_OF_PASSAGE_FILTER = Filters.Items.nameContains("Necklace of passage").and(NOT_NOTED);
    public static final Predicate<PItem> COMBAT_BRACE_FILTER = Filters.Items.nameContains("Combat bracelet(").and(NOT_NOTED);
    public static final Predicate<PItem> GAMES_NECKLACE_FILTER = Filters.Items.nameContains("Games necklace").and(NOT_NOTED);
    public static final Predicate<PItem> GLORY_FILTER = Filters.Items.nameContains("glory").and(Filters.Items.nameContains("eternal","(")).and(NOT_NOTED);
    public static final Predicate<PItem> SKILLS_FILTER = Filters.Items.nameContains("Skills necklace(").and(NOT_NOTED);
    public static final Predicate<PItem> BURNING_AMULET_FILTER = Filters.Items.nameContains("Burning amulet(").and(NOT_NOTED);
    public static final Predicate<PItem> DIGSITE_PENDANT_FILTER = Filters.Items.nameContains("Digsite pendant");
    public static final Predicate<PItem> TELEPORT_CRYSTAL_FILTER = Filters.Items.nameContains("Teleport crystal");
    public static final Predicate<PItem> XERICS_TALISMAN_FILTER = Filters.Items.nameEquals("Xeric's talisman");
    public static final Predicate<PItem> RADAS_BLESSING_FILTER = Filters.Items.nameContains("Rada's blessing");
    public static final Predicate<PItem> CRAFTING_CAPE_FILTER = Filters.Items.nameContains("Crafting cape");
    public static final Predicate<PItem> EXPLORERS_RING_FILTER = Filters.Items.nameContains("Explorer's ring");
    public static final Predicate<PItem> QUEST_CAPE_FILTER = Filters.Items.nameContains("Quest point cape");
    public static final Predicate<PItem> ARDOUGNE_CLOAK_FILTER = Filters.Items.nameContains("Ardougne cloak");
    public static final Predicate<PItem> CONSTRUCTION_CAPE_FILTER = Filters.Items.nameContains("Construct. cape");
    public static final Predicate<PItem> SLAYER_RING = Filters.Items.nameContains("Slayer ring");


    private WearableItemTeleport() {

    }

    public static boolean has(Predicate<PItem> filter) {
        return PInventory.findItem(filter) != null || PInventory.findEquipmentItem(filter) != null;
    }

    public static boolean teleport(Predicate<PItem> filter, String action) {
        return teleportWithItem(filter,action);
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

        if (!waitRes){
            log.info("Waiting for teleport movement failed");
        }

        return waitRes;
    }

}
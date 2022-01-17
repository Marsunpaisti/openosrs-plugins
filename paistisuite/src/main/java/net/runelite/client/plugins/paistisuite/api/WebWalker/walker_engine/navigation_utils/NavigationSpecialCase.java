package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.DoomsToggle;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.NPCInteraction;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.FairyRing;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.AccurateMouse;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.NavigationSpecialCase.SpecialLocation.*;

@Slf4j
public class NavigationSpecialCase {

    private static NavigationSpecialCase instance = null;

    private NavigationSpecialCase() {

    }

    private static NavigationSpecialCase getInstance() {
        return instance != null ? instance : (instance = new NavigationSpecialCase());
    }

    /**
     * THE ABSOLUTE POSITION
     */
    public enum SpecialLocation {


        RELLEKA_UPPER_PORT(2621, 3688, 0),
        SMALL_PIRATES_COVE_AREA(2213, 3794, 0),

        PIRATE_COVE_SHIP_TILE(2138, 3900, 2),
        CAPTAIN_BENTLY_PIRATES_COVE(2223, 3796, 2),
        CAPTAIN_BENTLY_LUNAR_ISLE(2130, 3899, 2),

        SHANTAY_PASS(3311, 3109, 0),
        UZER(3468, 3110, 0),
        BEDABIN_CAMP(3181, 3043, 0),
        POLLNIVNEACH_NORTH(3350, 3002, 0),
        POLLNIVNEACH_SOUTH(3352, 2941, 0),
        NARDAH(3400, 2917, 0),

        SHILO_ENTRANCE(2881, 2953, 0),
        SHILO_INSIDE(2864, 2955, 0),

        RELLEKKA_WEST_BOAT(2621, 3682, 0),
        WATERBIRTH(2546, 3760, 0),

        SPIRIT_TREE_GRAND_EXCHANGE(3183, 3508, 0),
        SPIRIT_TREE_STRONGHOLD(2461, 3444, 0),
        SPIRIT_TREE_KHAZARD(2555, 3259, 0),
        SPIRIT_TREE_VILLAGE(2542, 3170, 0),
        SPIRIT_TREE_BRIMHAVEN(2800, 3204, 0),
        SPIRIT_TREE_GUILD(1252, 3752, 0),
        SPIRIT_TREE_FELDIP(2486, 2849, 0),
        SPIRIT_TREE_PRIFDDINAS(3274, 6123, 0),
        SPIRIT_TREE_SARIM(3059, 3256, 0),
        SPIRIT_TREE_ETCETERIA(2611, 3857, 0),
        SPIRIT_TREE_HOSIDIUS(1692, 3540, 0),

        GNOME_TREE_GLIDER(GnomeGlider.Location.TA_QUIR_PRIW.getX(), GnomeGlider.Location.TA_QUIR_PRIW.getY(), GnomeGlider.Location.TA_QUIR_PRIW.getZ()),
        AL_KHARID_GLIDER(
                GnomeGlider.Location.KAR_HEWO.getX(), GnomeGlider.Location.KAR_HEWO.getY(), GnomeGlider.Location.KAR_HEWO.getZ()),
        DIG_SITE_GLIDER(GnomeGlider.Location.LEMANTO_ANDRA.getX(), GnomeGlider.Location.LEMANTO_ANDRA.getY(), GnomeGlider.Location.LEMANTO_ANDRA.getZ()),
        WOLF_MOUNTAIN_GLIDER(
                GnomeGlider.Location.SINDARPOS.getX(), GnomeGlider.Location.SINDARPOS.getY(), GnomeGlider.Location.SINDARPOS.getZ()),
        GANDIUS_GLIDER(
                GnomeGlider.Location.GANDIUS.getX(), GnomeGlider.Location.GANDIUS.getY(), GnomeGlider.Location.GANDIUS.getZ()),

        ZANARIS_RING(2452, 4473, 0),
        LUMBRIDGE_ZANARIS_SHED(3201, 3169, 0),

        ROPE_TO_ROCK(2512, 3476, 0),
        FINISHED_ROPE_TO_ROCK(2513, 3468, 0),

        ROPE_TO_TREE(2512, 3466, 0),
        WATERFALL_DUNGEON_ENTRANCE(2511, 3463, 0),

        WATERFALL_LEDGE(2511, 3463, 0),
        WATERFALL_DUNGEON(2575, 9861, 0),
        WATERFALL_FALL_DOWN(2527, 3413, 0),

        KALPHITE_TUNNEL(3226, 3108, 0),
        KALPHITE_TUNNEL_INSIDE(3483, 9510, 2),

        DWARF_CARTS_GE(3141, 3504, 0),
        DWARFS_CARTS_KELDAGRIM(2922, 10170, 0),

        BRIMHAVEN_DUNGEON_SURFACE(2744, 3152, 0),
        BRIMHAVEN_DUNGEON(2713, 9564, 0),

        GNOME_ENTRANCE(2461, 3382, 0), //entrance side
        GNOME_EXIT(2461, 3385, 0), //exit side

        GNOME_SHORTCUT_ELKOY_ENTER(2504, 3191, 0),
        GNOME_SHORTCUT_ELKOY_EXIT(2515, 3160, 0),

        GNOME_TREE_ENTRANCE(2465, 3493, 0), //entrance side
        GNOME_TREE_EXIT(2465, 3493, 0), //exit side

        ZEAH_SAND_CRAB(1784, 3458, 0),
        ZEAH_SAND_CRAB_ISLAND(1778, 3418, 0),

        PORT_SARIM_PAY_FARE(3029, 3217, 0),
        PORT_SARIM_PEST_CONTROL(3041, 3202, 0),
        PORT_SARIM_VEOS(3054, 3245, 0),
        KARAMJA_PAY_FARE(2953, 3146, 0),
        ARDOUGNE_PAY_FARE(2681, 3275, 0),
        BRIMHAVEN_PAY_FARE(2772, 3225, 0),
        RIMMINGTON_PAY_FARE(2915, 3225, 0),
        GREAT_KOUREND(1824, 3691, 0),
        LANDS_END(1504, 3399, 0),
        PEST_CONTROL(2659, 2676, 0),

        ARDY_LOG_WEST(2598, 3336, 0),
        ARDY_LOG_EAST(2602, 3336, 0),

        GNOME_TREE_DAERO(2482, 3486, 1),
        GNOME_WAYDAR(2649, 4516, 0),
        CRASH_ISLAND(2894, 2726, 0),
        APE_ATOLL_GLIDER_CRASH(2802, 2707, 0),
        GNOME_DROPOFF(2393, 3466, 0),

        HAM_OUTSIDE(3166, 3251, 0),
        HAM_INSIDE(3149, 9652, 0),

        CASTLE_WARS_DOOR(2444, 3090, 0),
        FEROX_ENCLAVE_PORTAL_F2P(3141, 3627, 0),

        FOSSIL_ISLAND_BARGE(3362, 3445, 0),
//        DIGSITE_BARGE(3724, 3808, 0),

        PORT_SARIM_TO_ENTRANA(3048, 3234, 0),
        ENTRANA_TO_PORT_SARIM(2834, 3335, 0),

        RELLEKKA_TO_MISCELLANIA(2629, 3693, 0),
        MISCELLANIA_TO_RELLEKKA(2577, 3853, 0),

        FAIRY_RING_ABYSSAL_AREA(3059, 4875, 0),
        FAIRY_RING_ABYSSAL_NEXUS(3037, 4763, 0),
        FAIRY_RING_APE_ATOLL(2740, 2738, 0),
        FAIRY_RING_ARCEUUS_LIBRARY(1639, 3868, 0),
        FAIRY_RING_ARDOUGNE_ZOO(2635, 3266, 0),
        FAIRY_RING_CANIFIS(3447, 3470, 0),
        FAIRY_RING_CHASM_OF_FIRE(1455, 3658, 0),
        FAIRY_RING_COSMIC_ENTITYS_PLANE(2075, 4848, 0),
        FAIRY_RING_DRAYNOR_VILLAGE_ISLAND(3082, 3206, 0),
        FAIRY_RING_EDGEVILLE(3129, 3496, 0),
        FAIRY_RING_ENCHANTED_VALLEY(3041, 4532, 0),
        FAIRY_RING_FELDIP_HILLS_HUNTER_AREA(2571, 2956, 0),
        FAIRY_RING_FISHER_KINGS_REALM(2650, 4730, 0),
        FAIRY_RING_GORAKS_PLANE(3038, 5348, 0),
        FAIRY_RING_HAUNTED_WOODS(3597, 3495, 0),
        FAIRY_RING_HAZELMERE(2682, 3081, 0),
        FAIRY_RING_ISLAND_SOUTHEAST_ARDOUGNE(2700, 3247, 0),
        FAIRY_RING_KALPHITE_HIVE(3251, 3095, 0),
        FAIRY_RING_KARAMJA_KARAMBWAN_SPOT(2900, 3111, 0),
        FAIRY_RING_LEGENDS_GUILD(2740, 3351, 0),
        FAIRY_RING_LIGHTHOUSE(2503, 3636, 0),
        FAIRY_RING_MCGRUBOR_WOODS(2644, 3495, 0),
        FAIRY_RING_MISCELLANIA(2513, 3884, 0),
        FAIRY_RING_MISCELLANIA_PENGUINS(2500, 3896, 0),
        FAIRY_RING_MORT_MYRE_ISLAND(3410, 3324, 0),
        FAIRY_RING_MORT_MYRE_SWAMP(3469, 3431, 0),
        FAIRY_RING_MOUNT_KARUULM(1302, 3762, 0),
        FAIRY_RING_MUDSKIPPER_POINT(2996, 3114, 0),
        FAIRY_RING_NORTH_OF_NARDAH(3423, 3016, 0),
        FAIRY_RING_PISCATORIS_HUNTER_AREA(2319, 3619, 0),
        FAIRY_RING_POISON_WASTE(2213, 3099, 0),
        FAIRY_RING_POLAR_HUNTER_AREA(2744, 3719, 0),
        FAIRY_RING_RELLEKKA_SLAYER_CAVE(2780, 3613, 0),
        FAIRY_RING_SHILO_VILLAGE(2801, 3003, 0),
        FAIRY_RING_SINCLAIR_MANSION(2705, 3576, 0),
        FAIRY_RING_SOUTH_CASTLE_WARS(2385, 3035, 0),
        FAIRY_RING_TOWER_OF_LIFE(2658, 3230, 0),
        FAIRY_RING_TZHAAR(2437, 5126, 0),
        FAIRY_RING_WIZARDS_TOWER(3108, 3149, 0),
        FAIRY_RING_YANILLE(2528, 3127, 0),
        FAIRY_RING_ZANARIS(2412, 4434, 0),
        FAIRY_RING_ZUL_ANDRA(2150, 3070, 0),

        FOSSIL_ISLAND_FERRY_NORTH(3734, 3893, 0),
        FOSSIL_ISLAND_FERRY_CAMP(3724, 3808, 0),
        FOSSIL_ISLAND_FERRY_ISLAND(3769, 3898, 0),

        WITCHHAVEN_FERRY(2720, 3303, 0),
        FISHING_PLATFORM_FERRY(2785, 3275, 0),

        RELLEKKA_DOCK_FROM_ISLES(2645, 3710, 0),
        JATIZSO_DOCK(2418, 3782, 0),
        NEITIZNOT_DOCK(2311, 3781, 0),

        OBSERVATORY_OUTSIDE(2449, 3155, 0),
        OBSERVATORY_INSIDE(2444, 3165, 0),

        MOSS_GIANT_ISLAND_ROPE(2709, 3209, 0),
        MOSS_GIANT_ISLAND_ROPE_LANDING(2704, 3209, 0),

        SHANTAY_PASS_ENTRANCE(3304, 3117, 0),
        SHANTAY_PASS_EXIT(3304, 3115, 0),

        PATERDOMUS_EAST_EXIT(3423, 3485, 0),
        PATERDOMUS_EAST_ENTRANCE(3440, 9887, 0),

        SWAMP_BOATY(3500, 3380, 0),
        SWAMP_BOATY_MORTTON(3522, 3285, 0),

        BRINE_RAT_CAVE_TREE(2748, 3733, 0),
        BRINE_RAT_CAVE_ENTER(2697, 10120, 0),
        FERRY_AL_KHARID_TO_UNKAH(3148, 2842, 0),
        FERRY_UNKAH_TO_AL_KHARID(3271, 3144, 0),

        UNKAH_SHANTAY_PASS_SOUTH_ENTRANCE(3167, 2819, 0),
        UNKAH_SHANTAY_PASS_SOUTH_EXIT(3167, 2816, 0),
        UNKAH_SHANTAY_PASS_EAST_ENTRANCE(3193, 2842, 0),
        UNKAH_SHANTAY_PASS_EAST_EXIT(3196, 2842, 0),

        YANILLE_BALANCE_EDGE_NORTH(2580, 9520, 0),
        YANILLE_BALANCE_EDGE_SOUTH(2580, 9512, 0),
        YANILLE_MONKEY_BARS_WEST(2572, 9506, 0),
        YANILLE_MONKEY_BARS_EAST(2578, 9506, 0);


        int x, y, z;

        SpecialLocation(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        RSTile getRSTile() {
            return new RSTile(x, y, z);
        }
    }

    public static SpecialLocation getLocation(RSTile rsTile) {
        log.info("Evaluating special locations");
        return Arrays.stream(
                        SpecialLocation.values()).filter(tile -> tile.z == rsTile.getPlane()
                        && Point2D.distance(tile.x, tile.y, rsTile.getX(), rsTile.getY()) <= 2)
                .findFirst().orElse(null);
    }

    /**
     * action for getting to the case
     *
     * @param specialLocation
     * @return
     */
    public static boolean handle(SpecialLocation specialLocation) {
        switch (specialLocation) {

            case BRIMHAVEN_DUNGEON:
                if (PVars.getSetting(393) != 1) {
                    if (!InteractionHelper.click(PObjects.findNPC(Filters.NPCs.nameEquals("Saniboch")), "Pay")) {
                        log.info("Could not pay saniboch");
                        break;
                    }
                    NPCInteraction.handleConversation("Yes", "Pay 875 coins to enter once");
                    return true;
                } else {
                    if (clickObject(Filters.Objects.nameEquals("Dungeon entrance"), "Enter", () -> PPlayer.location().getY() > 4000 ?
                            WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                        return true;
                    } else {
                        log.info("Could not enter dungeon");
                    }
                }
                break;

            case RELLEKA_UPPER_PORT:
            case SMALL_PIRATES_COVE_AREA:
                if (!NPCInteraction.talkTo(Filters.NPCs.nameContains("Lokar"), new String[]{"Travel"}, new String[]{
                        "That's fine, I'm just going to Pirates' Cove."})) {
                    System.out.println("Was not able to travel with Lokar");
                    break;
                }
                WaitFor.milliseconds(3300, 5200);
                break;
            case CAPTAIN_BENTLY_PIRATES_COVE:
            case CAPTAIN_BENTLY_LUNAR_ISLE:
                if (!NPCInteraction.talkTo(Filters.NPCs.nameContains("Captain"), new String[]{"Travel"}, new String[]{})) {
                    System.out.println("Was not able to travel with Captain");
                    break;
                }
                WaitFor.milliseconds(5300, 7200);
                break;
            case SHANTAY_PASS:
                handleCarpetRide("Shantay Pass");
                break;
            case UZER:
                handleCarpetRide("Uzer");
                break;
            case BEDABIN_CAMP:
                handleCarpetRide("Bedabin camp");
                break;
            case POLLNIVNEACH_NORTH:
            case POLLNIVNEACH_SOUTH:
                handleCarpetRide("Pollnivneach");
                break;
            case NARDAH:
                handleCarpetRide("Nardah");
                break;


            case SHILO_ENTRANCE:
                break;
            case SHILO_INSIDE:
                return NPCInteraction.talkTo(Filters.NPCs.nameEquals("Mosol Rei"), new String[]{"Talk-to"}, new String[]{"Yes, Ok, I'll go into the village!"});

            case RELLEKKA_WEST_BOAT:
                if (NPCInteraction.talkTo(Filters.NPCs.actionsEquals("Waterbirth"), new String[]{"Waterbirth"}, new String[0])) {
                    WaitFor.milliseconds(2000, 3000);
                }
                break;
            case MISCELLANIA_TO_RELLEKKA:
            case RELLEKKA_TO_MISCELLANIA:
                final WorldPoint curr = PPlayer.location();
                if (NPCInteraction.clickNpc(Filters.NPCs.actionsEquals("Rellekka", "Miscellania"), "Rellekka", "Miscellania")) {
                    WaitFor.condition(10000, () -> PPlayer.location().distanceTo(curr) > 20 ?
                            WaitFor.Return.SUCCESS :
                            WaitFor.Return.IGNORE);
                    WaitFor.milliseconds(4000, 5000);
                }
                break;

            case WATERBIRTH:
                if (PInteraction.npc(PObjects.findNPC(Filters.NPCs.actionsContains("Rellekka")), "Rellekka")) {
                    WaitFor.milliseconds(4000, 5000);
                } else {
                    String option = PObjects.findNPC(Filters.NPCs.nameContains("Jarvald").and(Filters.NPCs.actionsContains(
                            "Rellekka"))) != null ? "Rellekka" : "Talk-to";
                    if (NPCInteraction.talkTo(Filters.NPCs.nameEquals("Jarvald"), new String[]{option}, new String[]{
                            "What Jarvald is doing.",
                            "Can I come?",
                            "YES",
                            "Yes"
                    })) {
                        WaitFor.milliseconds(2000, 3000);
                    }
                }

                break;

            case SPIRIT_TREE_GRAND_EXCHANGE:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_GRAND_EXCHANGE);
            case SPIRIT_TREE_STRONGHOLD:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_STRONGHOLD);
            case SPIRIT_TREE_KHAZARD:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_KHAZARD);
            case SPIRIT_TREE_VILLAGE:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_VILLAGE);
            case SPIRIT_TREE_BRIMHAVEN:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_BRIMHAVEN);
            case SPIRIT_TREE_GUILD:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_GUILD);
            case SPIRIT_TREE_FELDIP:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_FELDIP);
            case SPIRIT_TREE_PRIFDDINAS:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_PRIFDDINAS);
            case SPIRIT_TREE_SARIM:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_SARIM);
            case SPIRIT_TREE_ETCETERIA:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_ETCETERIA);
            case SPIRIT_TREE_HOSIDIUS:
                return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_HOSIDIUS);

            case GNOME_TREE_GLIDER:
                return GnomeGlider.to(GnomeGlider.Location.TA_QUIR_PRIW);
            case AL_KHARID_GLIDER:
                return GnomeGlider.to(GnomeGlider.Location.KAR_HEWO);
            case DIG_SITE_GLIDER:
                return GnomeGlider.to(GnomeGlider.Location.LEMANTO_ANDRA);
            case WOLF_MOUNTAIN_GLIDER:
                return GnomeGlider.to(GnomeGlider.Location.SINDARPOS);
            case GANDIUS_GLIDER:
                return GnomeGlider.to(GnomeGlider.Location.GANDIUS);

            case ZANARIS_RING:
                if (PInventory.getEquipmentCount(772) == 0) {
                    if (!InteractionHelper.click(PInventory.findItem(Filters.Items.idEquals(772)), "Wield")) {
                        log.info("Could not equip Dramen staff.");
                        break;
                    }
                }
                if (InteractionHelper.click(
                        PObjects.findObject(Filters.Objects.nameEquals("Door")), "Open", () -> SpecialLocation.ZANARIS_RING.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                break;
            case LUMBRIDGE_ZANARIS_SHED:
                if (InteractionHelper.click(PObjects.findObject(Filters.Objects.nameEquals("Fairy ring")),
                        new String[]{"Use"}, () -> LUMBRIDGE_ZANARIS_SHED.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                break;

            case ROPE_TO_ROCK:
                break;
            case FINISHED_ROPE_TO_ROCK:
                if (PInteraction.useItemOnTileObject(
                        PInventory.findItem(Filters.Items.idEquals(954)),
                        PObjects.findObject(Filters.Objects.actionsContains("Swim to"))
                )) {
                    if (WaitFor.condition(15000,
                            () -> PPlayer.location().equals(new RSTile(2513, 3468, 0).toWorldPoint()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
                        return true;
                    }
                }

                log.info("Could not rope grab to rock.");
                break;

            case ROPE_TO_TREE:
                break;
            case WATERFALL_DUNGEON_ENTRANCE:
                if (WATERFALL_DUNGEON.getRSTile().toWorldPoint().distanceToHypotenuse(PPlayer.location()) < 500) {
                    return InteractionHelper.click(PObjects.findObject(Filters.Objects.nameEquals("Door")), new String[]{"Open"}, () -> WATERFALL_DUNGEON_ENTRANCE.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                } else if (PInteraction.useItemOnTileObject(
                        PInventory.findItem(Filters.Items.idEquals(954)),
                        PObjects.findObject(Filters.Objects.nameContains("Dead tree")))) {
                    if (WaitFor.condition(15000, () -> PPlayer.location().equals(new RSTile(2511, 3463, 0).toWorldPoint()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
                        return true;
                    }
                }
                log.info("Could not reach entrance to waterfall dungeon.");
                break;

            case WATERFALL_LEDGE:
                break;

            case WATERFALL_DUNGEON:
                if (InteractionHelper.click(
                        PObjects.findObject(Filters.Objects.idEquals(2010)), "Open", () -> PPlayer.location().getX() == WATERFALL_DUNGEON.x ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                log.info("Failed to get to waterfall dungeon");
                break;
            case WATERFALL_FALL_DOWN:
                if (InteractionHelper.click(PObjects.findObject(Filters.Objects.actionsContains("Get in")), "Get in", () -> PPlayer.location().distanceTo(new WorldPoint(2527, 3413, 0)) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                log.info("Failed to fall down waterfall");
                break;

            case KALPHITE_TUNNEL:
                if (clickObject(Filters.Objects.nameEquals("Rope"), "Climb-up", () -> PPlayer.location().getY() < 9000 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                break;
            case KALPHITE_TUNNEL_INSIDE:
                if (clickObject(Filters.Objects.nameEquals("Tunnel entrance").and(Filters.Objects.actionsEquals("Climb-down")), "Climb-down", () -> PPlayer.location().getY() > 4000 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                } else {
                    var tunnel = PObjects.findObject(Filters.Objects.nameEquals("Tunnel entrance").and(Filters.Objects.withinDistance(20)));
                    if (tunnel != null && walkToObject(tunnel)) {
                        String[] actions = tunnel.getSecond().getActions();
                        if (actions != null && Arrays.stream(actions).filter(Objects::nonNull).noneMatch(s -> s.startsWith("Climb-down"))) {
                            if (!PInteraction.useItemOnTileObject(PInventory.findItem(Filters.Items.idEquals(954)), PObjects.findObject(Filters.Objects.nameEquals("Tunnel entrance")))) {
                                return false;
                            } else {
                                WaitFor.milliseconds(3000, 6000);
                                return true;
                            }
                        }
                    }
                }

                log.info("Unable to go inside tunnel.");
                break;
            case DWARF_CARTS_GE:
                var carts = PObjects.findAllObjects(Filters.Objects.withinDistance(15).and(Filters.Objects.nameEquals("Train cart")).and((pair) -> pair.getFirst().getWorldLocation().getY() == 10171));
                carts.sort(Comparator.comparingInt(ob -> Math.round(ob.getFirst().getWorldLocation().distanceToHypotenuse(new WorldPoint(2935, 10172, 0)))));
                if (clickObject(carts.get(0), "Ride", () -> PPlayer.location().getX() == specialLocation.x ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    log.info("Rode cart to GE");
                    return true;
                } else {
                    log.info("Could not ride card to GE.");
                }

                break;

            case DWARFS_CARTS_KELDAGRIM:
                break;

            case BRIMHAVEN_DUNGEON_SURFACE:
                if (clickObject(Filters.Objects.nameEquals("Exit"), "Leave", () -> PPlayer.location().getY() < 8000 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                } else {
                    log.info("Failed to exit dungeon.");
                }
                break;

            case GNOME_ENTRANCE:
            case GNOME_EXIT:
                if (clickObject(
                        Filters.Objects.nameEquals("Gate").and(Filters.Objects.actionsContains("Open")),
                        new String[]{"Open"},
                        () -> {
                            if (NPCInteraction.isConversationWindowUp()) {
                                NPCInteraction.handleConversation(NPCInteraction.GENERAL_RESPONSES);
                            }
                            return PPlayer.location().getY() == 3383 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                        })) {
                    WaitFor.milliseconds(1060, 1500);
                    return true;
                } else {
                    log.info("Could not navigate through gnome door.");
                }
                break;

            case GNOME_SHORTCUT_ELKOY_ENTER:
            case GNOME_SHORTCUT_ELKOY_EXIT:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Elkoy"), "Follow")) {
                    WorldPoint current = PPlayer.location();
                    if (WaitFor.condition(8000, () -> PPlayer.location().distanceTo(current) > 20 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                        return false;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case GNOME_TREE_ENTRANCE:
            case GNOME_TREE_EXIT:
                if (clickObject(Filters.Objects.nameEquals("Tree Door").and(Filters.Objects.actionsContains("Open")), "Open",
                        () -> PPlayer.location().getY() == 3492 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    WaitFor.milliseconds(1060, 1500);
                    return true;
                } else {
                    log.info("Could not navigate through gnome door.");
                }

                break;

            case ZEAH_SAND_CRAB:
                if (InteractionHelper.click(PObjects.findNPC(Filters.NPCs.nameEquals("Sandicrahb")), "Travel") && WaitFor.condition(10000, () -> PPlayer.location().getY() >= 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
                    log.info("Paid for travel.");
                    return true;
                } else {
                    log.info("Failed to pay travel.");
                }
                break;
            case ZEAH_SAND_CRAB_ISLAND:
                if (InteractionHelper.click(PObjects.findNPC(Filters.NPCs.nameEquals("Sandicrahb")), "Travel") && WaitFor.condition(10000, () -> PPlayer.location().getY() < 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
                    log.info("Paid for travel.");
                    return true;
                } else {
                    log.info("Failed to pay travel.");
                }
                break;


            case KARAMJA_PAY_FARE:
            case PORT_SARIM_PAY_FARE:

                if (handleKaramjaShip()) {
                    log.info("Successfully boarded ship!");
                    return true;
                } else {
                    log.info("Failed to pay fare.");
                }
                return false;
            case ARDOUGNE_PAY_FARE:
                if (handleShip("Ardougne")) {
                    log.info("Successfully boarded ship!");
                    return true;
                } else {
                    log.info("Failed to pay fare.");
                }
                return false;
            case BRIMHAVEN_PAY_FARE:
                if (handleShip("Brimhaven")) {
                    log.info("Successfully boarded ship!");
                    return true;
                } else {
                    log.info("Failed to pay fare.");
                }
                return false;
            case RIMMINGTON_PAY_FARE:
                if (handleShip("Rimmington")) {
                    log.info("Successfully boarded ship!");
                    return true;
                } else {
                    log.info("Failed to pay fare.");
                }
                return false;
            case PEST_CONTROL:
            case PORT_SARIM_PEST_CONTROL:
                return InteractionHelper.click(
                        PObjects.findNPC(Filters.NPCs.actionsContains("Travel").and(Filters.NPCs.nameEquals("Squire"))), "Travel")
                        && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;

            case PORT_SARIM_VEOS:
                return handleZeahBoats("Travel to Port Sarim.");
            case GREAT_KOUREND:
                return handleZeahBoats("Travel to Port Piscarilius.");
            case LANDS_END:
                return handleZeahBoats("Travel to Land's End.");

            case ARDY_LOG_WEST:
            case ARDY_LOG_EAST:
                var logobj = PObjects.findObject(
                        Filters.Objects.withinDistance(15)
                                .and(Filters.Objects.nameEquals("Log balance"))
                                .and(Filters.Objects.actionsContains("Walk-across")));

                if (PInteraction.tileObject(logobj, "Walk-across")) {
                    int agilityXP = PSkills.getXp(Skill.AGILITY);
                    if (WaitFor.condition(PUtils.random(7600, 8000), () -> PSkills.getXp(Skill.AGILITY) > agilityXP ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
                        return true;
                    }
                    if (PPlayer.isMoving()) {
                        WaitFor.milliseconds(1200, 2300);
                    }
                }
                log.info("Could not navigate ardy log.");
                break;


            case GNOME_TREE_DAERO:
                break;

            case GNOME_WAYDAR:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Daero"), "Travel")) {
                    if (WaitFor.condition(5000, () -> PPlayer.location().distanceToHypotenuse(GNOME_WAYDAR.getRSTile().toWorldPoint()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case CRASH_ISLAND:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Waydar"), "Travel")) {
                    if (WaitFor.condition(5000, () -> PPlayer.location().distanceToHypotenuse(CRASH_ISLAND.getRSTile().toWorldPoint()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case APE_ATOLL_GLIDER_CRASH:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Lumdo"), "Travel")) {
                    if (WaitFor.condition(5000, () -> PPlayer.location().distanceToHypotenuse(APE_ATOLL_GLIDER_CRASH.getRSTile().toWorldPoint()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;
            case GNOME_DROPOFF:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Waydar"), "Travel")) {
                    if (WaitFor.condition(5000, () -> PPlayer.location().distanceToHypotenuse(CRASH_ISLAND.getRSTile().toWorldPoint()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case HAM_OUTSIDE:
                if (clickObject(Filters.Objects.nameEquals("Ladder"), "Climb-up", () -> PPlayer.location().getY() < 4000 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                break;

            case HAM_INSIDE:
                var lockDoor = PObjects.findObject(Filters.Objects.actionsContains("Pick-lock"));
                if (lockDoor != null) {
                    if (InteractionHelper.click(lockDoor, "Pick-Lock")) {
                        WaitFor.condition(
                                WaitFor.random(6000, 9000), () -> PObjects.findObject(Filters.Objects.actionsContains("Pick-lock")) == null ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        return true;
                    }
                } else {
                    var openDoor = PObjects.findObject(Filters.Objects.actionsContains("Climb-down"));
                    if (InteractionHelper.click(openDoor, "Climb-down")) {
                        WaitFor.condition(3000, () -> HAM_INSIDE.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        return true;
                    }
                }
                break;


            case FEROX_ENCLAVE_PORTAL_F2P:
                if (NPCInteraction.isConversationWindowUp() || clickObject(Filters.Objects.nameEquals("Large door"), "Open", () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    NPCInteraction.handleConversationRegex("Yes");
                    return WaitFor.condition(3000,
                            () -> FEROX_ENCLAVE_PORTAL_F2P.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;
            case CASTLE_WARS_DOOR:
                if (NPCInteraction.isConversationWindowUp() || clickObject(Filters.Objects.nameEquals("Castle Wars portal"), "Enter", () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    NPCInteraction.handleConversationRegex("Yes");
                    return WaitFor.condition(3000,
                            () -> CASTLE_WARS_DOOR.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;

            case FOSSIL_ISLAND_BARGE:
                if (clickObject(Filters.Objects.nameEquals("Rowboat"), "Travel", () -> NPCInteraction.isConversationWindowUp() ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    NPCInteraction.handleConversationRegex("Row to the barge and travel to the Digsite.");
                    return WaitFor.condition(5000,
                            () -> FOSSIL_ISLAND_BARGE.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;

            case ENTRANA_TO_PORT_SARIM:
            case PORT_SARIM_TO_ENTRANA:
                if (handleShip("Take-boat")) {
                    log.info("Successfully boarded ship!");
                    return true;
                } else {
                    log.info("Failed to take Entrana boat.");
                }
                break;

            case FAIRY_RING_ABYSSAL_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.ABYSSAL_AREA);
            case FAIRY_RING_ABYSSAL_NEXUS:
                return FairyRing.takeFairyRing(FairyRing.Locations.ABYSSAL_NEXUS);
            case FAIRY_RING_APE_ATOLL:
                return FairyRing.takeFairyRing(FairyRing.Locations.APE_ATOLL);
            case FAIRY_RING_ARCEUUS_LIBRARY:
                return FairyRing.takeFairyRing(FairyRing.Locations.ARCEUUS_LIBRARY);
            case FAIRY_RING_ARDOUGNE_ZOO:
                return FairyRing.takeFairyRing(FairyRing.Locations.ARDOUGNE_ZOO);
            case FAIRY_RING_CANIFIS:
                return FairyRing.takeFairyRing(FairyRing.Locations.CANIFIS);
            case FAIRY_RING_CHASM_OF_FIRE:
                return FairyRing.takeFairyRing(FairyRing.Locations.CHASM_OF_FIRE);
            case FAIRY_RING_COSMIC_ENTITYS_PLANE:
                return FairyRing.takeFairyRing(FairyRing.Locations.COSMIC_ENTITYS_PLANE);
            case FAIRY_RING_DRAYNOR_VILLAGE_ISLAND:
                return FairyRing.takeFairyRing(FairyRing.Locations.DRAYNOR_VILLAGE_ISLAND);
            case FAIRY_RING_EDGEVILLE:
                return FairyRing.takeFairyRing(FairyRing.Locations.EDGEVILLE);
            case FAIRY_RING_ENCHANTED_VALLEY:
                return FairyRing.takeFairyRing(FairyRing.Locations.ENCHANTED_VALLEY);
            case FAIRY_RING_FELDIP_HILLS_HUNTER_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.FELDIP_HILLS_HUNTER_AREA);
            case FAIRY_RING_FISHER_KINGS_REALM:
                return FairyRing.takeFairyRing(FairyRing.Locations.FISHER_KINGS_REALM);
            case FAIRY_RING_GORAKS_PLANE:
                return FairyRing.takeFairyRing(FairyRing.Locations.GORAKS_PLANE);
            case FAIRY_RING_HAUNTED_WOODS:
                return FairyRing.takeFairyRing(FairyRing.Locations.HAUNTED_WOODS);
            case FAIRY_RING_HAZELMERE:
                return FairyRing.takeFairyRing(FairyRing.Locations.HAZELMERE);
            case FAIRY_RING_ISLAND_SOUTHEAST_ARDOUGNE:
                return FairyRing.takeFairyRing(FairyRing.Locations.ISLAND_SOUTHEAST_ARDOUGNE);
            case FAIRY_RING_KALPHITE_HIVE:
                return FairyRing.takeFairyRing(FairyRing.Locations.KALPHITE_HIVE);
            case FAIRY_RING_KARAMJA_KARAMBWAN_SPOT:
                return FairyRing.takeFairyRing(FairyRing.Locations.KARAMJA_KARAMBWAN_SPOT);
            case FAIRY_RING_LEGENDS_GUILD:
                return FairyRing.takeFairyRing(FairyRing.Locations.LEGENDS_GUILD);
            case FAIRY_RING_LIGHTHOUSE:
                return FairyRing.takeFairyRing(FairyRing.Locations.LIGHTHOUSE);
            case FAIRY_RING_MCGRUBOR_WOODS:
                return FairyRing.takeFairyRing(FairyRing.Locations.MCGRUBOR_WOODS);
            case FAIRY_RING_MISCELLANIA:
                return FairyRing.takeFairyRing(FairyRing.Locations.MISCELLANIA);
            case FAIRY_RING_MISCELLANIA_PENGUINS:
                return FairyRing.takeFairyRing(FairyRing.Locations.MISCELLANIA_PENGUINS);
            case FAIRY_RING_MORT_MYRE_ISLAND:
                return FairyRing.takeFairyRing(FairyRing.Locations.MORT_MYRE_ISLAND);
            case FAIRY_RING_MORT_MYRE_SWAMP:
                return FairyRing.takeFairyRing(FairyRing.Locations.MORT_MYRE_SWAMP);
            case FAIRY_RING_MOUNT_KARUULM:
                return FairyRing.takeFairyRing(FairyRing.Locations.MOUNT_KARUULM);
            case FAIRY_RING_MUDSKIPPER_POINT:
                return FairyRing.takeFairyRing(FairyRing.Locations.MUDSKIPPER_POINT);
            case FAIRY_RING_NORTH_OF_NARDAH:
                return FairyRing.takeFairyRing(FairyRing.Locations.NORTH_OF_NARDAH);
            case FAIRY_RING_PISCATORIS_HUNTER_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.PISCATORIS_HUNTER_AREA);
            case FAIRY_RING_POISON_WASTE:
                return FairyRing.takeFairyRing(FairyRing.Locations.POISON_WASTE);
            case FAIRY_RING_POLAR_HUNTER_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.POLAR_HUNTER_AREA);
            case FAIRY_RING_RELLEKKA_SLAYER_CAVE:
                return FairyRing.takeFairyRing(FairyRing.Locations.RELLEKKA_SLAYER_CAVE);
            case FAIRY_RING_SHILO_VILLAGE:
                return FairyRing.takeFairyRing(FairyRing.Locations.SHILO_VILLAGE);
            case FAIRY_RING_SINCLAIR_MANSION:
                return FairyRing.takeFairyRing(FairyRing.Locations.SINCLAIR_MANSION);
            case FAIRY_RING_SOUTH_CASTLE_WARS:
                return FairyRing.takeFairyRing(FairyRing.Locations.SOUTH_CASTLE_WARS);
            case FAIRY_RING_TOWER_OF_LIFE:
                return FairyRing.takeFairyRing(FairyRing.Locations.TOWER_OF_LIFE);
            case FAIRY_RING_TZHAAR:
                return FairyRing.takeFairyRing(FairyRing.Locations.TZHAAR);
            case FAIRY_RING_WIZARDS_TOWER:
                return FairyRing.takeFairyRing(FairyRing.Locations.WIZARDS_TOWER);
            case FAIRY_RING_YANILLE:
                return FairyRing.takeFairyRing(FairyRing.Locations.YANILLE);
            case FAIRY_RING_ZANARIS:
                return FairyRing.takeFairyRing(FairyRing.Locations.ZANARIS);
            case FAIRY_RING_ZUL_ANDRA:
                return FairyRing.takeFairyRing(FairyRing.Locations.ZUL_ANDRA);

            case WITCHHAVEN_FERRY:
            case FISHING_PLATFORM_FERRY:
                return handleFishingPlatform();

            case FOSSIL_ISLAND_FERRY_NORTH:
                return takeFossilIslandBoat("Row to the north of the island.");
            case FOSSIL_ISLAND_FERRY_ISLAND:
                return takeFossilIslandBoat("Row out to sea.");
            case FOSSIL_ISLAND_FERRY_CAMP:
                var guard = PObjects.findNPC(Filters.NPCs.nameEquals("Barge guard"));
                if (guard != null) {
                    if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Barge guard"), "Quick-Travel")) {
                        return WaitFor.condition(8000,
                                () -> FOSSIL_ISLAND_FERRY_CAMP.getRSTile().toWorldPoint().distanceToHypotenuse(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                    }
                } else {
                    return takeFossilIslandBoat("Row to the camp.");
                }
                break;
            case RELLEKKA_DOCK_FROM_ISLES:
                return NPCInteraction.clickNpc(Filters.NPCs.actionsEquals("Rellekka"), "Rellekka") &&
                        WaitFor.condition(15000, () -> RELLEKKA_DOCK_FROM_ISLES.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case JATIZSO_DOCK:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Mord Gunnars"), "Jatizso") &&
                        WaitFor.condition(15000, () -> JATIZSO_DOCK.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case NEITIZNOT_DOCK:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Maria Gunnars"), "Neitiznot") &&
                        WaitFor.condition(15000, () -> NEITIZNOT_DOCK.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;

            case OBSERVATORY_INSIDE:
                return clickObject(Filters.Objects.nameEquals("Rope"), "Climb", () -> OBSERVATORY_INSIDE.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 5
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600, 1800) != null;
            case OBSERVATORY_OUTSIDE:
                return (NPCInteraction.isConversationWindowUp() || clickObject(Filters.Objects.nameEquals("Door"), "Open",
                        () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
                        && WaitFor.condition(15000, () -> {
                    if (NPCInteraction.isConversationWindowUp())
                        NPCInteraction.handleConversation("Yes.");
                    return OBSERVATORY_OUTSIDE.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 5
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) == WaitFor.Return.SUCCESS && WaitFor.milliseconds(600, 1800) != null;

            case MOSS_GIANT_ISLAND_ROPE:
            case MOSS_GIANT_ISLAND_ROPE_LANDING:
                if (PPlayer.location().distanceTo(MOSS_GIANT_ISLAND_ROPE.getRSTile().toWorldPoint()) >= 2) {
                    AccurateMouse.walkTo(MOSS_GIANT_ISLAND_ROPE.getRSTile());
                    WaitFor.milliseconds(200, 400);
                }
                if (clickObject(Filters.Objects.nameEquals("Ropeswing"), "Swing-on", () -> PPlayer.location().getX() < 2708 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                return false;
            case SHANTAY_PASS_ENTRANCE:
            case SHANTAY_PASS_EXIT:
                if (PPlayer.location().getY() < 3117) {
                    return clickObject(Filters.Objects.nameEquals("Shantay pass"), "Go-through", () -> SHANTAY_PASS_ENTRANCE.getRSTile().equals(PPlayer.location())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600, 1800) != null;
                } else if (PInventory.findItem(Filters.Items.idEquals(1854)) == null) {
                    NPCInteraction.talkTo(Filters.NPCs.actionsEquals("Buy-pass"), new String[]{"Buy-pass"}, new String[]{});
                }
                return PInventory.findItem(Filters.Items.idEquals(1854)) != null && clickObject(Filters.Objects.nameEquals("Shantay pass"), "Go-through", () -> {
                    DoomsToggle.handleToggle();
                    return SHANTAY_PASS_EXIT.getRSTile().equals(PPlayer.location())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) && WaitFor.milliseconds(600, 1800) != null;

            case PATERDOMUS_EAST_ENTRANCE:
                return clickObject(Filters.Objects.nameEquals("Trapdoor"), new String[]{"Open", "Climb-down"}, () -> PATERDOMUS_EAST_ENTRANCE.getRSTile().equals(PPlayer.location())
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600, 1800) != null;
            case PATERDOMUS_EAST_EXIT:
                return clickObject(Filters.Objects.nameEquals("Holy barrier"), "Pass-through", () -> PATERDOMUS_EAST_EXIT.getRSTile().equals(PPlayer.location())
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600, 1800) != null;

            case SWAMP_BOATY:
                return InteractionHelper.click(PObjects.findObject(Filters.Objects.nameEquals("Swamp Boaty")), "Quick-board") && WaitFor.condition(15000, () -> SWAMP_BOATY.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 5
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != null && WaitFor.milliseconds(600, 1800) != null;
            case SWAMP_BOATY_MORTTON:
                return InteractionHelper.click(PObjects.findObject(Filters.Objects.nameEquals("Swamp Boaty")), "Board") && WaitFor.condition(15000, () -> SWAMP_BOATY_MORTTON.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 5
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != null && WaitFor.milliseconds(600, 1800) != null;

            case BRINE_RAT_CAVE_TREE:
            case BRINE_RAT_CAVE_ENTER:
                if (PPlayer.location().distanceTo(BRINE_RAT_CAVE_TREE.getRSTile().toWorldPoint()) >= 2) {
                    if (AccurateMouse.walkTo(BRINE_RAT_CAVE_TREE.getRSTile())) {
                        WaitFor.condition(6000,
                                () -> PPlayer.location().distanceTo(BRINE_RAT_CAVE_TREE.getRSTile().toWorldPoint()) <= 2 ?
                                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    }
                }

                return PInteraction.item(PInventory.findItem(Filters.Items.nameEquals("Spade")), "Dig") && WaitFor.condition(10000,
                        () -> PPlayer.location().distanceTo(BRINE_RAT_CAVE_ENTER.getRSTile().toWorldPoint()) < 5 ?
                                WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS && WaitFor.milliseconds(1500, 2500) != null;

            case FERRY_AL_KHARID_TO_UNKAH:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Ferryman Sathwood"), "Ferry") &&
                        WaitFor.condition(15000, () -> FERRY_AL_KHARID_TO_UNKAH.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case FERRY_UNKAH_TO_AL_KHARID:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Ferryman Nathwood"), "Ferry") &&
                        WaitFor.condition(15000, () -> FERRY_UNKAH_TO_AL_KHARID.getRSTile().toWorldPoint().distanceTo(PPlayer.location()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;


            case UNKAH_SHANTAY_PASS_EAST_ENTRANCE:
            case UNKAH_SHANTAY_PASS_EAST_EXIT:
                if (PPlayer.location().getX() > 3195) {
                    return clickObject(Filters.Objects.nameEquals("Shantay pass"), "Go-through", () -> UNKAH_SHANTAY_PASS_EAST_ENTRANCE.getRSTile().equals(PPlayer.location())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600, 1800) != null;
                } else if (PInventory.getCount(1854) == 0) {
                    NPCInteraction.talkTo(Filters.NPCs.actionsEquals("Buy-pass"), new String[]{"Buy-pass"}, new String[]{});
                }
                return PInventory.getCount(1854) > 0 && clickObject(Filters.Objects.nameEquals("Shantay pass"), "Go-through", () -> {
                    DoomsToggle.handleToggle();
                    return UNKAH_SHANTAY_PASS_EAST_EXIT.getRSTile().equals(PPlayer.location())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) && WaitFor.milliseconds(600, 1800) != null;
            case UNKAH_SHANTAY_PASS_SOUTH_ENTRANCE:
            case UNKAH_SHANTAY_PASS_SOUTH_EXIT:
                if (PPlayer.location().getX() > 3195) {
                    return clickObject(Filters.Objects.nameEquals("Shantay pass"), "Go-through", () -> UNKAH_SHANTAY_PASS_SOUTH_ENTRANCE.getRSTile().equals(PPlayer.location())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600, 1800) != null;
                } else if (PInventory.getCount(1854) == 0) {
                    NPCInteraction.talkTo(Filters.NPCs.actionsEquals("Buy-pass"), new String[]{"Buy-pass"}, new String[]{});
                }
                return PInventory.getCount(1854) > 0 && clickObject(Filters.Objects.nameEquals("Shantay pass"), "Go-through", () -> {
                    DoomsToggle.handleToggle();
                    return UNKAH_SHANTAY_PASS_SOUTH_EXIT.getRSTile().equals(PPlayer.location())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) && WaitFor.milliseconds(600, 1800) != null;
            case YANILLE_BALANCE_EDGE_SOUTH:
                return clickObject(Filters.Objects.nameEquals("Balancing ledge"), "Walk-across",
                        () -> PPlayer.location().distanceTo(YANILLE_BALANCE_EDGE_NORTH.getRSTile().toWorldPoint()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case YANILLE_BALANCE_EDGE_NORTH:
                return clickObject(Filters.Objects.nameEquals("Balancing edge"), "Walk-across",
                        () -> PPlayer.location().distanceTo(YANILLE_BALANCE_EDGE_SOUTH.getRSTile().toWorldPoint()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case YANILLE_MONKEY_BARS_EAST:
                return clickObject(Filters.Objects.nameEquals("Monkeybars"), "Swing across",
                        () -> PPlayer.location().distanceTo(YANILLE_MONKEY_BARS_WEST.getRSTile().toWorldPoint()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case YANILLE_MONKEY_BARS_WEST:
                return clickObject(Filters.Objects.nameEquals("Monkeybars"), "Swing across",
                        () -> PPlayer.location().distanceTo(YANILLE_MONKEY_BARS_EAST.getRSTile().toWorldPoint()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        }

        return false;
    }

    public static boolean handleZeahBoats(String locationOption) {
        String travelOption = "Travel";
        List<NPC> npcs = PObjects.findAllNPCs(Filters.NPCs.nameEquals("Veos", "Captain Magoro"));
        if (npcs.size() > 0) {
            String[] actions = npcs.get(0).getTransformedComposition().getActions();
            if (actions != null) {
                List<String> asList = Arrays.asList(actions);
                if (asList.stream().filter(Objects::nonNull).anyMatch(a -> a.equals("Port Sarim") || a.equals("Land's End"))) {
                    if (locationOption.contains("Port Sarim")) {
                        travelOption = "Port Sarim";
                    } else if (locationOption.contains("Piscarilius")) {
                        travelOption = "Port Piscarilius";
                    } else if (locationOption.contains("Land")) {
                        travelOption = "Land's End";
                    }
                } else if (!asList.contains("Travel")) {
                    return handleFirstTripToZeah(locationOption);
                }
            }
        }
        if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Veos", "Captain Magoro"), travelOption)) {
            WorldPoint current = PPlayer.location();
            if (WaitFor.condition(8000, () -> (ShipUtils.isOnShip() || PPlayer.location().distanceTo(current) > 20) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                return false;
            }
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    private static boolean handleFirstTripToZeah(String locationOption) {
        log.info("First trip to zeah");
        if (NPCInteraction.talkTo(Filters.NPCs.nameEquals("Veos", "Captain Magoro"), new String[]{"Talk-to"}, new String[]{
                locationOption, "Can you take me somewhere?", "That's great, can you take me there please?", "Can you take me to Great Kourend?"})) {
            WorldPoint current = PPlayer.location();
            if (WaitFor.condition(8000, () -> (ShipUtils.isOnShip() || PPlayer.location().distanceTo(current) > 20) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                return false;
            }
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean handleShip(String... targetLocation) {
        if (NPCInteraction.clickNpc(Filters.NPCs.actionsContains(targetLocation), targetLocation)
                && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean handleKaramjaShip() {
        String[] options = {"Pay-fare", "Pay-Fare"};
        String[] chat = {"Yes please.", "Can I journey on this ship?", "Search away, I have nothing to hide.", "Ok."};
        boolean pirateTreasureComplete = PVars.getSetting(71) >= 4;
        if (pirateTreasureComplete) {
            return handleShip("Pay-fare", "Pay-Fare");
        } else if (NPCInteraction.talkTo(Filters.NPCs.actionsContains(options), options, chat)
                && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean walkToObject(PTileObject object) {
        if (!AccurateMouse.walkTo(object.getFirst().getWorldLocation())) {
            return false;
        }
        return WaitFor.condition(PUtils.random(7000, 10000), () -> PPlayer.location().distanceToHypotenuse(object.getFirst().getWorldLocation()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    public static boolean clickObject(PTileObject obj, String action, WaitFor.Condition condition) {
        return InteractionHelper.click(obj, action, condition);
    }

    public static boolean clickObject(Predicate<PTileObject> filter, String action, WaitFor.Condition condition) {
        return clickObject(filter, new String[]{action}, condition);
    }

    public static boolean clickObject(Predicate<PTileObject> filter, String[] action, WaitFor.Condition condition) {
        List<PTileObject> objects = PObjects.getAllObjects()
                .stream()
                .filter(filter)
                .filter(pair -> pair.getFirst().getWorldLocation().distanceToHypotenuse(PPlayer.location()) <= 15)
                .sorted(Comparator.comparingInt(a -> Math.round(a.getFirst().getWorldLocation().distanceToHypotenuse(PPlayer.location()))))
                .collect(Collectors.toList());

        if (objects.size() == 0) {
            return false;
        }
        return InteractionHelper.click(objects.get(0), action, condition);
    }


    private static boolean handleFishingPlatform() {
        NPC jeb = PObjects.findNPC(Filters.NPCs.nameEquals("Jeb").and(Filters.NPCs.actionsContains("Travel")));

        if (jeb != null) {
            return InteractionHelper.click(jeb, "Travel") &&
                    WaitFor.condition(20000, () -> (PWidgets.get(WidgetInfo.DIALOG_NPC_TEXT) != null && !PWidgets.get(WidgetInfo.DIALOG_NPC_TEXT).isHidden()) ?
                            WaitFor.Return.SUCCESS :
                            WaitFor.Return.IGNORE

                    ) == WaitFor.Return.SUCCESS;
        } else {
            return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Holgart"), "Travel") &&
                    WaitFor.condition(20000, () -> (PWidgets.get(WidgetInfo.DIALOG_NPC_TEXT) != null && !PWidgets.get(WidgetInfo.DIALOG_NPC_TEXT).isHidden()) ?
                            WaitFor.Return.SUCCESS :
                            WaitFor.Return.IGNORE

                    ) == WaitFor.Return.SUCCESS;
        }
    }

    private static boolean takeFossilIslandBoat(String destination) {
        if (NPCInteraction.isConversationWindowUp() || clickObject(
                Filters.Objects.nameEquals("Rowboat"),
                "Travel",
                () -> NPCInteraction.isConversationWindowUp() ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
            WorldPoint myPos = PPlayer.location();
            NPCInteraction.handleConversation(destination);
            return WaitFor.condition(5000, () -> PPlayer.location().distanceTo(myPos) > 10 ? WaitFor.Return.SUCCESS :
                    WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
        return false;
    }

    private static boolean handleCarpetRide(String carpetDestination) {
        if (NPCInteraction.talkTo(Filters.NPCs.actionsContains("Travel"), new String[]{"Travel"}, new String[]{carpetDestination})) {
            WaitFor.milliseconds(3500, 5000); //wait for board carpet before starting moving condition
            WaitFor.condition(30000, WaitFor.getNotMovingCondition());
            WaitFor.milliseconds(2250, 3250);
            return true;
        }
        return false;
    }
}
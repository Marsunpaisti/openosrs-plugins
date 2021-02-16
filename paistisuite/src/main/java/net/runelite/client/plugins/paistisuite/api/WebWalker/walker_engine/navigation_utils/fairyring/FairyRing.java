package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring;


import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemDefinition;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PObjects;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters.FirstLetter;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters.SecondLetter;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters.ThirdLetter;
import static net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters.FirstLetter.*;
import static net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters.SecondLetter.*;
import static net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters.ThirdLetter.*;

import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Filters;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSVarBit;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class FairyRing {

    public static final int
            INTERFACE_MASTER = 398,
            TELEPORT_CHILD = 26,
            ELITE_DIARY_VARBIT = 4538;
    private static final int[]
            DRAMEN_STAFFS = {772,9084};

    private static TileObject ring;


    private static RSInterface getTeleportButton() {
        return new RSInterface(PWidgets.get(INTERFACE_MASTER, TELEPORT_CHILD));
    }

    public static boolean takeFairyRing(Locations location){
        var ringSearch = PObjects.findObject(Filters.Objects.nameEquals("Fairy ring"));

        if (ringSearch == null) {
            log.error("Unable to find fairy ring!");
            return false;
        }

        ring = ringSearch.getFirst();
        if(location == null)
            return false;

        if (ring == null) return false;

        boolean staffEquipped = PInventory.getEquippedItems()
                .stream()
                .anyMatch(item -> item.getId() == DRAMEN_STAFFS[0] || item.getId() == DRAMEN_STAFFS[1]);

        if (RSVarBit.get(ELITE_DIARY_VARBIT).getValue() == 0 && !staffEquipped){
            Pair<WidgetItem, ItemDefinition> staff = PInventory.getAllItemsWithDefs()
                    .stream()
                    .filter(pair -> pair.getSecond().getId() == DRAMEN_STAFFS[0] || pair.getSecond().getId() == DRAMEN_STAFFS[1])
                    .findFirst()
                    .orElse(null);

            if (!InteractionHelper.click(staff, "Wield")){
                return false;
            } else {
                WaitFor.milliseconds(400, 800);
            }
        }
        if(!hasInterface()){
            if(hasCachedLocation(location)){
                return takeLastDestination(location);
            } else if(!openFairyRing()){
                return false;
            }
        }
        final WorldPoint startPos = PPlayer.getWorldLocation();
        if (location.turnTo() && pressTeleport()
                && WaitFor.condition(8000, () -> startPos.distanceToHypotenuse(PPlayer.location()) > 20 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.random(350, 1000);
            return true;
        }

        return false;
    }

    private static boolean hasInterface(){
        return PWidgets.isSubstantiated(INTERFACE_MASTER);
    }

    private static boolean hasCachedLocation(Locations location){
        TileObject ring = PObjects.getAllObjectsWithDefs()
                .stream()
                .filter(pair -> pair.getSecond().getName().equals("Fairy ring"))
                .filter(pair -> pair.getFirst().getWorldLocation().distanceToHypotenuse(PPlayer.location()) <= 25)
                .filter(pair -> Arrays.stream(pair.getSecond().getActions())
                                    .filter(Objects::nonNull)
                                    .anyMatch(a -> a.equals(location.toString())))
                .findFirst()
                .map(Pair::getFirst).orElse(null);

        return ring != null;
    }

    private static boolean takeLastDestination(Locations location){
        var ringSearch = PObjects.findObject(Filters.Objects.nameEquals("Fairy ring"));
        if (ringSearch != null) ring = ringSearch.getFirst();
        final WorldPoint startPos = PPlayer.location();
        return InteractionHelper.click(ring,"Last-destination (" + location + ")") &&
                WaitFor.condition(8000, () -> startPos.distanceToHypotenuse(PPlayer.location()) > 20? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static boolean pressTeleport(){
        RSInterface iface = getTeleportButton();
        return iface != null && iface.click();
    }

    private static boolean openFairyRing(){
        var ringSearch = PObjects.findObject(Filters.Objects.nameEquals("Fairy ring"));
        if (ringSearch != null) ring = ringSearch.getFirst();
        return InteractionHelper.click(ring,"Configure") &&
                WaitFor.condition(10000, () -> PWidgets.isSubstantiated(INTERFACE_MASTER) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    public enum Locations {
        ABYSSAL_AREA(A, L, R),
        ABYSSAL_NEXUS(D, I, P),
        APE_ATOLL(C, L, R),
        ARCEUUS_LIBRARY(C, I, S),
        ARDOUGNE_ZOO(B, I, S),
        CANIFIS(C, K, S),
        CHASM_OF_FIRE(D, J, R),
        COSMIC_ENTITYS_PLANE(C, K, P),
        DORGESH_KAAN_SOUTHERN_CAVE(A, J, Q),
        DRAYNOR_VILLAGE_ISLAND(C, L, P),
        EDGEVILLE(D, K, R),
        ENCHANTED_VALLEY(B, K, Q),
        FELDIP_HILLS_HUNTER_AREA(A, K, S),
        FISHER_KINGS_REALM(B, J, R),
        GORAKS_PLANE(D, I, R),
        HAUNTED_WOODS(A, L, Q),
        HAZELMERE(C, L, S),
        ISLAND_SOUTHEAST_ARDOUGNE(A, I, R),
        KALPHITE_HIVE(B, I, Q),
        KARAMJA_KARAMBWAN_SPOT(D, K, P),
        LEGENDS_GUILD(B, L, R),
        LIGHTHOUSE(A, L, P),
        MCGRUBOR_WOODS(A, L, S),
        MISCELLANIA(C, I, P),
        MISCELLANIA_PENGUINS(A, J, S),
        MORT_MYRE_ISLAND(B, I, P),
        MORT_MYRE_SWAMP(B, K, R),
        MOUNT_KARUULM(C, I, R),
        MUDSKIPPER_POINT(A, I, Q),
        MYREQUE_HIDEOUT(D, L, S),
        NORTH_OF_NARDAH(D, L, Q),
        PISCATORIS_HUNTER_AREA(A, K, Q),
        POH(D, I, Q),
        POISON_WASTE(D, L, R),
        POLAR_HUNTER_AREA(D, K, S),
        RELLEKKA_SLAYER_CAVE(A, J, R),
        SHILO_VILLAGE(C, K, R),
        SINCLAIR_MANSION(C, J, R),
        SOUTH_CASTLE_WARS(B, K, P),
        TOWER_OF_LIFE(D, J, P),
        TZHAAR(B, L, P),
        WIZARDS_TOWER(D, I, S),
        YANILLE(C, I, Q),
        ZANARIS(B, K, S),
        ZUL_ANDRA(B, J, S);

        FirstLetter first;
        SecondLetter second;
        ThirdLetter third;

        Locations(FirstLetter first, SecondLetter second, ThirdLetter third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public boolean turnTo() {
            return first.turnTo() && WaitFor.milliseconds(500, 1200) != null &&
                    second.turnTo() && WaitFor.milliseconds(500, 1200) != null &&
                    third.turnTo() && WaitFor.milliseconds(500, 1200) != null;
        }

        @Override
        public String toString() {
            return "" + first + second + third;
        }
    }
}
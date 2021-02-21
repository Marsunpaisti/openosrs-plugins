package net.runelite.client.plugins.paistisuite.api;

import com.google.common.collect.EvictingQueue;
import lombok.Synchronized;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.types.PGroundItem;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

@Slf4j
public class PGroundItems {
    private static final int KRAKEN_REGION = 9116;
    private static final int KBD_NMZ_REGION = 9033;
    private static final int COINS = ItemID.COINS_995;
    // items stay on the ground for 30 mins in an instance
    private static final int INSTANCE_DURATION_MILLIS = 45 * 60 * 1000;
    private static final int INSTANCE_DURATION_TICKS = (int) floor(30 * 60 / 0.6);
    //untradeables stay on the ground for 150 seconds (http://oldschoolrunescape.wikia.com/wiki/Item#Dropping_and_Destroying)
    private static final int UNTRADEABLE_DURATION_MILLIS = 150 * 1000;
    private static final int UNTRADEABLE_DURATION_TICKS = (int) floor(150 / 0.6);
    //items stay on the ground for 1 hour after death
    private static final int DEATH_DURATION_MILLIS = 60 * 60 * 1000;
    private static final int DEATH_DURATION_TICKS = (int) floor(60 * 60 / 0.6);
    private static final int NORMAL_DURATION_MILLIS = 60 * 1000;
    private static final int NORMAL_DURATION_TICKS = (int) floor(60 / 0.6);
    public static final Map<PGroundItem.GroundItemKey, PGroundItem> collectedGroundItems = new LinkedHashMap<>();
    private static final Queue<Integer> droppedItemQueue = EvictingQueue.create(16); // recently dropped items


    public static void onItemSpawned(ItemSpawned itemSpawned){
        final TileItem item = itemSpawned.getItem();
        final Tile tile = itemSpawned.getTile();

        final PGroundItem groundItem = buildGroundItem(tile, item);

        if (groundItem == null)
        {
            return;
        }

        final PGroundItem.GroundItemKey groundItemKey = new PGroundItem.GroundItemKey(item.getId(), tile.getWorldLocation());
        synchronized (collectedGroundItems){
            PGroundItem existing = collectedGroundItems.putIfAbsent(groundItemKey, groundItem);
            if (existing != null)
            {
                existing.setQuantity(existing.getQuantity() + groundItem.getQuantity());
                // The spawn time remains set at the oldest spawn
            }
        }
    }

    public static void onItemDespawned(final ItemDespawned itemDespawned)
    {
        final TileItem item = itemDespawned.getItem();
        final Tile tile = itemDespawned.getTile();

        final PGroundItem.GroundItemKey groundItemKey = new PGroundItem.GroundItemKey(item.getId(), tile.getWorldLocation());
        PGroundItem groundItem;
        synchronized (collectedGroundItems) {
            groundItem = collectedGroundItems.get(groundItemKey);

            if (groundItem == null)
            {
                return;
            }

            if (groundItem.getQuantity() <= item.getQuantity())
            {
                collectedGroundItems.remove(groundItemKey);
            }
            else
            {
                groundItem.setQuantity(groundItem.getQuantity() - item.getQuantity());
                // When picking up an item when multiple stacks appear on the ground,
                // it is not known which item is picked up, so we invalidate the spawn
                // time
                groundItem.setSpawnTime(null);
            }
        }
    }

    public static void onItemQuantityChanged(final ItemQuantityChanged itemQuantityChanged)
    {
        final TileItem item = itemQuantityChanged.getItem();
        final Tile tile = itemQuantityChanged.getTile();
        final int oldQuantity = itemQuantityChanged.getOldQuantity();
        final int newQuantity = itemQuantityChanged.getNewQuantity();

        final int diff = newQuantity - oldQuantity;
        final PGroundItem.GroundItemKey groundItemKey = new PGroundItem.GroundItemKey(item.getId(), tile.getWorldLocation());
        synchronized (collectedGroundItems) {
            final PGroundItem groundItem = collectedGroundItems.get(groundItemKey);
            if (groundItem != null) {
                groundItem.setQuantity(groundItem.getQuantity() + diff);
            }
        }
    }

    public static void onNpcLootReceived(final NpcLootReceived npcLootReceived)
    {
        synchronized (collectedGroundItems){
            npcLootReceived.getItems().forEach(item ->
                    {
                        final PGroundItem.GroundItemKey groundItemKey = new PGroundItem.GroundItemKey(item.getId(), npcLootReceived.getNpc().getWorldLocation());
                        if (collectedGroundItems.containsKey(groundItemKey))
                        {
                            collectedGroundItems.get(groundItemKey).setOwnedByPlayer(true);
                        }
                    }
            );

            final Collection<ItemStack> items = npcLootReceived.getItems();
            lootReceived(items, PGroundItem.LootType.PVM);
        }
    }

    public static void onPlayerLootReceived(final PlayerLootReceived playerLootReceived)
    {
        synchronized (collectedGroundItems) {
            final Collection<ItemStack> items = playerLootReceived.getItems();
            lootReceived(items, PGroundItem.LootType.PVP);
        }
    }


    private static void lootReceived(final Collection<ItemStack> items, final PGroundItem.LootType lootType)
    {
        synchronized (collectedGroundItems) {
            for (final ItemStack itemStack : items) {
                final WorldPoint location = WorldPoint.fromLocal(PUtils.getClient(), itemStack.getLocation());
                final PGroundItem.GroundItemKey groundItemKey = new PGroundItem.GroundItemKey(itemStack.getId(), location);
                final PGroundItem groundItem = collectedGroundItems.get(groundItemKey);
                if (groundItem != null) {
                    groundItem.setMine(true);
                    groundItem.setTicks(200);
                    groundItem.setLootType(lootType);
                }
            }
        }
    }

    public static void onGameTick(final GameTick event)
    {
        synchronized (collectedGroundItems) {
            for (final PGroundItem item : collectedGroundItems.values())
            {
                if (item.getTicks() == -1)
                {
                    continue;
                }
                item.setTicks(item.getTicks() - 1);
            }
        }
    }

    public static void onGameStateChanged(final GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOADING)
        {
            collectedGroundItems.clear();
        }
    }

    public static void onMenuOptionClicked(final MenuOptionClicked menuOptionClicked)
    {
        if (menuOptionClicked.getMenuAction() == MenuAction.ITEM_FIFTH_OPTION)
        {
            final int itemId = menuOptionClicked.getId();
            // Keep a queue of recently dropped items to better detect
            // item spawns that are drops
            droppedItemQueue.add(itemId);
        }
    }

    public static List<PGroundItem> getGroundItems() {
        List<PGroundItem> allItems = new ArrayList<PGroundItem>();
        synchronized (collectedGroundItems) {
            for (PGroundItem item : collectedGroundItems.values()) {
                PGroundItem copy = PGroundItem.builder()
                        .itemComposition(item.getItemComposition())
                        .id(item.getId())
                        .itemId(item.getItemId())
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .location(item.getLocation())
                        .height(item.getHeight())
                        .haPrice(item.getHaPrice())
                        .gePrice(item.getGePrice())
                        .offset(item.getOffset())
                        .tradeable(item.isTradeable())
                        .isMine(item.getLootType() != PGroundItem.LootType.UNKNOWN)
                        .ticks(item.getTicks())
                        .durationMillis(item.getDurationMillis())
                        .isAlwaysPrivate(item.isAlwaysPrivate())
                        .isOwnedByPlayer(item.isOwnedByPlayer())
                        .droppedInstant(item.getDroppedInstant())
                        .lootType(item.getLootType())
                        .spawnTime(item.getSpawnTime())
                        .stackable(item.isStackable())
                        .build();
                allItems.add(copy);
            }
        }
        return allItems;
    }

    public static List<PGroundItem> findGroundItems(Predicate<PGroundItem> filter){
        return getGroundItems()
            .stream()
            .filter(filter)
            .collect(Collectors.toList());
    }


    private static boolean isInKraken()
    {
        return ArrayUtils.contains(PUtils.getClient().getMapRegions(), KRAKEN_REGION);
    }

    private static boolean isInKBDorNMZ()
    {
        return ArrayUtils.contains(PUtils.getClient().getMapRegions(), KBD_NMZ_REGION);
    }

    @Nullable
    private static PGroundItem buildGroundItem(final Tile tile, final TileItem item)
    {
        // Collect the data for the item
        final int itemId = item.getId();
        final ItemComposition itemComposition = PaistiSuite.getInstance().itemManager.getItemComposition(itemId);
        final int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;
        final int alchPrice = itemComposition.getHaPrice();

        final Player player = PUtils.getClient().getLocalPlayer();

        if (player == null)
        {
            return null;
        }

        int durationMillis;
        int durationTicks;

        WorldPoint playerLocation = PPlayer.location();
        final boolean dropped = tile.getWorldLocation().equals(PPlayer.location()) && droppedItemQueue.remove(itemId);

        if (PUtils.getClient().isInInstancedRegion())
        {
            if (isInKraken())
            {
                durationMillis = -1;
                durationTicks = -1;
            }
            else if (isInKBDorNMZ())
            {
                // NMZ and the KBD lair uses the same region ID but NMZ uses planes 1-3 and KBD uses plane 0
                if (PPlayer.location().getPlane() == 0)
                {
                    // Items in the KBD instance use the standard despawn timer
                    if (dropped)
                    {
                        durationTicks = NORMAL_DURATION_TICKS * 3;
                        durationMillis = NORMAL_DURATION_MILLIS * 3;
                    }
                    else
                    {
                        durationTicks = NORMAL_DURATION_TICKS * 2;
                        durationMillis = NORMAL_DURATION_MILLIS * 2;
                    }
                }
                else
                {
                    // Dropped items in the NMZ instance appear to never despawn?
                    if (dropped)
                    {
                        durationMillis = -1;
                        durationTicks = -1;
                    }
                    else
                    {
                        durationTicks = NORMAL_DURATION_TICKS * 2;
                        durationMillis = NORMAL_DURATION_MILLIS * 2;
                    }
                }
            }
            else
            {
                durationMillis = INSTANCE_DURATION_MILLIS;
                durationTicks = INSTANCE_DURATION_TICKS;
            }
        }
        else if (!itemComposition.isTradeable() && realItemId != COINS)
        {
            durationMillis = UNTRADEABLE_DURATION_MILLIS;
            durationTicks = UNTRADEABLE_DURATION_TICKS;
        }
        else
        {
            durationTicks = dropped ? NORMAL_DURATION_TICKS * 3 : NORMAL_DURATION_TICKS * 2;
            durationMillis = dropped ? NORMAL_DURATION_MILLIS * 3 : NORMAL_DURATION_MILLIS * 2;
        }

        final PGroundItem groundItem = PGroundItem.builder()
                .id(itemId)
                .location(tile.getWorldLocation())
                .itemId(realItemId)
                .quantity(item.getQuantity())
                .name(itemComposition.getName())
                .haPrice(alchPrice)
                .gePrice(itemComposition.getPrice())
                .height(-1)
                .tradeable(itemComposition.isTradeable())
                .droppedInstant(Instant.now())
                .durationMillis(durationMillis)
                .isAlwaysPrivate(PUtils.getClient().isInInstancedRegion() || (!itemComposition.isTradeable() && realItemId != COINS))
                .isOwnedByPlayer(tile.getWorldLocation().equals(playerLocation))
                .ticks(durationTicks)
                .lootType(dropped ? PGroundItem.LootType.DROPPED : PGroundItem.LootType.UNKNOWN)
                .spawnTime(Instant.now())
                .stackable(itemComposition.isStackable())
                .itemComposition(PaistiSuite.getInstance().itemManager.getItemComposition(itemId))
                .build();


        // Update item price in case it is coins
        if (realItemId == COINS)
        {
            groundItem.setHaPrice(1);
            groundItem.setGePrice(1);
        }
        else
        {
            groundItem.setGePrice(PaistiSuite.getInstance().itemManager.getItemPrice(realItemId));
        }

        return groundItem;
    }


}

package net.runelite.client.plugins.paistisuite.api.types;

import lombok.Builder;
import lombok.Data;
import lombok.Synchronized;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemComposition;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.mixins.Inject;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.http.api.osbuddy.OSBGrandExchangeClient;
import net.runelite.http.api.osbuddy.OSBGrandExchangeResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Slf4j

@Data
@Builder
public class PGroundItem
{
    public enum LootType
    {
        UNKNOWN,
        DROPPED,
        PVP,
        PVM
    }
    private ItemComposition itemComposition;
    private int id;
    private int itemId;
    private String name;
    private int quantity;
    private WorldPoint location;
    private int height;
    private int haPrice;
    private int gePrice;
    private int offset;
    private boolean tradeable;
    private boolean isMine;
    private int ticks;
    private int durationMillis;
    private boolean isAlwaysPrivate;
    private boolean isOwnedByPlayer;
    private Instant droppedInstant;

    @Nonnull
    private LootType lootType;

    @Nullable
    private Instant spawnTime;
    private boolean stackable;

    public int getPricePerSlot(){
        if (isStackable()) return gePrice*quantity;
        return Math.max(gePrice, haPrice);
    }

    boolean isMine()
    {
        return lootType != LootType.UNKNOWN;
    }

    public String[] getActions(){
        return this.itemComposition.getGroundActions();
    }

    public ItemComposition getDef(){
        return getItemComposition();
    }

    public boolean isAlwaysPrivate(){
        return isAlwaysPrivate;
    }

    @Value
    public static class GroundItemKey
    {
        int itemId;
        WorldPoint location;
    }
}
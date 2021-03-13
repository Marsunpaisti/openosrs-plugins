package net.runelite.client.plugins.pgearsetup;

import lombok.AllArgsConstructor;
import lombok.Value;
import net.runelite.client.plugins.paistisuite.api.PInventory;

import java.io.Serializable;

@Value
public class GearSetupItem implements Serializable {
    int id, quantity;
    boolean isNoted;

    public GearSetupItem(int id, int quantity, boolean isNoted){
        this.id = id;
        this.quantity = quantity;
        this.isNoted = isNoted;
    }
}

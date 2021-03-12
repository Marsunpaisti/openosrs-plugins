package net.runelite.client.plugins.pgearsetup;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@AllArgsConstructor
@Value
public class GearSetupItem implements Serializable {
    int id, quantity;
}

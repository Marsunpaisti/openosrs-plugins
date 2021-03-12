package net.runelite.client.plugins.pgearsetup;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.kit.KitType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GearSetupData implements Serializable {
    @Getter
    @Setter
    String name;

    @Getter
    @Setter
    HashMap<KitType, GearSetupItemOptions> equipment;

    @Getter
    @Setter
    ArrayList<GearSetupItemOptions> inventory;

    public GearSetupData(String name, HashMap<KitType, GearSetupItemOptions> equipment, ArrayList<GearSetupItemOptions> inventory){
        this.name = name;
        if (equipment == null) {
            this.equipment = new HashMap<>();
        } else {
            this.equipment = equipment;
        }
        if (inventory == null){
            this.inventory = new ArrayList<GearSetupItemOptions>();
        } else {
            this.inventory = inventory;
        }
    }
}

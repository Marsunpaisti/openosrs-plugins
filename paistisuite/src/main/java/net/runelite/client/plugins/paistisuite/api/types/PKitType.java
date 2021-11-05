package net.runelite.client.plugins.paistisuite.api.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PKitType {
    HEAD("Head"),
    CAPE("Cape"),
    AMULET("Amulet"),
    WEAPON("Weapon"),
    TORSO("Torso"),
    SHIELD("Shield"),
    ARMS("Arms"),
    LEGS("Legs"),
    HAIR("Hair"),
    HANDS("Hands"),
    BOOTS("Boots"),
    JAW("Jaw"),
    RING("Ring"),
    AMMUNITION("Ammo");;

    private final String name;

    public int getIndex() {
        return ordinal();
    }
}


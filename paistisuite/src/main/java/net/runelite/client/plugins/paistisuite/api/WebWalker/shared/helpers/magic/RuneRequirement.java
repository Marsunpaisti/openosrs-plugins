package net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RuneRequirement {
    public int quantity;
    public RuneElement type;

    public int getFirst(){
        return this.quantity;
    }

    public RuneElement getSecond(){
        return this.type;
    }
}

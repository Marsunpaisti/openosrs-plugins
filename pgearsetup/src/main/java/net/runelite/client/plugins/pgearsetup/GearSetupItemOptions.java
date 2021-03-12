package net.runelite.client.plugins.pgearsetup;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.util.AsyncBufferedImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GearSetupItemOptions implements Serializable {
    @Getter
    @Setter
    private ArrayList<GearSetupItem> options;

    public static List<AsyncBufferedImage> getItemImages(PGearSetup plugin, GearSetupItemOptions item){
        if (item.options == null || item.options.size() == 0) return null;

        return PUtils.clientOnly(() -> item.getOptions()
                .stream()
                .map(option -> plugin.itemManager.getImage(option.getId(), option.getQuantity(), option.getQuantity() > 1))
                .collect(Collectors.toList())
                , "getImages");
    }

    public GearSetupItemOptions(int id, int quantity){
        ArrayList<GearSetupItem> options = new ArrayList<GearSetupItem>();;
        if (id != -1 && quantity != -1){
            options.add(new GearSetupItem(id, quantity));
        }
        this.options = options;
    }

    public GearSetupItemOptions(ArrayList<GearSetupItem> options){
        this.options = options;
    }
}

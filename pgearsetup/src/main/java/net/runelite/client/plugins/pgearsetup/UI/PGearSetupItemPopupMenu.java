package net.runelite.client.plugins.pgearsetup.UI;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.kit.KitType;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.pgearsetup.GearSetupItem;
import net.runelite.client.plugins.pgearsetup.GearSetupItemOptions;
import net.runelite.client.plugins.pgearsetup.PGearSetup;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PGearSetupItemPopupMenu extends JPopupMenu {
    PGearSetup plugin;
    PGearSetupPanel mainPanel;
    GearSetupItemOptions itemOptions;
    KitType slot;
    public PGearSetupItemPopupMenu(PGearSetup plugin, PGearSetupPanel mainPanel, GearSetupItemOptions itemOptions, KitType slot){
        this.plugin = plugin;
        this.mainPanel = mainPanel;
        this.itemOptions = itemOptions;
        this.slot = slot;
        buildPanel();
    }

    public void buildPanel(){
        for (GearSetupItem option : itemOptions.getOptions()) {
            JMenuItem removeButton = new JMenuItem("Remove " + (option.isNoted() ? "(NOTED) " : "") + PInventory.getItemDef(option.getId()).getName() + " [" + option.getId() + "]");
            removeButton.addActionListener((e) -> {
                this.itemOptions.setOptions(this.itemOptions.getOptions().stream().filter(i -> i.getId() != option.getId()).collect(Collectors.toCollection(ArrayList::new)));
                mainPanel.reBuild();
                plugin.saveSetups();
            });
            add(removeButton);
        }

        if (slot != null){
            PItem current = PInventory.findEquipmentItem(itm -> itm.kitType == this.slot);
            if (current == null || this.itemOptions.getOptions().stream().noneMatch(i -> i.getId() == current.getId())){
                JMenuItem addCurrent = new JMenuItem("Add option " + current.getDefinition().getName() + " [" + current.getId() + "]");
                addCurrent.addActionListener((e) -> {
                    itemOptions.getOptions().add(new GearSetupItem(current.getId(), current.getQuantity(), current.isNoted()));
                    mainPanel.reBuild();
                    plugin.saveSetups();
                });
                add(addCurrent);
            }
        }

        if (slot == null) {
            List<PItem> newInvItems = PInventory.findAllItems(itm -> itemOptions.getOptions().stream().noneMatch(opt -> opt.getId() == itm.getId()));
            for (PItem newItem : newInvItems){
                JMenuItem addNew = new JMenuItem("Add option " + (newItem.isNoted() ? "(NOTED) " : "") + newItem.getDefinition().getName() + " [" + newItem.getId() + "]");
                addNew.addActionListener((e) -> {
                    itemOptions.getOptions().add(new GearSetupItem(newItem.getId(), newItem.getQuantity(), newItem.isNoted()));
                    mainPanel.reBuild();
                    plugin.saveSetups();
                });
                add(addNew);
            }
        }
    }
}

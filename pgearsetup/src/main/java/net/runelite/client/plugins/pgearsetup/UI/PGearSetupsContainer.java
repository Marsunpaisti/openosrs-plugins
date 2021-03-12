package net.runelite.client.plugins.pgearsetup.UI;

import net.runelite.client.plugins.pgearsetup.GearSetupData;
import net.runelite.client.plugins.pgearsetup.PGearSetup;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static net.runelite.client.plugins.pgearsetup.UI.PGearSetupPanel.*;

public class PGearSetupsContainer extends JPanel {
    PGearSetup plugin;
    PGearSetupPanel mainPanel;
    ArrayList<GearSetupData> gearSetups;

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(PluginPanel.PANEL_WIDTH, super.getPreferredSize().height);
    }

    public void reBuild(){
        this.removeAll();
        this.buildPanel();
    }

    public PGearSetupsContainer(PGearSetup plugin, PGearSetupPanel mainPanel, ArrayList<GearSetupData> gearSetups){
        this.plugin = plugin;
        this.gearSetups = gearSetups;
        this.mainPanel = mainPanel;
        setBackground(BACKGROUND_COLOR);
        setLayout(new CardLayout());
        buildPanel();
    }

    private void buildPanel(){
        for (GearSetupData data : gearSetups){
            add(new PGearSetupElement(plugin, mainPanel, data), data.getName());
        }
        revalidate();
        repaint();
    }
}

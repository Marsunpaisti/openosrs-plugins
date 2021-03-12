package net.runelite.client.plugins.pgearsetup.UI;

import net.runelite.api.GameState;
import net.runelite.api.kit.KitType;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.pgearsetup.GearSetupData;
import net.runelite.client.plugins.pgearsetup.GearSetupItemOptions;
import net.runelite.client.plugins.pgearsetup.PGearSetup;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import static net.runelite.client.plugins.pgearsetup.UI.PGearSetupPanel.*;
import static net.runelite.client.ui.PluginPanel.PANEL_WIDTH;

public class PGearSetupElement extends JPanel implements ActionListener {
    PGearSetup plugin;
    PGearSetupPanel mainPanel;
    GearSetupData data;

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(PluginPanel.PANEL_WIDTH, super.getPreferredSize().height);
    }

    public void reBuild(){
        this.removeAll();
        this.buildPanel();
    }

    public PGearSetupElement(PGearSetup plugin, PGearSetupPanel mainPanel, GearSetupData data){
        this.plugin = plugin;
        this.data = data;
        this.mainPanel = mainPanel;
        setBackground(BACKGROUND_COLOR);
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, PANEL_BACKGROUND_COLOR),
                BorderFactory.createLineBorder(BACKGROUND_COLOR)
        ));
        buildPanel();
    }

    private void buildPanel(){
        JTextField name = new JTextField(this.data.getName());
        name.setHorizontalAlignment(JTextField.CENTER);
        name.setPreferredSize(new Dimension(200, 20));
        name.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    data.setName(name.getText());
                    plugin.saveSetups();
                    mainPanel.reBuild();
                    mainPanel.setSelectedSetup(data);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        add(name, BorderLayout.PAGE_START);


        JButton equipBtn = new JButton();
        equipBtn.setText("Equip");
        equipBtn.setPreferredSize(new Dimension(200, 30));
        equipBtn.addActionListener((e) -> {
            if (PUtils.getClient() == null || PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;
            plugin.startEquipping(this.data);
        });

        JButton saveBtn = new JButton();
        saveBtn.setText("Save current");
        saveBtn.setPreferredSize(new Dimension(200, 30));
        saveBtn.addActionListener(this);

        JPanel contentWrapper = new JPanel(new GridLayout(2, 1));
        contentWrapper.add(equipBtn);
        contentWrapper.add(saveBtn);
        contentWrapper.setBackground(BACKGROUND_COLOR);
        add(contentWrapper, BorderLayout.CENTER);
        add(new PGearSetupEditView(plugin, mainPanel, data), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (PUtils.getClient() == null || PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;
        HashMap<KitType, GearSetupItemOptions> equipment = new HashMap<KitType, GearSetupItemOptions>();
        PInventory.getEquipmentItems().forEach(i -> {
            equipment.put(i.kitType, new GearSetupItemOptions(i.getId(), i.getQuantity()));
        });

        ArrayList<GearSetupItemOptions> inventory = new ArrayList<GearSetupItemOptions>();
        PInventory.getAllItems().forEach(i -> {
            inventory.add(new GearSetupItemOptions(i.getId(), i.getQuantity()));
        });

        this.data.setEquipment(equipment);
        this.data.setInventory(inventory);
        this.reBuild();
        plugin.saveSetups();
    }
}

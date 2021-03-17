package net.runelite.client.plugins.pgearsetup.UI;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.pgearsetup.GearSetupData;
import net.runelite.client.plugins.pgearsetup.PGearSetup;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class PGearSetupPanel extends PluginPanel implements ItemListener {
    final static Color PANEL_BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
    final static Color BACKGROUND_COLOR = ColorScheme.DARKER_GRAY_COLOR;
    final static Font NORMAL_FONT = FontManager.getRunescapeFont();
    final static Font SMALL_FONT = FontManager.getRunescapeSmallFont();

    private final PGearSetup plugin;
    private PGearSetupsContainer pGearSetupsContainer;
    private String lastShownCard = null;
    private JComboBox setupComboBox;
    @Inject
    private PGearSetupPanel(PGearSetup plugin){
        super(false);
        log.info("Creating panel");
        this.plugin = plugin;
        this.setBackground(PANEL_BACKGROUND_COLOR);
        this.setLayout(new BorderLayout());
        buildPanel();
    }

    public void reBuild(){
        log.info("Re-rendering panel");
        this.removeAll();
        this.buildPanel();

        if (lastShownCard != null && plugin.gearSetups.stream().anyMatch(setup -> setup.getName().equals(lastShownCard))) {
            setSelectedSetup(lastShownCard);
        }
    }

    private void buildPanel(){
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(BACKGROUND_COLOR);
        titleWrapper.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, PANEL_BACKGROUND_COLOR),
                BorderFactory.createLineBorder(BACKGROUND_COLOR)
        ));
        titleWrapper.setPreferredSize(new Dimension(PANEL_WIDTH, 30));
        JLabel title = new JLabel();
        title.setText("PGearSetup");
        title.setFont(NORMAL_FONT);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 8, 0, 0));
        titleWrapper.add(title);
        add(titleWrapper, BorderLayout.PAGE_START);

        JPanel content = new JPanel();

        setupComboBox = new JComboBox(plugin.gearSetups.stream().map(GearSetupData::getName).toArray());
        setupComboBox.setPreferredSize(new Dimension(PANEL_WIDTH, 30));
        setupComboBox.addItemListener(this);
        content.add(setupComboBox);

        pGearSetupsContainer = new PGearSetupsContainer(this.plugin, this, plugin.gearSetups);
        content.add(pGearSetupsContainer);
        add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        CardLayout cl = (CardLayout)(pGearSetupsContainer.getLayout());
        cl.show(pGearSetupsContainer, (String)e.getItem());
        lastShownCard = (String)e.getItem();
    }

    public void setSelectedSetup(GearSetupData gearSetupData){
        CardLayout cl = (CardLayout)(pGearSetupsContainer.getLayout());
        cl.show(pGearSetupsContainer, (String)gearSetupData.getName());
        lastShownCard = (String)gearSetupData.getName();
    }

    public void setSelectedSetup(String key){
        CardLayout cl = (CardLayout)(pGearSetupsContainer.getLayout());
        cl.show(pGearSetupsContainer, key);
    }
}
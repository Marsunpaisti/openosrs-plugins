package net.runelite.client.plugins.pgearsetup.UI;

import net.runelite.api.GameState;
import net.runelite.api.kit.KitType;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.pgearsetup.GearSetupItemOptions;
import net.runelite.client.plugins.pgearsetup.PGearSetup;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import static net.runelite.client.plugins.pgearsetup.UI.PGearSetupPanel.*;

public class PGearSetupItemElement extends JPanel {
    final static HashMap<KitType, BufferedImage> bgImages = new HashMap<KitType, BufferedImage>(){{
        put(KitType.AMULET, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "amulet.png"));
        put(KitType.AMMUNITION, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "ammo.png"));
        put(KitType.TORSO, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "body.png"));
        put(KitType.BOOTS, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "boots.png"));
        put(KitType.CAPE, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "cape.png"));
        put(KitType.HANDS, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "gloves.png"));
        put(KitType.HEAD, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "head.png"));
        put(KitType.LEGS, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "legs.png"));
        put(KitType.RING, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "ring.png"));
        put(KitType.WEAPON, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "weapon.png"));
        put(KitType.SHIELD, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "shield.png"));
    }};
    final static BufferedImage emptyInventoryBg = ImageUtil.getResourceStreamFromClass(PGearSetup.class, "emptyInventory.png");
    final static BufferedImage emptyEquipmentBg = ImageUtil.getResourceStreamFromClass(PGearSetup.class, "empty.png");
    PGearSetup plugin;
    PGearSetupPanel mainPanel;
    KitType slot;
    GearSetupItemOptions item;
    JLabel imageLabel;
    List<AsyncBufferedImage> images;
    Timer imageCycleTimer;
    int currentImageIndex = 0;

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        BufferedImage bg;
        if (slot != null){
            bg = bgImages.getOrDefault(slot, emptyInventoryBg);
        } else {
            bg = emptyInventoryBg;
        }

        if (item.getOptions() != null && item.getOptions().size() != 0){
            // If item in slot draw blank bg
            if (slot != null){
                bg = emptyEquipmentBg;
            } else {
                bg = emptyInventoryBg;
            }
            //Image tmp = bg.getScaledInstance(this.getPreferredSize().width, this.getPreferredSize().height, Image.SCALE_SMOOTH);
            int middleX = Math.round((this.getWidth() - 36) / 2f);
            int middleY = Math.round((this.getHeight() - 36) / 2f);
            g.drawImage(bg, middleX, middleY, null);
        } else {
            // Otherwise draw corresponding slot bg
            //Image tmp = bg.getScaledInstance(this.getPreferredSize().width, this.getPreferredSize().height, Image.SCALE_SMOOTH);
            int middleX = Math.round((this.getWidth() - 36) / 2f);
            int middleY = Math.round((this.getHeight() - 36) / 2f);
            g.drawImage(bg, middleX, middleY, null);
        }
    }

    public void startUpdateTimer(){
        if (item.getOptions() != null && item.getOptions().size() > 1){
            imageCycleTimer = new Timer(1000, (e) -> {
                currentImageIndex++;
                if (currentImageIndex >= item.getOptions().size()) currentImageIndex = 0;
                if (images != null && images.size() > currentImageIndex && images.get(currentImageIndex) != null){
                    images.get(currentImageIndex).addTo(imageLabel);
                }
            });
            imageCycleTimer.start();
        }
    }

    public PGearSetupItemElement(PGearSetup plugin, PGearSetupPanel mainPanel, GearSetupItemOptions item){
        this.plugin = plugin;
        this.slot = null;
        this.item = item;
        this.mainPanel = mainPanel;
        setLayout(new BorderLayout(0, 0));
        setMinimumSize(new Dimension(36,36));
        setMaximumSize(new Dimension(36,36));
        buildPanel();
        startUpdateTimer();
    }

    public PGearSetupItemElement(PGearSetup plugin, PGearSetupPanel mainPanel, GearSetupItemOptions item, KitType slot){
        this.plugin = plugin;
        this.slot = slot;
        this.item = item;
        this.mainPanel = mainPanel;
        setLayout(new BorderLayout(0, 0));
        setMinimumSize(new Dimension(36,36));
        setMaximumSize(new Dimension(36,36));
        buildPanel();
        startUpdateTimer();
    }

    private void buildPanel(){
        imageLabel = new PGearSetupItemImageLabel();
        images = GearSetupItemOptions.getItemImages(plugin, item);
        if (images != null && images.size() != 0 && images.get(0) != null){
            images.get(0).addTo(imageLabel);
        }
        imageLabel.setMinimumSize(new Dimension(36,36));
        imageLabel.setMaximumSize(new Dimension(36,36));
        imageLabel.setPreferredSize(new Dimension(36, 36));
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) return;
                PGearSetupItemPopupMenu menu = new PGearSetupItemPopupMenu(plugin, mainPanel, item, slot);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        add(imageLabel, BorderLayout.CENTER);
    }
}

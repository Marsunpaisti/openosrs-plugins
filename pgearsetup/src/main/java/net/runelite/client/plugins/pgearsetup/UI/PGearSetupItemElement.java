package net.runelite.client.plugins.pgearsetup.UI;

import net.runelite.client.plugins.pgearsetup.GearSetupItemOptions;
import net.runelite.client.plugins.pgearsetup.PGearSetup;
import net.runelite.client.plugins.paistisuite.api.types.PKitType;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

public class PGearSetupItemElement extends JPanel {
    final static HashMap<PKitType, BufferedImage> bgImages = new HashMap<PKitType, BufferedImage>(){{
        put(PKitType.AMULET, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "amulet.png"));
        put(PKitType.AMMUNITION, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "ammo.png"));
        put(PKitType.TORSO, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "body.png"));
        put(PKitType.BOOTS, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "boots.png"));
        put(PKitType.CAPE, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "cape.png"));
        put(PKitType.HANDS, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "gloves.png"));
        put(PKitType.HEAD, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "head.png"));
        put(PKitType.LEGS, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "legs.png"));
        put(PKitType.RING, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "ring.png"));
        put(PKitType.WEAPON, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "weapon.png"));
        put(PKitType.SHIELD, ImageUtil.getResourceStreamFromClass(PGearSetup.class, "shield.png"));
    }};
    final static BufferedImage emptyInventoryBg = ImageUtil.getResourceStreamFromClass(PGearSetup.class, "emptyInventory.png");
    final static BufferedImage emptyEquipmentBg = ImageUtil.getResourceStreamFromClass(PGearSetup.class, "empty.png");
    PGearSetup plugin;
    PGearSetupPanel mainPanel;
    PKitType slot;
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

    public PGearSetupItemElement(PGearSetup plugin, PGearSetupPanel mainPanel, GearSetupItemOptions item, PKitType slot){
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

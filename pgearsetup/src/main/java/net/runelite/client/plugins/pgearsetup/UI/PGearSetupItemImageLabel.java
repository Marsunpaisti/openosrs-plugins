package net.runelite.client.plugins.pgearsetup.UI;

import javax.swing.*;
import java.awt.*;

public class PGearSetupItemImageLabel extends JLabel {
    ImageIcon icon;
    String text;
    int offsetX = 2;
    int offsetY = 0;

    public PGearSetupItemImageLabel(){
        super();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (icon != null){
            g.drawImage(icon.getImage(), (this.getWidth() - icon.getIconWidth()) / 2 + offsetX, (this.getHeight() - icon.getIconHeight()) / 2 + offsetY, null);
        }

        if (text != null){
            int w = g.getFontMetrics().stringWidth(text);
            int h = g.getFontMetrics().getHeight();
            g.drawString(text, (this.getWidth()-w) / 2, (this.getHeight() + h) /2);
        }
    }

    @Override
    public void setIcon(Icon icon){
        this.icon = (ImageIcon) icon;
        this.repaint();
    }

    @Override
    public void setText(String text){
        this.text = text;
    }
}

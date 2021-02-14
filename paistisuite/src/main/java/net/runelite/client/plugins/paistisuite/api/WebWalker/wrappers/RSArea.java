package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import net.runelite.api.coords.WorldPoint;

public class RSArea {
    private RSTile center;
    private WorldPoint wpCenter;
    int radius;

    public RSArea(RSTile center, int radius){
        this.center = center;
        this.wpCenter = center.toWorldPoint();
        this.radius = radius;
    }

    public boolean contains(RSTile tile){
        return tile.toWorldPoint().distanceTo(wpCenter) <= radius;
    }
}

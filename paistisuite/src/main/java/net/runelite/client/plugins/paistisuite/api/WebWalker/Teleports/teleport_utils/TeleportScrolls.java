package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic.Validatable;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import org.apache.commons.lang3.NotImplementedException;

public enum TeleportScrolls implements Validatable {
    /* TODO:
    ADD TELEPORTS

     */
    ;
    private String name;
    private RSTile location;
    TeleportScrolls(String name, RSTile location){
        this.name = name;
        this.location = location;
    }

    public int getX(){
        return location.getX();
    }
    public int getY(){
        return location.getY();
    }
    public int getZ(){
        return location.getPlane();
    }

    public boolean teleportTo(boolean shouldWait){
        throw new NotImplementedException();
    }

    public boolean hasScroll(){
        throw new NotImplementedException();
    }

    public RSTile getLocation(){
        return location;
    }

    @Override
    public boolean canUse(){
        throw new NotImplementedException();
    }

    public boolean scrollbookContains(){
        throw new NotImplementedException();
    }

}
package net.runelite.client.plugins.paistisuite.api.types;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PObjects;

@Slf4j
public class PTileObject {
    public TileObject tileObject;
    public ObjectComposition ObjectComposition;

    public PTileObject(TileObject tileObject, ObjectComposition definition){
        this.tileObject = tileObject;
        this.ObjectComposition = definition;
    }

    public PTileObject(TileObject tileObject){
        this.tileObject = tileObject;
        this.ObjectComposition = PObjects.getRealDefinition(tileObject.getId());
    }

    public TileObject getFirst(){
        return tileObject;
    }

    public ObjectComposition getSecond(){
        return ObjectComposition;
    }

    public ObjectComposition getDef(){
        return ObjectComposition;
    }

    public WorldPoint getWorldLocation(){
        return tileObject.getWorldLocation();
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof PTileObject){
            PTileObject other = ((PTileObject) o);
            if (other.tileObject != null && tileObject != null){
                return other.tileObject.equals(tileObject);
            }
        }

        return false;
    }
}

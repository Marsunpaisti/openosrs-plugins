package net.runelite.client.plugins.paistisuite.api.types;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemDefinition;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.paistisuite.api.PObjects;

@Slf4j
public class PTileObject {
    public TileObject tileObject;
    public ObjectDefinition objectDefinition;

    public PTileObject(TileObject tileObject, ObjectDefinition definition){
        this.tileObject = tileObject;
        this.objectDefinition = definition;
    }

    public PTileObject(TileObject tileObject){
        this.tileObject = tileObject;
        this.objectDefinition = PObjects.getRealDefinition(tileObject.getId());
    }

    public TileObject getFirst(){
        return tileObject;
    }

    public ObjectDefinition getSecond(){
        return objectDefinition;
    }

    public ObjectDefinition getDef(){
        return objectDefinition;
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

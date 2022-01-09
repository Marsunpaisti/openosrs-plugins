package net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

@Value
public class Point3D {
    int x, y, z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(RSTile tile) {
        this.x = tile.getX();
        this.y = tile.getY();
        this.z = tile.getPlane();
    }

    public Point3D(WorldPoint tile) {
        this.x = tile.getX();
        this.y = tile.getY();
        this.z = tile.getPlane();
    }

    public WorldPoint toWorldPoint() {
        return new WorldPoint(this.x, this.y, this.z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public JsonElement toJson() {
        return new Gson().toJsonTree(this);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}

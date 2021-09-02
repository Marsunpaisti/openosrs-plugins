package net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;


public class Point3D {
    private int x, y, z;

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
    public boolean equals(Object object) {
        if (!(object instanceof Point3D)) {
            return false;
        }

        Point3D point = (Point3D) object;
        return x == point.getX() && y == point.getY() && z == point.getZ();
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    /*
    public Positionable toPositionable() {
        return new Positionable() {
            @Override
            public RSTile getAnimablePosition() {
                return new RSTile(x, y, z);
            }

            @Override
            public boolean adjustCameraTo() {
                return false;
            }

            @Override
            public RSTile getPosition() {
                return new RSTile(x, y, z);
            }
        };
    }

     */
}

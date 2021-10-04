package net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PathResult {

    private PathStatus pathStatus;

    private List<Point3D> path;

    private int cost;

    private PathResult() {
        this.pathStatus = PathStatus.SUCCESS;
        this.path = new ArrayList<>();
        this.cost = 0;
    }

    public PathResult(PathStatus pathStatus) {
        this.pathStatus = pathStatus;
    }

    public PathResult(PathStatus pathStatus, List<Point3D> path, int cost) {
        this.pathStatus = pathStatus;
        this.path = path;
        this.cost = cost;
    }

    public PathStatus getPathStatus() {
        return pathStatus;
    }

    public void setPathStatus(PathStatus pathStatus) {
        this.pathStatus = pathStatus;
    }

    public List<Point3D> getPath() {
        return path;
    }

    public void setPath(List<Point3D> path) {
        this.path = path;
    }

    public void addPathPoint(Point3D point) {
        path.add(point);
    }

    public Point3D getFirstPoint() {
        return path.get(0);
    }

    public Point3D getLastPoint() {
        if (path == null || path.size() == 0) {
            return null;
        }
        return path.get(path.size() - 1);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public PathResult addPath(PathResult secondPath) {
        PathResult newPath = new PathResult();

        for (Point3D point : path) {
            newPath.addPathPoint(point);
        }

        for (Point3D point : secondPath.getPath()) {
            newPath.addPathPoint(point);
        }
        newPath.setCost(this.getCost() + secondPath.getCost());

        return newPath;
    }

    public ArrayList<RSTile> toRSTilePath() {
        if (getPath() == null) {
            return new ArrayList<>();
        }
        ArrayList<RSTile> path = new ArrayList<>();
        for (Point3D point3D : getPath()) {
            path.add(new RSTile(point3D));
        }
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathResult that = (PathResult) o;
        return cost == that.cost &&
                pathStatus == that.pathStatus &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathStatus, path, cost);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Point3D point : path) {
            builder.append(point);
        }
        return builder.toString();
    }

    public static PathResult fromJson(JsonElement jsonObject) {
        return new Gson().fromJson(jsonObject, PathResult.class);
    }
}

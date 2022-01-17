package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.RenderOverview;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.PathAnalyzer;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

@Slf4j
@Singleton
public class WebWalkerDebugRenderer {
    private Area mapClipArea;

    @Inject
    private Client client;

    public void render(Graphics2D graphics) {
        if (!WalkerEngine.getInstance().isNavigating()) {
            return;
        }
        PathAnalyzer.DestinationDetails destinationDetails = WalkerEngine.getInstance().getDebugFurthestTile();
        WorldPoint debugFurthestTile = null;
        WorldPoint debugAssumedNext = null;
        if (destinationDetails != null) {
            debugFurthestTile = destinationDetails.getDestination().getRSTile().toWorldPoint();
            debugAssumedNext = destinationDetails.getAssumed().toWorldPoint();
            if (debugFurthestTile != null) {
                drawTile(graphics, debugFurthestTile, new Color(0, 255, 0, 100));
            }
            if (debugAssumedNext != null
                    && destinationDetails.getState() != PathAnalyzer.PathState.FURTHEST_CLICKABLE_TILE
                    && destinationDetails.getState() != PathAnalyzer.PathState.END_OF_PATH) {
                drawTile(graphics, debugAssumedNext, new Color(255, 255, 0, 100));
            }
        }

        List<WorldPoint> path = WalkerEngine.getInstance().getCurrentPathWorldPoints();
        if (path == null) return;
        WorldPoint previous = null;
        for (WorldPoint tile : path) {
            if (previous != null) {
                if (previous.equals(debugFurthestTile) && tile.equals(debugAssumedNext) && destinationDetails.getState() != PathAnalyzer.PathState.FURTHEST_CLICKABLE_TILE) {
                    lineBetweenTiles(graphics, previous, tile, new Color(255, 255, 0, 100), 6);
                } else {
                    lineBetweenTiles(graphics, previous, tile, new Color(0, 255, 255, 100), 3);
                }
            }
            previous = tile;
            //drawTile(graphics, tile, Color.cyan);
        }


        return;
    }

    public Point mapWorldPointToGraphicsPoint(WorldPoint worldPoint) {
        RenderOverview ro = client.getRenderOverview();

        if (!ro.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY())) {
            return null;
        }

        float pixelsPerTile = ro.getWorldMapZoom();

        Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
        if (map != null) {
            Rectangle worldMapRect = map.getBounds();

            int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
            int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

            Point worldMapPosition = ro.getWorldMapPosition();

            //Offset in tiles from anchor sides
            int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
            int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
            int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();

            int xGraphDiff = ((int) (xTileOffset * pixelsPerTile));
            int yGraphDiff = (int) (yTileOffset * pixelsPerTile);

            //Center on tile.
            yGraphDiff -= pixelsPerTile - Math.ceil(pixelsPerTile / 2);
            xGraphDiff += pixelsPerTile - Math.ceil(pixelsPerTile / 2);

            yGraphDiff = worldMapRect.height - yGraphDiff;
            yGraphDiff += (int) worldMapRect.getY();
            xGraphDiff += (int) worldMapRect.getX();

            return new Point(xGraphDiff, yGraphDiff);
        }
        return null;
    }

    private Area getWorldMapClipArea(Rectangle baseRectangle) {
        final Widget overview = client.getWidget(WidgetInfo.WORLD_MAP_OVERVIEW_MAP);
        final Widget surfaceSelector = client.getWidget(WidgetInfo.WORLD_MAP_SURFACE_SELECTOR);

        Area clipArea = new Area(baseRectangle);

        if (overview != null && !overview.isHidden()) {
            clipArea.subtract(new Area(overview.getBounds()));
        }

        if (surfaceSelector != null && !surfaceSelector.isHidden()) {
            clipArea.subtract(new Area(surfaceSelector.getBounds()));
        }

        return clipArea;
    }

    public void renderWorldMap(Graphics2D graphics) {

        if (client.getWidget(WidgetInfo.WORLD_MAP_VIEW) == null) {
            return;
        }
        mapClipArea = getWorldMapClipArea(client.getWidget(WidgetInfo.WORLD_MAP_VIEW).getBounds());

        List<WorldPoint> path = WalkerEngine.getInstance().getCurrentPathWorldPoints();
        if (path == null) return;

        for (WorldPoint tile : path) {
            drawOnMap(graphics, tile, Color.cyan);
        }
        return;
    }

    private void drawOnMap(Graphics2D graphics, WorldPoint point, Color color) {
        net.runelite.api.Point start = mapWorldPointToGraphicsPoint(point);
        Point end = mapWorldPointToGraphicsPoint(point.dx(1).dy(-1));

        if (start == null || end == null) {
            return;
        }

        if (!mapClipArea.contains(start.getX(), start.getY()) || !mapClipArea.contains(end.getX(), end.getY())) {
            return;
        }

        graphics.setColor(color);
        graphics.fillRect(start.getX(), start.getY(), end.getX() - start.getX(), end.getY() - start.getY());
    }

    private void lineBetweenTiles(Graphics2D graphics, WorldPoint tile1, WorldPoint tile2, Color color, int width) {
        if (tile1.getPlane() != client.getPlane()) {
            return;
        }

        if (tile2.getPlane() != client.getPlane()) {
            return;
        }

        LocalPoint lp1 = LocalPoint.fromWorld(client, tile1);
        LocalPoint lp2 = LocalPoint.fromWorld(client, tile2);
        if (lp1 == null || lp2 == null) {
            return;
        }

        Polygon poly1 = Perspective.getCanvasTilePoly(client, lp1);
        Polygon poly2 = Perspective.getCanvasTilePoly(client, lp2);
        if (poly1 == null || poly2 == null) {
            return;
        }

        graphics.setStroke(new BasicStroke(width));
        graphics.setColor(color);
        graphics.drawLine(
                (int) Math.round(poly1.getBounds().getCenterX()), (int) Math.round(poly1.getBounds().getCenterY()),
                (int) Math.round(poly2.getBounds().getCenterX()), (int) Math.round(poly2.getBounds().getCenterY()));
    }

    private void drawTile(Graphics2D graphics, WorldPoint tile, Color color) {
        if (tile.getPlane() != client.getPlane()) {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, tile);
        if (lp == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, color);
    }
}

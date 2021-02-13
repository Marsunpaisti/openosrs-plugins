package net.runelite.client.plugins.paisticore.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.*;
import java.awt.event.MouseEvent;

import static java.awt.event.MouseEvent.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;

@Slf4j
public class PMouse {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
    public static void clickShape(Shape shape){
        executorService.submit(() -> {
            clickPoint(getClickPoint(shape));
        });
    }

    public static void clickPoint(Point p){
        executorService.submit(() -> {
            click(p);
        });
    }

    public static Point getClickPoint(Rectangle rectangle){
        int minX = (int) rectangle.getMinX();
        int maxX = (int) rectangle.getMaxX();
        int minY = (int) rectangle.getMinY();
        int maxY = (int) rectangle.getMaxY();
        int x = (int)PUtils.randomNormal(minX, maxX, (maxX-minX)/6d, (maxX-minX)/2d);
        int y = (int)PUtils.randomNormal(minY, maxY, (maxY-minY)/6d, (maxY-minY)/2d);
        return new Point(x, y);
    }

    public static Point getClickPoint(Shape shape) {
        if(shape == null) throw new NullPointerException("Shape is null in getClickPoint");
        int x = -1, y = -1;

        Rectangle bounds = shape.getBounds();
        int minX = (int) bounds.getMinX();
        int maxX = (int) bounds.getMaxX();
        int minY = (int) bounds.getMinY();
        int maxY = (int) bounds.getMaxY();

        while (!shape.contains(x, y)) {
            x = PUtils.random(minX, maxX);
            y = PUtils.random(minY, maxY);
        }
        return new Point(x, y);
    }

    private static Point adjustForStretched(Point p) {

        if (!PUtils.getClient().isStretchedEnabled()) return p;
        Dimension stretched = PUtils.getClient().getStretchedDimensions();
        Dimension real = PUtils.getClient().getRealDimensions();
        double width = (stretched.width / real.getWidth());
        double height = (stretched.height / real.getHeight());
        return new Point((int) (p.getX() * width), (int) (p.getY() * height));
    }

    private static void mouseEvent(int id, Point point) {
        if (point == null) return;
        MouseEvent e = new MouseEvent(
                PUtils.getClient().getCanvas(), id,
                System.currentTimeMillis(),
                0, point.getX(), point.getY(),
                1, false, 1
        );

        PUtils.getClient().getCanvas().dispatchEvent(e);
    }

    private static Point clampToViewport(Point p){
        int newX = p.getX();
        int newY = p.getY();
        int vw = PUtils.getClient().getViewportWidth();
        int vh = PUtils.getClient().getViewportHeight();
        boolean didClamp = false;
        if (newX < 0) {
            newX = 0;
            didClamp = true;
        } else if (newX > vw) {
            newX = vw;
            didClamp = true;
        }
        if (newY < 0) {
            newY = 0;
            didClamp = true;
        } else if (newY > vh){
            newY = vh;
            didClamp = true;
        }
        Point newPoint = new Point(newX, newY);
        if (didClamp){
            log.warn("Clamped point that was outside viewport! Original: " + p + " New: " + newPoint);
        }
        return newPoint;
    }

    private static void click(Point p) {
        assert !PUtils.getClient().isClientThread();
        p = adjustForStretched(p);
        p = clampToViewport(p);
        mouseEvent(MOUSE_PRESSED, p);
        mouseEvent(MOUSE_RELEASED, p);
        mouseEvent(MOUSE_CLICKED, p);
    }
}

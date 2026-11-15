package rasterize;

import model.Point2D;
import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        int size = polygon.getSize();
        // A polygon is built incrementally, so one and two vertices are valid
        // intermediate states and need different handling from a closed polygon.
        if (size == 0) return;
        if (size == 1) {
            Point2D point = polygon.getPoint(0);
            lineRasterizer.rasterize(point, point);
            return;
        }
        if (size == 2) {
            Point2D first = polygon.getPoint(0);
            Point2D second = polygon.getPoint(1);
            lineRasterizer.rasterize(first, second);
            return;
        }
        for (int i = 0; i < size; i++) {
            Point2D pointA = polygon.getPoint(i);
            // Modulo closes the polygon by connecting the last vertex to the first.
            Point2D pointB = polygon.getPoint((i + 1) % size);
            lineRasterizer.rasterize(pointA, pointB);
        }
    }

    public void rasterize(Polygon polygon, int color) {
        int size = polygon.getSize();

        if (size == 0) {
            return;
        }

        if (size == 1) {
            Point2D point = polygon.getPoint(0);

            lineRasterizer.rasterize(
                    point.x(),
                    point.y(),
                    point.x(),
                    point.y(),
                    color
            );
            return;
        }

        if (size == 2) {
            Point2D first = polygon.getPoint(0);
            Point2D second = polygon.getPoint(1);

            lineRasterizer.rasterize(
                    first.x(),
                    first.y(),
                    second.x(),
                    second.y(),
                    color
            );
            return;
        }

        for (int i = 0; i < size; i++) {
            Point2D pointA = polygon.getPoint(i);
            Point2D pointB = polygon.getPoint((i + 1) % size);

            lineRasterizer.rasterize(
                    pointA.x(),
                    pointA.y(),
                    pointB.x(),
                    pointB.y(),
                    color
            );
        }
    }
}

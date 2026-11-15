package fill;

import model.Edge;
import model.Point2D;
import model.Polygon;
import raster.Raster;

import java.util.ArrayList;
import java.util.List;

public class ScanLineFill implements Filler{

    private int fillColor;
    private Polygon polygon;
    private final Raster raster;

    public ScanLineFill(Raster raster) {
        this.raster = raster;
        setFillColor(0xff00ff);
    }



    @Override
    public void fill() {
        if (polygon == null || polygon.getSize() < 3) {
            return;
        }

        List<Edge> edges = new ArrayList<>();

        int yMin = polygon.getPoint(0).getY();
        int yMax = yMin;

        for (int i = 0; i < polygon.getSize(); i++) {
            Point2D pointA = polygon.getPoint(i);
            Point2D pointB = polygon.getPoint((i + 1) % polygon.getSize());

            yMin = Math.min(yMin, pointA.getY());
            yMax = Math.max(yMax, pointA.getY());

            Edge edge = new Edge(pointA, pointB);

            if (!edge.isHorizontal()) {
                edge.orientate();
                edges.add(edge);
            }
        }

        for (int y = yMin; y < yMax; y++) {
            List<Integer> intersections = new ArrayList<>();

            for (Edge edge : edges) {
                if (edge.isIntersection(y)) {
                    intersections.add(edge.getIntersection(y));
                }
            }

            intersections.sort(Integer::compareTo);

            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                int xStart = intersections.get(i);
                int xEnd = intersections.get(i + 1);

                if (xStart > xEnd) {
                    int temporary = xStart;
                    xStart = xEnd;
                    xEnd = temporary;
                }

                for (int x = xStart; x <= xEnd; x++) {
                    raster.setPixel(x, y, fillColor);
                }
            }
        }
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor & 0xFFFFFF;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}

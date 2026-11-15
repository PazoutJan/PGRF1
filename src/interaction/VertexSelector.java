package interaction;

import model.Line;
import model.Lines;
import model.Point2D;
import model.Polygon;
import model.Scene;

/**
 * Selects the closest editable vertex while preserving the original 10 pixel
 * selection radius and priority.
 */
public class VertexSelector {
    private static final double SELECTION_RADIUS_SQUARED = 10 * 10;

    public Selection findClosest(Scene scene, int x, int y, boolean includeClippingLines) {
        Selection selection = Selection.none();
        double closestDistanceSquared = SELECTION_RADIUS_SQUARED;

        Polygon polygon = scene.getPolygon();
        for (int i = 0; i < polygon.getSize(); i++) {
            double distance = distanceSquared(polygon.getPoint(i), x, y);
            if (distance < closestDistanceSquared) {
                closestDistanceSquared = distance;
                selection = Selection.polygonPoint(i);
            }
        }

        Lines lines = scene.getLines();
        for (int i = 0; i < lines.getSize(); i++) {
            Line line = lines.getLine(i);
            double startDistance = distanceSquared(line.getStart(), x, y);
            if (startDistance < closestDistanceSquared) {
                closestDistanceSquared = startDistance;
                selection = Selection.lineEndpoint(i, Selection.Endpoint.START);
            }

            double endDistance = distanceSquared(line.getEnd(), x, y);
            if (endDistance < closestDistanceSquared) {
                closestDistanceSquared = endDistance;
                selection = Selection.lineEndpoint(i, Selection.Endpoint.END);
            }
        }

        if (includeClippingLines) {
            Lines clippingLines = scene.getClippingLines();
            for (int i = 0; i < clippingLines.getSize(); i++) {
                Line line = clippingLines.getLine(i);
                double startDistance = distanceSquared(line.getStart(), x, y);
                if (startDistance < closestDistanceSquared) {
                    closestDistanceSquared = startDistance;
                    selection = Selection.clippingLineEndpoint(
                            i,
                            Selection.Endpoint.START
                    );
                }

                double endDistance = distanceSquared(line.getEnd(), x, y);
                if (endDistance < closestDistanceSquared) {
                    closestDistanceSquared = endDistance;
                    selection = Selection.clippingLineEndpoint(
                            i,
                            Selection.Endpoint.END
                    );
                }
            }
        }

        return selection;
    }

    private double distanceSquared(Point2D point, int x, int y) {
        double dx = point.getX() - x;
        double dy = point.getY() - y;
        return dx * dx + dy * dy;
    }
}

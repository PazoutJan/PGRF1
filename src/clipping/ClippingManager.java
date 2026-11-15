package clipping;

import model.Line;
import model.Lines;
import model.Point2D;
import model.Polygon;

/**
 * Keeps all clipping algorithms and updates the derived clipping results.
 */
public class ClippingManager {
    private final CohenSutherlandClipper lineClipper;
    private final ConvexPolygonClipper polygonClipper;

    public ClippingManager(double xmin, double xmax, double ymin, double ymax) {
        lineClipper = new CohenSutherlandClipper(xmin, xmax, ymin, ymax);
        polygonClipper = new ConvexPolygonClipper();
    }

    public void recomputeClippedLines(Lines source, Lines result) {
        result.clearLines();
        for (int i = 0; i < source.getSize(); i++) {
            Line clipped = lineClipper.clip(source.getLine(i));
            if (clipped != null) {
                result.addLine(clipped);
            }
        }
    }

    public void recomputeClippedPolygon(
            Polygon subject,
            Polygon clippingPolygon,
            Polygon result
    ) {
        result.clearPolygon();

        // Keep the existing interaction: a clipping polygon is considered
        // complete after the fifth right-clicked vertex.
        if (clippingPolygon.getSize() < 5 || subject.getSize() < 3) {
            return;
        }

        Polygon clipped = polygonClipper.clip(subject, clippingPolygon);
        copy(clipped, result);
    }

    private void copy(Polygon source, Polygon target) {
        for (int i = 0; i < source.getSize(); i++) {
            Point2D point = source.getPoint(i);
            target.addPoint(point);
        }
    }
}

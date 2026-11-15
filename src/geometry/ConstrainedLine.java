package geometry;

import model.Point2D;

/**
 * Computes the endpoint used by the Shift-constrained line drawing mode.
 */
public final class ConstrainedLine {
    private ConstrainedLine() {
    }

    public static Point2D endpoint(Point2D start, int mouseX, int mouseY) {
        int dx = mouseX - start.x();
        int dy = mouseY - start.y();

        double bestDistance = Double.POSITIVE_INFINITY;
        Point2D bestPoint = null;

        Point2D horizontal = new Point2D(start.x() + dx, start.y());
        double horizontalDistance = distanceSquared(mouseX, mouseY, horizontal);
        if (horizontalDistance < bestDistance) {
            bestDistance = horizontalDistance;
            bestPoint = horizontal;
        }

        Point2D vertical = new Point2D(start.x(), start.y() + dy);
        double verticalDistance = distanceSquared(mouseX, mouseY, vertical);
        if (verticalDistance < bestDistance) {
            bestDistance = verticalDistance;
            bestPoint = vertical;
        }

        double positiveOffset = (dx + dy) / 2.0;
        Point2D diagonalPositive = new Point2D(
                start.x() + (int) Math.round(positiveOffset),
                start.y() + (int) Math.round(positiveOffset)
        );
        double positiveDistance = distanceSquared(mouseX, mouseY, diagonalPositive);
        if (positiveDistance < bestDistance) {
            bestDistance = positiveDistance;
            bestPoint = diagonalPositive;
        }

        double negativeOffset = (dx - dy) / 2.0;
        Point2D diagonalNegative = new Point2D(
                start.x() + (int) Math.round(negativeOffset),
                start.y() - (int) Math.round(negativeOffset)
        );
        if (distanceSquared(mouseX, mouseY, diagonalNegative) < bestDistance) {
            bestPoint = diagonalNegative;
        }

        return bestPoint;
    }

    private static double distanceSquared(int x, int y, Point2D point) {
        double dx = x - point.x();
        double dy = y - point.y();
        return dx * dx + dy * dy;
    }
}

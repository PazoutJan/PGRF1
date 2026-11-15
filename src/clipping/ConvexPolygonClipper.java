package clipping;

import model.Point2D;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class ConvexPolygonClipper {
    private static final double EPSILON = 1e-9;

    public Polygon clip(Polygon subject, Polygon clipper) {
        if (subject == null || clipper == null) {
            throw new IllegalArgumentException(
                    "Subject and clipping polygon must not be null"
            );
        }

        if (subject.getSize() < 3 || clipper.getSize() < 3) {
            return new Polygon();
        }

        List<Point2D> output = toList(subject);
        boolean counterClockwise = signedArea(clipper) > 0.0;

        for (int i = 0; i < clipper.getSize(); i++) {
            Point2D edgeStart = clipper.getPoint(i);
            Point2D edgeEnd = clipper.getPoint((i + 1) % clipper.getSize());

            if (output.isEmpty()) {
                break;
            }

            output = clipAgainstEdge(output, edgeStart, edgeEnd, counterClockwise);
        }

        Polygon result = new Polygon();

        for (Point2D point : output) {
            addIfDifferentFromLast(result, point);
        }

        removeClosingDuplicate(result);

        return result;
    }

    private List<Point2D> clipAgainstEdge(
            List<Point2D> input,
            Point2D edgeStart,
            Point2D edgeEnd,
            boolean counterClockwise
    ) {
        List<Point2D> output = new ArrayList<>();

        Point2D previous = input.get(input.size() - 1);
        boolean previousInside = isInside(
                previous,
                edgeStart,
                edgeEnd,
                counterClockwise
        );

        for (Point2D current : input) {
            boolean currentInside = isInside(
                    current,
                    edgeStart,
                    edgeEnd,
                    counterClockwise
            );

            if (previousInside && currentInside) {
                addIfDifferentFromLast(output, current);
            } else if (previousInside && !currentInside) {
                Point2D intersection = intersection(
                        previous,
                        current,
                        edgeStart,
                        edgeEnd
                );

                if (intersection != null) {
                    addIfDifferentFromLast(output, intersection);
                }
            } else if (!previousInside && currentInside) {
                Point2D intersection = intersection(
                        previous,
                        current,
                        edgeStart,
                        edgeEnd
                );

                if (intersection != null) {
                    addIfDifferentFromLast(output, intersection);
                }

                addIfDifferentFromLast(output, current);
            }

            previous = current;
            previousInside = currentInside;
        }

        return output;
    }

    private boolean isInside(
            Point2D point,
            Point2D edgeStart,
            Point2D edgeEnd,
            boolean counterClockwise
    ) {
        double value = cross(edgeStart, edgeEnd, point);

        if (counterClockwise) {
            return value >= -EPSILON;
        }

        return value <= EPSILON;
    }

    private double cross(
            Point2D a,
            Point2D b,
            Point2D point
    ) {
        return (double) (b.x() - a.x())
                * (point.y() - a.y())
                - (double) (b.y() - a.y())
                * (point.x() - a.x());
    }

    private double signedArea(Polygon polygon) {
        double area = 0.0;

        for (int i = 0; i < polygon.getSize(); i++) {
            Point2D current = polygon.getPoint(i);
            Point2D next = polygon.getPoint(
                    (i + 1) % polygon.getSize()
            );

            area += (double) current.x() * next.y()
                    - (double) next.x() * current.y();
        }

        return area / 2.0;
    }

    private Point2D intersection(
            Point2D subjectStart,
            Point2D subjectEnd,
            Point2D clipStart,
            Point2D clipEnd
    ) {
        double subjectX = subjectEnd.x() - subjectStart.x();
        double subjectY = subjectEnd.y() - subjectStart.y();

        double clipX = clipEnd.x() - clipStart.x();
        double clipY = clipEnd.y() - clipStart.y();

        double denominator = subjectX * clipY - subjectY * clipX;

        if (Math.abs(denominator) < EPSILON) {
            return null;
        }

        double startToClipX = clipStart.x()
                - subjectStart.x();
        double startToClipY = clipStart.y()
                - subjectStart.y();

        double t = (startToClipX * clipY
                - startToClipY * clipX) / denominator;

        double x = subjectStart.x() + t * subjectX;
        double y = subjectStart.y() + t * subjectY;

        return new Point2D(
                (int) Math.round(x),
                (int) Math.round(y)
        );
    }

    private List<Point2D> toList(Polygon polygon) {
        List<Point2D> points = new ArrayList<>();

        for (int i = 0; i < polygon.getSize(); i++) {
            points.add(polygon.getPoint(i));
        }

        return points;
    }

    private void addIfDifferentFromLast(
            List<Point2D> points,
            Point2D point
    ) {
        if (points.isEmpty()) {
            points.add(point);
            return;
        }

        Point2D previous = points.get(points.size() - 1);

        if (previous.x() != point.x()
                || previous.y() != point.y()) {
            points.add(point);
        }
    }

    private void addIfDifferentFromLast(
            Polygon polygon,
            Point2D point
    ) {
        if (polygon.getSize() == 0) {
            polygon.addPoint(point);
            return;
        }

        Point2D previous = polygon.getPoint(
                polygon.getSize() - 1
        );

        if (previous.x() != point.x()
                || previous.y() != point.y()) {
            polygon.addPoint(point);
        }
    }

    private void removeClosingDuplicate(Polygon polygon) {
        if (polygon.getSize() < 2) {
            return;
        }

        Point2D first = polygon.getPoint(0);
        Point2D last = polygon.getPoint(
                polygon.getSize() - 1
        );

        if (first.x() == last.x()
                && first.y() == last.y()) {
            polygon.removePoint(polygon.getSize() - 1);
        }
    }
}
package model;

public record Line(Point2D start, Point2D end, int color) {

    public Line(Point2D start, Point2D end) {
        this(start, end, 0xFFFFFF);
    }

    public Line(Point2D start, Point2D end, int color) {
        this.start = start;
        this.end = end;
        this.color = color & 0xFFFFFF;
    }
}

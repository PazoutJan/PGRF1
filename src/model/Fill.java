package model;

public class Fill {
    private final Point2D seed;
    private final int color;

    public Fill(Point2D seed, int color) {
        this.seed = seed;
        this.color = color;
    }

    public Point2D getSeed() {
        return seed;
    }

    public int getColor() {
        return color;
    }
}

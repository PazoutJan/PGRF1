package model;

import fill.SeedFill;

public class Fill {
    private final Point2D seed;
    private final int color;
    private final SeedFill.BoundaryMode boundaryMode;
    private final int boundaryColor;

    public Fill(
            Point2D seed,
            int color,
            SeedFill.BoundaryMode boundaryMode,
            int boundaryColor
    ) {
        this.seed = seed;
        this.color = color;
        this.boundaryMode = boundaryMode;
        this.boundaryColor = boundaryColor;
    }

    public Point2D getSeed() {
        return seed;
    }

    public int getColor() {
        return color;
    }

    public SeedFill.BoundaryMode getBoundaryMode() {
        return boundaryMode;
    }

    public int getBoundaryColor() {
        return boundaryColor;
    }
}

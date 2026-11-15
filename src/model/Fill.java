package model;

import fill.SeedFill;

public record Fill(Point2D seed, int color, SeedFill.BoundaryMode boundaryMode, int boundaryColor) {
}

package fill;

import model.Point2D;
import raster.Raster;
import java.util.ArrayDeque;
import java.util.Deque;


public class SeedFill implements Filler {
    private final Raster raster;

    private Point2D seed;

    private int fillColor = 0xFF0000;

    private BoundaryMode boundaryMode = BoundaryMode.BACKGROUND_COLOR;
    private int boundaryColor = 0xFFFFFF;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill() {
        if (seed == null) {
            return;
        }
        fill(seed.getX(), seed.getY());
    }

    public void fill(int x, int y) {
        if (!isInside(x, y)) {
            return;
        }

        int targetColor = raster.getPixel(x, y);

        if (targetColor == fillColor) {
            return;
        }

        if (boundaryMode == BoundaryMode.BOUNDARY_COLOR
                && targetColor == boundaryColor) {
            return;
        }

        fillIterative(x, y, targetColor);
    }

    public void setSeed(Point2D seed) {
        this.seed = seed;
        ;
    }


    @Override
    public void setFillColor(int color) {
        this.fillColor = color & 0xFFFFFF;
    }


    private void fillRecursive(int x, int y, int targetColor) {
        if (!isInside(x, y) || raster.getPixel(x, y) != targetColor) {
            return;
        }

        raster.setPixel(x, y, fillColor);

        fillRecursive(x + 1, y, targetColor);
        fillRecursive(x - 1, y, targetColor);
        fillRecursive(x, y + 1, targetColor);
        fillRecursive(x, y - 1, targetColor);
    }

    private void fillIterative(int startX, int startY, int targetColor) {
        Deque<Point2D> pixels = new ArrayDeque<>();
        pixels.push(new Point2D(startX, startY));

        while (!pixels.isEmpty()) {
            Point2D pixel = pixels.pop();

            if (!isInside(pixel.getX(), pixel.getY())) {
                continue;
            }

            int currentColor = raster.getPixel(pixel.getX(), pixel.getY());

            if (!canFill(currentColor, targetColor)) {
                continue;
            }

            raster.setPixel(pixel.getX(), pixel.getY(), fillColor);

            pixels.push(new Point2D(pixel.getX() + 1, pixel.getY()));
            pixels.push(new Point2D(pixel.getX() - 1, pixel.getY()));
            pixels.push(new Point2D(pixel.getX(), pixel.getY() + 1));
            pixels.push(new Point2D(pixel.getX(), pixel.getY() - 1));
        }
    }


    private boolean canFill(int currentColor, int targetColor) {
        if (currentColor == fillColor) {
            return false;
        }

        if (boundaryMode == BoundaryMode.BACKGROUND_COLOR) {
            return currentColor == targetColor;
        }

        return currentColor != boundaryColor;
    }


    private boolean isInside(int x, int y) {
        return x >= 0 && x < raster.getWidth()
                && y >= 0 && y < raster.getHeight();
    }

    public enum BoundaryMode {
        BACKGROUND_COLOR,
        BOUNDARY_COLOR
    }

    public void setBoundaryMode(BoundaryMode boundaryMode) {
        this.boundaryMode = boundaryMode;
    }

    public void setBoundaryColor(int boundaryColor) {
        this.boundaryColor = boundaryColor & 0xFFFFFF;
    }
}
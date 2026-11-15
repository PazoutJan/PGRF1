package rasterize;

import model.Line;
import model.Lines;
import model.Point2D;
import raster.Raster;

public abstract class LineRasterizer {
    protected Raster raster;

    public LineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public abstract void rasterize(int x1, int y1, int x2, int y2);

    public void rasterize(Lines lines){
        for(int i = 0; i < lines.getSize(); i++){
            Line line = lines.getLine(i);
            rasterize(line.getStart(), line.getEnd());
        }
    }
    public void rasterize(Point2D pointA, Point2D pointB) {
        rasterize(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
    }

    public void rasterize(Point2D pointA, Point2D pointB, int color) {
        rasterize(
                pointA.getX(),
                pointA.getY(),
                pointB.getX(),
                pointB.getY(),
                color
        );
    }

    public abstract void rasterize(
            int x1,
            int y1,
            int x2,
            int y2,
            int color
    );
}

package rasterize;


import raster.Raster;

//Trivial algorithm for rasterizing lines
public class LineRasterizerTrivial extends LineRasterizer {
    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 == y2) {
            raster.setPixel(x1, y1, 0xFFFFFF);
            return;
        }
        // Handle vertical lines separately because the slope would be undefined.
        if (x1 == x2) {
            int fromY = Math.min(y1, y2);
            int toY = Math.max(y1, y2);
            for (int y = fromY; y <= toY; y++) {
                raster.setPixel(x1, y, 0xFFFFFF);
            }
            return;
        }
        double k = (double) (y2 - y1) / (x2 - x1);
        double q = y1 - k * x1;
        // Iterate over the dominant axis to produce one pixel per coordinate.
        if (Math.abs(y2 - y1) <= Math.abs(x2 - x1)) {
            int fromX = Math.min(x1, x2);
            int toX = Math.max(x1, x2);
            for (int x = fromX; x <= toX; x++) {
                int y = (int) Math.round(k * x + q);
                raster.setPixel(x, y, 0xFFFFFF);
            }
        }
        // For steep lines, iterate over y and calculate the corresponding x.
        else {
            int fromY = Math.min(y1, y2);
            int toY = Math.max(y1, y2);
            for (int y = fromY; y <= toY; y++) {
                int x = (int) Math.round((y - q) / k);
                raster.setPixel(x, y, 0xFFFFFF);
            }
        }
    }




}

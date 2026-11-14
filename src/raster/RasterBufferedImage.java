package raster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster {

    private final BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        // Ignore pixels outside the canvas so partially visible lines remain safe to draw.
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return;
        }
        image.setRGB(x, y, color);
    }

    @Override
    //TODO:Integer / Optional
    public int getPixel(int x, int y) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            throw new IndexOutOfBoundsException("Pixel outside raster: (" + x + ", " + y + ")");
        }
        return image.getRGB(x, y) & 0xFFFFFF;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void clear() {
        image.getGraphics().clearRect(
                0,
                0,
                image.getWidth(),
                image.getHeight()
        );
    }
}

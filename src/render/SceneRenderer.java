package render;

import fill.ScanLineFill;
import fill.SeedFill;
import model.*;
import raster.Raster;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import view.Panel;

/**
 * Draws a scene and its previews onto the panel raster.
 */
public class SceneRenderer {
    public static final int CLIPPING_WINDOW_COLOR = 0x0000FF;
    public static final int CLIPPING_RESULT_COLOR = 0xFF0000;

    private static final int CLIPPING_X_MIN = 150;
    private static final int CLIPPING_X_MAX = 650;
    private static final int CLIPPING_Y_MIN = 100;
    private static final int CLIPPING_Y_MAX = 450;

    private final Panel panel;
    private final Raster raster;
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final SeedFill seedFill;
    private final ScanLineFill scanLineFill;

    public SceneRenderer(Panel panel) {
        this.panel = panel;
        raster = panel.getRaster();
        lineRasterizer = new LineRasterizerTrivial(raster);
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        seedFill = new SeedFill(raster);
        scanLineFill = new ScanLineFill(raster);
    }

    public void render(
            Scene scene,
            int currentColor,
            boolean scanLineEnabled,
            boolean clippingEnabled,
            boolean polygonClippingMode
    ) {
        raster.clear();

        if (polygonClippingMode) {
            polygonRasterizer.rasterize(
                    scene.getPolygonToClip(),
                    scene.getPolygonToClip().getColor()
            );
            polygonRasterizer.rasterize(
                    scene.getClippingPolygon(),
                    CLIPPING_WINDOW_COLOR
            );
            polygonRasterizer.rasterize(
                    scene.getClippedPolygon(),
                    CLIPPING_RESULT_COLOR
            );
            panel.repaint();
            return;
        }

        if (scanLineEnabled) {
            scanLineFill.setPolygon(scene.getPolygon());
            scanLineFill.setFillColor(scene.getPolygon().getColor());
            scanLineFill.fill();
        }

        polygonRasterizer.rasterize(
                scene.getPolygon(),
                scene.getPolygon().getColor()
        );
        rasterizeLines(scene.getLines());

        // Fill after drawing boundaries so seed fill cannot cross them.
        for (Fill fill : scene.getFills()) {
            seedFill.setSeed(fill.seed());
            seedFill.setFillColor(fill.color());
            seedFill.setBoundaryMode(fill.boundaryMode());
            seedFill.setBoundaryColor(fill.boundaryColor());
            seedFill.fill();
        }

        if (clippingEnabled) {
            drawClippingWindow();
            //rasterizeLines(scene.getClippingLines(), currentColor);
            rasterizeLines(scene.getClippedLines(), CLIPPING_RESULT_COLOR);
        }

        panel.repaint();
    }

    public void drawLinePreview(Point2D start, Point2D end, int color) {
        lineRasterizer.rasterize(start, end, color);
    }

    public void drawPolygonPreview(Scene scene, Point2D previewPoint, int color) {
        if (scene.getPolygon().getSize() == 0) {
            return;
        }

        Point2D firstPoint = scene.getPolygon().getPoint(0);
        Point2D lastPoint = scene.getPolygon().getPoint(
                scene.getPolygon().getSize() - 1
        );
        lineRasterizer.rasterize(previewPoint, firstPoint, color);

        if (scene.getPolygon().getSize() > 1) {
            lineRasterizer.rasterize(previewPoint, lastPoint, color);
        }
    }

    private void rasterizeLines(Lines lines) {
        for (int i = 0; i < lines.getSize(); i++) {
            Line line = lines.getLine(i);
            lineRasterizer.rasterize(
                    line.start(),
                    line.end(),
                    line.color()
            );
        }
    }

    private void rasterizeLines(Lines lines, int color) {
        for (int i = 0; i < lines.getSize(); i++) {
            Line line = lines.getLine(i);
            lineRasterizer.rasterize(line.start(), line.end(), color);
        }
    }

    private void drawClippingWindow() {
        lineRasterizer.rasterize(
                CLIPPING_X_MIN,
                CLIPPING_Y_MIN,
                CLIPPING_X_MAX,
                CLIPPING_Y_MIN,
                CLIPPING_WINDOW_COLOR
        );
        lineRasterizer.rasterize(
                CLIPPING_X_MAX,
                CLIPPING_Y_MIN,
                CLIPPING_X_MAX,
                CLIPPING_Y_MAX,
                CLIPPING_WINDOW_COLOR
        );
        lineRasterizer.rasterize(
                CLIPPING_X_MAX,
                CLIPPING_Y_MAX,
                CLIPPING_X_MIN,
                CLIPPING_Y_MAX,
                CLIPPING_WINDOW_COLOR
        );
        lineRasterizer.rasterize(
                CLIPPING_X_MIN,
                CLIPPING_Y_MAX,
                CLIPPING_X_MIN,
                CLIPPING_Y_MIN,
                CLIPPING_WINDOW_COLOR
        );
    }
}

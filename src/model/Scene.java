package model;

import java.util.ArrayList;
import java.util.List;

/**
 * All editable and derived objects that make up the drawing scene.
 */
public class Scene {
    private final Polygon polygon = new Polygon();
    private final Lines lines = new Lines();
    private final List<Fill> fills = new ArrayList<>();

    private final Lines clippingLines = new Lines();
    private final Lines clippedLines = new Lines();
    private final Polygon clippingPolygon = new Polygon();
    private final Polygon polygonToClip = new Polygon();
    private final Polygon clippedPolygon = new Polygon();

    public Polygon getPolygon() {
        return polygon;
    }

    public Lines getLines() {
        return lines;
    }

    public List<Fill> getFills() {
        return fills;
    }

    public Lines getClippingLines() {
        return clippingLines;
    }

    public Lines getClippedLines() {
        return clippedLines;
    }

    public Polygon getClippingPolygon() {
        return clippingPolygon;
    }

    public Polygon getPolygonToClip() {
        return polygonToClip;
    }

    public Polygon getClippedPolygon() {
        return clippedPolygon;
    }

    public void clear() {
        polygon.clearPolygon();
        lines.clearLines();
        fills.clear();
        clippingLines.clearLines();
        clippedLines.clearLines();
        clippingPolygon.clearPolygon();
        polygonToClip.clearPolygon();
        clippedPolygon.clearPolygon();
    }
}

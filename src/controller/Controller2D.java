package controller;

import clipping.ClippingManager;
import fill.SeedFill;
import geometry.ConstrainedLine;
import interaction.Selection;
import interaction.VertexSelector;
import model.*;
import render.SceneRenderer;
import view.ColorPalette;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;
    private final Scene scene;
    private final SceneRenderer renderer;
    private final ClippingManager clippingManager;
    private final VertexSelector vertexSelector;
    private final ColorPalette colorPalette;

    private SeedFill.BoundaryMode seedFillBoundaryMode =
            SeedFill.BoundaryMode.BACKGROUND_COLOR;
    private DrawMode drawMode = DrawMode.LINE;
    private Point2D lineStart;
    private boolean drawingPolygonPoint;
    private Selection selection = Selection.none();
    private boolean scanLineEnabled;
    private boolean clippingEnabled;
    private int currentColor;

    public Controller2D(Panel panel) {
        this.panel = panel;
        scene = new Scene();
        renderer = new SceneRenderer(panel);
        clippingManager = new ClippingManager(150, 650, 100, 450);
        vertexSelector = new VertexSelector();
        colorPalette = new ColorPalette();
        currentColor = colorPalette.getColor(1);

        initListeners();
        drawScene();
    }

    private void initListeners() {
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) {
                    return;
                }

                switch (drawMode) {
                    case LINE:
                        handleLineDragged(e);
                        break;
                    case POLYGON:
                        handlePolygonDragged(e);
                        break;
                    case EDIT:
                        handleEditDragged(e);
                        break;
                    default:
                        break;
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (drawMode == DrawMode.POLYGON_CLIPPING) {
                    handlePolygonClippingPressed(e);
                    return;
                }

                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                switch (drawMode) {
                    case LINE:
                        handleLinePressed(e);
                        break;
                    case POLYGON:
                        handlePolygonPressed();
                        break;
                    case EDIT:
                        selection = vertexSelector.findClosest(
                                scene,
                                e.getX(),
                                e.getY(),
                                clippingEnabled
                        );
                        break;
                    case DELETE:
                        selection = vertexSelector.findClosest(
                                scene,
                                e.getX(),
                                e.getY(),
                                clippingEnabled
                        );
                        handleDeletePressed();
                        drawScene();
                        break;
                    case FILL:
                        scene.getFills().add(new Fill(
                                new Point2D(e.getX(), e.getY()),
                                currentColor,
                                seedFillBoundaryMode,
                                getBoundaryColor()
                        ));
                        drawScene();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                switch (drawMode) {
                    case LINE:
                        handleLineReleased(e);
                        break;
                    case POLYGON:
                        handlePolygonReleased(e);
                        break;
                    case EDIT:
                    case DELETE:
                        selection = Selection.none();
                        break;
                    default:
                        break;
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C:
                        clearScene();
                        break;
                    case KeyEvent.VK_L:
                        scanLineEnabled = false;
                        clippingEnabled = false;
                        setDrawMode(DrawMode.LINE);
                        drawScene();
                        break;
                    case KeyEvent.VK_P:
                        scanLineEnabled = false;
                        clippingEnabled = false;
                        setDrawMode(DrawMode.POLYGON);
                        drawScene();
                        break;
                    case KeyEvent.VK_E:
                        setDrawMode(DrawMode.EDIT);
                        drawScene();
                        break;
                    case KeyEvent.VK_D:
                        setDrawMode(DrawMode.DELETE);
                        drawScene();
                        break;
                    case KeyEvent.VK_F:
                        clippingEnabled = false;
                        seedFillBoundaryMode =
                                SeedFill.BoundaryMode.BACKGROUND_COLOR;
                        setDrawMode(DrawMode.FILL);
                        break;
                    case KeyEvent.VK_B:
                        clippingEnabled = false;
                        seedFillBoundaryMode =
                                SeedFill.BoundaryMode.BOUNDARY_COLOR;
                        setDrawMode(DrawMode.FILL);
                        break;
                    case KeyEvent.VK_S:
                        clippingEnabled = false;
                        scanLineEnabled = true;
                        setDrawMode(DrawMode.POLYGON);
                        drawScene();
                        break;
                    case KeyEvent.VK_R:
                        scanLineEnabled = false;
                        clippingEnabled = true;
                        setDrawMode(DrawMode.LINE);
                        drawScene();
                        break;
                    case KeyEvent.VK_O:
                        scanLineEnabled = false;
                        clippingEnabled = false;
                        scene.getClippingPolygon().clearPolygon();
                        scene.getPolygonToClip().clearPolygon();
                        scene.getClippedPolygon().clearPolygon();
                        setDrawMode(DrawMode.POLYGON_CLIPPING);
                        drawScene();
                        break;
                    case KeyEvent.VK_0:
                    case KeyEvent.VK_NUMPAD0:
                        selectColor(0);
                        break;
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_NUMPAD1:
                        selectColor(1);
                        break;
                    case KeyEvent.VK_2:
                    case KeyEvent.VK_NUMPAD2:
                        selectColor(2);
                        break;
                    case KeyEvent.VK_3:
                    case KeyEvent.VK_NUMPAD3:
                        selectColor(3);
                        break;
                    case KeyEvent.VK_4:
                    case KeyEvent.VK_NUMPAD4:
                        selectColor(4);
                        break;
                    case KeyEvent.VK_5:
                    case KeyEvent.VK_NUMPAD5:
                        selectColor(5);
                        break;
                    case KeyEvent.VK_6:
                    case KeyEvent.VK_NUMPAD6:
                        selectColor(6);
                        break;
                    case KeyEvent.VK_7:
                    case KeyEvent.VK_NUMPAD7:
                        selectColor(7);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void drawScene() {
        renderer.render(
                scene,
                currentColor,
                scanLineEnabled,
                clippingEnabled,
                drawMode == DrawMode.POLYGON_CLIPPING
        );
    }

    private void clearScene() {
        scene.clear();
        scanLineEnabled = false;
        clippingEnabled = false;
        resetInteraction();
        panel.getRaster().clear();
        panel.repaint();
    }

    private void setDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
        resetInteraction();
    }

    private void resetInteraction() {
        lineStart = null;
        drawingPolygonPoint = false;
        selection = Selection.none();
    }

    private void handleLinePressed(MouseEvent e) {
        lineStart = new Point2D(e.getX(), e.getY());
    }

    private void handlePolygonPressed() {
        drawingPolygonPoint = true;
        if (scene.getPolygon().getSize() == 0) {
            scene.getPolygon().setColor(currentColor);
        }
    }

    private void handleLineDragged(MouseEvent e) {
        if (lineStart == null) {
            return;
        }

        drawScene();
        Point2D lineEnd = constrainedEndpoint(e);
        renderer.drawLinePreview(lineStart, lineEnd, currentColor);
        panel.repaint();
    }

    private void handlePolygonDragged(MouseEvent e) {
        if (!drawingPolygonPoint) {
            return;
        }

        drawScene();
        renderer.drawPolygonPreview(
                scene,
                new Point2D(e.getX(), e.getY()),
                currentColor
        );
        panel.repaint();
    }

    private Point2D constrainedEndpoint(MouseEvent e) {
        if (e.isShiftDown()) {
            return ConstrainedLine.endpoint(lineStart, e.getX(), e.getY());
        }
        return new Point2D(e.getX(), e.getY());
    }

    private void handleLineReleased(MouseEvent e) {
        if (lineStart == null) {
            return;
        }

        Line line = new Line(
                lineStart,
                constrainedEndpoint(e),
                currentColor
        );
        if (clippingEnabled) {
            scene.getClippingLines().addLine(line);
            recomputeClippedLines();
        } else {
            scene.getLines().addLine(line);
        }

        lineStart = null;
        drawScene();
    }

    private void handlePolygonReleased(MouseEvent e) {
        if (!drawingPolygonPoint) {
            return;
        }

        scene.getPolygon().addPoint(new Point2D(e.getX(), e.getY()));
        drawingPolygonPoint = false;
        drawScene();
    }

    private void handleEditDragged(MouseEvent e) {
        if (!selection.isSelected()) {
            return;
        }

        Point2D newPoint = new Point2D(e.getX(), e.getY());
        switch (selection.getTarget()) {
            case POLYGON_POINT:
                scene.getPolygon().setPoint(selection.getIndex(), newPoint);
                break;
            case LINE_ENDPOINT:
                replaceEndpoint(scene.getLines(), selection, newPoint);
                break;
            case CLIPPING_LINE_ENDPOINT:
                replaceEndpoint(scene.getClippingLines(), selection, newPoint);
                recomputeClippedLines();
                break;
            default:
                return;
        }
        drawScene();
    }

    private void replaceEndpoint(
            Lines lines,
            Selection selected,
            Point2D newPoint
    ) {
        Line oldLine = lines.getLine(selected.getIndex());
        if (selected.getEndpoint() == Selection.Endpoint.START) {
            lines.setLine(
                    selected.getIndex(),
                    new Line(newPoint, oldLine.end(), oldLine.color())
            );
        } else {
            lines.setLine(
                    selected.getIndex(),
                    new Line(oldLine.start(), newPoint, oldLine.color())
            );
        }
    }

    private void handleDeletePressed() {
        switch (selection.getTarget()) {
            case POLYGON_POINT:
                scene.getPolygon().removePoint(selection.getIndex());
                break;
            case LINE_ENDPOINT:
                scene.getLines().removeLine(selection.getIndex());
                break;
            case CLIPPING_LINE_ENDPOINT:
                scene.getClippingLines().removeLine(selection.getIndex());
                recomputeClippedLines();
                break;
            default:
                break;
        }
    }

    private void recomputeClippedLines() {
        clippingManager.recomputeClippedLines(
                scene.getClippingLines(),
                scene.getClippedLines()
        );
    }

    private void handlePolygonClippingPressed(MouseEvent e) {
        Point2D point = new Point2D(e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (scene.getPolygonToClip().getSize() == 0) {
                scene.getPolygonToClip().setColor(currentColor);
            }
            scene.getPolygonToClip().addPoint(point);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            scene.getClippingPolygon().addPoint(point);
        } else {
            return;
        }

        recomputePolygonClipping();
        drawScene();
    }

    private void recomputePolygonClipping() {
        clippingManager.recomputeClippedPolygon(
                scene.getPolygonToClip(),
                scene.getClippingPolygon(),
                scene.getClippedPolygon()
        );
    }

    private void selectColor(int index) {
        currentColor = colorPalette.getColor(index);
        drawScene();
    }

    private int getBoundaryColor() {
        if (scene.getPolygon().getSize() >= 3) {
            return scene.getPolygon().getColor();
        }

        if (scene.getLines().getSize() > 0) {
            return scene.getLines().getLine(0).color();
        }

        return 0xFFFFFF;
    }

    private enum DrawMode {
        LINE,
        POLYGON,
        EDIT,
        DELETE,
        FILL,
        POLYGON_CLIPPING
    }
}

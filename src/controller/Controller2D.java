package controller;
import fill.SeedFill;
import model.*;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import view.Panel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Controller2D {
    private static final int FILL_COLOR = 0xFF0000;
    private SeedFill.BoundaryMode seedFillBoundaryMode =
            SeedFill.BoundaryMode.BACKGROUND_COLOR;

    private final Panel panel;
    // The active mode determines how the left mouse button is interpreted.
    private DrawMode drawMode = DrawMode.LINE;


    // Rasterizers
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final SeedFill seedFill;
    private final Polygon polygon;
    private final Lines lines;
    private final List<Fill> fills;
    private Point2D lineStart;
    private boolean drawingPolygonPoint;

    private int selectedPolygonPointIndex = -1;
    private int selectedLineIndex = -1;

    //private final Edge fixedEdge = new Edge(200, 300, 400, 100);

    private enum LineEndpoint{
        START,
        END
    }
    private LineEndpoint selectedLineEndpoint = null;

    public Controller2D(Panel panel) {
        this.panel = panel;
        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        seedFill = new SeedFill(panel.getRaster());
        polygon = new Polygon();
        lines = new Lines();
        fills = new ArrayList<>();

        initListeners();
        drawScene();
    }

    private void initListeners() {
        panel.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                // A drag event does not reliably provide getButton(), so inspect the modifier mask.
                if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) return;

                switch(drawMode){
                    case LINE:
                        handleLineDragged(e);
                        break;

                    case POLYGON:
                        handlePolygonDragged(e);
                        break;

                    case EDIT:
                       if(selectedPolygonPointIndex != -1) {
                           polygon.setPoint(selectedPolygonPointIndex, new Point2D(e.getX(), e.getY()));
                           drawScene();
                       }
                       else if(selectedLineIndex != -1) {
                           Line oldLine = lines.getLine(selectedLineIndex);
                           Point2D newPoint = new Point2D(e.getX(), e.getY());
                           if(selectedLineEndpoint == null) return;
                           else if(selectedLineEndpoint == LineEndpoint.START){
                               lines.setLine(selectedLineIndex, new Line(newPoint,oldLine.getEnd()));
                           }
                           else if(selectedLineEndpoint == LineEndpoint.END){
                               lines.setLine(selectedLineIndex,new Line(oldLine.getStart(),newPoint));
                           }
                           drawScene();
                       }
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {

                    Edge line = new Edge(0,0,0,0);



                    //1. Spočítáme tečný vektor
                    Vector vektorT = new Vector(line.getX2()-line.getX1(),line.getY2()-line.getY1());

                    //2. Spočítáme normálový vektor
                    Vector vektorN = new Vector(vektorT.getY(),-vektorT.getX());

                    //3. Spočítáme vektor k bodu P
                    Vector vektorV = new Vector(e.getX()-line.getX1(),e.getY()-line.getY1());

                    //4. Skalární součin
                    double dot = vektorV.getX()*vektorN.getX() + vektorV.getY()*vektorN.getY();

                    //5. porovnat znaménko výsledku skalárního součinu (úhel)
                    //vyhodnotit jestli je vlevo nebo vpravo

                    if(dot > 0){
                        System.out.println("Je vlevo");
                    }
                    else System.out.println("Je vpravo");

                }


                if(e.getButton() != MouseEvent.BUTTON1) return;

                switch (drawMode) {
                    case LINE:
                        handleLinePressed(e);
                        break;
                    case POLYGON:
                        handlePolygonPressed();
                        break;

                    case EDIT:
                        findClosestVertex(e);
                        break;

                    case DELETE:
                        findClosestVertex(e);
                        handleDeletePressed();
                        drawScene();
                        break;

                    case FILL:
                        fills.add(new Fill(
                                new Point2D(e.getX(), e.getY()),
                                FILL_COLOR,
                                seedFillBoundaryMode,
                                0xFFFFFF
                        ));
                        drawScene();
                        break;

                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;

                switch (drawMode) {
                    case LINE:
                        handleLineReleased(e);
                        break;
                    case  POLYGON:
                        handlePolygonReleased(e);
                        break;

                    case EDIT, DELETE:
                        selectedPolygonPointIndex = -1;
                        selectedLineIndex = -1;
                        selectedLineEndpoint = null;
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
                        setDrawMode(DrawMode.LINE);
                        drawScene();
                        break;

                    case KeyEvent.VK_P:
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
                        seedFillBoundaryMode =
                                SeedFill.BoundaryMode.BACKGROUND_COLOR;
                        setDrawMode(DrawMode.FILL);
                        break;

                    case KeyEvent.VK_B:
                        seedFillBoundaryMode =
                                SeedFill.BoundaryMode.BOUNDARY_COLOR;
                        setDrawMode(DrawMode.FILL);
                        break;
                }
            }
        });
    }

    private void drawScene(){
        // Rebuild the raster from the model so temporary preview pixels do not remain.
        panel.getRaster().clear();
        polygonRasterizer.rasterize(polygon);
        lineRasterizer.rasterize(lines);


        // Výplně se aplikují až po hranicích, aby seed fill nemohl projít skrz
        // polygon nebo úsečku do okolní oblasti.
        for (Fill fill : fills) {
            seedFill.setSeed(fill.getSeed());
            seedFill.setFillColor(fill.getColor());
            seedFill.setBoundaryMode(fill.getBoundaryMode());
            seedFill.setBoundaryColor(fill.getBoundaryColor());
            seedFill.fill();
        }

        panel.repaint();
    }

    private void clearScene(){
        polygon.clearPolygon();
        lines.clearLines();
        fills.clear();
        panel.getRaster().clear();
        panel.repaint();
        lineStart = null;
        drawingPolygonPoint = false;
        selectedPolygonPointIndex = -1;
        selectedLineIndex = -1;
        selectedLineEndpoint = null;
    }
    private enum DrawMode{
        LINE,
        POLYGON,
        EDIT,
        DELETE,
        FILL
    }

    private void setDrawMode(DrawMode drawMode){
        this.drawMode = drawMode;
        lineStart = null;
        drawingPolygonPoint = false;
        selectedPolygonPointIndex = -1;
        selectedLineIndex = -1;
        selectedLineEndpoint = null;
    }

    private void handleLinePressed(MouseEvent e){
        lineStart = new Point2D(e.getX(), e.getY());
    }
    private void handlePolygonPressed(){
        drawingPolygonPoint = true;
    }

    private void handleLineDragged(MouseEvent e){
        if (lineStart == null) {
            return;
        }
            drawScene();

        Point2D lineEnd;
        if(e.isShiftDown()){
            lineEnd = getConstrainedLineEnd(
                    lineStart,
                    e.getX(),
                    e.getY()
            );
        }else {
            lineEnd = new Point2D(e.getX(), e.getY());
        }

        lineRasterizer.rasterize(lineStart,lineEnd);
        panel.repaint();
    }

    private void handlePolygonDragged(MouseEvent e){
        if(!drawingPolygonPoint) return;

        drawScene();

        Point2D previewPoint = new Point2D(e.getX(), e.getY());
        drawPolygonPreview(previewPoint);

        panel.repaint();
    }

    private void drawPolygonPreview(Point2D previewPoint){
        if(polygon.getSize() == 0) return;

        Point2D firstPoint = polygon.getPoint(0);
        Point2D lastPoint = polygon.getPoint(polygon.getSize() - 1);

        // The new polygon vertex is only a preview until the mouse is released.
        lineRasterizer.rasterize(previewPoint,firstPoint);

        if(polygon.getSize() > 1){
            lineRasterizer.rasterize(previewPoint,lastPoint);
        }
    }

    private void handleLineReleased(MouseEvent e){
        if(lineStart == null) return;

        Point2D lineEnd;

        if(e.isShiftDown()){
            lineEnd = getConstrainedLineEnd(
                    lineStart,
                    e.getX(),
                    e.getY()
            );
        }else {
            lineEnd = new Point2D(e.getX(), e.getY());
        }

        lines.addLine(new Line(lineStart, lineEnd));
        lineStart = null;
        drawScene();
    }

    private void handlePolygonReleased(MouseEvent e){
        if(!drawingPolygonPoint) return;

        polygon.addPoint(new Point2D(e.getX(), e.getY()));

        drawingPolygonPoint = false;
        drawScene();
    }

    private void findClosestVertex(MouseEvent e){

        selectedPolygonPointIndex = -1;
        selectedLineIndex = -1;
        selectedLineEndpoint = null;
        // The squared distance avoids an unnecessary square-root calculation.
        double closestDistanceSquared = 10*10;

        for(int i = 0; i < polygon.getSize();i++){
            double dx = polygon.getPoint(i).getX() - e.getX();
            double dy = polygon.getPoint(i).getY() - e.getY();
            double distanceSquared = dx*dx + dy*dy;

            if (distanceSquared< closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                selectedPolygonPointIndex = i;
                selectedLineIndex = -1;
                selectedLineEndpoint = null;
            }
        }
        for(int i = 0; i < lines.getSize();i++){
            Line line = lines.getLine(i);
            {
                double dx = line.getStart().getX() - e.getX();
                double dy = line.getStart().getY() - e.getY();
                double distanceSquared = dx * dx + dy * dy;
                if (distanceSquared < closestDistanceSquared) {
                    closestDistanceSquared = distanceSquared;
                    selectedLineIndex = i;
                    selectedLineEndpoint = LineEndpoint.START;
                    selectedPolygonPointIndex = -1;
                }
            }
            {
                double dx = line.getEnd().getX() - e.getX();
                double dy = line.getEnd().getY() - e.getY();
                double distanceSquared = dx * dx + dy * dy;
                if (distanceSquared < closestDistanceSquared) {
                    closestDistanceSquared = distanceSquared;
                    selectedLineIndex = i;
                    selectedLineEndpoint = LineEndpoint.END;
                    selectedPolygonPointIndex = -1;
                }
            }
        }
    }

    private void handleDeletePressed(){
        // Clicking either endpoint removes the whole line; a single endpoint
        // would no longer represent a valid line.
        if(selectedPolygonPointIndex!=-1) {
            polygon.removePoint(selectedPolygonPointIndex);
        }
        else if(selectedLineIndex!=-1) {
            lines.removeLine(selectedLineIndex);
        }

    }
    //TODO: Přesunout někam jinam
    private Point2D getConstrainedLineEnd(Point2D start, int mouseX, int mouseY) {
        int dx = mouseX - start.getX();
        int dy = mouseY - start.getY();

        double bestDistance = Double.POSITIVE_INFINITY;
        Point2D bestPoint = null;

        // 1. Vodorovná úsečka.
        Point2D horizontal = new Point2D(
                start.getX() + dx,
                start.getY()
        );

        double horizontalDistance = distanceSquared(
                mouseX,
                mouseY,
                horizontal
        );

        if (horizontalDistance < bestDistance) {
            bestDistance = horizontalDistance;
            bestPoint = horizontal;
        }

        // 2. Svislá úsečka.
        Point2D vertical = new Point2D(
                start.getX(),
                start.getY() + dy
        );

        double verticalDistance = distanceSquared(
                mouseX,
                mouseY,
                vertical
        );

        if (verticalDistance < bestDistance) {
            bestDistance = verticalDistance;
            bestPoint = vertical;
        }

        // 3. Diagonála se sklonem +1.
        //
        // Projekce bodu myši na přímku:
        // x - startX = y - startY
        double diagonalPositiveOffset = (dx + dy) / 2.0;

        Point2D diagonalPositive = new Point2D(
                start.getX() + (int) Math.round(diagonalPositiveOffset),
                start.getY() + (int) Math.round(diagonalPositiveOffset)
        );

        double diagonalPositiveDistance = distanceSquared(
                mouseX,
                mouseY,
                diagonalPositive
        );

        if (diagonalPositiveDistance < bestDistance) {
            bestDistance = diagonalPositiveDistance;
            bestPoint = diagonalPositive;
        }

        // 4. Diagonála se sklonem -1.
        //
        // Projekce bodu myši na přímku:
        // x - startX = -(y - startY)
        double diagonalNegativeOffset = (dx - dy) / 2.0;

        Point2D diagonalNegative = new Point2D(
                start.getX() + (int) Math.round(diagonalNegativeOffset),
                start.getY() - (int) Math.round(diagonalNegativeOffset)
        );

        double diagonalNegativeDistance = distanceSquared(
                mouseX,
                mouseY,
                diagonalNegative
        );

        if (diagonalNegativeDistance < bestDistance) {
            bestPoint = diagonalNegative;
        }

        return bestPoint;
    }

    private double distanceSquared(int x, int y, Point2D point) {
        double dx = x - point.getX();
        double dy = y - point.getY();

        return dx * dx + dy * dy;
    }


}

package fill;

import model.Edge;
import model.Point2D;
import model.Polygon;
import raster.Raster;

import java.util.ArrayList;
import java.util.List;

public class ScanLineFill implements Filler{

    private int fillColor;
    private Polygon polygon;
    private final Raster raster;

    public ScanLineFill(Raster raster) {
        this.raster = raster;
        setFillColor(0xff00ff);
    }



    public void fill() {
        if(polygon == null || polygon.getSize()<3) return;

        List<Edge> edges = new ArrayList<Edge>();
        for(int i = 0; i < polygon.getSize();i++){
            Point2D pointA = polygon.getPoint(i);
            Point2D pointB = polygon.getPoint((i+1) % polygon.getSize());

            Edge edge = new Edge(pointA, pointB);

            if(!edge.isHorizontal()) {
                edge.orientate();
                edges.add(edge);
            }
        }



        //TODO:Najít min a max Y
        int yMin = polygon.getPoint(0).getY();
        int yMax = yMin;

        for(int i = 1; i < polygon.getSize();i++){
            int y = polygon.getPoint(i).getY();

            if(y < yMin) yMin = y;
            if(y > yMax) yMax = y;
        }


        for(int y = yMin; y < yMax; y++) {
            List<Integer> intersections = new ArrayList<>();

            //TODO:Pro danou scanline se ptáme jestli existuje
            //Průsečík s hranou (se všema)

            for(Edge edge : edges) {

                if(edge.isIntersection(y)) {
                    //TODO:Spočítáme průsečík
                    //TODO:průsečík uložit - do seznamu průsečíků
                    intersections.add(edge.getIntersection(y));
                }
            }
            intersections.sort(Integer::compareTo);

            //Máme průsečíky
            //TODO:Spojíme lichý se sudým

            for(int i = 0; i < intersections.size(); i+=2) {
                int xStart = intersections.get(i);
                int xEnd = intersections.get(i+1);

                for(int x = xStart; x <= xEnd; x++) {
                    raster.setPixel(x, y, fillColor);
                }
            }
            //TODO:Obtáhneme polygon
        }


    }
    public void setFillColor(int fillColor) {
        this.fillColor = fillColor & 0xFFFFFF;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}

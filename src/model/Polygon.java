package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final List<Point2D> points;

    public Polygon(){
        points = new ArrayList<>();
    }

    public void addPoint(Point2D p){
        points.add(p);
    }

    public Point2D getPoint(int index){
        return points.get(index);
    }
    public int getSize(){
        return points.size();
    }
    public void clearPolygon(){
        points.clear();
    }
    public void setPoint(int index, Point2D p){
        points.set(index,p);
    }
    public void removePoint(int index){
        points.remove(index);
    }
}

package clipping;

import model.Line;
import model.Point2D;

public class CohenSutherlandClipper {
    private static final int INSIDE = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 4;
    private static final int TOP = 8;

    private final double xmin;
    private final double xmax;
    private final double ymin;
    private final double ymax;


    public CohenSutherlandClipper(double xmin, double xmax, double ymin, double ymax) {
        if(xmin > xmax || ymin > ymax) {
            throw new IllegalArgumentException("Invalid clipping window");
        }

        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public Line clip(Line line){
        double x1 = line.getStart().getX();
        double y1 = line.getStart().getY();
        double x2 = line.getEnd().getX();
        double y2 = line.getEnd().getY();

        int code1 = computeCode(x1, y1);
        int code2 = computeCode(x2, y2);

        while(true){
            //Oba body uvnitř okna
            if((code1 | code2) == 0) {
                return new Line(
                        new Point2D((int) Math.round(x1),(int) Math.round(y1)),
                        new Point2D((int) Math.round(x2),(int) Math.round(y2))
                );
            }
            //Body leží ve stejné vnější oblasti
            if((code1 & code2)!= 0) {
                return null;
            }

            int outsideCode = code1 != 0 ? code1 :code2;

            double x;
            double y;

            if((outsideCode & TOP) != 0) {
                x = x1 + (x2 - x1) * (ymax - y1) / (y2 - y1);
                y = ymax;
            } else if((outsideCode & BOTTOM) != 0) {
                x = x1 + (x2-x1) * (ymin - y1) / (y2 - y1);
                y = ymin;
            } else if ((outsideCode & RIGHT) !=0) {
                y = y1 + (y2 - y1) * (xmax - x1) / (x2 - x1);
                x = xmax;
            } else{
                y = y1 + (y2 - y1) * (xmin - x1) / (x2 - x1);
                x = xmin;
            }

            if(outsideCode == code1){
                x1 = x;
                y1 = y;
                code1 = computeCode(x1,y1);
            } else{
                x2 = x;
                y2 = y;
                code2 = computeCode(x2,y2);
            }
        }
    }

    private int computeCode(double x, double y){
        int code = INSIDE;

        if(x<xmin){
            code |= LEFT;
        }else if(x>xmax){
            code |= RIGHT;
        }

        if(y<ymin){
            code |= BOTTOM;
        } else if(y>ymax){
            code |= TOP;
        }

        return code;
    }
}

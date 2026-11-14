package model;

import java.util.ArrayList;
import java.util.List;

public class Lines {
    private final List<Line> lines;

    public Lines() {
        this.lines = new ArrayList<>();
    }

    public void addLine(Line line){
        lines.add(line);
    }
    public int getSize(){
        return lines.size();
    }
    public Line getLine(int index){
        return lines.get(index);
    }
    public void clearLines(){
        lines.clear();
    }
    public void setLine(int index, Line line){
        lines.set(index, line);
    }
    public void removeLine(int index){
        lines.remove(index);
    }
}

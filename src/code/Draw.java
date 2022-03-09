package code;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Draw{
    ArrayList<Point> paint;
    private List<List<Point>> drawannotation = new ArrayList<>();
    double x;
    double y;
    double width;
    double lentgh;
    Color c;
    boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Draw(ArrayList<Point> paint, List<List<Point>> drawannotation, double x, double y, double width, double lentgh, Color c) {
        this.paint = paint;
        this.drawannotation = drawannotation;
        this.x = x;
        this.y = y;
        this.width = width;
        this.lentgh = lentgh;
        this.c = c;
        this.selected = false;
    }


    public ArrayList<Point> getPaint() {
        return paint;
    }

    public void setPaint(ArrayList<Point> paint) {
        this.paint = paint;
    }

    public List<List<Point>> getDrawannotation() {
        return drawannotation;
    }

    public void setDrawannotation(List<List<Point>> drawannotation) {
        this.drawannotation = drawannotation;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLentgh() {
        return lentgh;
    }

    public void setLentgh(double lentgh) {
        this.lentgh = lentgh;
    }

    public Color getC() {
        return c;
    }

    public void setC(Color c) {
        this.c = c;
    }
}

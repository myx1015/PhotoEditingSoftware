package code;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * class TextComponent
 */
public class TextComponent extends JPanel {
    private Point start, end;
    private int x, y;
    private int height, width;
    public Color c ;
    private ArrayList<String> text;

    public void setStart(Point start) {
        this.start = start;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Color getC() {
        return c;
    }

    public void setC(Color c) {
        this.c = c;
    }

    public void setText(ArrayList<String> text) {
        this.text = text;
    }

    public TextComponent(Point first, Point last, Color c) {
        this.start = first;
        this.end = last;

        x = Math.abs((int) first.getX());
        y = Math.abs((int) first.getY());

        this.height = (int) (start.getY() - end.getY());
        this.width = (int) (start.getX() - end.getX());

        text = new ArrayList<String>();
        this.c = c;
    }

    public Point getStart() {
        return this.start;
    }

    public Point getEnd() {
        return this.end;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void addText(String s) {
        text.add(s);
    }

    public void backspace() {
        if (!text.isEmpty()) {
            text.remove(text.size()-1);
        }
    }

    public ArrayList<String> getText() {
        return text;
    }
}

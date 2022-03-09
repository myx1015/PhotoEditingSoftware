package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static code.PhotoJFrame.fontColor;
import static java.lang.Math.abs;


public class PhotoComponent extends JComponent {
    static List<Point> stroke = new ArrayList<Point>();
    static List<Point2D> gesture = new ArrayList<>();
    static List<Draw> drawAnnotation = new ArrayList<>();
    static List<Text> texts = new ArrayList<>();
    private static final Font serifFont = new Font("Serif", Font.BOLD, 12);
    public ImageIcon pic;
    //private List<List<Point>> draw = new ArrayList<>();
    public List<TextComponent> write = new ArrayList<TextComponent>();
    public double scaleFactor = 1;
    boolean flipped;
    double pminx;
    double pmaxx;
    double pminy;
    double pmaxy;
    int state;
    int posi,posi2;
    int posdraw, postext;
    boolean ismove = false;
    private final Image image;
    private final Set<TagEnum> tags = new HashSet<>();
    private Point start, curr, newpos, newpos2;
    private String text = "";
    private boolean rightClick = false;
    private boolean isannotation = false;
    private final List<Rectangle> rects = new ArrayList<>();
    private final List<Rectangle> circles = new ArrayList<>();

    public PhotoComponent(ImageIcon p) {
        pic = p;
        image = p.getImage();
        this.setSize(new Dimension(pic.getIconWidth(), pic.getIconHeight()));
        addListeners();
    }

    @Override
    public void paintComponent(Graphics g) {
        //default
        super.paintComponent(g);
        //cast to Graphics2D
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(this.scaleFactor, this.scaleFactor);
        //center
        int left = ((this.getWidth() / 2) - (pic.getIconWidth() / 2));
        int top = ((this.getHeight() / 2) - (pic.getIconHeight() / 2));
        g2.setClip(left, top, pic.getIconWidth(), pic.getIconHeight());

        //get focus for keypress events
        this.setFocusable(true);
        this.requestFocus();

        if (flipped) {//draw white rect and enable draw/write if in flipped state

            g2.drawImage(image, left, top, pic.getIconWidth(), pic.getIconHeight(), null);

            for (Draw dlist : drawAnnotation) { //connect each point to the next point with a line
                // for drawing
                List<List<Point>> thisdraw = dlist.getDrawannotation();
                for (List<Point> line : thisdraw) {
                    for (int i = 0; i < line.size() - 1; i++) {
                        Point p1 = line.get(i);
                        Point p2 = line.get(i + 1);
                        g2.setColor(dlist.getC());
                        g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
//                        Annotation a = new Annotation((int)line.get(0).getX(),(int)line.get(0).getY(),(int)dlist.getWidth(),(int)dlist.getLentgh());
//                        annotations.add(a);
                    }

                }
            }
            //g2.setColor(Color.BLACK);
            if (curr != null) {
                g2.setColor(fontColor); //if currently drawing, paint curr for drag
                // effect
                int width = abs((int) (curr.getX() - start.getX()));
                int height = abs((int) (curr.getY() - start.getY()));
                g2.draw3DRect((int) start.getX(), (int) start.getY(), width, height, true);
                //g2.drawRect();
            }

            if (newpos != null) {
                Draw d = drawAnnotation.get(posi);
//                    List<List<Point>> thisdraw = d.getDrawannotation();
                List<Point> thispoints = d.getPaint();
                for (Point p : thispoints) {
                    for (int i = 0; i < thispoints.size() - 1; i++) {

                        Point p1 = thispoints.get(i);
                        int i1 = abs((int) (p1.getX() - newpos.getX()));
                        int i2 = abs((int) (p1.getY() - newpos.getY()));
                        Point p1n = new Point(i1, i2);
                        thispoints.set(i, p1n);
                    }
                    d.setPaint((ArrayList<Point>) thispoints);
                }
            }
            if (newpos2 != null) {
                TextComponent t = write.get(posi2);
                //System.out.println(posi2);
                t.setStart(newpos2);
                g2.setColor(fontColor);
                g2.drawRect((int) t.getStart().getX(), (int) t.getStart().getY(),
                        abs(t.getWidth()),
                        abs(t.getHeight()));
                if (t == write.get(write.size() - 1) && PhotoJFrame.editEnum.toString().equals("text")) {
                    g2.setColor(Color.BLACK); //if most recent textBox change color to black for
                    // active border
                } else {
                    g2.setColor(fontColor);
                }
                g2.drawRect((int) t.getStart().getX(), ((int) t.getStart().getY()),
                        abs(t.getWidth()),
                        abs(t.getHeight())); //draw border
                FontMetrics fm = g2.getFontMetrics();
                g2.setFont(serifFont);
                g2.setColor(Color.BLACK);
                fm = g2.getFontMetrics();
                String type = "";
                int stringWidth = fm.stringWidth(type);
                int height = g2.getFontMetrics().getHeight();
                int x = (int) (t.getStart().getX() - t.getWidth() / 10) - (stringWidth / 4);
                int y = (int) (t.getStart().getY() - t.getHeight() / 10) + height / 2;
                g2.setFont(new Font("Serif", Font.BOLD, PhotoJFrame.getFontSize()));
                for (String text : t.getText()) {
                    //wrap text to next line if reaches textBox bounds
                    if (fm.stringWidth(type + text) > abs(t.getWidth()) - abs(t.getWidth() / 6)) {
                        type = ""; //clear type string
                        y += g2.getFontMetrics().getHeight();
                    }
                    type += text;
                    g2.setColor(t.getC());
                    g2.drawString(type, x, y);
                }
                    //d.setPaint((ArrayList<Point>) thispoints);
            }

            for (TextComponent rect : write) { //draw all textBoxes in write array

                g2.setColor(fontColor);
                g2.drawRect((int) rect.getStart().getX(), (int) rect.getStart().getY(),
                        abs(rect.getWidth()),
                        abs(rect.getHeight()));
                if (rect == write.get(write.size() - 1) && PhotoJFrame.editEnum.toString().equals("text")) {
                    g2.setColor(Color.BLACK); //if most recent textBox change color to black for
                    // active border
                } else {
                    g2.setColor(fontColor);
                }
                g2.drawRect((int) rect.getStart().getX(), ((int) rect.getStart().getY()),
                        abs(rect.getWidth()),
                        abs(rect.getHeight())); //draw border
                FontMetrics fm = g2.getFontMetrics();
                g2.setFont(serifFont);
                g2.setColor(Color.BLACK);
                fm = g2.getFontMetrics();
                String type = "";
                int stringWidth = fm.stringWidth(type);
                int height = g2.getFontMetrics().getHeight();
                int x = (int) (rect.getStart().getX() - rect.getWidth() / 10) - (stringWidth / 4);
                int y = (int) (rect.getStart().getY() - rect.getHeight() / 10) + height / 2;
                g2.setFont(new Font("Serif", Font.BOLD, PhotoJFrame.getFontSize()));
                for (String text : rect.getText()) {
                    //wrap text to next line if reaches textBox bounds
                    if (fm.stringWidth(type + text) > abs(rect.getWidth()) - abs(rect.getWidth() / 6)) {
                        type = ""; //clear type string
                        y += g2.getFontMetrics().getHeight();
                    }
                    type += text;
                    g2.setColor(rect.getC());
                    g2.drawString(type, x, y);
                }
//                Text t1= new Text(rect,rect.getText());
//                texts.add(t1);
            }
            g2.setFont(serifFont);
            g2.setColor(Color.BLACK);
            if (!rects.isEmpty()) {
                for (Rectangle rect : rects) {
                    g2.drawRect((int) rect.getBounds().getX(), (int) rect.getBounds().getY(),
                            (int) rect.getBounds().getWidth(), (int) rect.getBounds().getHeight());
                }
            }
            if (!circles.isEmpty()) {
                for (Rectangle circle : circles) {
                    g2.drawOval((int) circle.getBounds().getX(), (int) circle.getBounds().getY(),
                            (int) circle.getBounds().getWidth(),
                            (int) circle.getBounds().getHeight());
                }
            }
            if (!gesture.isEmpty()) {
                g2.setColor(Color.RED);
                for (int i = 0; i < gesture.size() - 1; i++) {
                    Point2D p1 = gesture.get(i);
                    Point2D p2 = gesture.get(i + 1);
                    g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                }
            }
        } else { //if not flipped, paint as normal
            pic.paintIcon(this, g2, left, top);
            if (!gesture.isEmpty()) {
                g2.setColor(Color.RED);
                for (int i = 0; i < gesture.size() - 1; i++) {
                    Point2D p1 = gesture.get(i);
                    Point2D p2 = gesture.get(i + 1);
                    g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                }
            }
        }
    }

    public Set<TagEnum> getTags() {
        return tags;
    }

    public void addTag(TagEnum tag) {
        tags.add(tag);
    }

    public void removeTag(TagEnum tag) {
        tags.remove(tag);
    }

    public double getScaleFactor() {
        return this.scaleFactor;
    }

    public void setScaleFactor(double sf) {
        this.scaleFactor = sf;
    }

    public void rescale() { //called when switching between grid and photo view to scale images
        Image scale = image.getScaledInstance((int) (image.getWidth(null) * scaleFactor),
                (int) (image.getHeight(null) * scaleFactor), Image.SCALE_DEFAULT);
        this.pic = new ImageIcon(scale);
        this.setSize(new Dimension(pic.getIconWidth(), pic.getIconHeight()));
        revalidate();
    }


    private void modify(int i) {
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = JColorChooser.createDialog(this, "Please choose color", false, colorChooser,
                e1 -> drawAnnotation.get(i).setC(colorChooser.getColor()), null);
        dialog.setVisible(true);
        repaint();
    }

    private void modifytext(int i) {

        //repaint();
    }

    public void addListeners() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem DeleteItem = new JMenuItem("Delete");
        JMenuItem ModifyItem = new JMenuItem("Modify");
        popup.add(DeleteItem);
        popup.add(ModifyItem);


        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(isEnabled())) {
                    Component source = (Component) e.getSource();
                    source.getParent().dispatchEvent(e);
                    return;
                }
                if (e.getClickCount() == 2) { //double click changes flip state (photo mode) or
                    // mode (grid mode)
                    flipped = !flipped;
                }
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!(isEnabled())) {
                    return;
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    rightClick = true;
                    Point2D Point = new Point(e.getX(), e.getY());
                    //gesture.add(Point);
                } else {
                    rightClick = false;
                    isannotation = false;
                }
                if (!rightClick && flipped && !isannotation) {
                    if (PhotoJFrame.editEnum.toString().equals("pen")) {
                        Point point = new Point(e.getX(), e.getY()); //stroke start point for draw
                        stroke.add(point);
                    } else if (PhotoJFrame.editEnum.toString().equals("text")) {
                        start = new Point(e.getX(), e.getY()); //textBox start point for text
                    } else if (PhotoJFrame.editEnum.toString().equals("modify draw")) {
                        //posi = Integer.parseInt(null);
                        start = new Point(e.getX(), e.getY()); //textBox start point for text
                    }
                    else if (PhotoJFrame.editEnum.toString().equals("modify text")) {

                        //posi2 = Integer.parseInt(null);
                        start = new Point(e.getX(), e.getY()); //textBox start point for text

                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!(isEnabled())) {
                    return;
                }

//                posdraw =1000;
                if (PhotoJFrame.editEnum.toString().equals("modify draw")) {
                    if (rightClick && flipped) { // add new points to
                        for (Draw dlist : drawAnnotation) {
                            if (dlist.getX() - 1 <= e.getX() && e.getX() <= dlist.getX() + dlist.getWidth() + 1) {
                                if (dlist.getY() - 1 <= e.getY() && e.getY() <= dlist.getY() + dlist.getLentgh() + 1) {
                                    posdraw = drawAnnotation.indexOf(dlist);

                                }
                            }
                        }
                        DeleteItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent avt) {
                                drawAnnotation.remove(posdraw);
                                repaint();
                            }
                        });


                        ModifyItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent avt) {
                                JColorChooser colorChooser = new JColorChooser();
                                JDialog dialog = JColorChooser.createDialog(new JFrame(), "Please choose color", false, colorChooser,
                                        e1 -> drawAnnotation.get(posdraw).setC(colorChooser.getColor()), null);
                                dialog.setVisible(true);
                                repaint();
                            }
                        });

                        popup.show(e.getComponent(), e.getX(), e.getY());

//                        Graphics g = getGraphics();
//                        g.setColor(new Color(221, 221, 150, 100));
//                        g.drawRect((int) (drawAnnotation.get(posdraw).getX() - 1), (int) (drawAnnotation.get(posdraw).getY() - 1), (int) drawAnnotation.get(posdraw).getWidth(), (int) drawAnnotation.get(posdraw).getLentgh());
                        repaint();
                        rightClick = false;
//                        posdraw =1000;
                    }
                }

                if (PhotoJFrame.editEnum.toString().equals("modify text")) {
                    if (rightClick && flipped) { // add new points to
                        for (TextComponent t : write) {
                            if (t.getStart().getX() <= e.getX() && e.getX() <= t.getStart().getX() + t.getWidth()) {
                                if (t.getStart().getY() <= e.getY() && e.getY() <= t.getStart().getY() + t.getHeight()) {
                                    postext = write.indexOf(t);
                                    posi2 = write.indexOf(t);
                                    System.out.println(posi2);
                                }
                            }
                        }
                        DeleteItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent avt) {
                                write.remove(postext);
                                repaint();
                            }
                        });

                        ModifyItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent avt) {
                                JColorChooser colorChooser = new JColorChooser();
                                JDialog dialog = JColorChooser.createDialog(new JFrame(), "Please choose color", false, colorChooser,
                                        e1 -> write.get(postext).setC((colorChooser.getColor())), null);
                                dialog.setVisible(true);
                                repaint();
                            }
                        });

                        popup.show(e.getComponent(), e.getX(), e.getY());

                        repaint();
                        rightClick = false;
                    }
                    start = null;
                    newpos2 = null;
                }

                if (!rightClick) {
                    for (Draw dlist : drawAnnotation) {
                        dlist.setSelected(false);
                    }
                }
                if (PhotoJFrame.editEnum == EditEnum.PENCIL) {
                    Color drawcolor = fontColor;

                    //add stroke to drawing on mouse release so new stroke can be started
                    ArrayList<Point> line = new ArrayList<>();
                    for (int i = 0; i < stroke.size(); i++) {
                        line.add(stroke.get(i));
                        pminx = stroke.get(0).getX();
                        pminy = stroke.get(0).getY();
                        pmaxx = stroke.get(0).getX();
                        pmaxy = stroke.get(0).getY();
                    }

                    for (Point p : stroke) {
                        if (p.getX() < pminx) pminx = p.getX();
                        if (p.getY() < pminy) pminy = p.getY();
                        if (p.getX() > pmaxx) pmaxx = p.getX();
                        if (p.getY() > pmaxy) pmaxy = p.getY();
                    }

                    List<List<Point>> temp = new ArrayList<List<Point>>();
                    temp.add(line);

                    Draw d = new Draw(line, temp, pminx, pminy, pmaxx - pminx, pmaxy - pminy, drawcolor);

                    stroke.clear();
                    drawAnnotation.add(d);

                } else if (PhotoJFrame.editEnum == EditEnum.TEXT) {
                    if (start == null || curr == null) {
                        System.out.println("do nothing");
                        return;
                    }
                    TextComponent text = new TextComponent(start, curr, fontColor);
                    write.add(text);
                    start = null;
                    curr = null;
                }
                else if (PhotoJFrame.editEnum == EditEnum.MODIFYDRAW) {
                    start = null;
                    newpos = null;
                }
                repaint();
            }

        });


        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!(isEnabled())) {
                    return;
                }
                if (rightClick) {


                }

                if (!rightClick && flipped) {
                    if (PhotoJFrame.editEnum.toString().equals("pen")) { // add new points to
                        // stroke as mouse is dragged
                        Point point = new Point(e.getX(), e.getY());
                        stroke.add(point);
                        //draw.add(stroke);
                    } else if (PhotoJFrame.editEnum.toString().equals("text")) { // update new
                        // current drag spot of textbox
                        curr = new Point(e.getX(), e.getY());
                    } else if(PhotoJFrame.editEnum.toString().equals("modify text")){
                        newpos2 = new Point(e.getX(), e.getY());
                    }
                    else if (PhotoJFrame.editEnum.toString().equals("modify draw")) {
                        newpos = new Point(e.getX(), e.getY());
                        if (PhotoJFrame.editEnum.toString().equals("modify draw") && flipped && !rightClick) {
                            for (Draw dlist : drawAnnotation) {
                                if (dlist.getX() - 1 <= start.getX() && start.getX() <= dlist.getX() + dlist.getWidth() + 1) {
                                    if (dlist.getY() - 1 <= start.getY() && start.getY() <= dlist.getY() + dlist.getLentgh() + 1) {
                                        posi = drawAnnotation.indexOf(dlist);
                                    }
                                }
                            }
                        }
                    }
                    else if (PhotoJFrame.editEnum.toString().equals("modify text")) {
                        newpos2 = new Point(e.getX(), e.getY());
                        System.out.println(newpos2);
//                       if (PhotoJFrame.editEnum.toString().equals("modify text") && flipped && !rightClick) {
//                            for (TextComponent t : write) {
//                                if (t.getStart().getX() <= start.getX() && start.getX() <= t.getStart().getX() + t.getWidth()) {
//                                    if (t.getStart().getY() <= start.getY() && start.getY() <= t.getStart().getY() + t.getHeight()) {
//                                        posi2 = write.indexOf(t);
//                                        System.out.println(posi2);
////                                    }
//                                    }
//                                }
//                            }
//                        }
                    }
                }
                repaint();
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                repaint();
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!(isEnabled())) {
                    return;
                }
                if (flipped && PhotoJFrame.editEnum == EditEnum.TEXT) {
                    TextComponent currBox = write.get(write.size() - 1);
                    char typedText = e.getKeyChar();
                    if (typedText == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE) {
                        currBox.backspace();
                    } else if (Character.isAlphabetic(typedText) || e.getKeyChar() == KeyEvent.VK_SPACE) {
                        text += typedText;
                        currBox.addText(text);
                        text = "";
                    }
                    repaint();
                }
            }
        });
    }


}

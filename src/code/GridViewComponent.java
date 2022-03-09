package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * class GridViewComponent
 */
public class GridViewComponent extends JComponent {
    public static List<PhotoComponent> thumbnails;
    private static final int row_count = 3;
    private static final int offset = 20;
    static PhotoComponent selected = PhotoJFrame.images.get(PhotoJFrame.currentIndex);
    static TagComponent tagSelected;
    static List<TagComponent> tagComponents = new ArrayList<>();
    static HashMap<PhotoComponent, Point> locations = new HashMap();
    public static Timer timer;

    public GridViewComponent(List<PhotoComponent> images, List<TagComponent> mags, JPanel parent) {
        this.setSize(new Dimension (parent.getWidth() - 500 ,parent.getHeight() - 500));
        tagComponents = mags;
        thumbnails = images;
        if (!(tagComponents.isEmpty())) {
            for (TagComponent mag : tagComponents) {
                this.add(mag);
            }
        }
        if (!(thumbnails.isEmpty())) {
            for (PhotoComponent thumbnail : thumbnails) {
                this.add(thumbnail);
            }
        }
        tagSelected = null;
        selected = PhotoJFrame.images.get(PhotoJFrame.currentIndex); //set the currently selected photo to PhotoLayout's curr
        addListeners();
        timer = new Timer(20, taskPerformer);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.red);
        //highlight currently selected photo
        if (selected!= null) {
            g2.drawRect(selected.getX() - 2, selected.getY() - 2, (int) (selected.getWidth() * 0.5) + 4, (int) (selected.getHeight() * 0.5) + 4);
        }
        if (tagSelected != null) {
            g2.setColor(Color.BLACK);
            g2.drawRect(tagSelected.getX() -2 , tagSelected.getY() -2 , (int) (tagSelected.getWidth()) + 4, (int) (tagSelected.getHeight()) + 4);
        }
    }

    @Override
    public void doLayout() {
        if (tagComponents.isEmpty() && !(thumbnails.isEmpty())) {
            int max_width = 0;
            int max_height = 0;
            int row = 0;
            for (PhotoComponent thumbnail : thumbnails) {
                if (thumbnail.getWidth() > max_width) {
                    max_width = thumbnail.getWidth(); //find largest width amongst all thumbnails
                } if (thumbnail.getHeight() > max_height) {
                    max_height = thumbnail.getHeight(); //find largest height amongst all thumbnails
                }
            }
            for (int i = 0; i < thumbnails.size(); i++) {
                if ((i + 1) % row_count == 0) { //at the end of the row, calculate positions for images in that row
                    for (int k = row_count; k > 0; k--) {
                        thumbnails.get(i - k + 1).setLocation((offset * (row_count - k + 1)) + (max_width * (row_count - k)),
                                (row * max_height) + offset);
                    }
                    row++; //increment row after row_count has been filled
                } else if (i == thumbnails.size() - 1) { //if you reach the last image, this row might not have full row_count
                    int last_row = (thumbnails.size()) % row_count; //number of images left over for last row
                    for (int j = last_row; j > 0; j--) { //calculate positions based on last_row count
                        thumbnails.get(thumbnails.size() - j).setLocation((offset * (last_row - j + 1)) + (max_width * (last_row - j)),
                                (row * max_height) + offset);
                    }
                }
            }
        }
    }

    public static void updateSelected() {
        selected = PhotoJFrame.images.get(PhotoJFrame.currentIndex);
    }

    public TagComponent getSelectedMagnet() {
        return tagSelected;
    }

    //snap back to original grid format if all magnets are deleted
    public static void setOriginal() {
        if (!(thumbnails.isEmpty())) {
            int max_width = 0;
            int max_height = 0;
            int row = 0;
            for (PhotoComponent thumbnail : thumbnails) {
                if (thumbnail.getWidth() > max_width) {
                    max_width = thumbnail.getWidth(); //find largest width amongst all thumbnails
                } if (thumbnail.getHeight() > max_height) {
                    max_height = thumbnail.getHeight(); //find largest height amongst all thumbnails
                }
            }
            for (int i = 0; i < thumbnails.size(); i++) {
                if ((i + 1) % row_count == 0) { //at the end of the row, calculate positions for images in that row
                    for (int k = row_count; k > 0; k--) {
                        thumbnails.get(i - k + 1).setLocation((offset * (row_count - k + 1)) + (max_width * (row_count - k)),
                                (row * max_height) + offset);
                    }
                    row++; //increment row after row_count has been filled
                } else if (i == thumbnails.size() - 1) { //if you reach the last image, this row might not have full row_count
                    int last_row = (thumbnails.size()) % row_count; //number of images left over for last row
                    for (int j = last_row; j > 0; j--) { //calculate positions based on last_row count
                        thumbnails.get(thumbnails.size() - j).setLocation((offset * (last_row - j + 1)) + (max_width * (last_row - j)),
                                (row * max_height) + offset);
                    }
                }
            }
        }
    }

    //calculate new locations based on magnet locations
    public static void updateThumbnails() {
        if (!(tagComponents.isEmpty()) && PhotoJFrame.viewEnum.toString().equals("grid")) {
            for (PhotoComponent thumbnail : thumbnails) {
                int mag_count = 0;
                int x = 0;
                int y = 0;
                for (TagComponent tagComponent : tagComponents) {
                    if (tagComponent.tag == TagEnum.PERSON && thumbnail.getTags().contains(TagEnum.PERSON)) {
                        mag_count++;
                        x += tagComponent.getX();
                        y += tagComponent.getY();
                    }
                    if (tagComponent.tag  == TagEnum.VIEW && thumbnail.getTags().contains(TagEnum.VIEW)) {
                        mag_count++;
                        x += tagComponent.getX();
                        y += tagComponent.getY();
                    }
                    if (tagComponent.tag == TagEnum.PLACES && thumbnail.getTags().contains(TagEnum.PLACES)) {
                        mag_count++;
                        x += tagComponent.getX();
                        y += tagComponent.getY();
                    }
                    if (tagComponent.tag  == TagEnum.SCHOOL && thumbnail.getTags().contains(TagEnum.SCHOOL)) {
                        mag_count++;
                        x += tagComponent.getX();
                        y += tagComponent.getY();
                    }
                    if (tagComponent.tag == TagEnum.ANIMAL && thumbnail.getTags().contains(TagEnum.ANIMAL)) {
                        mag_count++;
                        x += tagComponent.getX();
                        y += tagComponent.getY();
                    }
                }
                if (mag_count != 0) {
                    int pad = 5 * thumbnails.indexOf(thumbnail);
                    locations.put(thumbnail, new Point((x / mag_count) + pad, (y / mag_count) + pad));
                } else if (PhotoJFrame.viewEnum.toString().equals("grid")){
                    locations.put(thumbnail, thumbnail.getLocation());
                }
            }
        }
        //start timer once locations are calculated
        timer.start();
    }

    public static boolean approximatelyEqual(double goal, double current) {
        double diff = Math.abs(goal - current);
        return (diff < 7);
    }

    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            selected = null;
            for (PhotoComponent thumbnail : thumbnails) {
                if (locations.get(thumbnail) == null) {
                    continue;
                } else if (approximatelyEqual(thumbnail.getLocation().getX(), locations.get(thumbnail).getX())
                        && approximatelyEqual(thumbnail.getLocation().getY(), locations.get(thumbnail).getY())) {
                    continue;
                } else {
                    int final_x = (int) locations.get(thumbnail).getX();
                    int final_y = (int) locations.get(thumbnail).getY();
                    int x = thumbnail.getX();
                    int y = thumbnail.getY();
                    //if the x is lss than the goal x, move up 5, otherwise move down 5 (likewise with y)
                    x += final_x < x ? -5 : 5;
                    y += final_y < y ? -5 : 5;
                    thumbnail.setLocation(x, y);
                }
            }
            if (tagComponents.isEmpty()) {
                setOriginal();
            }
        }
    };

    public void addListeners() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Component pc = e.getComponent();
                //select magnet
                if (!(tagComponents.isEmpty())) {
                    for (TagComponent mag : tagComponents) {
                        if (mag.getBounds().contains(e.getPoint()) || pc instanceof TagComponent) {
                            tagSelected = mag;
                            revalidate();
                            repaint();
                        }
                        break;
                    }
                    //select photo
                    for (PhotoComponent thumbnail : thumbnails) {
                        Rectangle box = new Rectangle(thumbnail.getX() - 2, thumbnail.getY() - 2, (int) (thumbnail.getWidth() * 0.5) + 4, (int) (thumbnail.getHeight() * 0.5) + 4);
                        if (box.contains(e.getPoint())) {
                            pc = thumbnail;
                            selected = (PhotoComponent) pc; //update selected
                            PhotoJFrame.currentIndex = PhotoJFrame.images.indexOf(pc); //update curr in PhotoLayout
                            PhotoJFrame.updateCheckboxes();
                            if (e.getClickCount() == 2) { //if double click, switch back to photo mode
                                selected = (PhotoComponent) pc;
                                PhotoJFrame.viewEnum = ViewEnum.PHOTO;
                                PhotoJFrame.switchViewMode();
                            }
                        }
                    }
                }
                if (x < pc.getWidth() * 0.5 + 10 && y < pc.getHeight() * 0.5 + 10 && pc instanceof PhotoComponent) { //if click is within bounds of scaled photo
                    selected = (PhotoComponent) pc; //update selected
                    PhotoJFrame.currentIndex = PhotoJFrame.images.indexOf(pc); //update curr in PhotoLayout
                    PhotoJFrame.updateCheckboxes();
                    if (e.getClickCount() == 2) { //if double click, switch back to photo mode
                        PhotoJFrame.viewEnum = ViewEnum.PHOTO;
                        PhotoJFrame.switchViewMode();
                    }
                }
                revalidate();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Component pc = e.getComponent();
                if (!(tagComponents.isEmpty())) {
                    for (TagComponent mag : tagComponents) {
                        if (mag.getBounds().contains(e.getPoint()) || pc instanceof TagComponent) {
                            tagSelected = mag;
                        }
                    }
                }
                revalidate();
                repaint();
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                if (tagSelected != null) {
                    updateThumbnails();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            //drag magnet
            public void mouseDragged(MouseEvent e) {
                if (tagSelected != null) {
                    tagSelected.updateLocation(e.getX() - tagSelected.getWidth()/2, e.getY() - tagSelected.getHeight()/2);
                    updateThumbnails();
                    revalidate();
                    repaint();
                }
            }
        });
    }

}

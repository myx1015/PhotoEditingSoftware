package code;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * class TagComponent
 */
public class TagComponent extends JComponent {
    public TagEnum tag;
    public JLabel label;

    public TagComponent(TagEnum tag, GridViewComponent lt) {
        this.setLayout(new BorderLayout());
        this.setSize(60,60);
        this.setLocation(lt.getWidth()/3, lt.getHeight()/3);
        label = new JLabel(tag.toString());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBackground(Color.RED);
        label.setOpaque(true);
        this.add(label);
        this.setBackground(Color.RED);
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        this.tag = tag;
        addListeners();
    }

    public TagEnum getTag() {
        return this.tag;
    }


    //called when magnet is dragged
    public void updateLocation(int newX, int newY) {
        this.setLocation(newX, newY);
    }

    //pass events to LightTable
    public void addListeners() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GridViewComponent.tagSelected = (TagComponent) e.getComponent();
                PhotoJFrame.gridview.revalidate();
                PhotoJFrame.gridview.repaint();
            }

            @Override
            public void mousePressed (MouseEvent e){
                Component source=(Component)e.getSource();
                source.getParent().dispatchEvent(e);
                return;
            }

            @Override
            public void mouseReleased (MouseEvent e){

            }

            @Override
            public void mouseEntered (MouseEvent e){

            }

            @Override
            public void mouseExited (MouseEvent e){

            }

            @Override
            public void mouseMoved (MouseEvent e){

            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged (MouseEvent e){
                Component source=(Component)e.getSource();
                source.getParent().dispatchEvent(e);
                return;
            }
        });
    }
}

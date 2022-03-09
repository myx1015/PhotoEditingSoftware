package code;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * the photo JFrame
 */
public class PhotoJFrame extends JFrame {
    static JLabel status = new JLabel("Status");
    JMenuItem importItem;
    static JMenuItem deleteItem;
    JMenuItem exitItem;
    static JRadioButtonMenuItem photoView, gridView;
    static JButton nextButton;
    static JButton prevButton;
    static JButton personButton, viewButton, placesButton, schoolButton, animalButton;
    static List<TagComponent> tagComponents = new ArrayList<>();
    static boolean personFlag, viewFLag, placesFlag, schoolFlag, animalFlag;
    static JCheckBox personCheckBox, viewCheckBox, placesCheckBox, schoolCheckBox, animalCheckBox;
    static Map<TagEnum, JCheckBox> checkBoxMap = new HashMap<>();
    static JRadioButton pen, text,change,change2;
    static JTextField fontSizeTextField;
    static Color fontColor = Color.RED;
    static JPanel contentPanel;
    JScrollPane scrollPanel = new JScrollPane();
    static List<PhotoComponent> images = new ArrayList();
    static EditEnum editEnum;
    static ViewEnum viewEnum = ViewEnum.PHOTO;
    static GridViewComponent gridview;
    static int currentIndex = 0;


    public PhotoJFrame() {
        setTitle("Photo Album");
        setSize(1000, 1000);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel toppanel = new JPanel();
        toppanel.setLayout(new GridLayout(4,1,3,3));
        toppanel.setBackground(new Color(205,186,150));

        JLabel jLabeltop2 = new JLabel("Notice! Import photos can only be imported by folders! " +"\n",JLabel.CENTER);
        jLabeltop2.setFont(new Font("Dialog", 1,20));
        jLabeltop2.setForeground(Color.red);
        toppanel.add(jLabeltop2);

        JLabel jLabeltop3 = new JLabel("(You can only choose the folders to import photos)"+"\n" ,JLabel.CENTER);
        jLabeltop3.setFont(new Font("Dialog", 1,20));
        jLabeltop3.setForeground(Color.red);
        toppanel.add(jLabeltop3);

        JLabel jLabeltop4 = new JLabel("Click \"Edit draw/text\" to enter the edit annotation mode"+"\n" ,JLabel.CENTER);
        jLabeltop4.setFont(new Font("Dialog", 1,20));
        jLabeltop4.setForeground(Color.blue);
        toppanel.add(jLabeltop4);

        JLabel jLabeltop5 = new JLabel("then -> right click on the note to select the corresponding operation"+"\n" ,JLabel.CENTER);
        jLabeltop5.setFont(new Font("Dialog", 1,20));
        jLabeltop5.setForeground(Color.blue);
        toppanel.add(jLabeltop5);



        add(toppanel,BorderLayout.NORTH);


        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(getWidth(), 35));
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        status.setHorizontalAlignment(JLabel.CENTER);
        statusPanel.add(status, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4, 1));
        controlPanel.setPreferredSize(new Dimension(200, getHeight()));
        controlPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        controlPanel.add(createNavigation());
        controlPanel.add(createTag());
        controlPanel.add(createMagnet());
        //controlPanel.add(createEdit());

        JPanel rightPanel = new JPanel();
        rightPanel.add(createEdit());
        add(BorderLayout.EAST,rightPanel);

        //CONTENT PANEL
        contentPanel = new JPanel();
        JViewport view = new JViewport();
        view.setView(contentPanel);
        scrollPanel.setViewport(view); //create ScrollPanel with JPanel as Viewport
        scrollPanel.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPanel.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPanel.setPreferredSize(new Dimension(getWidth() - controlPanel.getWidth(),
                getHeight() - statusPanel.getHeight()));
        contentPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        contentPanel.setLayout(new BorderLayout());
        if (images.isEmpty()) {  //if no images are read from command line, set JLabel to no
            // photos imported text
            JLabel empty = new JLabel("No Photos Imported");
            empty.setHorizontalAlignment(JLabel.CENTER);
            contentPanel.add(empty, BorderLayout.CENTER);
        } else { //if images imported from cmd line, display image at index 0
            contentPanel.add(images.get(0), BorderLayout.CENTER);
        }

        //FILE MENU
        JMenu fileMenu = new JMenu("File");

        //populate file menu with import, delete, and exit items

        //import
        importItem = new JMenuItem("Import");
        importItem.addActionListener(ev -> {
            importPhoto();
            if (!(images.isEmpty()) && viewEnum.toString().equals("photo")) {
                nextButton.setEnabled(true);
                prevButton.setEnabled(true);
                nextButton.setForeground(Color.BLACK);
                prevButton.setForeground(Color.BLACK);
                contentPanel.add(images.get(currentIndex));
            } else if (!(images.isEmpty()) && viewEnum.toString().equals("grid")) {
                for (PhotoComponent pc : images) {
                    pc.setScaleFactor(0.5);
                    pc.rescale();
                    pc.setEnabled(false);
                    pc.flipped = false;
                }
                gridview = new GridViewComponent(images, tagComponents, contentPanel);
                contentPanel.add(gridview, BorderLayout.CENTER);
            }
            status.setText("Imported Photos");
            contentPanel.revalidate();
            contentPanel.repaint();
        });
        fileMenu.add(importItem);

        //delete
        deleteItem = new JMenuItem("Delete");
        if (images.isEmpty()) { //disable delete option if images array is empty
            deleteItem.setEnabled(false);
            deleteItem.setForeground(Color.LIGHT_GRAY);
        }
        deleteItem.addActionListener(ev -> {
            deletePhoto();
        });
        fileMenu.add(deleteItem);

        //exit
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(ev -> {
            status.setText("Exit Button Clicked");
            System.exit(0);
        });
        fileMenu.add(exitItem);

        //add file menu to menu bar
        menuBar.add(fileMenu);

        //VIEW MENU
        JMenu viewMenu = new JMenu("View");
        //create Button Group so grid/photo selection can be mutually exclusive
        ButtonGroup viewGroup = new ButtonGroup();
        //populate view menu
        photoView = new JRadioButtonMenuItem("Photo");
        photoView.setSelected(true);
        photoView.addActionListener(ev -> {
            viewEnum = ViewEnum.PHOTO;
            switchViewMode();
        });
        viewGroup.add(photoView);
        viewMenu.add(photoView);
        gridView = new JRadioButtonMenuItem("Grid");
        gridView.addActionListener(ev -> {
            viewEnum = ViewEnum.GRID;
            switchViewMode();
        });
        viewGroup.add(gridView);
        viewMenu.add(gridView);
        //add view menu to menu bar
        menuBar.add(viewMenu);


        //add all components to main frame and set to visible
        setJMenuBar(menuBar);
        add(statusPanel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.WEST);
        add(scrollPanel, BorderLayout.CENTER);
    }

    public static void deletePhoto() {
        if (viewEnum.toString().equals("photo")) {
            for (Component pc : contentPanel.getComponents()) {
                if (pc instanceof PhotoComponent || pc instanceof JLabel) {
                    contentPanel.remove(pc);
                }
            }
            contentPanel.revalidate();
            contentPanel.repaint();
            images.remove(currentIndex); //remove image from image array
            if (images.isEmpty()) { //if last image deleted, set new PhotoComponent to no photos
                // imported text, and disable next/prev/delete
                deleteItem.setEnabled(false);
                nextButton.setEnabled(false);
                prevButton.setEnabled(false);
                nextButton.setForeground(Color.LIGHT_GRAY);
                prevButton.setForeground(Color.LIGHT_GRAY);
                deleteItem.setForeground(Color.LIGHT_GRAY);
                JLabel empty = new JLabel("No Photos Imported");
                empty.setHorizontalAlignment(JLabel.CENTER);
                contentPanel.add(empty, BorderLayout.CENTER);
                currentIndex = 0;
            } else { //if last image in array is deleted, set former previous image as current
                // JLabel displayed
                if (currentIndex > images.size() - 1 && images.size() > 0) {
                    currentIndex--;
                }
                contentPanel.add(images.get(currentIndex)); //display new curr
                updateCheckboxes();
            }
        } else { //similar construction, except remove and re-add LightTable
            for (Component lt : contentPanel.getComponents()) {
                if (lt instanceof GridViewComponent) {
                    contentPanel.remove(lt);
                }
            }
            images.remove(currentIndex);
            if (images.isEmpty()) {
                deleteItem.setEnabled(false);
                deleteItem.setForeground(Color.LIGHT_GRAY);
                JLabel empty = new JLabel("No Photos Imported");
                empty.setHorizontalAlignment(JLabel.CENTER);
                contentPanel.add(empty, BorderLayout.CENTER);
                currentIndex = 0;
            } else {
                if (currentIndex > images.size() - 1 && images.size() > 0) {
                    currentIndex--;
                }
                gridview = new GridViewComponent(images, tagComponents, contentPanel);
                contentPanel.add(gridview, BorderLayout.CENTER);
                updateCheckboxes();
            }
            contentPanel.repaint();
            contentPanel.revalidate();
        }
        status.setText("Photo Deleted");
    }

    //called by import button action event, opens file chooser to allow user to select a
    // directory of images
    private void importPhoto() {
        if (images.isEmpty()) { //if no images previous imported, remove text to import photos
            for (Component pc : contentPanel.getComponents()) {
                if (pc instanceof PhotoComponent || pc instanceof JLabel) {
                    contentPanel.remove(pc);
                }
            }
        }

        //new file chooser, open dialog box in new frame
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog; wait until dialog is closed
        Component frame = null;
        fc.showOpenDialog(frame);

        File dir = null;

        // Retrieve the JPEG files from the directory, add to static images ArrayList
        dir = fc.getSelectedFile();
        if (dir == null) {
            System.out.println("dir is null");
            return;
        }
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().endsWith(".jpg") || child.getName().endsWith(".JPG")
                        || child.getName().endsWith(".png")) {
                    ImageIcon icon = new ImageIcon(child.getAbsolutePath());
                    PhotoComponent pc = new PhotoComponent(icon);
                    images.add(pc);
                }
            }
        }

        if (!(images.isEmpty())) { //enable prev, next, and delete options now that images are
            // imported
            deleteItem.setEnabled(true);
            deleteItem.setForeground(Color.BLACK);
        }
    }

    //called by constructor to initialize previous/next buttons and action listeners
    private JPanel createNavigation() {
        // Create Navigation Panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setPreferredSize(new Dimension(150, getHeight() / 20));
        navigationPanel.setLayout(new GridLayout(2, 1));
        navigationPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        // Create prev/next buttons
        nextButton = new JButton("Next>>");
        //disable and gray out if no images imported
        if (images.isEmpty()) {
            nextButton.setEnabled(false);
            nextButton.setForeground(Color.LIGHT_GRAY);
        }
        nextButton.addActionListener(ev -> {
            nextPhoto();
            status.setText("Next Photo");
        });
        navigationPanel.add(nextButton);

        prevButton = new JButton(" <<Previous");
        //disable and gray out if no images imported
        if (images.isEmpty()) {
            prevButton.setEnabled(false);
            prevButton.setForeground(Color.LIGHT_GRAY);
        }
        prevButton.addActionListener(ev -> {
            previousPhoto();
            status.setText("Previous Photo");
        });
        navigationPanel.add(prevButton);
        return navigationPanel;
    }

    public static void previousPhoto() {
        if (viewEnum.toString().equals("photo")) {
            //remove currently displayed image
            for (Component pc : contentPanel.getComponents()) {
                if (pc instanceof PhotoComponent || pc instanceof JLabel) {
                    contentPanel.remove(pc);
                }
            }
            contentPanel.revalidate();
            contentPanel.repaint();
            if (currentIndex - 1 < 0) { //decrement current image index
                currentIndex = images.size() - 1;
            } else {
                currentIndex--;
            }
            contentPanel.add(images.get(currentIndex)); //display new current image
        } else {
            if (currentIndex - 1 < 0) { //decrement current image index
                currentIndex = images.size() - 1;
            } else {
                currentIndex--;
            }
            gridview.updateSelected();
            gridview.revalidate();
            gridview.repaint();
        }
        updateCheckboxes();
    }

    public static void nextPhoto() {
        if (viewEnum.toString().equals("photo")) {
            //remove currently displayed image
            for (Component pc : contentPanel.getComponents()) {
                if (pc instanceof PhotoComponent || pc instanceof JLabel) {
                    contentPanel.remove(pc);
                }
            }
            if (currentIndex + 1 > images.size() - 1) { //increment currently displayed photo index
                currentIndex = 0;
            } else {
                currentIndex++;
            }
            contentPanel.add(images.get(currentIndex)); //display new current image
            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            if (currentIndex + 1 > images.size() - 1) { //increment currently displayed photo index
                currentIndex = 0;
            } else {
                currentIndex++;
            }
            gridview.updateSelected();
            gridview.revalidate();
            gridview.repaint();
        }
        updateCheckboxes();
    }

    /**
     * to create panel with tag checkboxes
     */
    private JPanel createTag() {
        //Create Panel for Tags
        JPanel tagPanel = new JPanel();
        tagPanel.setLayout(new GridLayout(6, 1));
        tagPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        //create header text
        //Add header label
        JLabel tagLabel = new JLabel("Tag Photo");
        tagLabel.setHorizontalAlignment(JLabel.CENTER);
        tagPanel.add(tagLabel);

        personCheckBox = new JCheckBox("Person");
        personCheckBox.addActionListener(ev -> {
            if (!(personCheckBox.isSelected())) {
                status.setText("Unchecked Person");
                images.get(currentIndex).removeTag(TagEnum.PERSON);
            } else {
                images.get(currentIndex).addTag(TagEnum.PERSON);
                status.setText("Checked Person");
            }
        });
        tagPanel.add(personCheckBox);

        viewCheckBox = new JCheckBox("View");
        viewCheckBox.addActionListener(ev -> {
            if (!(viewCheckBox.isSelected())) {
                status.setText("Unchecked View");
                images.get(currentIndex).removeTag(TagEnum.VIEW);
            } else {
                images.get(currentIndex).addTag(TagEnum.VIEW);
                status.setText("Checked View");
            }
        });
        tagPanel.add(viewCheckBox);

        placesCheckBox = new JCheckBox("Places");
        placesCheckBox.addActionListener(ev -> {
            if (!(placesCheckBox.isSelected())) {
                status.setText("Unchecked Places");
                images.get(currentIndex).removeTag(TagEnum.PLACES);
            } else {
                images.get(currentIndex).addTag(TagEnum.PLACES);
                status.setText("Checked Places");
            }
        });
        tagPanel.add(placesCheckBox);

        schoolCheckBox = new JCheckBox("School");
        schoolCheckBox.addActionListener(ev -> {
            if (!(schoolCheckBox.isSelected())) {
                status.setText("Unchecked School");
                images.get(currentIndex).removeTag(TagEnum.SCHOOL);
            } else {
                status.setText("Checked School");
                images.get(currentIndex).removeTag(TagEnum.SCHOOL);
            }
        });
        tagPanel.add(schoolCheckBox);

        animalCheckBox = new JCheckBox("Animal");
        animalCheckBox.addActionListener(ev -> {
                    if (!(viewCheckBox.isSelected())) {
                        status.setText("Unchecked Animal");
                        images.get(currentIndex).removeTag(TagEnum.ANIMAL);
                    } else {
                        status.setText("Checked Animal");
                        images.get(currentIndex).removeTag(TagEnum.ANIMAL);
                    }
                }
        );
        tagPanel.add(animalCheckBox);
        checkBoxMap.put(TagEnum.PERSON, personCheckBox);
        checkBoxMap.put(TagEnum.VIEW, viewCheckBox);
        checkBoxMap.put(TagEnum.PLACES, placesCheckBox);
        checkBoxMap.put(TagEnum.SCHOOL, schoolCheckBox);
        checkBoxMap.put(TagEnum.ANIMAL, animalCheckBox);
        return tagPanel;
    }

    //called by constructor to create panel with editing option radio buttons
    private JPanel createEdit() {
        JPanel editPanel = new JPanel();
        editPanel.setPreferredSize(new Dimension(150, getHeight()/3+200));
        editPanel.setLayout(new GridLayout(13, 1));
        editPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel editLabel = new JLabel("Edit Photo");
        editLabel.setHorizontalAlignment(JLabel.CENTER);
        editPanel.add(editLabel);

        ButtonGroup editGroup = new ButtonGroup();
        pen = new JRadioButton("Pen: Create draw");
        pen.addActionListener(ev -> {
            editEnum = EditEnum.PENCIL;
            status.setText("Selected Pen");
            contentPanel.repaint();
        });
        editGroup.add(pen);
        editPanel.add(pen);
        text = new JRadioButton("Text : Creat text");
        text.addActionListener(ev -> {
            editEnum = EditEnum.TEXT;
            status.setText("Selected Text");
            contentPanel.repaint();
        });
        editGroup.add(text);
        editPanel.add(text);

        change = new JRadioButton("Edit draw");
        change.addActionListener(ev -> {
            editEnum = EditEnum.MODIFYDRAW;
            status.setText("Selected Change");
            contentPanel.repaint();
        });
        editGroup.add(change);
        editPanel.add(change);


        change2 = new JRadioButton("Edit text");
        change2.addActionListener(ev -> {
            editEnum = EditEnum.MODIFYTEXT;
            status.setText("Selected Change2");
            contentPanel.repaint();
        });
        editGroup.add(change2);
        editPanel.add(change2);

        JLabel fontLabel = new JLabel("Font Size: ");
        editPanel.add(fontLabel);
        fontSizeTextField = new JTextField("12");
        fontSizeTextField.setPreferredSize(new Dimension(30, 15));
        editPanel.add(fontSizeTextField);

        JLabel colorLabel = new JLabel("Font Color: ");
        editPanel.add(colorLabel);
        ButtonGroup colorGroup = new ButtonGroup();

        JRadioButton red = new JRadioButton("Red");
        JRadioButton blue = new JRadioButton("Blue");
        JRadioButton black = new JRadioButton("Black");
        JRadioButton yellow = new JRadioButton("Yellow");
        JButton more = new JButton("More Color");

        red.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
               fontColor = Color.RED;
            }
        });

        blue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fontColor = Color.blue;
            }
        });

        black.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fontColor = Color.black;
            }
        });

        yellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fontColor = Color.yellow;
            }
        });


        more.addActionListener(e -> {
            JColorChooser colorChooser = new JColorChooser();
            JDialog dialog = JColorChooser.createDialog(new JFrame(),"Please choose color", false, colorChooser,
                    e1 -> fontColor=colorChooser.getColor(), null);
            dialog.setVisible(true);
        });

        colorGroup.add(red);
        colorGroup.add(blue);
        colorGroup.add(black);
        colorGroup.add(yellow);
        editPanel.add(red);
        editPanel.add(blue);
        editPanel.add(black);
        editPanel.add(yellow);
        editPanel.add(more);


//        for (String colorString : colorMap.keySet()) {
//            JRadioButton redButton = new JRadioButton(colorString);
//            if (colorString.equals("Red")) {
//                redButton.setSelected(true);
//            }
//            redButton.addActionListener(ev -> {
//                //System.out.println("select " + colorString);
//                fontColor = colorMap.get(colorString);
//                //System.out.println(fontColor.toString());
//            });
//            colorGroup.add(redButton);
//            editPanel.add(redButton);
//        }

        return editPanel;
    }

    private JPanel createMagnet() {
        JPanel tagPanel = new JPanel();
        tagPanel.setPreferredSize(new Dimension(150, getHeight() / 8));
        tagPanel.setLayout(new GridLayout(6, 1));
        tagPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        // Create buttons for each tag magnet
        personButton = new JButton("Person");
        personButton.addActionListener(ev -> {
            if (viewEnum.toString().equals("grid")) {
                if (!personFlag) {
                    TagComponent mag = new TagComponent(TagEnum.PERSON, gridview);
                    tagComponents.add(mag);
                    gridview = new GridViewComponent(images, tagComponents, contentPanel);
                    gridview.tagSelected = mag;
                    status.setText("Tag Person Added");
                    personFlag = true;
                }
                contentPanel.add(gridview, BorderLayout.CENTER);
                gridview.updateThumbnails();
                contentPanel.revalidate();
                contentPanel.repaint();
                ;
            }
        });
        tagPanel.add(personButton);
        viewButton = new JButton("View");
        viewButton.addActionListener(ev -> {
            if (viewEnum.toString().equals("grid")) {
                if (!viewFLag) {
                    TagComponent mag = new TagComponent(TagEnum.VIEW, gridview);
                    tagComponents.add(mag);
                    gridview = new GridViewComponent(images, tagComponents, contentPanel);
                    gridview.tagSelected = mag;
                    status.setText("Tag View Added");
                    viewFLag = true;
                }
                contentPanel.add(gridview, BorderLayout.CENTER);
                gridview.updateThumbnails();
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        tagPanel.add(viewButton);
        placesButton = new JButton("Places");
        placesButton.addActionListener(ev -> {
            if (viewEnum.toString().equals("grid")) {
                if (!placesFlag) {
                    TagComponent mag = new TagComponent(TagEnum.PLACES, gridview);
                    tagComponents.add(mag);
                    gridview = new GridViewComponent(images, tagComponents, contentPanel);
                    gridview.tagSelected = mag;
                    status.setText("Tag Places Added");
                    placesFlag = true;
                }
                contentPanel.add(gridview, BorderLayout.CENTER);
                gridview.updateThumbnails();
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        tagPanel.add(placesButton);
        schoolButton = new JButton("School");
        schoolButton.addActionListener(ev -> {
            if (viewEnum.toString().equals("grid")) {
                if (!schoolFlag) {
                    TagComponent mag = new TagComponent(TagEnum.SCHOOL, gridview);
                    tagComponents.add(mag);
                    gridview = new GridViewComponent(images, tagComponents, contentPanel);
                    gridview.tagSelected = mag;
                    status.setText("Tag School Added");
                    schoolFlag = true;
                }
                contentPanel.add(gridview, BorderLayout.CENTER);
                gridview.updateThumbnails();
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        tagPanel.add(schoolButton);
        animalButton = new JButton("Animal");
        animalButton.addActionListener(ev -> {
            if (viewEnum.toString().equals("grid")) {
                if (!animalFlag) {
                    TagComponent mag = new TagComponent(TagEnum.ANIMAL, gridview);
                    tagComponents.add(mag);
                    gridview = new GridViewComponent(images, tagComponents, contentPanel);
                    gridview.tagSelected = mag;
                    status.setText("Tag Animal Added");
                    animalFlag = true;
                }
                contentPanel.add(gridview, BorderLayout.CENTER);
                gridview.updateThumbnails();
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        tagPanel.add(animalButton);

        viewButton.setEnabled(false);
        schoolButton.setEnabled(false);
        personButton.setEnabled(false);
        placesButton.setEnabled(false);
        animalButton.setEnabled(false);
        return tagPanel;
    }

    /**
     * update tags
     */
    public static void updateCheckboxes() {
        for (JCheckBox checkBox : checkBoxMap.values()) {
            checkBox.setSelected(false);
        }
        if (images.size() > 0) {
            Set<TagEnum> tags = images.get(currentIndex).getTags();
            for (TagEnum tag : tags) {
                checkBoxMap.get(tag).setSelected(true);
            }
        }
    }

    public static void switchViewMode() {
        if (viewEnum.toString().equals("photo")) { //switch to photo View
            gridview.timer.stop();
            status.setText("Switched to Photo View");
            photoView.setSelected(true);
            nextButton.setEnabled(true);
            prevButton.setEnabled(true);
            nextButton.setForeground(Color.BLACK);
            prevButton.setForeground(Color.BLACK);
            viewButton.setEnabled(false);
            schoolButton.setEnabled(false);
            personButton.setEnabled(false);
            placesButton.setEnabled(false);
            animalButton.setEnabled(false);
            contentPanel.remove(gridview);
            if (!images.isEmpty()) {
                for (Component lt : contentPanel.getComponents()) {
                    if (lt instanceof GridViewComponent) {
                        contentPanel.remove(lt);
                    }
                }
                for (PhotoComponent pc : images) {
                    pc.setScaleFactor(1);
                    pc.rescale();
                    pc.setEnabled(true);
                    contentPanel.add(images.get(currentIndex));
                }
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        } else { //switch to grid view
            status.setText("Switched to Grid View");
            viewButton.setEnabled(true);
            schoolButton.setEnabled(true);
            personButton.setEnabled(true);
            placesButton.setEnabled(true);
            animalButton.setEnabled(true);
            viewEnum = ViewEnum.GRID;
            gridView.setSelected(true);
            if (!(images.isEmpty())) { //remove PhotoComponent
                for (Component pc : contentPanel.getComponents()) {
                    if (pc instanceof PhotoComponent) {
                        contentPanel.remove(pc);
                    }
                }
                for (PhotoComponent pc : images) {
                    pc.setScaleFactor(0.5); //scale down images to thumbnail size
                    pc.rescale(); //update size of component
                    pc.setEnabled(false);
                    pc.flipped = false;
                }
                gridview = new GridViewComponent(images, tagComponents, contentPanel); //add
                // light table with
                // scaled images
                gridview.setOriginal();
                gridview.updateThumbnails();
                contentPanel.add(gridview, BorderLayout.CENTER);
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        }
    }

    public static int getFontSize(){
        String text = fontSizeTextField.getText();
        int res = 12;
        try {
            res = Integer.parseInt(text);
            if(res<6||res>100){
                res =12;
            }
        }catch (NumberFormatException ex){
            res = 12;
        }
        return res;
    }
}

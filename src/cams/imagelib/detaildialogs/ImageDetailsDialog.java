package cams.imagelib.detaildialogs;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.swing.border.*;
import java.text.*;
import java.awt.print.*;

import cams.imagelib.*;
import cams.database.*;
// import cams.console.*;


public class ImageDetailsDialog extends JDialog {
  public boolean userCancel = true;
  private ImageLibRecord mCurrentRecord = null;
  private JFrame mParent = null;
  private boolean mSingle = false;
  private CamsDB mCamsDB = null;
  private HashMap mCategoryFullPathHash = null;
  private int mCatalogId = -1;
  private String mCatalogName = null;
  private int mCurrentRow = -1;
  private JTable mTable = null;
  public boolean dataSaved = false;

  ImageIcon imageSave;
  ImageIcon imageCancel;
  ImageIcon imageFirst;
  ImageIcon imagePrev;
  ImageIcon imageNext;
  ImageIcon imageLast;
  ImageIcon imagePrint;

  JPanel panelMain = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jpnlContent = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JTextField jtxtCatalogUser = new JTextField();
  JLabel jlblPreview = new JLabel();
  JLabel jLabel18 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JTextField jtxtFullPathWin = new JTextField();
  JLabel jLabel8 = new JLabel();
  JTextField jtxtFullPathMac = new JTextField();
  JLabel jLabel9 = new JLabel();
  JTextField jtxtFileName = new JTextField();
  TitledBorder titledBorder1;
  JToolBar jToolBar = new JToolBar();
  JButton jbtnFirst = new JButton();
  JButton jbtnPrev = new JButton();
  JButton jbtnNext = new JButton();
  JButton jbtnLast = new JButton();
  JTextField jtxtLastModified = new JTextField();
  JLabel jLabel10 = new JLabel();
//  JButton jbtnPrint = new JButton();
  JLabel jLabel11 = new JLabel();
  JTextField jtxtFileDate = new JTextField();
  JLabel jLabel12 = new JLabel();
  JTextField jtxtFileSize = new JTextField();
  JTextField jtxtCategory = new JTextField();
  JLabel jLabel13 = new JLabel();
  JTextField jtxtFileType = new JTextField();
  XYLayout theXYLayout = new XYLayout();
  JButton jButtonOpenAsset = new JButton();

  // Menu Bar
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuClose = new JMenuItem();
//  JMenuItem jMenuPrint = new JMenuItem();
  JMenu jMenuAsset = new JMenu();
  JMenuItem jMenuFirst = new JMenuItem();
  JMenuItem jMenuPrevious = new JMenuItem();
  JMenuItem jMenuNext = new JMenuItem();
  JMenuItem jMenuLast = new JMenuItem();
  JMenuItem jMenuPreview = new JMenuItem();
  JButton jButtonOpenFolder = new JButton();

  public ImageDetailsDialog(Frame frame, CamsDB theCamsDB, JTable theTable,
                            HashMap categoryPathHash, int currentRow,
                            int cascadeIndex, boolean isModal) {
    super(new Frame(), "Asset Details (Image)", isModal);
    try {
      mParent = (JFrame) frame;
      mCamsDB = theCamsDB;
      mTable = theTable;
      mCategoryFullPathHash = categoryPathHash;
      mCurrentRow = currentRow;
      jbInit();

      pack();
      setSize(getSize().width + 10, getSize().height + 10);

      if (isModal) {
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        setLocation( (screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);
      } else {
        // Position based on MultiSelected Asset Index
        setLocation(10 + cascadeIndex * 10, 10 + cascadeIndex * 10);
      }

    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    jpnlContent.setLayout(theXYLayout);
    panelMain.setLayout(borderLayout1);

    jLabel11.setText("File Modified :");
    jLabel11.setDebugGraphicsOptions(0);
    jLabel11.setFont(new java.awt.Font("Dialog", 1, 11));
    jtxtFileDate.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtFileDate.setEditable(false);
    jtxtFileDate.setText("");
    jLabel12.setText("File Size :");
    jLabel12.setDebugGraphicsOptions(0);
    jLabel12.setFont(new java.awt.Font("Dialog", 1, 11));
    jtxtFileSize.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtFileSize.setEditable(false);
    jtxtFileSize.setText("");
    jtxtCategory.setText("");
    jtxtCategory.setEditable(false);
    jtxtCategory.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel13.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel13.setDebugGraphicsOptions(0);
    jLabel13.setText("File Type :");
    jtxtFileType.setText("");
    jtxtFileType.setEditable(false);
    jtxtFileType.setBorder(BorderFactory.createLoweredBevelBorder());
    jButtonOpenAsset.setFont(new java.awt.Font("Dialog", 0, 10));
    jButtonOpenAsset.setText("Open");
    jButtonOpenAsset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonOpenAsset_actionPerformed(e);
      }
    });
    jMenuPreview.setText("Preview Asset");
    jMenuPreview.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuPreview_actionPerformed(e);
      }
    });
    jButtonOpenFolder.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonOpenFolder_actionPerformed(e);
      }
    });
    jButtonOpenFolder.setFont(new java.awt.Font("Dialog", 0, 10));
    jButtonOpenFolder.setAlignmentY((float) 0.5);
    jButtonOpenFolder.setText("Browse");
    jpnlContent.add(jLabel7, new XYConstraints(6, 62, 108, -1));
    jpnlContent.add(jLabel8, new XYConstraints(6, 36, 108, -1));
    jpnlContent.add(jLabel9, new XYConstraints(6, 10, 108, -1));
    jpnlContent.add(jtxtFileName, new XYConstraints(107, 7, 224, -1));
    jpnlContent.add(jtxtFullPathMac, new XYConstraints(107, 33, 224, -1));
    jpnlContent.add(jtxtFullPathWin, new XYConstraints(107, 59, 224, -1));
    jpnlContent.add(jlblPreview, new XYConstraints(426, 7, 130, 130));
    jpnlContent.add(jLabel18, new XYConstraints(351, 7, 88, -1));
    jpnlContent.add(jLabel11, new XYConstraints(6, 87, 108, -1));
    jpnlContent.add(jtxtFileDate, new XYConstraints(107, 85, 224, -1));
    jpnlContent.add(jtxtFileSize, new XYConstraints(107, 137, 224, -1));
    jpnlContent.add(jLabel1, new XYConstraints(6, 218, 108, -1));
    jpnlContent.add(jtxtCategory, new XYConstraints(107, 216, 224, -1));
    jpnlContent.add(jtxtCatalogUser, new XYConstraints(107, 190, 224, -1));
    jpnlContent.add(jLabel5, new XYConstraints(6, 193, 108, -1));
    jpnlContent.add(jLabel10, new XYConstraints(6, 166, 108, -1));
    jpnlContent.add(jtxtLastModified, new XYConstraints(107, 164, 224, -1));
    jpnlContent.add(jLabel12, new XYConstraints(6, 139, 108, -1));
    jpnlContent.add(jLabel13, new XYConstraints(6, 114, 108, -1));
    jpnlContent.add(jtxtFileType, new XYConstraints(107, 111, 224, -1));
    jpnlContent.add(jButtonOpenFolder,  new XYConstraints(336, 56, 68, 22));
    jpnlContent.add(jButtonOpenAsset, new XYConstraints(336, 33, 68, 22));
    imageSave = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("save.png"));
    imageCancel = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("erase.png"));
    imagePrint = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("print.png"));
    imageFirst = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("first.png"));
    imagePrev = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("prev.png"));
    imageNext = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("next.png"));
    imageLast = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("last.png"));

    titledBorder1 = new TitledBorder("");
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setDebugGraphicsOptions(0);
    jLabel1.setToolTipText("");
    jLabel1.setText("Category :");
    jLabel5.setText("Cataloging User :");
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel5.setDebugGraphicsOptions(0);
    jtxtCatalogUser.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtCatalogUser.setEditable(false);
    jtxtCatalogUser.setText("");
    jlblPreview.setBackground(Color.white);
    jlblPreview.setBorder(BorderFactory.createEtchedBorder());
    jlblPreview.setHorizontalAlignment(SwingConstants.CENTER);
    jlblPreview.setText("");
    jlblPreview.setBorder(BorderFactory.createEtchedBorder());
    jLabel18.setText("Thumbnail :");
    jLabel18.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel18.setDebugGraphicsOptions(0);
    jLabel18.setRequestFocusEnabled(true);
    jLabel7.setText("Full Path Win :");
    jLabel7.setDebugGraphicsOptions(0);
    jLabel7.setFont(new java.awt.Font("Dialog", 1, 11));
    jtxtFullPathWin.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtFullPathWin.setEditable(false);
    jtxtFullPathWin.setText("");
    jtxtFullPathWin.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jtxtFullPathWin_mouseClicked(e);
      }
    });
    jLabel8.setText("Full Path Mac :");
    jLabel8.setDebugGraphicsOptions(0);
    jLabel8.setFont(new java.awt.Font("Dialog", 1, 11));
    jtxtFullPathMac.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtFullPathMac.setEditable(false);
    jtxtFullPathMac.setText("");
    jtxtFullPathMac.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jtxtFullPathMac_mouseClicked(e);
      }
    });
    jLabel9.setText("File Name :");
    jLabel9.setDebugGraphicsOptions(0);
    jLabel9.setFont(new java.awt.Font("Dialog", 1, 11));
    jtxtFileName.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtFileName.setEditable(false);
    jtxtFileName.setText("");
    jbtnFirst.setMaximumSize(new Dimension(23, 23));
    jbtnFirst.setMinimumSize(new Dimension(23, 23));
    jbtnFirst.setIcon(imageFirst);
    jbtnFirst.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnFirst_actionPerformed(e);
      }
    });
    jbtnPrev.setMaximumSize(new Dimension(23, 23));
    jbtnPrev.setMinimumSize(new Dimension(23, 23));
    jbtnPrev.setIcon(imagePrev);
    jbtnPrev.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnPrev_actionPerformed(e);
      }
    });
    jbtnNext.setMaximumSize(new Dimension(23, 23));
    jbtnNext.setMinimumSize(new Dimension(23, 23));
    jbtnNext.setIcon(imageNext);
    jbtnNext.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnNext_actionPerformed(e);
      }
    });
    jbtnLast.setMaximumSize(new Dimension(23, 23));
    jbtnLast.setMinimumSize(new Dimension(23, 23));
    jbtnLast.setIcon(imageLast);
    jbtnLast.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnLast_actionPerformed(e);
      }
    });
    jToolBar.setBorder(BorderFactory.createEtchedBorder());
    jToolBar.setBorderPainted(true);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing();
      }
    });
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    jtxtLastModified.setText("");
    jtxtLastModified.setEditable(false);
    jtxtLastModified.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel10.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel10.setDebugGraphicsOptions(0);
    jLabel10.setText("Record Modified :");
//    jbtnPrint.setIcon(imagePrint);
//    jbtnPrint.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        jbtnPrint_actionPerformed(e);
//      }
//    });
//    jbtnPrint.setMinimumSize(new Dimension(23, 23));
//    jbtnPrint.setToolTipText("Print");
//    jbtnPrint.setMaximumSize(new Dimension(23, 23));
    getContentPane().add(panelMain);

    jToolBar.setFloatable(false);
    panelMain.add(jToolBar, BorderLayout.NORTH);
//    jToolBar.addSeparator();
//    jToolBar.add(jbtnPrint, null);
//    jToolBar.addSeparator();
    jToolBar.add(jbtnFirst, null);
    jToolBar.add(jbtnPrev, null);
    jToolBar.add(jbtnNext, null);
    jToolBar.add(jbtnLast, null);
    panelMain.add(jpnlContent, BorderLayout.CENTER);

    // Menu Bar
    jMenuFile.setText("File");
    jMenuClose.setText("Close");
    jMenuClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        this_windowClosing();
      }
    });
//    jMenuPrint.setText("Print");
//    jMenuPrint.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        jbtnPrint_actionPerformed(e);
//      }
//    });
    jMenuAsset.setText("Record");
    jMenuFirst.setText("First");
    jMenuFirst.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnFirst_actionPerformed(e);
      }
    });
    jMenuPrevious.setText("Previous");
    jMenuPrevious.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnPrev_actionPerformed(e);
      }
    });
    jMenuNext.setText("Next");
    jMenuNext.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnNext_actionPerformed(e);
      }
    });
    jMenuLast.setText("Last");
    jMenuLast.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnLast_actionPerformed(e);
      }
    });
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuAsset);
//    jMenuFile.add(jMenuPrint);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuClose);
    jMenuAsset.add(jMenuFirst);
    jMenuAsset.add(jMenuPrevious);
    jMenuAsset.add(jMenuNext);
    jMenuAsset.add(jMenuLast);
    jMenuAsset.addSeparator();
    jMenuAsset.add(jMenuPreview);

    if (mCamsDB.windowsOS()) {
//      jMenuPrint.setAccelerator(KeyStroke.getKeyStroke('P', java.awt.event.InputEvent.CTRL_MASK));
      jMenuPrevious.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.CTRL_MASK));
      jMenuNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.CTRL_MASK));
      jMenuClose.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.event.InputEvent.CTRL_MASK));
      jMenuPreview.setAccelerator(KeyStroke.getKeyStroke('Y', java.awt.event.InputEvent.CTRL_MASK));
    } else { // MAC
//      jMenuPrint.setAccelerator(KeyStroke.getKeyStroke('P', java.awt.event.InputEvent.META_MASK));
      jMenuPrevious.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.META_MASK));
      jMenuNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.META_MASK));
      jMenuClose.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.event.InputEvent.META_MASK));
      jMenuPreview.setAccelerator(KeyStroke.getKeyStroke('Y', java.awt.event.InputEvent.META_MASK));
    }
    this.setJMenuBar(jMenuBar1);

  }

  public ImageLibRecord getRecord() { return mCurrentRecord; }

  public void setRecord(ImageLibRecord theValue) { mCurrentRecord = theValue; }

  public int getCatalogId() { return mCatalogId; }
  public void setCatalogId(int theId) { mCatalogId = theId; }

  public String getCatalogName() { return mCatalogName; }
  public void setCatalogName(String theName) { mCatalogName = theName; }

  public void setUserName(String theName) { jtxtCatalogUser.setText(theName); }

  void jbtnExit_actionPerformed(ActionEvent e) {
    hide();
  }


  /**
   * refreshScreen: Copy data from mCurrentRecord to Form Fields
   */
  public void refreshScreen() {
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

//    mCurrentRecord = ((ImageLibFrame) mParent).getImageRecordByRow(mTable.getSelectedRow());
    mCurrentRecord = ((ImageLibFrame) mParent).getImageRecordByRow(mCurrentRow);

    jtxtFileName.setText(mCurrentRecord.getFileName());
    jtxtFullPathMac.setText(mCurrentRecord.getFullPathMac());
    jtxtFullPathMac.setCaretPosition(0);
    jtxtFullPathWin.setText(mCurrentRecord.getFullPathWin());
    jtxtFullPathWin.setCaretPosition(0);
    jtxtFileDate.setText(mCurrentRecord.getFileDateStr12());
    jtxtFileType.setText(mCurrentRecord.getFileFormat());
    jtxtFileSize.setText(String.valueOf(mCurrentRecord.getFileSizeBytes()));
    jtxtLastModified.setText(df.format(mCurrentRecord.getRecordModified()));
    jtxtCatalogUser.setText(mCurrentRecord.getCatalogUser());

    mCatalogId = mCurrentRecord.getCatalogId();
    mCatalogName = mCurrentRecord.getCatalogName();

    ArrayList theCategories = mCurrentRecord.getCategories(mCamsDB, mCategoryFullPathHash);
    IDNamePair theCategoryPair = (IDNamePair) theCategories.get(0);
    String theCategory = theCategoryPair.getName();
    theCategory = theCategory.substring(theCategory.indexOf(",") + 1);
    jtxtCategory.setText(theCategory);

    // Show Thumbnail
    ImageIcon thePreviewIcon = (ImageIcon)jlblPreview.getIcon();
    if (thePreviewIcon != null) {
      thePreviewIcon.getImage().flush();
      thePreviewIcon = null;
    }
    byte[] theThumbnail = mCurrentRecord.getThumbMedium(mCamsDB);
    if (theThumbnail != null)
      thePreviewIcon = new ImageIcon(theThumbnail);
    else
      thePreviewIcon = null;
    jlblPreview.setIcon(thePreviewIcon);
    jlblPreview.revalidate();
  }

  void jbtnFirst_actionPerformed(ActionEvent e) {
    mCurrentRow = 0;
    refreshScreen();

  }

  void jbtnPrev_actionPerformed(ActionEvent e) {
    if (mCurrentRow > 0) mCurrentRow--;
    refreshScreen();
  }

  void jbtnNext_actionPerformed(ActionEvent e) {
    if (mCurrentRow < mTable.getRowCount() - 1) mCurrentRow++;
    refreshScreen();
  }

  void jbtnLast_actionPerformed(ActionEvent e) {
    mCurrentRow = mTable.getRowCount() - 1;
    refreshScreen();
  }

  void jbtnCancel_actionPerformed(ActionEvent e) {
    refreshScreen();
  }

  class CustomListModel extends AbstractListModel {
    private ArrayList mData = new ArrayList();

    public CustomListModel() {}

    // Return the size of the list
    public int getSize() {
      return mData.size();
    }

    // Return an element from the list
    public Object getElementAt(int index) {
      IDNamePair theElement = (IDNamePair) mData.get(index);
      // return theElement;

      // Strip Catalog
      String fullPath = theElement.getName().substring(theElement.getName().indexOf(",")+1);
      return fullPath;
    }

    public IDNamePair get(int index) {
      return (IDNamePair) mData.get(index);
    }

    public void addElement(Object element) {
      mData.add(element);
    }

    public void clear() {
      mData.clear();
    }
  }


  void this_windowClosing() {
    dispose();
  }

  class dropdownItem implements Comparable {
    private int id;
    private String description;

    public String toString() { return description; }
    public String getDesc() { return description; }
    public int getId() { return id; }

    public dropdownItem(int id, String description) {
      this.id = id;
      this.description = description;
    }

    public int compareTo(Object theObject) {
      return toString().compareTo(theObject.toString());
    }

    public boolean equals(Object theObject) {
      return description.equals(theObject.toString());
    }

  }

  void jButtonOpenAsset_actionPerformed(ActionEvent e) {
    if (mCamsDB.windowsOS())
      jtxtFullPathWin_mouseClicked(new MouseEvent(this, e.getID(), e.getWhen(),
                                                  e.getModifiers(), 0, 0, 2, false));
    else
      jtxtFullPathMac_mouseClicked(new MouseEvent(this, e.getID(), e.getWhen(),
                                                  e.getModifiers(), 0, 0, 2, false));
   }

  void jtxtFullPathMac_mouseClicked(MouseEvent e) {
    if ((e.getClickCount() == 2) && (jtxtFullPathMac.getText().length() > 0)) {
      try {
        Runtime r = Runtime.getRuntime();
        String[] theCommands = new String[] {
            "sh", "-c",
            "open \"" + jtxtFullPathMac.getText() + "\""};
        r.exec(theCommands);
      }
      catch (Exception ex) {}
    }
  }

  void jtxtFullPathWin_mouseClicked(MouseEvent e) {
    if ((e.getClickCount() == 2) && (jtxtFullPathWin.getText().length() > 0)) {
      try {
        Runtime r = Runtime.getRuntime();
        r.exec("cmd /c \"" + jtxtFullPathWin.getText() + "\"");
      }
      catch (Exception ex) {}
    }
  }

  /**
   * Print Detail Record
   */
  void jbtnPrint_actionPerformed(ActionEvent e) {
    PrinterJob pjob = PrinterJob.getPrinterJob();
    PageFormat pf = pjob.defaultPage();
    pjob.setPrintable(new PrintDetailClass(), pf);

    try {
      if (pjob.printDialog()) {
        pjob.print();
      }
    }
    catch (PrinterException ex) {
      cams.console.Console.println("Print Error: " + ex.getMessage());
    }
  }

  /**
   * PrintDetailClass: Prints Current Record Information
   */
  class PrintDetailClass extends JComponent implements Printable {
    int mCurrentX = 0;
    int mCurrentY = 0;
    int mFontSize = 10;
    int mIndent = 100;

    public int print(Graphics g, PageFormat pf, int pageIndex) {
      if (pageIndex > 0) {
        return Printable.NO_SUCH_PAGE;
      }
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pf.getImageableX(), pf.getImageableY());
      drawGraphics(g2d, pf);
      return Printable.PAGE_EXISTS;
    }

    private void drawGraphics(Graphics2D graphics, PageFormat pf) {

      // Set starting Y position
      setBold(graphics);
      mCurrentY = graphics.getFontMetrics().getHeight() + 1;

      mIndent = graphics.getFontMetrics().stringWidth("Imported Keywords:") + 5;

      printLine(graphics, "File Name:", jtxtFileName.getText());
      printLine(graphics, "Full Path Mac:", jtxtFullPathMac.getText());
      printLine(graphics, "Full Path Windows:", jtxtFullPathWin.getText());
      printLine(graphics, "Record Modified:", jtxtLastModified.getText());
      printLine(graphics, "Cataloging User:", jtxtCatalogUser.getText());
      printLine(graphics, "Record Name:", jtxtCategory.getText());

      // Draw Thumbnail
      byte[] theThumbBytes = null;
      theThumbBytes = mCurrentRecord.getThumbMedium(mCamsDB);
      Image theImage = new ImageIcon(theThumbBytes).getImage();
      graphics.drawImage(theImage, (int) pf.getImageableWidth() - 140,
                         graphics.getFontMetrics().getHeight() * 5, this);

    } // drawGraphics

    private void setBold(Graphics2D graphics) { graphics.setFont(new Font("Arial", Font.BOLD, mFontSize)); }
    private void setPlain(Graphics2D graphics) { graphics.setFont(new Font("Arial", Font.PLAIN, mFontSize)); }

    /**
     * Prints a line (or more) of text by first printing the header in bold
     * followed by the data.
     *
     * @param header String
     * @param data String
     */
    private void printLine(Graphics2D graphics, String header, String data) {
      setBold(graphics);
      graphics.drawString(header, mCurrentX, mCurrentY);
      setPlain(graphics);

      String[] multiLine = data.split("\n");
      for (int i=0; i < multiLine.length; i++) {
        graphics.drawString(multiLine[i], mCurrentX + mIndent, mCurrentY);
        mCurrentY += graphics.getFontMetrics().getHeight();
      }

    }
    // PrintDetailClass
  }



 void jMenuPreview_actionPerformed(ActionEvent e) {
   mCurrentRecord.getThumbXLarge(mCamsDB); // Make sure XLarge image is loaded
   ViewImageDialog preview = new ViewImageDialog(new JFrame(), "Preview Image",
       640, mCurrentRecord, this.isModal(), mCamsDB);
   preview.setProperties(mCamsDB.getProperties());
   preview.show();
 }

 void jButtonOpenFolder_actionPerformed(ActionEvent e) {
   if (mCamsDB.windowsOS()) {
     // Windows
     try {
       String theFolder = jtxtFullPathWin.getText();
       if (theFolder.indexOf("\\") == -1)return;
       theFolder = theFolder.substring(0, theFolder.lastIndexOf("\\") + 1);
       Runtime r = Runtime.getRuntime();
       r.exec("Explorer \"" + theFolder + "\"");
     }
     catch (Exception ex) {}
   }
   else {
     // Mac
     try {
       String theFolder = jtxtFullPathMac.getText();
       if (theFolder.indexOf("/") == -1)return;
       theFolder = theFolder.substring(0, theFolder.lastIndexOf("/") + 1);
       Runtime r = Runtime.getRuntime();
       String[] theCommands = new String[] {
           "sh", "-c",
           "open \"" + theFolder + "\""};
       r.exec(theCommands);
     }
     catch (Exception ex) {}
   }
 }

}

package cams.imagelib.detaildialogs;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.border.*;

import cams.console.*;
import cams.database.*;
import cams.imagelib.*;
import com.borland.jbcl.layout.*;

public class StockDetailsDialog extends JDialog {
  public boolean userCancel = true;
  private ImageLibRecord mCurrentRecord = null;
  private JFrame mParent = null;
  private JTable mTable = null;
  private boolean mSingle = false;
  private CamsDB mCamsDB = null;
  private HashMap mCategoryFullPathHash = null;
  private int mCatalogId = -1;
  private String mCatalogName = null;
  private int mCurrentRow = -1;
  public boolean dataSaved = false;
  private int[] mSelectedRows = null;
  private ArrayList mFormFields = null;

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
  XYLayout xYLayout1 = new XYLayout();
  JLabel jLabel1 = new JLabel();
  JTextField jtxtRecordName = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField jtxtScanNum = new JTextField();
  JLabel jLabel3 = new JLabel();
  JTextField jtxtKeyline = new JTextField();
  JLabel jLabel4 = new JLabel();
  JCheckBox jckDontDelete = new JCheckBox();
  JLabel jLabel5 = new JLabel();
  JTextField jtxtCatalogUser = new JTextField();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JTextField jtxtCDName = new JTextField();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JTextArea jtxtNotes = new JTextArea();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollMain = new JScrollPane();
  JLabel jLabel14 = new JLabel();
  JLabel jLabel15 = new JLabel();
  JTextField jtxtImageCompany = new JTextField();
  JLabel jLabel16 = new JLabel();
  JList jListKeywords = new JList(new CustomListModel());
  JScrollPane jScrollPane2 = new JScrollPane();
  JButton jbtnEditKeywords = new JButton();
  JButton jbtnContinue = new JButton();
  JButton jbtnExit = new JButton();
  JLabel jLabel17 = new JLabel();
  JScrollPane jScrollPane3 = new JScrollPane();
  JList jListCategories = new JList(new CustomListModel());
  JButton jbtnEditCategories = new JButton();
  JButton jbtnAddCategories = new JButton();
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
  JButton jbtnSave = new JButton();
  JButton jbtnCancel = new JButton();
  JButton jbtnFirst = new JButton();
  JButton jbtnPrev = new JButton();
  JButton jbtnNext = new JButton();
  JButton jbtnLast = new JButton();
  JLabel jlblImportedKeywords = new JLabel();
  JScrollPane jScrollPane4 = new JScrollPane();
  JTextArea jtxtKeywords = new JTextArea();
  JComboBox cboStatus = new JComboBox();
  JTextField jtxtLastModified = new JTextField();
  JLabel jLabel10 = new JLabel();
  JTextField jtxtArea = new JTextField();
  JLabel jLabel110 = new JLabel();
  JTextField jtxtAdvertiser = new JTextField();
  JLabel jLabel111 = new JLabel();
  JScrollPane jScrollPane5 = new JScrollPane();
  JTextArea jtxtUsageRules = new JTextArea();
  JComboBox cboReleases = new JComboBox();
  JLabel jLabel19 = new JLabel();
  JCheckBox jckWebDisplay = new JCheckBox();
  JButton jbtnPrint = new JButton();
  JButton jButtonOpenAsset = new JButton();
  JButton jButtonOpenFolder = new JButton();
  // Menu Bar
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuSave = new JMenuItem();
  JMenuItem jMenuClose = new JMenuItem();
  JMenuItem jMenuPrint = new JMenuItem();
  JMenu jMenuAsset = new JMenu();
  JMenuItem jMenuFirst = new JMenuItem();
  JMenuItem jMenuPrevious = new JMenuItem();
  JMenuItem jMenuNext = new JMenuItem();
  JMenuItem jMenuLast = new JMenuItem();
  JMenuItem jMenuPreview = new JMenuItem();
  JCheckBox jckPriorityDisplay = new JCheckBox();
  JLabel jLabel112 = new JLabel();

  public StockDetailsDialog(Frame frame, CamsDB theCamsDB, JTable theTable,
                            HashMap categoryPathHash, int currentRow,
                            int cascadeIndex, boolean isModal) {
    super(new Frame(), "Asset Details (Stock)", isModal);
    try {
      mParent = (JFrame) frame;
      mCamsDB = theCamsDB;
      mTable = theTable;
      mCategoryFullPathHash = categoryPathHash;
      mCurrentRow = currentRow;
      jbInit();

      loadStatus();
      loadReleases();

      if (theTable == null) {
      // Importing Records, Don't Show Toolbar
        jToolBar.setVisible(false);
        jlblImportedKeywords.setEnabled(false);
        jtxtKeywords.setEnabled(false);
        jButtonOpenAsset.setVisible(false);
        jButtonOpenFolder.setVisible(false);
        this.setJMenuBar(null);
      }
      else {
        // Editing Rows from Table
        jbtnExit.setVisible(false);
        jbtnContinue.setVisible(false);
      }

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

      configureTextFields();
      jtxtCatalogUser.requestFocus();
      jtxtCatalogUser.selectAll();

    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * StockDetailsDialog in Batch Edit Mode
   *
   * @param frame Frame
   * @param theCamsDB CamsDB
   * @param theTable JTable
   * @param categoryPathHash HashMap
   * @param selectedRows int[]
   */
  public StockDetailsDialog(Frame frame, CamsDB theCamsDB, JTable theTable,
                            HashMap categoryPathHash, int[] selectedRows) {
    super(new Frame(), "Asset Details (Stock)", true);
    try {
      mParent = (JFrame) frame;
      mCamsDB = theCamsDB;
      mTable = theTable;
      mCategoryFullPathHash = categoryPathHash;
      mSelectedRows = selectedRows;
      jbInit();

      loadStatus();
      loadReleases();

      // Don't Show Toolbar
      jToolBar.setVisible(false);
      jlblImportedKeywords.setEnabled(false);
      jtxtKeywords.setEnabled(false);
      jButtonOpenAsset.setVisible(false);
      jButtonOpenFolder.setVisible(false);
      this.setJMenuBar(null);

      // Set CheckBoxes to be Three State
      jckDontDelete.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          jCheckBoxThreeState_mouseClicked(jckDontDelete);
        }
      });
      jckDontDelete.setEnabled(false);

      jckWebDisplay.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          jCheckBoxThreeState_mouseClicked(jckWebDisplay);
        }
      });
      jckWebDisplay.setEnabled(false);

      jckPriorityDisplay.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          jCheckBoxThreeState_mouseClicked(jckPriorityDisplay);
        }
      });
      jckPriorityDisplay.setEnabled(false);

      mCurrentRecord = ((ImageLibFrame) mParent).getImageRecordByRow(mSelectedRows[0]);
      mCatalogId = mCurrentRecord.getCatalogId();
      mCatalogName = mCurrentRecord.getCatalogName();
      mCurrentRecord = null;

      pack();
      setSize(getSize().width + 10, getSize().height + 10);

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

      configureTextFields();
      jtxtCatalogUser.requestFocus();
      jtxtCatalogUser.selectAll();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    imageSave = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("save.png"));
    imageCancel = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("erase.png"));
    imagePrint = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("print.png"));
    imageFirst = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("first.png"));
    imagePrev = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("prev.png"));
    imageNext = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("next.png"));
    imageLast = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("last.png"));

    titledBorder1 = new TitledBorder("");
    panelMain.setLayout(borderLayout1);
    jpnlContent.setLayout(xYLayout1);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setDebugGraphicsOptions(0);
    jLabel1.setText("Record Name :");
    jtxtRecordName.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtRecordName.setText("");
    jLabel2.setText("Scan # :");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel2.setDebugGraphicsOptions(0);
    jtxtScanNum.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtScanNum.setText("");
    jLabel3.setDebugGraphicsOptions(0);
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel3.setText("Keyline :");
    jtxtKeyline.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtKeyline.setText("");
    jLabel4.setText("Don\'t Delete Record :");
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel4.setDebugGraphicsOptions(0);
    jckDontDelete.setText("");
    jLabel5.setText("Cataloging User :");
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel5.setDebugGraphicsOptions(0);
    jtxtCatalogUser.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtCatalogUser.setText("");
    jLabel6.setText("Status :");
    jLabel6.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel6.setDebugGraphicsOptions(0);
    jLabel11.setDebugGraphicsOptions(0);
    jLabel11.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel11.setText("CD Name :");
    jtxtCDName.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtCDName.setText("");
    jLabel12.setText("Usage Rules :");
    jLabel12.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel12.setDebugGraphicsOptions(0);
    jLabel12.setRequestFocusEnabled(true);
    jLabel13.setRequestFocusEnabled(true);
    jLabel13.setDebugGraphicsOptions(0);
    jLabel13.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel13.setText("Notes :");
    jtxtNotes.setFont(new java.awt.Font("Dialog", 0, 11));
    jtxtNotes.setText("");
    jtxtNotes.setLineWrap(true);
    jtxtNotes.setWrapStyleWord(true);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel14.setRequestFocusEnabled(true);
    jLabel14.setDebugGraphicsOptions(0);
    jLabel14.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel14.setText("Releases :");
    jLabel15.setRequestFocusEnabled(true);
    jLabel15.setDebugGraphicsOptions(0);
    jLabel15.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel15.setText("Image Company :");
    jtxtImageCompany.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtImageCompany.setText("");
    jLabel16.setText("Keywords :");
    jLabel16.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel16.setDebugGraphicsOptions(0);
    jLabel16.setRequestFocusEnabled(true);
    jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane2.setBorder(BorderFactory.createLoweredBevelBorder());
    jbtnEditKeywords.setText("Edit Keywords");
    jbtnEditKeywords.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnEditKeywords_actionPerformed(e);
      }
    });
    jbtnContinue.setText("Continue");
    jbtnContinue.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnContinue_actionPerformed(e);
      }
    });
    jbtnExit.setText("Cancel");
    jbtnExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnExit_actionPerformed(e);
      }
    });
    jLabel17.setRequestFocusEnabled(true);
    jLabel17.setToolTipText("");
    jLabel17.setDebugGraphicsOptions(0);
    jLabel17.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel17.setText("Categories :");
    jScrollPane3.setBorder(BorderFactory.createLoweredBevelBorder());
    jbtnEditCategories.setText("Edit");
    jbtnEditCategories.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnEditCategories_actionPerformed(e);
      }
    });
    jbtnAddCategories.setText("Add");
    jbtnAddCategories.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnAddCategories_actionPerformed(e);
      }
    });
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
    jtxtFullPathWin.setEnabled(false);
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
    jtxtFullPathMac.setEnabled(false);
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
    jtxtFileName.setEnabled(false);
    jtxtFileName.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtFileName.setEditable(false);
    jtxtFileName.setText("");
    jbtnSave.setMaximumSize(new Dimension(23, 23));
    jbtnSave.setMinimumSize(new Dimension(23, 23));
    jbtnSave.setToolTipText("Save");
    jbtnSave.setIcon(imageSave);
    jbtnSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnSave_actionPerformed(e);
      }
    });
    jbtnCancel.setMaximumSize(new Dimension(23, 23));
    jbtnCancel.setMinimumSize(new Dimension(23, 23));
    jbtnCancel.setToolTipText("Cancel");
    jbtnCancel.setIcon(imageCancel);
    jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnCancel_actionPerformed(e);
      }
    });
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
    jlblImportedKeywords.setRequestFocusEnabled(true);
    jlblImportedKeywords.setDebugGraphicsOptions(0);
    jlblImportedKeywords.setFont(new java.awt.Font("Dialog", 1, 11));
    jlblImportedKeywords.setText("Keywords (Original) :");
    jtxtKeywords.setFont(new java.awt.Font("Dialog", 0, 11));
    jtxtKeywords.setText("");
    jtxtKeywords.setLineWrap(true);
    jtxtKeywords.setWrapStyleWord(true);
    jScrollPane4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane4.setBorder(BorderFactory.createLoweredBevelBorder());
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing();
      }
    });
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    jtxtLastModified.setText("");
    jtxtLastModified.setEditable(false);
    jtxtLastModified.setEnabled(false);
    jtxtLastModified.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel10.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel10.setDebugGraphicsOptions(0);
    jLabel10.setText("Record Modified :");
    jtxtAdvertiser.setText("");
    jtxtAdvertiser.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel110.setText("Advertiser :");
    jLabel110.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel110.setDebugGraphicsOptions(0);
    jLabel110.setRequestFocusEnabled(true);
    jLabel111.setRequestFocusEnabled(true);
    jLabel111.setDebugGraphicsOptions(0);
    jLabel111.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel111.setText("Area :");
    jtxtArea.setBorder(BorderFactory.createLoweredBevelBorder());
    if (mCamsDB.windowsOS())
      cboStatus.setBorder(BorderFactory.createLoweredBevelBorder());
    jScrollPane5.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane5.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtUsageRules.setToolTipText("");
    jtxtUsageRules.setText("");
    jtxtUsageRules.setLineWrap(true);
    jtxtUsageRules.setWrapStyleWord(true);
    jtxtUsageRules.setFont(new java.awt.Font("Dialog", 0, 11));
    if (mCamsDB.windowsOS())
      cboReleases.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel19.setDebugGraphicsOptions(0);
    jLabel19.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel19.setText("Display on Web :");
    jckWebDisplay.setText("");
    jbtnPrint.setIcon(imagePrint);
    jbtnPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnPrint_actionPerformed(e);
      }
    });
    jbtnPrint.setMinimumSize(new Dimension(23, 23));
    jbtnPrint.setToolTipText("Print");
    jbtnPrint.setMaximumSize(new Dimension(23, 23));
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
    jButtonOpenFolder.setText("Browse");
    jckPriorityDisplay.setText("");
    jLabel112.setText("Priority Display :");
    jLabel112.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel112.setDebugGraphicsOptions(0);
    jScrollMain.setBorder(null);
    jScrollMain.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollMain.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jpnlContent.add(jLabel7, new XYConstraints(6, 62, 108, -1));
    jpnlContent.add(jLabel8, new XYConstraints(6, 36, 108, -1));
    jpnlContent.add(jLabel9, new XYConstraints(6, 10, 108, -1));
    jpnlContent.add(jtxtFileName, new XYConstraints(107, 7, 224, -1));
    jpnlContent.add(jtxtFullPathMac, new XYConstraints(107, 33, 224, -1));
    jpnlContent.add(jtxtFullPathWin, new XYConstraints(107, 59, 224, -1));
    jpnlContent.add(jbtnEditKeywords,    new XYConstraints(438, 213, 144, 26));
    jpnlContent.add(jbtnEditCategories,    new XYConstraints(438, 368, 62, 26));
    jpnlContent.add(jScrollPane3, new XYConstraints(428, 252, 165, 114));
    jpnlContent.add(jLabel17, new XYConstraints(353, 252, 84, -1));
    jpnlContent.add(jScrollPane2, new XYConstraints(428, 97, 165, 114));
    jpnlContent.add(jLabel16, new XYConstraints(353, 97, 83, -1));
    jpnlContent.add(jlblImportedKeywords, new XYConstraints(353, 10, 125, -1));
    jpnlContent.add(jScrollPane4, new XYConstraints(428, 27, 165, 62));
    jpnlContent.add(jlblPreview, new XYConstraints(428, 402, 130, 130));
    jpnlContent.add(jLabel18, new XYConstraints(353, 402, 88, -1));
    jpnlContent.add(jbtnExit, new XYConstraints(495, 535, 91, 28));
    jpnlContent.add(jbtnContinue, new XYConstraints(379, 535, 91, 28));
    jpnlContent.add(jLabel10, new XYConstraints(6, 87, 108, -1));
    jpnlContent.add(jtxtLastModified, new XYConstraints(107, 85, 224, -1));
    jpnlContent.add(jtxtRecordName, new XYConstraints(107, 137, 224, -1));
    jpnlContent.add(jLabel1, new XYConstraints(6, 140, 108, -1));
    jpnlContent.add(jLabel2, new XYConstraints(6, 166, 108, -1));
    jpnlContent.add(jLabel3, new XYConstraints(6, 192, 108, -1));
    jpnlContent.add(jLabel4,  new XYConstraints(6, 216, 127, -1));
    jpnlContent.add(jtxtScanNum, new XYConstraints(107, 163, 224, -1));
    jpnlContent.add(jtxtKeyline, new XYConstraints(107, 189, 224, -1));
    jpnlContent.add(jtxtCatalogUser, new XYConstraints(107, 111, 224, -1));
    jpnlContent.add(jLabel5, new XYConstraints(6, 114, 108, -1));
    jpnlContent.add(jLabel6, new XYConstraints(6, 239, 108, -1));
    jpnlContent.add(jLabel11, new XYConstraints(6, 263, 108, -1));
    jpnlContent.add(jLabel12, new XYConstraints(6, 288, 108, -1));
    jpnlContent.add(cboStatus,  new XYConstraints(107, 235, 224, -1));
    jpnlContent.add(jtxtImageCompany, new XYConstraints(107, 417, 224, -1));
    jpnlContent.add(jLabel110, new XYConstraints(6, 341, 108, -1));
    jpnlContent.add(jtxtAdvertiser, new XYConstraints(107, 339, 224, -1));
    jpnlContent.add(jLabel111, new XYConstraints(6, 367, 108, -1));
    jpnlContent.add(jtxtArea, new XYConstraints(107, 365, 224, -1));
    jpnlContent.add(jLabel14, new XYConstraints(6, 392, 108, -1));
    jpnlContent.add(jLabel15, new XYConstraints(6, 419, 108, -1));
    jpnlContent.add(jLabel13, new XYConstraints(6, 445, 108, -1));
    jpnlContent.add(jScrollPane1,   new XYConstraints(107, 444, 224, 87));
    jpnlContent.add(jtxtCDName, new XYConstraints(107, 263, 224, -1));
    jpnlContent.add(jScrollPane5,  new XYConstraints(107, 290, 224, 42));
    jpnlContent.add(cboReleases,       new XYConstraints(107, 391, 224, 20));
    jpnlContent.add(jbtnAddCategories,     new XYConstraints(525, 368, 62, 26));
    jpnlContent.add(jButtonOpenFolder,  new XYConstraints(336, 56, 68, 22));
    jpnlContent.add(jButtonOpenAsset, new XYConstraints(336, 33, 68, 22));
    jpnlContent.add(jLabel112, new XYConstraints(298, 216, 100, -1));
    jpnlContent.add(jLabel19, new XYConstraints(164, 216, 100, -1));
    jpnlContent.add(jckWebDisplay,  new XYConstraints(258, 211, -1, -1));
    jpnlContent.add(jckPriorityDisplay, new XYConstraints(390, 211, -1, -1));
    jpnlContent.add(jckDontDelete, new XYConstraints(127, 211, -1, -1));
    jScrollPane5.getViewport().add(jtxtUsageRules, null);
    jScrollPane1.getViewport().add(jtxtNotes, null);
    jScrollPane4.getViewport().add(jtxtKeywords, null);
    jScrollPane2.getViewport().add(jListKeywords, null);
    jScrollPane3.getViewport().add(jListCategories, null);
    getContentPane().add(jScrollMain);
    jScrollMain.getViewport().add(panelMain, null);
    // getContentPane().add(panelMain);

    jToolBar.setFloatable(false);
    panelMain.add(jToolBar, BorderLayout.NORTH);
    jToolBar.add(jbtnSave, null);
    jToolBar.add(jbtnCancel, null);
    jToolBar.addSeparator();
    jToolBar.add(jbtnPrint, null);
    jToolBar.addSeparator();
    jToolBar.add(jbtnFirst, null);
    jToolBar.add(jbtnPrev, null);
    jToolBar.add(jbtnNext, null);
    jToolBar.add(jbtnLast, null);
    panelMain.add(jpnlContent, BorderLayout.WEST);

    // Menu Bar
    jMenuFile.setText("File");
    jMenuSave.setText("Save Asset");
    jMenuSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnSave_actionPerformed(e);
      }
    });
    jMenuClose.setText("Close");
    jMenuClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        this_windowClosing();
      }
    });
    jMenuPrint.setText("Print");
    jMenuPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnPrint_actionPerformed(e);
      }
    });
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
    jMenuFile.add(jMenuSave);
    jMenuFile.add(jMenuPrint);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuClose);
    jMenuAsset.add(jMenuFirst);
    jMenuAsset.add(jMenuPrevious);
    jMenuAsset.add(jMenuNext);
    jMenuAsset.add(jMenuLast);
    jMenuAsset.addSeparator();
    jMenuAsset.add(jMenuPreview);

    if (mCamsDB.windowsOS()) {
      jMenuSave.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.event.InputEvent.CTRL_MASK));
      jMenuPrint.setAccelerator(KeyStroke.getKeyStroke('P', java.awt.event.InputEvent.CTRL_MASK));
      jMenuPrevious.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.CTRL_MASK));
      jMenuNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.CTRL_MASK));
      jMenuClose.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.event.InputEvent.CTRL_MASK));
      jMenuPreview.setAccelerator(KeyStroke.getKeyStroke('Y', java.awt.event.InputEvent.CTRL_MASK));
    } else { // MAC
      jMenuSave.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.event.InputEvent.META_MASK));
      jMenuPrint.setAccelerator(KeyStroke.getKeyStroke('P', java.awt.event.InputEvent.META_MASK));
      jMenuPrevious.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.META_MASK));
      jMenuNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.META_MASK));
      jMenuClose.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.event.InputEvent.META_MASK));
      jMenuPreview.setAccelerator(KeyStroke.getKeyStroke('Y', java.awt.event.InputEvent.META_MASK));
    }
    this.setJMenuBar(jMenuBar1);
  }

  /**
   * Search through all the controls and set the tab order,
   * background color for disabled fields, and key-event handler, etc.
   */
  private void configureTextFields() {
    mFormFields = new ArrayList();
    mFormFields.add(jtxtCatalogUser);
    mFormFields.add(jtxtRecordName);
    mFormFields.add(jtxtScanNum);
    mFormFields.add(jtxtKeyline);
    mFormFields.add(jckDontDelete);
    mFormFields.add(jckWebDisplay);
    mFormFields.add(jckPriorityDisplay);
    mFormFields.add(cboStatus);
    mFormFields.add(jtxtCDName);
    mFormFields.add(jtxtUsageRules);
    mFormFields.add(jtxtAdvertiser);
    mFormFields.add(jtxtArea);
    mFormFields.add(cboReleases);
    mFormFields.add(jtxtImageCompany);
    mFormFields.add(jtxtNotes);
    mFormFields.add(jtxtKeywords);
    mFormFields.add(jListKeywords);
    mFormFields.add(jListCategories);

    for (int i=0; i < mFormFields.size(); i++) {
      JComponent theField = (JComponent) mFormFields.get(i);
      theField.setFocusTraversalKeysEnabled(false);
      theField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(KeyEvent e) { formField_keyPressed(e); }
      });
    }
  }

  // Handle key presses in Form Fields
  void formField_keyPressed(KeyEvent e) {
    JComponent sender = null;
    JComponent nextField = null;
    int fieldIndex = 0;
    if (e.getKeyCode() == e.VK_TAB) {
      sender = (JComponent) e.getComponent();
      fieldIndex = mFormFields.indexOf(sender);
      if (e.isShiftDown())
        fieldIndex--;
      else
        fieldIndex++;
      if (fieldIndex >= mFormFields.size()) fieldIndex = 0;
      if (fieldIndex < 0) fieldIndex = 0;
      nextField = ((JComponent) mFormFields.get(fieldIndex));
      nextField.requestFocus();
      if (nextField.getClass().equals(JTextField.class))
        ((JTextField) nextField).selectAll();
      else if (nextField.getClass().equals(JTextArea.class))
        ((JTextArea) nextField).selectAll();

      e.consume();
    }

    // Handle Ctrl-Arrow to navigate Prev and Next
    if (mCamsDB.windowsOS()) {
      if ((e.getKeyCode() == e.VK_LEFT) && (e.isControlDown())) {
        jbtnPrev_actionPerformed(new ActionEvent(this, 0, null));
        e.consume();
      }
      else if ((e.getKeyCode() == e.VK_RIGHT) && (e.isControlDown())) {
        jbtnNext_actionPerformed(new ActionEvent(this, 0, null));
        e.consume();
      }
    } else { // MAC
      if ((e.getKeyCode() == e.VK_LEFT) && (e.isMetaDown())) {
        jbtnPrev_actionPerformed(new ActionEvent(this, 0, null));
        e.consume();
      }
      else if ((e.getKeyCode() == e.VK_RIGHT) && (e.isMetaDown())) {
        jbtnNext_actionPerformed(new ActionEvent(this, 0, null));
        e.consume();
      }
    }

  }


  public ImageLibRecord getRecord() { return mCurrentRecord; }

  public void setRecord(ImageLibRecord theValue) { mCurrentRecord = theValue; }

  public int getCatalogId() { return mCatalogId; }
  public void setCatalogId(int theId) { mCatalogId = theId; }

  public String getCatalogName() { return mCatalogName; }
  public void setCatalogName(String theName) { mCatalogName = theName; }

  public void setUserName(String theName) { jtxtCatalogUser.setText(theName); }

  private void loadStatus() {
    ResultSet rs = null;

    DefaultComboBoxModel theModel = (DefaultComboBoxModel) cboStatus.getModel();
    theModel.removeAllElements();

    theModel.addElement(new dropdownItem(-1, ""));

    try {
      rs = mCamsDB.query("SELECT DISTINCT status_id, status FROM ImageLibStatus " +
                         "WHERE Upper(catalog_type) like 'STOCK' " +
                         "ORDER BY status");
      while (rs.next()) {
        // adSizes.add(new dropdownItem(rs.getInt("AdvertSizeID"), rs.getString("AdvertDescription")));
        theModel.addElement(new dropdownItem(rs.getInt("status_id"), rs.getString("status")));
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close(); rs = null;
    }
    catch (Exception ex) {
      Console.println("StockDetailsDialog:loadStatus: " + ex.getMessage());
    }
    cboStatus.setSelectedIndex(0);
  }

  private void loadReleases() {
    ResultSet rs = null;

    DefaultComboBoxModel theModel = (DefaultComboBoxModel) cboReleases.getModel();
    theModel.removeAllElements();

    theModel.addElement(new dropdownItem(-1, ""));

    try {
      rs = mCamsDB.query("SELECT release_id, release FROM ImageLibRelease " +
                         "ORDER BY sort_order");
      while (rs.next()) {
        theModel.addElement(new dropdownItem(rs.getInt("release_id"), rs.getString("release")));
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close(); rs = null;
    }
    catch (Exception ex) {
      Console.println("StockDetailsDialog:loadReleases: " + ex.getMessage());
    }
    cboReleases.setSelectedIndex(0);
  }

  void jbtnExit_actionPerformed(ActionEvent e) {
    hide();
  }

  void jbtnContinue_actionPerformed(ActionEvent e) {
    if (mSelectedRows != null) {
      doBatchEdit();
      return;
    }

    // Make sure at least one Category is selected
    if (((CustomListModel)jListCategories.getModel()).mData.size() == 0) {
      JOptionPane.showMessageDialog(this, "No Categories Specified.  Please " +
                                    "select at least one category for these " +
                                    "images.", "No Categories Specified",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (mCurrentRecord == null) mCurrentRecord = new ImageLibRecord();

    updateRecord();

    userCancel = false;

    hide();
  }

  /**
   * UpdateRecord: Copy data from form fields into mCurrentRecord
   */
  void updateRecord() {
    dataSaved = true;

    // Update ImageLibRecord with user entries
    mCurrentRecord.setRecordName(jtxtRecordName.getText());
    mCurrentRecord.setScanNumber(jtxtScanNum.getText());
    mCurrentRecord.setKeyline(jtxtKeyline.getText());
    mCurrentRecord.setDontDelete(jckDontDelete.isSelected());
    mCurrentRecord.setWebDisplay(jckWebDisplay.isSelected());
    mCurrentRecord.setPriorityDisplay(jckPriorityDisplay.isSelected());
    mCurrentRecord.setCatalogUser(jtxtCatalogUser.getText());
    dropdownItem theStatus = (dropdownItem) cboStatus.getSelectedItem();
    mCurrentRecord.setStatusId(theStatus.getId());
    mCurrentRecord.setStatus(theStatus.getDesc());
    mCurrentRecord.setCDName(jtxtCDName.getText());
    mCurrentRecord.setUsageRules(jtxtUsageRules.getText());
    mCurrentRecord.setNotes(jtxtNotes.getText());

    dropdownItem theReleases = (dropdownItem) cboReleases.getSelectedItem();
    mCurrentRecord.setReleaseId(theReleases.getId());
    mCurrentRecord.setReleases(theReleases.getDesc());

    mCurrentRecord.setImageCompany(jtxtImageCompany.getText());
    mCurrentRecord.setOriginalKeywords(jtxtKeywords.getText());
    mCurrentRecord.setArea(jtxtArea.getText());
    mCurrentRecord.setAdvertiser(jtxtAdvertiser.getText());

    // Update Categories
    ArrayList theSelectedCategories = ((CustomListModel)jListCategories.getModel()).mData;
    mCurrentRecord.setCategories((ArrayList)theSelectedCategories.clone());

    // Update Keywords
    ArrayList theSelectedKeywords = ((CustomListModel)jListKeywords.getModel()).mData;
    mCurrentRecord.setKeywords((ArrayList)theSelectedKeywords.clone());
  }

  /**
   * doBatchEdit: Create UPDATE statement based on data from form fields
   */
  void doBatchEdit() {
    ArrayList theUpdates = new ArrayList();

    addStringField(theUpdates, jtxtRecordName, "RecordName");
    addStringField(theUpdates, jtxtScanNum, "ScanNum");
    addStringField(theUpdates, jtxtKeyline, "Keyline");
    addStringField(theUpdates, jtxtCatalogUser, "CatalogUser");
    addStringField(theUpdates, jtxtCDName, "CDName");
    addStringField(theUpdates, jtxtUsageRules, "UsageRules");
//    addStringField(theUpdates, jtxtNotes, "Notes");
    addStringField(theUpdates, jtxtImageCompany, "ImageCompany");
//    addStringField(theUpdates, jtxtKeywords, "OriginalKeywords");
    addStringField(theUpdates, jtxtArea, "Area");
    addStringField(theUpdates, jtxtAdvertiser, "Advertiser");

    addCheckBox(theUpdates, jckDontDelete, "DontDelete");
    addCheckBox(theUpdates, jckWebDisplay, "WebDisplay");
    addCheckBox(theUpdates, jckPriorityDisplay, "PriorityDisplay");

    addDropDown(theUpdates, cboStatus, "Status_id");
    addDropDown(theUpdates, cboReleases, "Release_id");

    // Update Categories
    ArrayList theSelectedCategories = ((CustomListModel)jListCategories.getModel()).mData;
//    mCurrentRecord.setCategories((ArrayList)theSelectedCategories.clone());

    // Update Keywords
    ArrayList theSelectedKeywords = ((CustomListModel)jListKeywords.getModel()).mData;
//    mCurrentRecord.setKeywords((ArrayList)theSelectedKeywords.clone());

    if ( (theUpdates.size() > 0) || (theSelectedCategories.size() > 0) ||
         (theSelectedKeywords.size() > 0) || (jtxtNotes.getText().length() > 0) ) {
      int rspnse = JOptionPane.showConfirmDialog(this, "Batch Edit the data entered " +
                                                 "in to the selected " + mSelectedRows.length +
                                                 " records?", "Confirm Changes",
                                                 JOptionPane.YES_NO_CANCEL_OPTION);
      if (rspnse != JOptionPane.YES_OPTION) return;
    } else {
      JOptionPane.showMessageDialog(this, "No Changes Detected");
      return;
    }

    dataSaved = true;

    // First Do Updates to ImageLibRecord
    if (theUpdates.size() > 0) {
      String sql = "UPDATE ImageLibRecord SET ";
      for (int i = 0; i < theUpdates.size(); i++) {
        sql += (String) theUpdates.get(i);
        if (i < theUpdates.size() - 1) sql += ", ";
      }
      sql += " WHERE record_id = ";

      Console.println("Batch Edit: " + sql);
      for (int i=0; i < mSelectedRows.length; i++) {
        mCurrentRecord = ((ImageLibFrame) mParent).getImageRecordByRow(mSelectedRows[i]);
        mCamsDB.execute(sql + mCurrentRecord.getRecordId());
      }
    }

    // Now update Notes (if necessary)
    if (jtxtNotes.getText().length() > 0) {
      String sql = "UPDATE ImageLibNotes SET Notes = '" + jtxtNotes.getText().replaceAll("'", "''") +
                   "' WHERE record_id = ";
      for (int i=0; i < mSelectedRows.length; i++) {
        mCurrentRecord = ( (ImageLibFrame) mParent).getImageRecordByRow(mSelectedRows[i]);
        mCamsDB.execute(sql +  mCurrentRecord.getRecordId());
      }
    }

    // Now Update Categories (if necessary)
    if (theSelectedCategories.size() > 0) {
      for (int i=0; i < mSelectedRows.length; i++) {
        mCurrentRecord = ( (ImageLibFrame) mParent).getImageRecordByRow(mSelectedRows[i]);
        mCurrentRecord.setCategories( (ArrayList) theSelectedCategories.clone());
        mCurrentRecord.updateRecordCategories(mCamsDB);
      }
    }

    // Now Update Keywords (if necessary)
    if (theSelectedKeywords.size() > 0) {
      for (int i=0; i < mSelectedRows.length; i++) {
        mCurrentRecord = ( (ImageLibFrame) mParent).getImageRecordByRow(mSelectedRows[i]);
        mCurrentRecord.setKeywords( (ArrayList) theSelectedKeywords.clone());
        mCurrentRecord.updateRecordKeywords(mCamsDB);
      }
    }

    mCurrentRecord = null;

    hide();
  }

  private void addStringField(ArrayList theUpdates, JTextField jtxtBox, String fieldName) {
    if (jtxtBox.getText().length() > 0)
      theUpdates.add(fieldName + " = '" + jtxtBox.getText().replaceAll("'", "''") + "'");
  }

  private void addStringField(ArrayList theUpdates, JTextArea jtxtArea, String fieldName) {
    if (jtxtArea.getText().length() > 0)
      theUpdates.add(fieldName + " = '" + jtxtArea.getText().replaceAll("'", "''") + "'");
  }

  private void addCheckBox(ArrayList theUpdates, JCheckBox jchkBox, String fieldName) {
    if (jchkBox.isEnabled() && jchkBox.isSelected())
      theUpdates.add(fieldName + " = 1");
    else if (jchkBox.isEnabled())
      theUpdates.add(fieldName + " = 0");
  }

  private void addDropDown(ArrayList theUpdates, JComboBox jcboBox, String fieldName) {
    dropdownItem theComboItem = (dropdownItem) jcboBox.getSelectedItem();
    if (theComboItem.getId() > -1)
      theUpdates.add(fieldName + " = " + theComboItem.getId());
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
    jtxtLastModified.setText(df.format(mCurrentRecord.getRecordModified()));
    jtxtRecordName.setText(mCurrentRecord.getRecordName());
    jtxtScanNum.setText(mCurrentRecord.getScanNumber());
    jtxtKeyline.setText(mCurrentRecord.getKeyline());
    jckDontDelete.setSelected(mCurrentRecord.getDontDelete());
    jckWebDisplay.setSelected(mCurrentRecord.getWebDisplay());
    jckPriorityDisplay.setSelected(mCurrentRecord.getPriorityDisplay());
    jtxtCatalogUser.setText(mCurrentRecord.getCatalogUser());
    cboStatus.setSelectedItem(new dropdownItem(mCurrentRecord.getStatusId(),
                                               mCurrentRecord.getStatus()));
    jtxtCDName.setText(mCurrentRecord.getCDName());
    jtxtUsageRules.setText(mCurrentRecord.getUsageRules());
    jtxtNotes.setText(mCurrentRecord.getNotes(mCamsDB));
    jtxtNotes.setCaretPosition(0);
    cboReleases.setSelectedItem(new dropdownItem(mCurrentRecord.getReleaseId(),
                                               mCurrentRecord.getReleases()));
    jtxtImageCompany.setText(mCurrentRecord.getImageCompany());
    jtxtKeywords.setText(mCurrentRecord.getOriginalKeywords());
    jtxtKeywords.setCaretPosition(0);
    jtxtArea.setText(mCurrentRecord.getArea());
    jtxtAdvertiser.setText(mCurrentRecord.getAdvertiser());

    mCatalogId = mCurrentRecord.getCatalogId();
    mCatalogName = mCurrentRecord.getCatalogName();

    ArrayList theCategories = mCurrentRecord.getCategories(mCamsDB, mCategoryFullPathHash);
    refreshCategories(theCategories);

    ArrayList theKeywords = mCurrentRecord.getKeywords(mCamsDB);
    refreshKeywords(theKeywords);

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

  public void refreshCategories(ArrayList theCategories) {
    CustomListModel theListModel = (CustomListModel) jListCategories.getModel();
    theListModel.clear();

    Collections.sort(theCategories);

    theListModel.mData.addAll(theCategories);
    jListCategories.updateUI();
  }

  public void refreshKeywords(ArrayList theKeywords) {
    CustomListModel theListModel = (CustomListModel) jListKeywords.getModel();
    theListModel.clear();
    theListModel.mData.addAll(theKeywords);
    jListKeywords.updateUI();
  }

  /**
   * dataChanged - See if data changed and we need to save it back to the db
   * @return boolean
   */
  private boolean dataChanged() {

    if (!jtxtRecordName.getText().equals(mCurrentRecord.getRecordName())) return true;
    if (!jtxtScanNum.getText().equals(mCurrentRecord.getScanNumber())) return true;
    if (!jtxtKeyline.getText().equals(mCurrentRecord.getKeyline())) return true;
    if (jckDontDelete.isSelected() != mCurrentRecord.getDontDelete()) return true;
    if (jckWebDisplay.isSelected() != mCurrentRecord.getWebDisplay()) return true;
    if (jckPriorityDisplay.isSelected() != mCurrentRecord.getPriorityDisplay()) return true;
    if (!jtxtCatalogUser.getText().equals(mCurrentRecord.getCatalogUser())) return true;
    dropdownItem theStatus = (dropdownItem) cboStatus.getSelectedItem();
    if (theStatus.getId() != mCurrentRecord.getStatusId()) return true;
    if (!jtxtCDName.getText().equals(mCurrentRecord.getCDName())) return true;
    if (!jtxtUsageRules.getText().equals(mCurrentRecord.getUsageRules())) return true;
    if (!jtxtNotes.getText().equals(mCurrentRecord.getNotes(mCamsDB))) return true;
    dropdownItem theReleases = (dropdownItem) cboReleases.getSelectedItem();
    if (theReleases.getId() != mCurrentRecord.getReleaseId()) return true;
    if (!jtxtImageCompany.getText().equals(mCurrentRecord.getImageCompany())) return true;
    if (!jtxtKeywords.getText().equals(mCurrentRecord.getOriginalKeywords())) return true;
    if (!jtxtArea.getText().equals(mCurrentRecord.getArea())) return true;
    if (!jtxtAdvertiser.getText().equals(mCurrentRecord.getAdvertiser())) return true;

    // Check Categories
    ArrayList theSelectedCategories = ((CustomListModel)jListCategories.getModel()).mData;
//    Collections.sort(theSelectedCategories);
    if (!mCurrentRecord.getCategories().equals(theSelectedCategories)) {
      System.out.println("Categories Changed");
      return true;
    }

    // Check Keywords
    ArrayList theSelectedKeywords = ((CustomListModel)jListKeywords.getModel()).mData;
    if (!mCurrentRecord.getKeywords().equals(theSelectedKeywords)) {
      System.out.println("Keywords Changed");
      return true;
    }

    return false;
  }


  void jbtnFirst_actionPerformed(ActionEvent e) {
    if (dataChanged()) {
      int response = JOptionPane.showConfirmDialog(this,
          "Save changes made to this record?",
          "Save Changes",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE);

      if (response == JOptionPane.CANCEL_OPTION)
        return;
      else if (response == JOptionPane.YES_OPTION) {
        updateRecord();
        mCurrentRecord.save(mCamsDB);
      }
    }

//  mTable.setRowSelectionInterval(0, 0);
    mCurrentRow = 0;
    refreshScreen();

  }

  void jbtnPrev_actionPerformed(ActionEvent e) {
    if (dataChanged()) {
      int response = JOptionPane.showConfirmDialog(this,
          "Save changes made to this record?",
          "Save Changes",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE);

      if (response == JOptionPane.CANCEL_OPTION)
        return;
      else if (response == JOptionPane.YES_OPTION) {
        updateRecord();
        mCurrentRecord.save(mCamsDB);
      }
    }

//    if (mTable.getSelectedRow() > 0)
//      mTable.setRowSelectionInterval(mTable.getSelectedRow() - 1,
//                                     mTable.getSelectedRow() - 1);
    if (mCurrentRow > 0) mCurrentRow--;

    refreshScreen();
  }

  void jbtnNext_actionPerformed(ActionEvent e) {
    if (dataChanged()) {
      int response = JOptionPane.showConfirmDialog(this,
          "Save changes made to this record?",
          "Save Changes",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE);

      if (response == JOptionPane.CANCEL_OPTION)
        return;
      else if (response == JOptionPane.YES_OPTION) {
        updateRecord();
        mCurrentRecord.save(mCamsDB);
      }
    }

//    if (mTable.getSelectedRow() < mTable.getRowCount() - 1)
//      mTable.setRowSelectionInterval(mTable.getSelectedRow() + 1,
//                                     mTable.getSelectedRow() + 1);

    if (mCurrentRow < mTable.getRowCount() - 1) mCurrentRow++;
    refreshScreen();
  }

  void jbtnLast_actionPerformed(ActionEvent e) {
    if (dataChanged()) {
      int response = JOptionPane.showConfirmDialog(this,
          "Save changes made to this record?",
          "Save Changes",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE);

      if (response == JOptionPane.CANCEL_OPTION)
        return;
      else if (response == JOptionPane.YES_OPTION) {
        updateRecord();
        mCurrentRecord.save(mCamsDB);
      }
    }

//    mTable.setRowSelectionInterval(mTable.getRowCount() - 1,
//                                   mTable.getRowCount() - 1);

    mCurrentRow = mTable.getRowCount() - 1;
    refreshScreen();
  }

  void jbtnCancel_actionPerformed(ActionEvent e) {
    refreshScreen();
  }

  void jbtnSave_actionPerformed(ActionEvent e) {
    updateRecord();
    mCurrentRecord.save(mCamsDB);
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

  void jbtnEditCategories_actionPerformed(ActionEvent e) {
    CategoryDialog catDlg = new CategoryDialog(new Frame(), mParent);
    catDlg.setTreeCatalog(mCatalogId, mCatalogName);
    CustomListModel theListModel = (CustomListModel) jListCategories.getModel();
    catDlg.setSelectedCategories(theListModel.mData, false);
    catDlg.show();
    if (!catDlg.userCancel) {
      refreshCategories(catDlg.getSelectedCategories());
    }
    catDlg.dispose();
  }

  void jbtnAddCategories_actionPerformed(ActionEvent e) {
    CategoryDialog catDlg = new CategoryDialog(new Frame(), mParent);
    catDlg.setTreeCatalog(mCatalogId, mCatalogName);
    CustomListModel theListModel = (CustomListModel) jListCategories.getModel();
    catDlg.setSelectedCategories(theListModel.mData, true);
    catDlg.show();
    if (!catDlg.userCancel) {
      refreshCategories(catDlg.getSelectedCategories());
    }
    catDlg.dispose();
  }

  void jbtnEditKeywords_actionPerformed(ActionEvent e) {
    CustomListModel theListModel = (CustomListModel) jListKeywords.getModel();

    KeywordDialog dlg = new KeywordDialog(new Frame(), mCamsDB, theListModel.mData);
//    dlg.setSelectedKeywords(theListModel.mData);
    dlg.setOriginalKeywords(jtxtKeywords.getText());
    dlg.show();
    if (!dlg.userCancel) {
      refreshKeywords(dlg.getSelectedKeywords());
    }
    dlg.dispose();
  }

  void this_windowClosing() { // WindowEvent e) {
    if ((mCurrentRecord != null) && (dataChanged())) {
      int response = JOptionPane.showConfirmDialog(this,
                                  "Save changes made to this record before closing?",
                                  "Close and Lose Changes",
                                  JOptionPane.YES_NO_CANCEL_OPTION,
                                  JOptionPane.QUESTION_MESSAGE);

      if (response == JOptionPane.YES_OPTION) {
        updateRecord();
        mCurrentRecord.save(mCamsDB);
      }
      else if (response == JOptionPane.CANCEL_OPTION)
        return;
    }
    if (!isModal())
      ((ImageLibFrame) mParent).doDetailWindowClosing(dataSaved);
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

  void jButtonOpenFolder_actionPerformed(ActionEvent e) {
    if (mCamsDB.windowsOS()) {
      // Windows
      try {
        String theFolder = jtxtFullPathWin.getText();
        if (theFolder.indexOf("\\") == -1) return;
        theFolder = theFolder.substring(0, theFolder.lastIndexOf("\\") + 1);
        Runtime r = Runtime.getRuntime();
        r.exec("Explorer \"" + theFolder + "\"");
      }
      catch (Exception ex) {}
    } else {
      // Mac
      try {
        String theFolder = jtxtFullPathMac.getText();
        if (theFolder.indexOf("/") == -1) return;
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

  /**
   * Print Detail Record
   */
  void jbtnPrint_actionPerformed(ActionEvent e) {
    htmlPrintDetails();

//    Console.println("StockDetails:Print Started");
//    PrinterJob pjob = PrinterJob.getPrinterJob();
//    PageFormat pf = pjob.defaultPage();
//    pjob.setPrintable(new PrintDetailClass(), pf);
//
//    try {
//      if (pjob.printDialog()) {
//        Console.println("StockDetails:Print Dialog Returned, calling .print()");
//        pjob.print();
//      }
//    }
//    catch (PrinterException ex) {
//      Console.println("Print Error: " + ex.getMessage());
//    }
  }

  private String getKeywordsStr() {
     ArrayList theSelectedKeywords = ((CustomListModel)jListKeywords.getModel()).mData;
     String result = "";
     for (int i=0; i < theSelectedKeywords.size(); i++) {
       result += ((IDNamePair) theSelectedKeywords.get(i)).getName() + "\n";
     }

     // Trim last \n
     if (result.length() > 0)
       result = result.substring(0, result.length() - 1);

     return result;
  }

  private String getCategoriesStr() {
     ArrayList theSelectedCategories = ((CustomListModel)jListCategories.getModel()).mData;
     String result = "";
     for (int i=0; i < theSelectedCategories.size(); i++) {
       result += ((IDNamePair) theSelectedCategories.get(i)).getName() + "\n";
     }

     // Trim last \n
     if (result.length() > 0)
       result = result.substring(0, result.length() - 1);

     return result;
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
      Console.println("PrintDetailClass:print: pageIndex = " + pageIndex);
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pf.getImageableX(), pf.getImageableY());
      drawGraphics(g2d, pf);
      return Printable.PAGE_EXISTS;
    }

    private void drawGraphics(Graphics2D graphics, PageFormat pf) {
      Console.println("StockDetailsDialog:drawGraphics: Start");
      // Set starting Y position
      setBold(graphics);
      mCurrentY = graphics.getFontMetrics().getHeight() + 1;

      mIndent = graphics.getFontMetrics().stringWidth("Imported Keywords:") + 5;

      printLine(graphics, "File Name:", jtxtFileName.getText());
      printLine(graphics, "Full Path Mac:", jtxtFullPathMac.getText());
      printLine(graphics, "Full Path Windows:", jtxtFullPathWin.getText());
      printLine(graphics, "Record Modified:", jtxtLastModified.getText());
      printLine(graphics, "Cataloging User:", jtxtCatalogUser.getText());
      printLine(graphics, "Record Name:", jtxtRecordName.getText());
      printLine(graphics, "Scan #:", jtxtScanNum.getText());
      printLine(graphics, "Keyline:", jtxtKeyline.getText());
      printLine(graphics, "Don't Delete Record:", jckDontDelete.isSelected() ? "Yes" : "No");
      printLine(graphics, "Display on Web:", jckWebDisplay.isSelected() ? "Yes" : "No");
      printLine(graphics, "Priority Display:", jckPriorityDisplay.isSelected() ? "Yes" : "No");
      printLine(graphics, "Status:", cboStatus.getSelectedItem().toString());
      printLine(graphics, "CD Name:", jtxtCDName.getText());
      printLine(graphics, "Usage Rules:", jtxtUsageRules.getText());
      printLine(graphics, "Advertiser:", jtxtAdvertiser.getText());
      printLine(graphics, "Area:", jtxtArea.getText());
      printLine(graphics, "Releases:", cboReleases.getSelectedItem().toString());
      printLine(graphics, "Image Company:", jtxtImageCompany.getText());
      printLine(graphics, "Notes:", jtxtNotes.getText());
      printLine(graphics, "Imported Keywords:", jtxtKeywords.getText());
      printLine(graphics, "Keywords:", getKeywordsStr());
      printLine(graphics, "Categories:", getCategoriesStr());

      // Draw Thumbnail
      byte[] theThumbBytes = null;
      theThumbBytes = mCurrentRecord.getThumbMedium(mCamsDB);
      if (theThumbBytes != null) {
        try {
          Image theImage = new ImageIcon(theThumbBytes).getImage();
          graphics.drawImage(theImage, (int) pf.getImageableWidth() - 140,
                             graphics.getFontMetrics().getHeight() * 5, this);
        }
        catch (Exception ex) {
          Console.println("StockDetailsDialog:print Error: " + ex.getMessage());
        }
      }

      Console.println("StockDetailsDialog:drawGraphics: Finish");
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

  }   // PrintDetailClass

  void jMenuPreview_actionPerformed(ActionEvent e) {
    mCurrentRecord.getThumbXLarge(mCamsDB); // Make sure XLarge image is loaded
    ViewImageDialog preview = new ViewImageDialog(new JFrame(), "Preview Image",
        640, mCurrentRecord, this.isModal(), mCamsDB);
    preview.setProperties(mCamsDB.getProperties());
    preview.show();
  }

  void jCheckBoxThreeState_mouseClicked(JCheckBox theCheckBox) {
    if (theCheckBox.isEnabled() && !theCheckBox.isSelected()) {
      theCheckBox.setSelected(false);
      theCheckBox.setEnabled(false);
    }
    else if (!theCheckBox.isEnabled())
      theCheckBox.setEnabled(true);
  }

  /**
   * Print a single page with the info from this form
   */
  void htmlPrintDetails() {
    String tempDir = mCamsDB.getImaging().getTempDir();
    FileWriter fw = null;
    String lf = String.valueOf('\r') + String.valueOf('\n');
    String fileName = tempDir + "/cams_print.html";
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    try {
      fw = new FileWriter(fileName, false);
      // Create Header HTML
      fw.write("<html><head><title>Clipper Asset Management</title>" + lf);
      fw.write("<STYLE TYPE=\"text/css\">" + lf);
      fw.write("TD { FONT-FAMILY: Arial; FONT-SIZE: 12px; }" + lf);
      fw.write("DIV.page { page-break-after: always; }" + lf);
      fw.write("</STYLE>" + lf + "</head><body>" + lf);
    }
    catch (Exception ex) {
      Console.println("Error opening html file for printing: " + ex.getMessage());
      return;
    }

    try {
      fw.write("<table border=\"0\">");
      fw.write(htmlPrintRow("File Name:", mCurrentRecord.getFileName()));
      fw.write(htmlPrintRow("Full Path Mac:", mCurrentRecord.getFullPathMac()));
      fw.write(htmlPrintRow("Full Path Win:", mCurrentRecord.getFullPathWin()));
      fw.write(htmlPrintRow("Record Modified:", df.format(mCurrentRecord.getRecordModified())));
      fw.write(htmlPrintRow("Cataloging User:", mCurrentRecord.getCatalogUser()));
      fw.write(htmlPrintRow("Record Name:", mCurrentRecord.getRecordName()));
      fw.write(htmlPrintRow("Scan #:", mCurrentRecord.getScanNumber()));
      fw.write(htmlPrintRow("Keyline:", mCurrentRecord.getKeyline()));
      fw.write(htmlPrintRow("Don't Delete:", mCurrentRecord.getDontDelete()));
      fw.write(htmlPrintRow("Display on Web:", mCurrentRecord.getWebDisplay()));
      fw.write(htmlPrintRow("Priority Display:", mCurrentRecord.getPriorityDisplay()));
      fw.write(htmlPrintRow("Status:", mCurrentRecord.getStatus()));
      fw.write(htmlPrintRow("CD Name:", mCurrentRecord.getCDName()));
      fw.write(htmlPrintRow("Usage Rules:", mCurrentRecord.getUsageRules()));
      fw.write(htmlPrintRow("Advertiser:", mCurrentRecord.getAdvertiser()));
      fw.write(htmlPrintRow("Area:", mCurrentRecord.getArea()));
      fw.write(htmlPrintRow("Releases:", mCurrentRecord.getReleases()));
      fw.write(htmlPrintRow("Image Company:", mCurrentRecord.getImageCompany()));
      fw.write(htmlPrintRow("Notes:", mCurrentRecord.getNotes()));
      fw.write(htmlPrintRow("Keywords (Original):", mCurrentRecord.getOriginalKeywords()));
      fw.write(htmlPrintRow("Keywords:", mCurrentRecord.getKeywords()));
      fw.write(htmlPrintRow("Categories:", mCurrentRecord.getCategories()));

      String jpgFileName = tempDir + "/thumbMed" + mCurrentRecord.getRecordId() + ".jpg";
      ((ImageLibFrame) mParent).writeJPGFile(mCurrentRecord.getThumbMedium(mCamsDB), jpgFileName);
      fw.write("<tr><td colspan=\"2\"><img src=\"" + new File(jpgFileName).getName() + "\"></td></tr>");

      fw.write("</body></html>");
      fw.close();

      ((ImageLibFrame) mParent).viewHTMLFile(fileName);
    }
    catch (Exception ex) {
      Console.println("Error writing to html file for printing: " +
                      ex.getMessage());
    }

  }

  String htmlPrintRow(String header, String data) {
    String data2 = data;
    data2 = data2.replaceAll("\n", "<br>");

    return "<tr><td align=\"left\" valign=\"top\" nowrap><strong>" + header
        + "&nbsp;</strong></td><td align=\"left\" valign=\"top\">" + data2 + "</td></tr>";
  }

  String htmlPrintRow(String header, boolean checked) {
    String data = checked ? "Yes" : "No";
    return htmlPrintRow(header, data);
  }

  String htmlPrintRow(String header, ArrayList theList) {
    String data = "";

    for (int i=0; i < theList.size(); i++)
      data += theList.get(i).toString() + "<br>";

    return htmlPrintRow(header, data);
  }

}

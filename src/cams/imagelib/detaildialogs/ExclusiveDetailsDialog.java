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
import java.io.*;
import java.io.OutputStream;
import java.io.FileOutputStream;

import cams.imagelib.*;
import cams.database.*;
import cams.console.*;


public class ExclusiveDetailsDialog extends JDialog {
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
  private boolean mShowPNum = false;
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
  JLabel jLabel112 = new JLabel();
  JTextField jtxtMFRName = new JTextField();
  JLabel jLabel15 = new JLabel();
  JCheckBox jckExclusiveToAdvert = new JCheckBox();
  JLabel jLabel113 = new JLabel();
  JCheckBox jckOriginalOnFile = new JCheckBox();
  JLabel jLabel114 = new JLabel();
  JCheckBox jckModelRelease = new JCheckBox();
  JTextArea jtxtUsageRules = new JTextArea();
  JScrollPane jScrollPane5 = new JScrollPane();
  JComboBox cboReleases = new JComboBox();
  JLabel jlblPNumber = new JLabel();
  JScrollPane jScrollPNum = new JScrollPane();
  JTextArea jtxtPNumber = new JTextArea();
  JLabel jLabel19 = new JLabel();
  JCheckBox jckWebDisplay = new JCheckBox();
  JButton jbtnPrint = new JButton();
  JButton jButtonOpenAsset = new JButton();

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
  JButton jbtnAddCategories = new JButton();
  JMenuItem jMenuPreview = new JMenuItem();
  JButton jButtonOpenFolder = new JButton();

  public ExclusiveDetailsDialog(Frame frame, CamsDB theCamsDB, JTable theTable,
                                HashMap categoryPathHash, int currentRow,
                                int cascadeIndex, boolean isModal, boolean showPNum) {
    super(new Frame(), "Asset Details (Exclusive)", isModal);
    try {
      mParent = (JFrame) frame;
      mCamsDB = theCamsDB;
      mTable = theTable;
      mCategoryFullPathHash = categoryPathHash;
      mCurrentRow = currentRow;
      mShowPNum = showPNum;
      jbInit();

      loadStatus(showPNum);
      loadReleases();

      if (theTable == null) {
      // Importing Records, Don't Show Toolbar
        jToolBar.setVisible(false);
        jlblImportedKeywords.setEnabled(false);
        jtxtKeywords.setEnabled(false);
        jtxtRecordName.setEnabled(false);
        jtxtScanNum.setEnabled(false);
        jButtonOpenAsset.setVisible(false);
        jButtonOpenFolder.setVisible(false);
        this.setJMenuBar(null);
      }
      else {
        // Editing Rows from Table
        jbtnExit.setVisible(false);
        jbtnContinue.setVisible(false);
      }

      if (showPNum) {
        setTitle("Asset Details (P Number)");
        jtxtRecordName.setEnabled(false);
        jtxtRecordName.setEditable(false);
        jtxtCDName.setEnabled(false);
        jtxtCDName.setEditable(false);
      }
      else {
        jlblPNumber.setVisible(false);
        jScrollPNum.setVisible(false);
      }

      pack();
      setSize(getSize().width + 10, getSize().height + 10);

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      if (getSize().height > screenSize.height) {
        setSize(getSize().width + jScrollMain.getVerticalScrollBar().getWidth() + 10,
                screenSize.height - 30);
      }

      if (isModal) {
        //Center the window
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
//        setLocation( (screenSize.width - frameSize.width) / 2,
//                    (screenSize.height - frameSize.height) / 2);
        setLocation( (screenSize.width - frameSize.width) / 2, 10);
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
   * ExclusiveDetailsDialog in Batch Edit Mode
   *
   * @param frame Frame
   * @param theCamsDB CamsDB
   * @param theTable JTable
   * @param categoryPathHash HashMap
   * @param selectedRows int[]
   */
  public ExclusiveDetailsDialog(Frame frame, CamsDB theCamsDB, JTable theTable,
                            HashMap categoryPathHash, int[] selectedRows, boolean showPNum) {
    super(new Frame(), "Asset Details (Exclusive)", true);
    try {
      mParent = (JFrame) frame;
      mCamsDB = theCamsDB;
      mTable = theTable;
      mCategoryFullPathHash = categoryPathHash;
      mSelectedRows = selectedRows;
      mShowPNum = showPNum;
      jbInit();

      loadStatus(showPNum);
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

      jckExclusiveToAdvert.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          jCheckBoxThreeState_mouseClicked(jckExclusiveToAdvert);
        }
      });
      jckExclusiveToAdvert.setEnabled(false);

      jckModelRelease.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          jCheckBoxThreeState_mouseClicked(jckModelRelease);
        }
      });
      jckModelRelease.setEnabled(false);

      jckOriginalOnFile.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          jCheckBoxThreeState_mouseClicked(jckOriginalOnFile);
        }
      });
      jckOriginalOnFile.setEnabled(false);

      jButtonOpenAsset.setVisible(false);

      mCurrentRecord = ((ImageLibFrame) mParent).getImageRecordByRow(mSelectedRows[0]);
      mCatalogId = mCurrentRecord.getCatalogId();
      mCatalogName = mCurrentRecord.getCatalogName();
      mCurrentRecord = null;

      if (showPNum)
        setTitle("Asset Details (P Number)");
      else {
        jlblPNumber.setVisible(false);
        jScrollPNum.setVisible(false);
      }

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
    jbtnSave.setIcon(imageSave);
    jbtnSave.setMnemonic(KeyEvent.VK_S);
    jbtnSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnSave_actionPerformed(e);
      }
    });
    jbtnCancel.setMaximumSize(new Dimension(23, 23));
    jbtnCancel.setMinimumSize(new Dimension(23, 23));
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
    jbtnPrev.setMnemonic(KeyEvent.VK_LEFT);
    jbtnPrev.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnPrev_actionPerformed(e);
      }
    });
    jbtnNext.setMaximumSize(new Dimension(23, 23));
    jbtnNext.setMinimumSize(new Dimension(23, 23));
    jbtnNext.setIcon(imageNext);
    jbtnNext.setMnemonic(KeyEvent.VK_RIGHT);
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
    jLabel112.setText("MFR. Name :");
    jLabel112.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel112.setDebugGraphicsOptions(0);
    jtxtMFRName.setText("");
    jtxtMFRName.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel15.setDebugGraphicsOptions(0);
    jLabel15.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel15.setText("Excl. to Orig. Advert. :");
    jckExclusiveToAdvert.setText("");
    jLabel113.setText("Original on File :");
    jLabel113.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel113.setDebugGraphicsOptions(0);
    jckOriginalOnFile.setText("");
    jLabel114.setDebugGraphicsOptions(0);
    jLabel114.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel114.setText("Model Rel. on File :");
    jckModelRelease.setText("");
    jtxtUsageRules.setText("");
    jtxtUsageRules.setLineWrap(true);
    jtxtUsageRules.setWrapStyleWord(true);
    jtxtUsageRules.setFont(new java.awt.Font("Dialog", 0, 11));
    jScrollPane5.setBorder(BorderFactory.createLoweredBevelBorder());
    if (mCamsDB.windowsOS())
      cboReleases.setBorder(BorderFactory.createLoweredBevelBorder());
    jlblPNumber.setText("P # :");
    jlblPNumber.setFont(new java.awt.Font("Dialog", 1, 11));
    jlblPNumber.setDebugGraphicsOptions(0);
    jlblPNumber.setRequestFocusEnabled(true);
    jScrollPNum.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPNum.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtPNumber.setFont(new java.awt.Font("Dialog", 0, 11));
    jtxtPNumber.setText("");
    jtxtPNumber.setLineWrap(true);
    jtxtPNumber.setWrapStyleWord(true);
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
    jbtnPrint.setMnemonic(KeyEvent.VK_P);
    jbtnPrint.setMaximumSize(new Dimension(23, 23));

    jButtonOpenAsset.setFont(new java.awt.Font("Dialog", 0, 10));
    jButtonOpenAsset.setText("Open");
    jButtonOpenAsset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonOpenAsset_actionPerformed(e);
      }
    });
    jbtnAddCategories.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnAddCategories_actionPerformed(e);
      }
    });
    jbtnAddCategories.setText("Add");
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
    jButtonOpenFolder.setText("Browse");
    jButtonOpenFolder.setFont(new java.awt.Font("Dialog", 0, 10));
    jScrollMain.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollMain.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jpnlContent.add(jLabel7, new XYConstraints(6, 62, 108, -1));
    jpnlContent.add(jLabel8, new XYConstraints(6, 36, 108, -1));
    jpnlContent.add(jLabel9, new XYConstraints(6, 10, 108, -1));
    jpnlContent.add(jtxtFileName, new XYConstraints(107, 7, 224, -1));
    jpnlContent.add(jtxtFullPathMac, new XYConstraints(107, 33, 224, -1));
    jpnlContent.add(jtxtFullPathWin, new XYConstraints(107, 59, 224, -1));
    jpnlContent.add(jLabel10, new XYConstraints(6, 87, 108, -1));
    jpnlContent.add(jtxtLastModified, new XYConstraints(107, 85, 224, -1));
    jpnlContent.add(jtxtRecordName, new XYConstraints(107, 137, 224, -1));
    jpnlContent.add(jLabel1, new XYConstraints(6, 140, 108, -1));
    jpnlContent.add(jLabel2, new XYConstraints(6, 166, 108, -1));
    jpnlContent.add(jLabel3, new XYConstraints(6, 192, 108, -1));
    jpnlContent.add(jLabel4, new XYConstraints(6, 216, 127, -1));
    jpnlContent.add(jtxtScanNum, new XYConstraints(107, 163, 224, -1));
    jpnlContent.add(jtxtKeyline, new XYConstraints(107, 189, 224, -1));
    jpnlContent.add(jtxtCatalogUser, new XYConstraints(107, 111, 224, -1));
    jpnlContent.add(jLabel5, new XYConstraints(6, 114, 108, -1));
    jpnlContent.add(jLabel6, new XYConstraints(6, 239, 108, -1));
    jpnlContent.add(jLabel11, new XYConstraints(6, 263, 108, -1));
    jpnlContent.add(jtxtCDName, new XYConstraints(107, 262, 224, -1));
    jpnlContent.add(cboStatus, new XYConstraints(107, 235, 224, -1));
    jpnlContent.add(jLabel112, new XYConstraints(7, 290, 99, -1));
    jpnlContent.add(jtxtMFRName, new XYConstraints(107, 288, 224, -1));
    jpnlContent.add(jLabel12, new XYConstraints(6, 342, 108, -1));
    jpnlContent.add(jLabel15, new XYConstraints(7, 316, 127, -1));
    jpnlContent.add(jtxtAdvertiser, new XYConstraints(107, 393, 224, -1));
    jpnlContent.add(jLabel111, new XYConstraints(6, 421, 108, -1));
    jpnlContent.add(jLabel110, new XYConstraints(6, 395, 108, -1));
    jpnlContent.add(jLabel14, new XYConstraints(6, 446, 108, -1));
    jpnlContent.add(jtxtArea, new XYConstraints(107, 419, 224, -1));

    jScrollMain.setBorder(null);
    jScrollMain.getViewport().add(panelMain);
    getContentPane().add(jScrollMain);
//  getContentPane().add(panelMain);
    jToolBar.setFloatable(false);
    jToolBar.add(jbtnSave, null);
    jToolBar.add(jbtnCancel, null);
    jToolBar.addSeparator();
    jToolBar.add(jbtnPrint, null);
    jToolBar.addSeparator();
    jToolBar.add(jbtnFirst, null);
    jToolBar.add(jbtnPrev, null);
    jToolBar.add(jbtnNext, null);
    jToolBar.add(jbtnLast, null);
    panelMain.add(jpnlContent,  BorderLayout.CENTER);
    panelMain.add(jToolBar, BorderLayout.NORTH);
    jpnlContent.add(jScrollPane5,  new XYConstraints(107, 339, 224, 46));
    jpnlContent.add(cboReleases,          new XYConstraints(107, 445, 224, 21));
    jpnlContent.add(jLabel19,   new XYConstraints(170, 216, 102, -1));
    jpnlContent.add(jButtonOpenFolder,  new XYConstraints(336, 56, 68, 22));
    jpnlContent.add(jButtonOpenAsset, new XYConstraints(336, 33, 68, 22));
    jpnlContent.add(jLabel113, new XYConstraints(161, 315, 104, -1));
    jpnlContent.add(jLabel114, new XYConstraints(289, 315, 109, -1));
    jpnlContent.add(jLabel18,  new XYConstraints(380, 387, 88, -1));
    jpnlContent.add(jScrollPane3,  new XYConstraints(453, 238, 165, 114));
    jpnlContent.add(jScrollPane2, new XYConstraints(453, 97, 165, 103));
    jpnlContent.add(jScrollPane4,  new XYConstraints(453, 27, 165, 62));
    jpnlContent.add(jbtnEditCategories,  new XYConstraints(467, 354, 62, 26));
    jpnlContent.add(jbtnAddCategories, new XYConstraints(545, 354, 62, 26));
    jpnlContent.add(jbtnEditKeywords, new XYConstraints(475, 203, 127, 26));
    jpnlContent.add(jLabel17,  new XYConstraints(380, 238, 84, -1));
    jpnlContent.add(jLabel16, new XYConstraints(380, 97, 83, -1));
    jpnlContent.add(jlblImportedKeywords, new XYConstraints(380, 10, 125, -1));
    jpnlContent.add(jlblPreview,                          new XYConstraints(471, 387, 130, 130));
    jpnlContent.add(jbtnExit, new XYConstraints(496, 522, 91, 28));
    jpnlContent.add(jbtnContinue, new XYConstraints(380, 522, 91, 28));
    jpnlContent.add(jLabel13, new XYConstraints(6, 473, 108, -1));
    jpnlContent.add(jScrollPane1,     new XYConstraints(107, 472, 225, 102));
    jpnlContent.add(jlblPNumber,              new XYConstraints(6, 581, 108, -1));
    jpnlContent.add(jScrollPNum,    new XYConstraints(107, 581, 511, 141));
    jpnlContent.add(jckModelRelease,  new XYConstraints(395, 310, -1, -1));
    jpnlContent.add(jckOriginalOnFile, new XYConstraints(255, 310, -1, -1));
    jpnlContent.add(jckExclusiveToAdvert, new XYConstraints(127, 311, -1, -1));
    jpnlContent.add(jckDontDelete,  new XYConstraints(127, 211, -1, -1));
    jpnlContent.add(jckWebDisplay, new XYConstraints(266, 211, -1, -1));
    jScrollPNum.getViewport().add(jtxtPNumber, null);
    jScrollPane1.getViewport().add(jtxtNotes, null);
    jScrollPane4.getViewport().add(jtxtKeywords, null);
    jScrollPane2.getViewport().add(jListKeywords, null);
    jScrollPane3.getViewport().add(jListCategories, null);
    jScrollPane5.getViewport().add(jtxtUsageRules, null);

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
    if (!mShowPNum)
      mFormFields.add(jtxtRecordName);
    mFormFields.add(jtxtScanNum);
    mFormFields.add(jtxtKeyline);
    mFormFields.add(jckDontDelete);
    mFormFields.add(jckWebDisplay);
    mFormFields.add(cboStatus);
    if (!mShowPNum)
      mFormFields.add(jtxtCDName);
    mFormFields.add(jtxtMFRName);
    mFormFields.add(jckExclusiveToAdvert);
    mFormFields.add(jckOriginalOnFile);
    mFormFields.add(jckModelRelease);
    mFormFields.add(jtxtUsageRules);
    mFormFields.add(jtxtAdvertiser);
    mFormFields.add(jtxtArea);
    mFormFields.add(cboReleases);
    mFormFields.add(jtxtNotes);
    if (mShowPNum)
      mFormFields.add(jtxtPNumber);
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

  private void loadStatus(boolean showPNum) {
    ResultSet rs = null;

    DefaultComboBoxModel theModel = (DefaultComboBoxModel) cboStatus.getModel();
    theModel.removeAllElements();

    theModel.addElement(new dropdownItem(-1, ""));

    try {
      String sql;
      if (showPNum) // Show both Exclusive and Stock
//      sql = "SELECT DISTINCT status_id, status + ' (' + UPPER(LEFT(catalog_type, 2)) + ')' AS Status " +
        sql = "SELECT DISTINCT status_id, status FROM ImageLibStatus " +
//            "WHERE Upper(catalog_type) like 'EXCLUSIVE' " +
              "ORDER BY status";
      else // Only show Exclusive Status's
        sql = "SELECT DISTINCT status_id, status FROM ImageLibStatus " +
              "WHERE Upper(catalog_type) like 'EXCLUSIVE' " +
              "ORDER BY status";

      rs = mCamsDB.query(sql);
      while (rs.next()) {
        // adSizes.add(new dropdownItem(rs.getInt("AdvertSizeID"), rs.getString("AdvertDescription")));
        theModel.addElement(new dropdownItem(rs.getInt("status_id"), rs.getString("status")));
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close(); rs = null;
    }
    catch (Exception ex) {
      Console.println("ExclusiveDetailsDialog:loadStatus: " + ex.getMessage());
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

    mCurrentRecord.setOriginalKeywords(jtxtKeywords.getText());
    mCurrentRecord.setArea(jtxtArea.getText());
    mCurrentRecord.setAdvertiser(jtxtAdvertiser.getText());

    mCurrentRecord.setMFRName(jtxtMFRName.getText());
    mCurrentRecord.setExclusive(jckExclusiveToAdvert.isSelected());
    mCurrentRecord.setOrigOnFile(jckOriginalOnFile.isSelected());
    mCurrentRecord.setModelRelease(jckModelRelease.isSelected());
    mCurrentRecord.setPNumber(jtxtPNumber.getText());

    // Update Categories
    ArrayList theSelectedCategories = ((CustomListModel)jListCategories.getModel()).mData;
    // Collections.sort(theSelectedCategories);
    mCurrentRecord.setCategories((ArrayList)theSelectedCategories.clone());

    // Update Keywords
    ArrayList theSelectedKeywords = ((CustomListModel)jListKeywords.getModel()).mData;
    // Collections.sort(theSelectedKeywords);
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
    addStringField(theUpdates, jtxtArea, "Area");
    addStringField(theUpdates, jtxtAdvertiser, "Advertiser");
    addStringField(theUpdates, jtxtMFRName, "MFRName");
    addStringField(theUpdates, jtxtPNumber, "PNumber");

    addCheckBox(theUpdates, jckDontDelete, "DontDelete");
    addCheckBox(theUpdates, jckWebDisplay, "WebDisplay");
    addCheckBox(theUpdates, jckExclusiveToAdvert, "Exclusive");
    addCheckBox(theUpdates, jckOriginalOnFile, "OrigOnFile");
    addCheckBox(theUpdates, jckModelRelease, "ModelRelease");

    addDropDown(theUpdates, cboStatus, "Status_id");
    addDropDown(theUpdates, cboReleases, "Release_id");

    // Update Categories
    ArrayList theSelectedCategories = ((CustomListModel)jListCategories.getModel()).mData;

    // Update Keywords
    ArrayList theSelectedKeywords = ((CustomListModel)jListKeywords.getModel()).mData;

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
    jtxtCatalogUser.setText(mCurrentRecord.getCatalogUser());
    cboStatus.setSelectedItem(new dropdownItem(mCurrentRecord.getStatusId(),
                                               mCurrentRecord.getStatus()));
    jtxtCDName.setText(mCurrentRecord.getCDName());
    jtxtUsageRules.setText(mCurrentRecord.getUsageRules());
    jtxtNotes.setText(mCurrentRecord.getNotes(mCamsDB));
    jtxtNotes.setCaretPosition(0);
    cboReleases.setSelectedItem(new dropdownItem(mCurrentRecord.getReleaseId(),
                                               mCurrentRecord.getReleases()));
    jtxtKeywords.setText(mCurrentRecord.getOriginalKeywords());
    jtxtKeywords.setCaretPosition(0);
    jtxtArea.setText(mCurrentRecord.getArea());
    jtxtAdvertiser.setText(mCurrentRecord.getAdvertiser());

    jtxtMFRName.setText(mCurrentRecord.getMFRName());
    jckExclusiveToAdvert.setSelected(mCurrentRecord.getExclusive());
    jckOriginalOnFile.setSelected(mCurrentRecord.getOrigOnFile());
    jckModelRelease.setSelected(mCurrentRecord.getModelRelease());

    jtxtPNumber.setText(mCurrentRecord.getPNumber());
    jtxtPNumber.setCaretPosition(0);

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

    if (!jtxtRecordName.getText().equals(mCurrentRecord.getRecordName())) {
      Console.println("dataChanged: RecordName");
      return true;
    }
    if (!jtxtScanNum.getText().equals(mCurrentRecord.getScanNumber())) {
      Console.println("dataChanged: ScanNumber");
      return true;
    }
    if (!jtxtKeyline.getText().equals(mCurrentRecord.getKeyline())) {
      Console.println("dataChanged: KeyLine");
      return true;
    }
    if (jckDontDelete.isSelected() != mCurrentRecord.getDontDelete()) {
      Console.println("dataChanged: DontDelete");
      return true;
    }
    if (jckWebDisplay.isSelected() != mCurrentRecord.getWebDisplay()) {
      Console.println("dataChanged: WebDisplay");
      return true;
    }
    if (!jtxtCatalogUser.getText().equals(mCurrentRecord.getCatalogUser())) {
      Console.println("dataChanged: CatalogUser");
      return true;
    }

    dropdownItem theStatus = (dropdownItem) cboStatus.getSelectedItem();
    if (theStatus.getId() != mCurrentRecord.getStatusId()) {
      Console.println("dataChanged: StatusId");
      return true;
    }

    if (!jtxtCDName.getText().equals(mCurrentRecord.getCDName())) {
      Console.println("dataChanged: CDName");
      return true;
    }
    if (!jtxtUsageRules.getText().equals(mCurrentRecord.getUsageRules())) {
      Console.println("dataChanged: UsageRules");
      return true;
    }
    if (!jtxtNotes.getText().equals(mCurrentRecord.getNotes(mCamsDB))) {
      Console.println("dataChanged: Notes");
      return true;
    }

    dropdownItem theReleases = (dropdownItem) cboReleases.getSelectedItem();
    if (theReleases.getId() != mCurrentRecord.getReleaseId()) {
      Console.println("dataChanged: ReleaseId");
      return true;
    }

    if (!jtxtKeywords.getText().equals(mCurrentRecord.getOriginalKeywords())) {
      Console.println("dataChanged: OriginalKeywords");
      return true;
    }
    if (!jtxtArea.getText().equals(mCurrentRecord.getArea())) {
      Console.println("dataChanged: Area");
      return true;
    }
    if (!jtxtAdvertiser.getText().equals(mCurrentRecord.getAdvertiser())) {
      Console.println("dataChanged: Advertiser");
      return true;
    }

    if (!jtxtMFRName.getText().equals(mCurrentRecord.getMFRName())) {
      Console.println("dataChanged: MFRName");
      return true;
    }
    if (jckExclusiveToAdvert.isSelected() != mCurrentRecord.getExclusive()) {
      Console.println("dataChanged: Exclusive");
      return true;
    }
    if (jckOriginalOnFile.isSelected() != mCurrentRecord.getOrigOnFile()) {
      Console.println("dataChanged: OrigOnFile");
      return true;
    }
    if (jckModelRelease.isSelected() != mCurrentRecord.getModelRelease()) {
      Console.println("dataChanged: ModelRelease");
      return true;
    }
    if (!jtxtPNumber.getText().equals(mCurrentRecord.getPNumber())) {
      Console.println("dataChanged: PNumber");
      return true;
    }

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

//    mTable.setRowSelectionInterval(0, 0);
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
    dlg.show();
    if (!dlg.userCancel) {
      refreshKeywords(dlg.getSelectedKeywords());
    }
    dlg.dispose();
  }

  void this_windowClosing() {
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

  /**
   * Print Detail Record
   */
  void jbtnPrint_actionPerformed(ActionEvent e) {
    htmlPrintDetails();

//    PrinterJob pjob = PrinterJob.getPrinterJob();
//    PageFormat pf = pjob.defaultPage();
//    pjob.setPrintable(new PrintDetailClass(), pf);
//
//    try {
//      if (pjob.printDialog()) {
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
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pf.getImageableX(), pf.getImageableY());
      drawGraphics(g2d, pf);
      return Printable.PAGE_EXISTS;
    }

    private void drawGraphics(Graphics2D graphics, PageFormat pf) {

      // Set starting Y position
      setBold(graphics);
      mCurrentY = graphics.getFontMetrics().getHeight() + 1;

      mIndent = graphics.getFontMetrics().stringWidth("Model Release on File:") + 5;

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
      printLine(graphics, "Status:", cboStatus.getSelectedItem().toString());
      printLine(graphics, "CD Name:", jtxtCDName.getText());
      printLine(graphics, "MFR. Name:", jtxtMFRName.getText());
      printLine(graphics, "Excl. to Orig. Advert.:", jckExclusiveToAdvert.isSelected() ? "Yes" : "No");
      printLine(graphics, "Usage Rules:", jtxtUsageRules.getText());
      printLine(graphics, "Advertiser:", jtxtAdvertiser.getText());
      printLine(graphics, "Area:", jtxtArea.getText());
      printLine(graphics, "Releases:", cboReleases.getSelectedItem().toString());
      printLine(graphics, "Original on File:", jckOriginalOnFile.isSelected() ? "Yes" : "No");
      printLine(graphics, "Model Release on File:", jckModelRelease.isSelected() ? "Yes" : "No");
      printLine(graphics, "Notes:", jtxtNotes.getText());
      if (jlblPNumber.isVisible()) {
        printLine(graphics, "P #:", jtxtPNumber.getText())    ;
      }
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
          Console.println("ExclusiveDetailsDialog:print Error: " + ex.getMessage());
        }
      }
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

  void jCheckBoxThreeState_mouseClicked(JCheckBox theCheckBox) {
    if (theCheckBox.isEnabled() && !theCheckBox.isSelected()) {
      theCheckBox.setSelected(false);
      theCheckBox.setEnabled(false);
    }
    else if (!theCheckBox.isEnabled())
      theCheckBox.setEnabled(true);
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
      if (!mShowPNum)
        fw.write(htmlPrintRow("Record Name:", mCurrentRecord.getRecordName()));
      fw.write(htmlPrintRow("Scan #:", mCurrentRecord.getScanNumber()));
      fw.write(htmlPrintRow("Keyline:", mCurrentRecord.getKeyline()));
      fw.write(htmlPrintRow("Don't Delete:", mCurrentRecord.getDontDelete()));
      fw.write(htmlPrintRow("Display on Web:", mCurrentRecord.getWebDisplay()));
      fw.write(htmlPrintRow("Status:", mCurrentRecord.getStatus()));
      if (!mShowPNum)
        fw.write(htmlPrintRow("CD Name:", mCurrentRecord.getCDName()));
      fw.write(htmlPrintRow("MFR. Name:", mCurrentRecord.getMFRName()));
      fw.write(htmlPrintRow("Excl. to Orig. Advert:", mCurrentRecord.getExclusive()));
      fw.write(htmlPrintRow("Original on File:", mCurrentRecord.getOrigOnFile()));
      fw.write(htmlPrintRow("Model Rel. on File:", mCurrentRecord.getModelRelease()));
      fw.write(htmlPrintRow("Usage Rules:", mCurrentRecord.getUsageRules()));
      fw.write(htmlPrintRow("Advertiser:", mCurrentRecord.getAdvertiser()));
      fw.write(htmlPrintRow("Area:", mCurrentRecord.getArea()));
      fw.write(htmlPrintRow("Releases:", mCurrentRecord.getReleases()));
      fw.write(htmlPrintRow("Notes:", mCurrentRecord.getNotes()));
      if (mShowPNum)
        fw.write(htmlPrintRow("P #:", mCurrentRecord.getPNumber()));
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

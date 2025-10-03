package cams.imagelib;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

// import cams.console.*;
import cams.database.*;
import cams.imagelib.detaildialogs.*;
import cams.imagelib.importrecord.*;
import cams.imagelib.search.*;
import cams.imaging.*;
import cams.memory.*;
import com.borland.jbcl.layout.*;


/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ImageLibFrame extends JFrame {
  private String mVersion = "1.26 Jan 27, 2006";
  CamsDB mCamsDB = null;
  ArrayList mCatalogList = null;
  ArrayList mCategoryList = null;
  HashMap mCategoryPathHash = null;
  HashMap mBatchesHash = new HashMap();
  public int mThumbXLarge = 640;
  public int mThumbLarge = 256;
  public int mThumbMedium = 130;
  public int mThumbSmall = 65;
  public String mAcrobatPath = null;
  SearchDialog mSearchDialog = null;
  private int mLastDividerLocation = 200;
  boolean mShowSearchResults = false;
  String mSearchOrder = "";
  private javax.swing.Timer mRefreshTimer = null;
  private int mNumDetailDialogsOpen = 0;
  private boolean mAnyDetailDialogsSaved = false;

//  private int mImagesPerPageSmall = 100;
//  private int mImagesPerPageMedium = 50;
//  private int mImagesPerPageLarge = 25;
  private int mGalleryStartIndex = 0;

  private ArrayList mAssetsToCopy = new ArrayList();
  private boolean mCutAssets = false; // False = Copy, True = Cut

  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JToolBar jToolBar = new JToolBar();
  JButton jbtnCut = new JButton();
  JButton jbtnCopy = new JButton();
  JButton jbtnPaste = new JButton();
  ImageIcon imageCut;
  ImageIcon imageCopy;
  ImageIcon imagePaste;
  ImageIcon imageDetail;
  ImageIcon imageSmall;
  ImageIcon imageMedium;
  ImageIcon imageLarge;
  ImageIcon imageSearch;
  ImageIcon imageSearchPlus;
  ImageIcon imageXSearch;
  ImageIcon imagePrint;
  JPopupMenu assetPopup = new JPopupMenu();
  JPopupMenu catalogPopup = new JPopupMenu();

  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane jSplitMain = new JSplitPane();
  JPanel jpanelStatus = new JPanel();
  JLabel jlblStatus = new JLabel();
  JScrollPane jScrollTree = new JScrollPane();
  DefaultMutableTreeNode treeTop =
      new DefaultMutableTreeNode("Catalogs");
  DefaultMutableTreeNode batchesNode =
      new DefaultMutableTreeNode("Import Batches");
  public JTree jTreeFolders = new JTree(treeTop);
  JScrollPane jScrollDetails = new JScrollPane();
  ImageLibTableModel theTableModel = new ImageLibTableModel();
  cams.imagelib.TableSorter theTableSorter = new TableSorter(theTableModel);
  JTable jtableDetails = new JTable(theTableSorter);
  JMenu jMenuCatalog = new JMenu();
  JMenuItem jMenuImportRecords = new JMenuItem();
  JMenu jMenuEdit = new JMenu();
  JMenuItem jMenuCut = new JMenuItem();
  JMenuItem jMenuCopy = new JMenuItem();
  JMenuItem jMenuPaste = new JMenuItem();
  JMenuItem jMenuAddCat = new JMenuItem();
  JMenuItem jMenuDeleteCat = new JMenuItem();
  JMenu jMenuView = new JMenu();
  JMenuItem jMenuViewDetail = new JMenuItem();
  JMenuItem jMenuViewSmall = new JMenuItem();
  JMenuItem jMenuViewMedium = new JMenuItem();
  JMenuItem jMenuViewLarge = new JMenuItem();
  JToggleButton jbtnThumbLarge = new JToggleButton();
  JToggleButton jbtnThumbMedium = new JToggleButton();
  JToggleButton jbtnThumbSmall = new JToggleButton();
  JToggleButton jbtnDetails = new JToggleButton();
  JMenuItem jMenuEditProperties = new JMenuItem();
  JMenuItem jMenuBatchEdit = new JMenuItem();
  JMenuItem jMenuViewAsset = new JMenuItem();
  JMenuItem jMenuRemoveAsset = new JMenuItem();
  JScrollPane jScrollGallery = new JScrollPane();
  JPanel jPanelGallery = new JPanel();
  ButtonGroup btnGroupGallery = new ButtonGroup();
  JMenuItem jMenuAddCategory = new JMenuItem();
  JMenuItem jMenuDeleteCategory = new JMenuItem();
  JMenuItem jMenuRenameCategory = new JMenuItem();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton jbtnSearch = new JButton();
  JButton jbtnSearchPlus = new JButton();
  JButton jbtnPrint = new JButton();
  JMenuItem jMenuSearch = new JMenuItem();
  JMenuItem jMenuPreviewAsset = new JMenuItem();
  JButton jbtnXSearch = new JButton();
  JButton jbtnPrevious = new JButton();
  JButton jbtnNext = new JButton();
  JPanel jPanelStatusRight = new JPanel();
  FlowLayout flowLayout3 = new FlowLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanelStatusLeft = new JPanel();
  JMenu jMenuOptions = new JMenu();
  JMenuItem jmnuShowBatches = new JMenuItem();
  JMenuItem jMenuCutAsset = new JMenuItem();
  JMenuItem jMenuCopyAsset = new JMenuItem();
  JProgressBar jProgress = new JProgressBar();
  BorderLayout borderLayout3 = new BorderLayout();

  //Construct the frame
  public ImageLibFrame(CamsDB camsDB) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    setDatabase(camsDB);
    try {
      jbInit();
      configCatalogTree();

      jlblStatus.setText("");

      mRefreshTimer = new javax.swing.Timer(15000, refreshMemory);
      mRefreshTimer.start();

      // Clear old records from ImageLibSearch
      String sql = "DELETE FROM ImageLibSearch WHERE (search_date < GETDATE() - 2) " +
          "AND (search_user LIKE '" + camsDB.getUserInfo().login + "{%')";
      camsDB.execute(sql);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setDatabase(CamsDB theCamsDB) {
    mCamsDB = theCamsDB;
  }

  public ArrayList getCategoryList() {
    return mCategoryList;
  }

  //Component initialization
  private void jbInit() throws Exception  {
    imageCut = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("cut.png"));
    imageCopy = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("copy.png"));
    imagePaste = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("paste.png"));
    imageLarge = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("large.png"));
    imageMedium = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("medium.png"));
    imageSmall = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("small.png"));
    imageDetail = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("detail.png"));
    imageSearch = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("search.png"));
    imageSearchPlus = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("search+.png"));
    imageXSearch = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("x_search.png"));
    imagePrint = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("print.png"));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(635, 381));
    this.setTitle("CAMS - Image Library");
    this.addComponentListener(new ImageLibFrame_this_componentAdapter(this));
    jMenuFile.setMnemonic('0');
    jMenuFile.setText("File");
    jMenuFileExit.setMnemonic('0');
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ImageLibFrame_jMenuFileExit_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ImageLibFrame_jMenuHelpAbout_ActionAdapter(this));
    jpanelStatus.setLayout(borderLayout2);
    jlblStatus.setBorder(null);
    jlblStatus.setHorizontalAlignment(SwingConstants.CENTER);
//    jlblStatus.setMinimumSize(new Dimension(100, 19));
//    jlblStatus.setPreferredSize(new Dimension(250, 19));
//    jlblStatus.setHorizontalAlignment(SwingConstants.RIGHT);
    jlblStatus.setText("Displaying Records xx of yy          ");
    jSplitMain.setRightComponent(null);
    jMenuCatalog.setText("Catalog");
    jMenuImportRecords.setText("Import Records");
    jMenuImportRecords.addActionListener(new ImageLibFrame_jMenuImportRecords_actionAdapter(this));
    jMenuEdit .setText("Edit");
    jMenuCut.setText("Cut");
    jMenuCut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuCutAsset_actionPerformed(e);
      }
    });
    jMenuCopy.setText("Copy");
    jMenuCopy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuCopyAsset_actionPerformed(e);
      }
    });
    jMenuPaste.setText("Paste");
    jMenuPaste.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuPasteAsset_actionPerformed(e);
      }
    });
    jMenuAddCat.setText("Add Category");
    jMenuAddCat.addActionListener(new ImageLibFrame_jMenuAddCategory_actionAdapter(this));
    jMenuDeleteCat.setText("Delete Category");
    jMenuDeleteCat.addActionListener(new ImageLibFrame_jMenuDeleteCategory_actionAdapter(this));
    jMenuRenameCategory.setText("Rename Category");
    jMenuRenameCategory.addActionListener(new ImageLibFrame_jMenuRenameCategory_actionAdapter(this));
    jMenuView.setText("View");
    jMenuViewDetail.setText("Details");
    jMenuViewDetail.addActionListener(new ImageLibFrame_jMenuViewDetail_actionAdapter(this));
    jMenuViewSmall.setText("Small Thumbnails");
    jMenuViewSmall.addActionListener(new ImageLibFrame_jMenuViewSmall_actionAdapter(this));
    jMenuViewMedium.setText("Medium Thumbnails");
    jMenuViewMedium.addActionListener(new ImageLibFrame_jMenuViewMedium_actionAdapter(this));
    jMenuViewLarge.setText("Large Thumbnails");
    jMenuViewLarge.addActionListener(new ImageLibFrame_jMenuViewLarge_actionAdapter(this));
    jbtnCut.setIcon(imageCut);
    jbtnCut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuCutAsset_actionPerformed(e);
      }
    });
    jbtnCut.setToolTipText("Cut");
    jbtnCopy.setIcon(imageCopy);
    jbtnCopy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuCopyAsset_actionPerformed(e);
      }
    });
    jbtnCopy.setToolTipText("Copy");
    jbtnPaste.setIcon(imagePaste);
    jbtnPaste.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuPasteAsset_actionPerformed(e);
      }
    });
    jbtnPaste.setToolTipText("Paste");
    jbtnThumbLarge.setIcon(imageLarge);
    jbtnThumbLarge.addActionListener(new ImageLibFrame_jbtnThumbLarge_actionAdapter(this));
    jbtnThumbLarge.setToolTipText("Large Icons");
    jbtnThumbMedium.setIcon(imageMedium);
    jbtnThumbMedium.addActionListener(new ImageLibFrame_jbtnThumbMedium_actionAdapter(this));
    jbtnThumbMedium.setToolTipText("Medium Icons");
    jbtnThumbMedium.setSelected(true);
    jbtnThumbSmall.setIcon(imageSmall);
    jbtnThumbSmall.addActionListener(new ImageLibFrame_jbtnThumbSmall_actionAdapter(this));
    jbtnThumbSmall.setToolTipText("Small Icons");
    jbtnDetails.setIcon(imageDetail);
//    jbtnDetails.setSelected(true);
    jbtnDetails.addActionListener(new ImageLibFrame_jbtnDetails_actionAdapter(this));
    jbtnDetails.setToolTipText("Detail View");
    jToolBar.setFloatable(false);
    jMenuEditProperties.setText("Edit Properties");
    jMenuEditProperties.addActionListener(new ImageLibFrame_jMenuEditProperties_actionAdapter(this));
    jMenuBatchEdit.setText("Batch Edit");
    jMenuBatchEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetBatchEdit_actionPerformed(e);
      }
    });
    jMenuViewAsset.setText("Open Asset (External)");
    jMenuViewAsset.addActionListener(new ImageLibFrame_jMenuViewAsset_actionAdapter(this));
    jMenuRemoveAsset.setText("Remove Record");
    jMenuRemoveAsset.addActionListener(new ImageLibFrame_jMenuRemoveAsset_actionAdapter(this));
    jScrollGallery.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollGallery.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jPanelGallery.setLayout(flowLayout1);
    jMenuAddCategory.setText("Add Category");
    jMenuAddCategory.addActionListener(new ImageLibFrame_jMenuAddCategory_actionAdapter(this));
    jMenuDeleteCategory.setText("Delete Category");
    jMenuDeleteCategory.addActionListener(new ImageLibFrame_jMenuDeleteCategory_actionAdapter(this));
    flowLayout1.setAlignment(FlowLayout.LEFT);
    jPanelGallery.setPreferredSize(new Dimension(10, 300));
    jbtnSearch.setToolTipText("Search");
    jbtnSearch.setIcon(imageSearch);
    jbtnSearch.setSelected(false);
    jbtnSearch.addActionListener(new ImageLibFrame_jbtnSearch_actionAdapter(this));
    jbtnSearchPlus.setToolTipText("Search +");
    jbtnSearchPlus.setIcon(imageSearchPlus);
    jbtnSearchPlus.setSelected(false);
    jbtnSearchPlus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnSearchPlus_actionPerformed(e);
      }
    });
    jMenuSearch.setText("Search");
    jMenuSearch.addActionListener(new ImageLibFrame_jMenuSearch_actionAdapter(this));
    jMenuPreviewAsset.setText("Preview Asset");
    jMenuPreviewAsset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuPreviewAsset_actionPerformed(e);
      }
    });
    jbtnXSearch.setSelected(false);
    jbtnXSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnXSearch_actionPerformed(e);
      }
    });
    jbtnXSearch.setIcon(imageXSearch);
    jbtnXSearch.setEnabled(false);
    jbtnXSearch.setToolTipText("Cancel Search");
    jbtnPrevious.setText("Previous");
    jbtnPrevious.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnPrevious_actionPerformed(e);
      }
    });
//    jbtnPrevious.setVisible(false);
    jbtnNext.setText("Next");
    jbtnNext.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnNext_actionPerformed(e);
      }
    });
//    jbtnNext.setVisible(false);
    jPanelStatusRight.setLayout(flowLayout3);
    flowLayout3.setAlignment(FlowLayout.RIGHT);
    flowLayout3.setHgap(5);
    flowLayout3.setVgap(0);
    jpanelStatus.setBorder(null);
    jPanelStatusRight.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanelStatusLeft.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanelStatusLeft.setLayout(borderLayout3);
    jMenuOptions.setText("Options");
    jmnuShowBatches.setText("Show Import Batches");
    jmnuShowBatches.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jmnuShowBatches_actionPerformed(e);
      }
    });
    jMenuCutAsset.setText("Cut Record");
    jMenuCutAsset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuCutAsset_actionPerformed(e);
      }
    });
    jMenuCopyAsset.setText("Copy Record");
    jMenuCopyAsset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuCopyAsset_actionPerformed(e);
      }
    });
    jProgress.setMaximum(128);
    jProgress.setString("x MB of x MB");
    jProgress.setValue(0);
    jProgress.setStringPainted(true);
    jProgress.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jProgress_mouseClicked(e);
      }
    });
    jMenuPasteAsset.setText("Paste Record");
    jMenuPasteAsset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuPasteAsset_actionPerformed(e);
      }
    });
    jMenuAsset.setText("Asset");
    jMenuAssetDelete.setText("Delete");
    jMenuAssetDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetDelete_actionPerformed(e);
      }
    });
    jMenuAssetRemove.setText("Remove");
    jMenuAssetRemove.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuRemoveAsset_actionPerformed(e);
      }
    });
    jMenuAssetPreview.setText("Preview");
    jMenuAssetPreview.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuPreviewAsset_actionPerformed(e);
      }
    });
    jMenuAssetEdit.setText("Edit");
    jMenuAssetEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuEditProperties_actionPerformed(e);
      }
    });
    jMenuAssetBatchEdit.setText("Batch Edit");
    jMenuAssetBatchEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetBatchEdit_actionPerformed(e);
      }
    });
    jMenuAssetPrint.setEnabled(true);
    jMenuAssetPrint.setText("Print");
    jMenuAssetPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetPrint_actionPerformed(e);
      }
    });
    jMenuAssetRedirect.setText("Redirect Reference");
    jMenuAssetRedirect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetRedirect_actionPerformed(e);
      }
    });
    jMenuSort.setText("Sort");
    jMenuSortRecordName.setText("By Record Name");
    jMenuSortRecordName.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuSortRecordName_actionPerformed(e);
      }
    });
    jMenuSortScanNum.setText("By Scan #");
    jMenuSortScanNum.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuSortScanNum_actionPerformed(e);
      }
    });
    jMenuAssetRotateRight.setText("Rotate Right (+90)");
    jMenuAssetRotateRight.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetRotateRight_actionPerformed(e);
      }
    });
    jMenuAssetRotateLeft.setText("Rotate Left (-90)");
    jMenuAssetRotateLeft.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetRotateLeft_actionPerformed(e);
      }
    });
    jMenuCancelSearch.setText("Cancel Search");
    jMenuCancelSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnXSearch_actionPerformed(e);
      }
    });
    jMenuRecord.setText("Record");
    jbtnPrint.setToolTipText("Print");
    jbtnPrint.setIcon(imagePrint);
    jbtnPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuAssetPrint_actionPerformed(e);
      }
    });
    jmnuMaintainKeywords.setText("Maintain Keywords");
    jmnuMaintainKeywords.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jmnuMaintainKeywords_actionPerformed(e);
      }
    });
    jmnuEditProps.setText("Edit Application Properties");
    jmnuEditProps.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jmnuEditProps_actionPerformed(e);
      }
    });
    jtableDetails.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        jtableDetails_keyPressed(e);
      }
    });

    // Accelerator Keys
    if (mCamsDB.windowsOS()) {
      jMenuImportRecords.setAccelerator(KeyStroke.getKeyStroke('I',
          java.awt.event.InputEvent.CTRL_MASK));
      jMenuSearch.setAccelerator(KeyStroke.getKeyStroke('F',
          java.awt.event.InputEvent.CTRL_MASK));
      jMenuSearchPlus.setAccelerator(KeyStroke.getKeyStroke('F',
          java.awt.event.InputEvent.CTRL_MASK | java.awt.event.InputEvent.SHIFT_MASK));
      jMenuAssetRemove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
          0));
      jMenuAssetPreview.setAccelerator(KeyStroke.getKeyStroke('Y',
          java.awt.event.InputEvent.CTRL_MASK));
      jMenuAssetPrint.setAccelerator(KeyStroke.getKeyStroke('P',
          java.awt.event.InputEvent.CTRL_MASK));
      jMenuCut.setAccelerator(KeyStroke.getKeyStroke('X',
          java.awt.event.InputEvent.CTRL_MASK));
      jMenuCopy.setAccelerator(KeyStroke.getKeyStroke('C',
          java.awt.event.InputEvent.CTRL_MASK));
      jMenuPaste.setAccelerator(KeyStroke.getKeyStroke('V',
          java.awt.event.InputEvent.CTRL_MASK));
      jMenuSelectAll.setAccelerator(KeyStroke.getKeyStroke('A',
          java.awt.event.InputEvent.CTRL_MASK));
    }
    else {
      jMenuImportRecords.setAccelerator(KeyStroke.getKeyStroke('I',
          java.awt.event.InputEvent.META_MASK));
      jMenuSearch.setAccelerator(KeyStroke.getKeyStroke('F',
          java.awt.event.InputEvent.META_MASK));
      jMenuSearchPlus.setAccelerator(KeyStroke.getKeyStroke('F',
          java.awt.event.InputEvent.META_MASK | java.awt.event.InputEvent.SHIFT_MASK));
      jMenuAssetRemove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.
          VK_BACK_SPACE, 0));
      jMenuAssetPreview.setAccelerator(KeyStroke.getKeyStroke('Y',
          java.awt.event.InputEvent.META_MASK));
      jMenuAssetPrint.setAccelerator(KeyStroke.getKeyStroke('P',
          java.awt.event.InputEvent.META_MASK));
      jMenuCut.setAccelerator(KeyStroke.getKeyStroke('X',
          java.awt.event.InputEvent.META_MASK));
      jMenuCopy.setAccelerator(KeyStroke.getKeyStroke('C',
          java.awt.event.InputEvent.META_MASK));
      jMenuPaste.setAccelerator(KeyStroke.getKeyStroke('V',
          java.awt.event.InputEvent.META_MASK));
      jMenuSelectAll.setAccelerator(KeyStroke.getKeyStroke('A',
          java.awt.event.InputEvent.META_MASK));
    }

    jMenuSelectAll.setText("Select All");
    jMenuSelectAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuSelectAll_actionPerformed(e);
      }
    });
    jMenuReplaceThumbnail.setText("Replace Thumbnail");
    jMenuReplaceThumbnail.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuReplaceThumbnail_actionPerformed(e);
      }
    });
    jLabelSpacer.setText("          ");
    jbtnFirst.setText("First");
    jbtnFirst.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnFirst_actionPerformed(e);
      }
    });
    jbtnLast.setText("Last");
    jbtnLast.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnLast_actionPerformed(e);
      }
    });
    jMenuSearchPlus.setText("Find +");
    jMenuSearchPlus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnSearchPlus_actionPerformed(e);
      }
    });
    jToolBar.add(jbtnPrint);
    jToolBar.add(jbtnPrint);
    jToolBar.addSeparator();
    jToolBar.add(jbtnCut);
    jToolBar.add(jbtnCopy);
    jToolBar.add(jbtnPaste);
    jToolBar.addSeparator();
    jToolBar.add(jbtnThumbLarge);
    jToolBar.add(jbtnThumbMedium);
    jToolBar.add(jbtnThumbSmall);
    jToolBar.add(jbtnDetails);
    jToolBar.addSeparator();
    jToolBar.add(jbtnSearch);
    jToolBar.add(jbtnSearchPlus);
    jToolBar.add(jbtnXSearch, null);
    jToolBar.addSeparator();
    contentPane.add(jSplitMain, BorderLayout.CENTER);
    jSplitMain.add(jScrollTree, JSplitPane.LEFT);
    jMenuFile.add(jMenuAssetPrint);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuOptions.add(jmnuMaintainKeywords);
    jMenuOptions.add(jmnuShowBatches);
    jMenuOptions.add(jmnuEditProps);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuEdit);
    jMenuBar1.add(jMenuView);
    jMenuBar1.add(jMenuCatalog);
    jMenuBar1.add(jMenuAsset);
    jMenuBar1.add(jMenuRecord);
    jMenuBar1.add(jMenuSort);
    jMenuBar1.add(jMenuOptions);
    jMenuBar1.add(jMenuHelp);
    this.setJMenuBar(jMenuBar1);
    contentPane.add(jToolBar,  BorderLayout.NORTH);
    contentPane.add(jpanelStatus,  BorderLayout.SOUTH);
    contentPane.add(jScrollGallery,  BorderLayout.EAST);
    jScrollGallery.getViewport().add(jPanelGallery, null);
    jScrollGallery.setVisible(false);
    jScrollTree.getViewport().add(jTreeFolders, null);
    jSplitMain.add(jScrollDetails, JSplitPane.RIGHT);
    jScrollDetails.getViewport().add(jtableDetails, null);
    jMenuCatalog.add(jMenuImportRecords);
    jMenuCatalog.add(jMenuAddCat);
    jMenuCatalog.add(jMenuDeleteCat);
    jMenuCatalog.addSeparator();
    jMenuCatalog.add(jMenuSearch);
    jMenuCatalog.add(jMenuSearchPlus);
    jMenuCatalog.add(jMenuCancelSearch);
    jMenuEdit.add(jMenuCut);
    jMenuEdit.add(jMenuCopy);
    jMenuEdit.add(jMenuPaste);
    jMenuEdit.add(jMenuSelectAll);
    jMenuEdit.addSeparator();
    jMenuView.add(jMenuViewDetail);
    jMenuView.add(jMenuViewSmall);
    jMenuView.add(jMenuViewMedium);
    jMenuView.add(jMenuViewLarge);
    assetPopup.add(jMenuEditProperties);
    assetPopup.add(jMenuBatchEdit);
    assetPopup.add(jMenuPreviewAsset);
    assetPopup.add(jMenuViewAsset);
    assetPopup.addSeparator();
    assetPopup.add(jMenuCutAsset);
    assetPopup.add(jMenuCopyAsset);
    assetPopup.addSeparator();
    assetPopup.add(jMenuRemoveAsset);
    jSplitMain.setDividerLocation(200);

    jtableDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    for (int i=0; i < theTableModel.getColumnCount(); i++) {
      jtableDetails.getColumnModel().getColumn(i).setPreferredWidth(theTableModel.getColumnWidth(i));
    }
    theTableSorter.setTableHeader(jtableDetails.getTableHeader());
    jtableDetails.setDefaultRenderer(java.util.Date.class, new DateCellRenderer());
    jtableDetails.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    jTreeFolders.getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
    jTreeFolders.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
//        processTreeSelection(e);
      }
    });

    jTreeFolders.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseReleased (MouseEvent e) {
            jTreeFolders_mouseClicked(e);
        }
    });

    jtableDetails.addMouseListener(new java.awt.event.MouseAdapter() {
//        public void mouseClicked (MouseEvent e) {
//            jtableDetails_mouseClicked(e);
//        }
        public void mouseReleased (MouseEvent e) {
            jtableDetails_mouseClicked(e);
        }
    });
    btnGroupGallery.add(jbtnThumbLarge);
    btnGroupGallery.add(jbtnThumbMedium);
    btnGroupGallery.add(jbtnThumbSmall);
    btnGroupGallery.add(jbtnDetails);
    catalogPopup.add(jMenuAddCategory);
    catalogPopup.add(jMenuDeleteCategory);
    catalogPopup.add(jMenuRenameCategory);
    catalogPopup.addSeparator();
    catalogPopup.add(jMenuPasteAsset);
    jPanelStatusRight.add(jbtnFirst, null);
    jPanelStatusRight.add(jbtnPrevious, null);
    jPanelStatusRight.add(jlblStatus, null);
    jPanelStatusRight.add(jbtnNext, null);
    jPanelStatusRight.add(jbtnLast, null);
    jPanelStatusRight.add(jLabelSpacer, null);

    jpanelStatus.add(jPanelStatusRight, BorderLayout.CENTER);
    jpanelStatus.add(jPanelStatusLeft,  BorderLayout.WEST);
    jPanelStatusLeft.add(jProgress, BorderLayout.CENTER);
    jMenuRecord.add(jMenuAssetDelete);
    jMenuRecord.add(jMenuAssetRemove);
    jMenuAsset.add(jMenuAssetRedirect);
    jMenuAsset.add(jMenuAssetPreview);
    jMenuAsset.add(jMenuViewAsset);
    jMenuAsset.add(jMenuReplaceThumbnail);
    jMenuRecord.add(jMenuAssetEdit);
    jMenuRecord.add(jMenuAssetBatchEdit);
    jMenuEdit.add(jMenuAssetRotateRight);
    jMenuEdit.add(jMenuAssetRotateLeft);
    jMenuSort.add(jMenuSortRecordName);
    jMenuSort.add(jMenuSortScanNum);
  }

  /**
   * Configure Catalog Tree
   */
  private void configCatalogTree() {
    // Set icon for leaf (folder with no subfolders) to the ClosedFolder icon
//    DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();
    CustomTreeCellRenderer cellRenderer = new CustomTreeCellRenderer();
    cellRenderer.setLeafIcon(cellRenderer.getClosedIcon());
    cellRenderer.setBatchesNode(batchesNode);
    jTreeFolders.setCellRenderer(cellRenderer);

    // Display + and - next to folders
    jTreeFolders.setShowsRootHandles(true);

    // Draw Lines between nodes
    jTreeFolders.putClientProperty("JTree.lineStyle", "Angled");

    treeTop.removeAllChildren();
    treeTop.add(new DefaultMutableTreeNode("Loading..."));
    jTreeFolders.expandRow(0);

//    jTreeFolders.setLargeModel(true);
}

  /**
   * showLastImport - Called after images are imported to show just
   * the newly dragged files.  Accomplished by simulating a search
   * to show the last batch.
   *
   * @param batchId int
   */
  public void showLastImport(int batchId) {
    String sql = "";

    // If showing search results, just refresh current view
    if (mShowSearchResults) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                             jTreeFolders.getLastSelectedPathComponent();
      refreshDetailTable(node, true);
      return;
    }

    // Clear Previous Search Results
    sql = "DELETE FROM ImageLibSearch WHERE search_user = '" +
        mCamsDB.getUserInfo().getSearchID() + "'";
    mCamsDB.execute(sql);

    sql = "INSERT INTO ImageLibSearch " +
        "SELECT '" + mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
        "ImageLibRecord.record_id FROM ImageLibRecord WHERE " +
        "ImageLibRecord.batch_id = " + batchId;
    int numInserted = mCamsDB.executeUpdate(sql);

    if (numInserted > 0)
      showSearchResults();
    else if (jScrollGallery.isVisible()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                             jTreeFolders.getLastSelectedPathComponent();
      if (node == null) return;
      // Import Batches Node
      if (!node.equals(batchesNode)) {
        refreshDetailTable(node);
      }
    }
  }

  /**
   * Reload tree structure from database
   */
  public void refreshCatalogTree() {
    DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance();
    df.applyPattern("0.000");

    cams.console.Console.println("refreshCatalogTree - Start");
    java.util.Date startTime = new java.util.Date();

    // Clear Tree
    treeTop.removeAllChildren();

    // Clear list of Catalogs
    mCatalogList = new ArrayList();
    // Clear list of Categories (Folders)
    mCategoryList = new ArrayList();
    mCategoryPathHash = new HashMap();

    ResultSet rs = null;
    String sql = "";

    try {
      // First add top level Catalogs
      if (!mShowSearchResults)
        sql = "SELECT ImageLibCatalog.catalog_id, catalog_name, catalog_type " +
            "FROM ImageLibCatalog INNER JOIN ImageLibAccess ON " +
            "ImageLibCatalog.catalog_id = ImageLibAccess.catalog_id " +
            "WHERE ImageLibAccess.login = '" + mCamsDB.getUserInfo().login + "' " +
            "ORDER BY sort_order";
      else // Only show catalogs with results
        sql = "SELECT DISTINCT ImageLibCatalog.catalog_id, ImageLibCatalog.catalog_name, " +
            "ImageLibCatalog.catalog_type, ImageLibCatalog.sort_order FROM " +
            "ImageLibCatalog INNER JOIN " +
            "ImageLibRecord ON ImageLibCatalog.catalog_id = ImageLibRecord.catalog_id " +
            "INNER JOIN ImageLibSearch ON ImageLibRecord.record_id = " +
            "ImageLibSearch.record_id WHERE search_user = '" +
            mCamsDB.getUserInfo().getSearchID() + "' ORDER BY ImageLibCatalog.sort_order";

//    cams.console.Console.println("RefreshCatalogTree (1): " + df.format((new java.util.Date().getTime() - startTime.getTime()) / 1000.0));
      rs = mCamsDB.query(sql);
      while (rs.next()) {
        CamsTreeNode theCamsNode = new CamsTreeNode();
        theCamsNode.catalog_id = rs.getInt(1);
        theCamsNode.name = rs.getString(2);
        theCamsNode.catalog_type = rs.getString(3);
        treeTop.add(new DefaultMutableTreeNode(theCamsNode));
        mCatalogList.add(theCamsNode);
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close();

      // Now Load Categories
      if (!mShowSearchResults)
        sql = "SELECT category_id, catalog_id, category_name, category_parent " +
              "FROM ImageLibCategory ORDER BY category_parent, category_name";
      else
        sql = "SELECT DISTINCT ImageLibCategory.category_id, ImageLibCategory.catalog_id, " +
              "ImageLibCategory.category_name, ImageLibCategory.category_parent " +
              "FROM ImageLibSearch INNER JOIN ImageLibRecord ON " +
              "ImageLibSearch.record_id = ImageLibRecord.record_id INNER JOIN " +
              "ImageLibCategory ON ImageLibRecord.catalog_id = ImageLibCategory.catalog_id " +
              "WHERE ImageLibSearch.search_user = '" + mCamsDB.getUserInfo().getSearchID() +
              "' ORDER BY ImageLibCategory.category_parent, ImageLibCategory.category_name";

//    cams.console.Console.println("RefreshCatalogTree (2): " + df.format((new java.util.Date().getTime() - startTime.getTime()) / 1000.0));
      rs = mCamsDB.query(sql);

      DefaultMutableTreeNode theCatalogNode = null;
      DefaultMutableTreeNode theParentNode = null;
      while (rs.next()) {
        CamsTreeNode theCamsNode = new CamsTreeNode();
        theCamsNode.id = rs.getInt(1);
        theCamsNode.catalog_id = rs.getInt(2);
        theCamsNode.name = rs.getString(3);
        theCamsNode.parent_id = rs.getInt(4);
        // Find Catalog Node
        if ((theCatalogNode == null) || (((CamsTreeNode) theCatalogNode.getUserObject()).catalog_id != theCamsNode.catalog_id))
          theCatalogNode = findTreeCatalogNode(treeTop, theCamsNode.catalog_id);
        if (theCatalogNode == null) continue; // Catalog Not Found (no access)
        theCamsNode.catalog_type = ((CamsTreeNode)theCatalogNode.getUserObject()).catalog_type;
        if (theCamsNode.parent_id == -1) {
          // Top Level Category within this Catalog
          theCamsNode.fullPath = ((CamsTreeNode) theCatalogNode.getUserObject()).name +
              "," + theCamsNode.name;
          mCategoryList.add(theCamsNode);
          mCategoryPathHash.put(new Integer(theCamsNode.id), theCamsNode.fullPath);
          // Add it to the end of this category
          theCatalogNode.add(new DefaultMutableTreeNode(theCamsNode));
        }
        else {
          // Now Find Parent Category Node
          if ((theParentNode == null) || (((CamsTreeNode) theParentNode.getUserObject()).id != theCamsNode.parent_id))
            theParentNode = findTreeNode(theCatalogNode, theCamsNode.parent_id);
          if (theParentNode != null) {
            // Add it to the end of this category
            theParentNode.add(new DefaultMutableTreeNode(theCamsNode));
            String theFolder = "";
            for (int i = 1; i < theParentNode.getPath().length; i++)
              theFolder += theParentNode.getPath()[i].toString() + ",";
            theCamsNode.fullPath = theFolder + theCamsNode.name;
            mCategoryList.add(theCamsNode);
            mCategoryPathHash.put(new Integer(theCamsNode.id), theCamsNode.fullPath);
          }
        }
      }
      try {
        if (rs.getStatement() != null) rs.getStatement().close();
        rs.close();
      }
      catch (Exception ex) {}

//    cams.console.Console.println("RefreshCatalogTree (3): " + df.format((new java.util.Date().getTime() - startTime.getTime()) / 1000.0));

      // Add Import Batches
      if ((!mShowSearchResults) && (jmnuShowBatches.getText().startsWith("Hide"))) {
        treeTop.add(batchesNode);

        // Add Folder for "Import Batches"
        mBatchesHash.clear();
        batchesNode.removeAllChildren();
//        sql = "SELECT batch_id, batch_time FROM ImageLibBatch " +
//            "ORDER BY batch_time";
        sql = "SELECT DISTINCT ImageLibBatch.batch_id, ImageLibBatch.batch_time, " +
            "ImageLibCatalog.catalog_type FROM ImageLibBatch INNER JOIN " +
            "ImageLibRecord ON ImageLibBatch.batch_id = ImageLibRecord.batch_id " +
            "INNER JOIN ImageLibCatalog ON " +
            "ImageLibRecord.catalog_id = ImageLibCatalog.catalog_id " +
            "WHERE ISNULL(description, '') NOT LIKE 'Clipboard' " +
            "ORDER BY ImageLibBatch.batch_time";

        rs = mCamsDB.query(sql);
        while (rs.next()) {
          int batchId = rs.getInt("batch_id");
          java.util.Date theBatchTime = rs.getTimestamp("batch_time");
          String catalog_type = rs.getString("catalog_type");
          String formattedDate = new java.text.SimpleDateFormat(
              "MM/dd/yyyy hh:mm:ss aa").format( (
              java.util.Date) theBatchTime);
          mBatchesHash.put(new Integer(batchId), formattedDate);
          CamsTreeNode theCamsNode = new CamsTreeNode();
          theCamsNode.id = -1 * batchId;
          theCamsNode.parent_id = -1;
          theCamsNode.name = formattedDate;
          theCamsNode.fullPath = batchesNode.getPath()[1].toString() + "," +
              theCamsNode.name;
          theCamsNode.catalog_type = catalog_type;
          batchesNode.add(new DefaultMutableTreeNode(theCamsNode));
        }
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("refreshCatalogTree: " + ex.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          if (rs.getStatement() != null) rs.getStatement().close();
          rs.close();
        } catch (Exception ex) {}
      }
    }

//  cams.console.Console.println("RefreshCatalogTree (4): " + df.format((new java.util.Date().getTime() - startTime.getTime()) / 1000.0));

    jTreeFolders.setRootVisible(false);
    DefaultTreeModel treeModel = (DefaultTreeModel) jTreeFolders.getModel();
    treeModel.reload();
    jTreeFolders.expandRow(0);
    jTreeFolders.setSelectionRow(0);

    java.util.Date endTime = new java.util.Date();

    doRefreshMemory();

    String theTime = df.format((endTime.getTime() - startTime.getTime()) / 1000.0);
    cams.console.Console.println("refreshCatalogTree - Finish.  " + theTime + " sec");
}

  /**
   * Search the tree starting from the node passed in looking for
   * the node with the id requested
   *
   * @param node DefaultMutableTreeNode - Starting Node
   * @param id int - ID to Search For
   * @return DefaultMutableTreeNode
   */
  public DefaultMutableTreeNode findTreeNode(DefaultMutableTreeNode node,
                                       int id) {
  DefaultMutableTreeNode child;
  if (node == null) node = treeTop;
  for (int i = 0; i < node.getChildCount(); i++) {
    child = (DefaultMutableTreeNode) node.getChildAt(i);
    if (child.getUserObject() != null) {
      CamsTreeNode theChild = (CamsTreeNode) child.getUserObject();
      if (theChild.id == id)
        return child;
    }
    // If this child has children, call this function recursively
    if (child.getChildCount() > 0) {
      DefaultMutableTreeNode child2 = findTreeNode(child, id);
      if (child2 != null)
        return child2;
    }
  }
  return null;
}

/**
 * Search the tree starting from the node passed in looking for
 * the Catalog node with the catalog_id requested
 *
 * @param node DefaultMutableTreeNode - Starting Node
 * @param catalog_id int - Catalog_ID to Search For
 * @return DefaultMutableTreeNode
 */
public DefaultMutableTreeNode findTreeCatalogNode(DefaultMutableTreeNode node,
    int catalog_id) {
  DefaultMutableTreeNode child;
  if (node == null) node = treeTop;
  for (int i = 0; i < node.getChildCount(); i++) {
    child = (DefaultMutableTreeNode) node.getChildAt(i);
    if (child.getUserObject() != null) {
      CamsTreeNode theChild = (CamsTreeNode) child.getUserObject();
      if (theChild.catalog_id == catalog_id)
        return child;
    }
  }
  return null;
}

public DefaultMutableTreeNode findTreeCatalogNode(int catalog_id) {
  return findTreeCatalogNode(treeTop, catalog_id);
}

  /**
   * Handles the selection of a tree item
   *
   * @param e TreeSelectionEvent
   */
  public void processTreeSelection(TreeSelectionEvent e) {

    if (!jTreeFolders.isEnabled()) {
//    cams.console.Console.println("(ProcessTreeSelection) Folders are disabled");
      return;
    }

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeFolders.getLastSelectedPathComponent();

    if (node == null) return;

    // Import Batches Node
    if (!node.equals(batchesNode)) {
//      CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();
      refreshDetailTable(node);
    }

  }

  /**
   * Loads and displays the records for the currently selected Catalog folder
   *
   * @param catalogId int
   */
  public void refreshDetailTable(DefaultMutableTreeNode treeNode) {
    new doRefreshDetailTableThread(treeNode).start();
//    refreshDetailTable(treeNode, false);
  }

  private class doRefreshDetailTableThread extends Thread {
    private DefaultMutableTreeNode mTreeNode = null;
    public doRefreshDetailTableThread(DefaultMutableTreeNode treeNode) {
      mTreeNode = treeNode;
    }
    public void run() {
      refreshDetailTable(mTreeNode, false);
    }
  }

  public void refreshDetailTable(DefaultMutableTreeNode treeNode, boolean afterEdit) {
    cams.console.Console.println("refreshDetailTable - Start");

    CamsTreeNode theNode = (CamsTreeNode) treeNode.getUserObject();
    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    ArrayList theData = theModel.getData();
    long numAssets = -1;

    theData.clear();
    theTableSorter.fireTableDataChanged();
    jlblStatus.setText("");

    doRefreshMemory();

    String sql = "";

//    System.out.println("RefreshDetailTable: catalogId = " + theNode.catalog_id +
//                      ", categoryId = " + theNode.id);

   // Top level Catalog Folder
    if ( (theNode.id == -1)  && (theNode.catalog_id > 0) && !mShowSearchResults) {
       // If not showing Search Results, don't query database.
       if (jScrollGallery.isVisible()) {
         mGalleryStartIndex = 0;
         refreshGallery();
       }
       return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    doEnableDisableFrame(false);

    cams.console.Console.println("refreshDetailTable - Building SQL Query");
    if ( (theNode.id == -1)  && (theNode.catalog_id > 0) ) {
    // Show ALL matching records in this Catalog
    sql =
        "SELECT record_id, batch_id, ImageLibRecord.catalog_id, RecordName, " +
        "ISNULL(ImageLibRecord.Status_id, -1) Status_id, ISNULL(Status, '') Status, " +
        "FullPathMac, FullPathWin, " +
        "FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive, " +
        "Area, OrigOnFile, ModelRelease, FileFormat, FileSize, FileDate, " +
        "RecordCreated, LastUpdate, ScanNum, UsageRules, " +
        "ISNULL(ImageLibRecord.Release_id, -1) Release_id, ISNULL(Release, '') Release, ImageCompany, " +
        "Advertiser, OriginalKeywords, catalog_name, PNumber, WebDisplay, PriorityDisplay FROM ImageLibRecord INNER JOIN " +
        "ImageLibCatalog ON ImageLibRecord.catalog_id = ImageLibCatalog.catalog_id " +
        "LEFT OUTER JOIN ImageLibRelease ON ImageLibRecord.Release_id = ImageLibRelease.Release_id " +
        "LEFT OUTER JOIN ImageLibStatus ON ImageLibRecord.Status_id = ImageLibStatus.status_id " +
        "WHERE record_id IN (" +
        "SELECT ImageLibCategoryRecord.record_id " +
        "FROM ImageLibCategoryRecord INNER JOIN ImageLibSearch ON " +
        "ImageLibCategoryRecord.record_id = ImageLibSearch.record_id " +
        "WHERE ImageLibRecord.catalog_id = " + theNode.catalog_id + " " +
        "AND ImageLibSearch.search_user = '" + mCamsDB.getUserInfo().getSearchID() +
        "')";
      sql += mSearchOrder;
    }
    else if (theNode.id < 0) { // Load Records from a Batch
      sql =
          "SELECT record_id, batch_id, ImageLibRecord.catalog_id, RecordName, " +
          "ISNULL(ImageLibRecord.Status_id, -1) Status_id, ISNULL(Status, '') Status, " +
          "FullPathMac, FullPathWin, " +
          "FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive, " +
          "Area, OrigOnFile, ModelRelease, FileFormat, FileSize, FileDate, " +
          "RecordCreated, LastUpdate, ScanNum, UsageRules, " +
          "ISNULL(ImageLibRecord.Release_id, -1) Release_id, ISNULL(Release, '') Release, ImageCompany, " +
          "Advertiser, OriginalKeywords, catalog_name, PNumber, WebDisplay, PriorityDisplay FROM ImageLibRecord INNER JOIN " +
          "ImageLibCatalog ON ImageLibRecord.catalog_id = ImageLibCatalog.catalog_id " +
          "LEFT OUTER JOIN ImageLibRelease ON ImageLibRecord.Release_id = ImageLibRelease.Release_id " +
          "LEFT OUTER JOIN ImageLibStatus ON ImageLibRecord.Status_id = ImageLibStatus.status_id " +
          "WHERE batch_id = " + (-1 * theNode.id);
    }
    else { // Records matching selected category (and sub-categories)
      ArrayList numAssetsResult = getNumAssetsInFolder(treeNode);
      numAssets = ((Long) numAssetsResult.get(0)).longValue();
      String inClause = "(SELECT record_id FROM ImageLibSearch WHERE " +
          "search_user = " + (String) numAssetsResult.get(1) + ")";

      if (!mShowSearchResults)
        sql =
          "SELECT record_id, batch_id, ImageLibRecord.catalog_id, RecordName, " +
          "ISNULL(ImageLibRecord.Status_id, -1) Status_id, ISNULL(Status, '') Status, " +
          "FullPathMac, FullPathWin, " +
          "FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive, " +
          "Area, OrigOnFile, ModelRelease, FileFormat, FileSize, FileDate, " +
          "RecordCreated, LastUpdate, ScanNum, UsageRules, " +
          "ISNULL(ImageLibRecord.Release_id, -1) Release_id, ISNULL(Release, '') Release, ImageCompany, " +
          "Advertiser, OriginalKeywords, catalog_name, PNumber, WebDisplay, PriorityDisplay FROM ImageLibRecord INNER JOIN " +
          "ImageLibCatalog ON ImageLibRecord.catalog_id = ImageLibCatalog.catalog_id " +
          "LEFT OUTER JOIN ImageLibRelease ON ImageLibRecord.Release_id = ImageLibRelease.Release_id " +
          "LEFT OUTER JOIN ImageLibStatus ON ImageLibRecord.Status_id = ImageLibStatus.status_id " +
          "WHERE record_id IN (SELECT record_id " +
          "FROM ImageLibCategoryRecord WHERE category_id IN " + inClause + ")";
      else
        sql =
          "SELECT record_id, batch_id, ImageLibRecord.catalog_id, RecordName, " +
          "ISNULL(ImageLibRecord.Status_id, -1) Status_id, ISNULL(Status, '') Status, " +
          "FullPathMac, FullPathWin, " +
          "FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive, " +
          "Area, OrigOnFile, ModelRelease, FileFormat, FileSize, FileDate, " +
          "RecordCreated, LastUpdate, ScanNum, UsageRules, " +
          "ISNULL(ImageLibRecord.Release_id, -1) Release_id, ISNULL(Release, '') Release, ImageCompany, " +
          "Advertiser, OriginalKeywords, catalog_name, PNumber, WebDisplay, PriorityDisplay FROM ImageLibRecord INNER JOIN " +
          "ImageLibCatalog ON ImageLibRecord.catalog_id = ImageLibCatalog.catalog_id " +
          "LEFT OUTER JOIN ImageLibRelease ON ImageLibRecord.Release_id = ImageLibRelease.Release_id " +
          "LEFT OUTER JOIN ImageLibStatus ON ImageLibRecord.Status_id = ImageLibStatus.status_id " +
          "WHERE record_id IN (" +
          "SELECT ImageLibCategoryRecord.record_id " +
          "FROM ImageLibCategoryRecord INNER JOIN ImageLibSearch ON " +
          "ImageLibCategoryRecord.record_id = ImageLibSearch.record_id " +
          "WHERE ImageLibCategoryRecord.category_id IN " + inClause + " " +
          "AND ImageLibSearch.search_user = '" + mCamsDB.getUserInfo().getSearchID() + "')";
       sql += mSearchOrder;
    }

    new RefreshTableDetails(sql, theModel, numAssets, afterEdit).start();
  }

  class RefreshTableDetails extends Thread {
    private String mSql;
    private ArrayList mData;
    private ImageLibTableModel mTableModel = null;
    private boolean mResetGallery = true;
    private long mNumAssets = -1;

    public RefreshTableDetails(String sql, ImageLibTableModel theModel, long numAssets, boolean afterEdit) {
      mSql = sql;
      mTableModel = theModel;
      mData = theModel.getData();
      mResetGallery = !afterEdit;
      mNumAssets = numAssets;
    }

    public void run() {
      ResultSet rs = null;
      try {
        if (mNumAssets == -1)
          mTableModel.setTotalRecords(mCamsDB.getQueryRows(mSql));
        else
          mTableModel.setTotalRecords(mNumAssets);
        cams.console.Console.println("RefreshRecords: Start (" + mTableModel.getTotalRecords() + " total records)");

        if (mSql.toUpperCase().startsWith("SELECT "))
          mSql = "SELECT TOP " + getMaxRows() + mSql.substring(6);
        else
          cams.console.Console.println("RefreshTableDetails Query DID NOT start with 'SELECT '");

        cams.console.Console.println("RefreshRecords: Running Query '" + mSql + "'");
        rs = mCamsDB.query(mSql);
        cams.console.Console.println("RefreshRecords: Query Finished, Fetching each row...");
        while (rs.next()) {
          ImageLibRecord theRecord = new ImageLibRecord(rs);
          mData.add(theRecord);
        }
      }
      catch (Exception ex) {
        cams.console.Console.println("RefreshTableDetails: " + ex.getMessage());
        cams.console.Console.println("SQL = " + mSql);
      }
      finally {
        if (rs != null) {
          try {
            if (rs.getStatement() != null) rs.getStatement().close();
            rs.close();
          } catch (Exception ex) {}
        }
      }

//    jtableDetails.updateUI();
//    cams.console.Console.println("RefreshRecords: Data Loaded, updating display (fireTableDataChanged)");
      theTableSorter.fireTableDataChanged();

      if (mData.size() == 0)
        jlblStatus.setText("");
      else
        jlblStatus.setText("Displaying Assets 1 to " + mData.size() +
                           "  (" + mTableModel.getTotalRecords() + " Total)");


      if (jScrollGallery.isVisible())
      {
        if (mResetGallery || (mGalleryStartIndex >= mData.size()))
          mGalleryStartIndex = 0;
        refreshGallery();
      }

      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      doEnableDisableFrame(true);
      cams.console.Console.println("RefreshRecords: End");
    }
  }

  // Configure the Gallery for the correct size thumbnails
  // and refresh the view
  public void refreshGallery() { // JJS
    int thumbSize;
    int mImagesPerPage = 100;
    if (jbtnThumbSmall.isSelected()) {
      thumbSize = mThumbSmall;
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeSmall", "100"));
    }
    else if (jbtnThumbMedium.isSelected()) {
      thumbSize = mThumbMedium + 20;
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeMedium", "50"));
    }
    else if (jbtnThumbLarge.isSelected()) {
      thumbSize = mThumbLarge;
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeLarge", "25"));
    }
    else
      return;

    int dividerLoc = jSplitMain.getDividerLocation();

    // Empty Gallery Panel of Thumbnails
    jPanelGallery.removeAll();
    jPanelGallery.updateUI();

    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    ArrayList theData = theModel.getData();

    // Loop through each ImageLibRecord
    // Sorted however the detail view was sorted
    int rowStart = mGalleryStartIndex;
    if (rowStart >= theData.size()) rowStart = theData.size() - mImagesPerPage;
    if (rowStart < 0) rowStart = 0;

    int rowStop = rowStart + mImagesPerPage;
    if (rowStop > theData.size()) rowStop = theData.size();

    // Determine Catalog Type
    boolean isPNumber = false;
    try {
      if (rowStart < rowStop) {
        String cat_type = getCatalogTypeFromID( ( (ImageLibRecord) theData.get(
            sorter.modelIndex(rowStart))).getCatalogId());
        isPNumber = cat_type.equalsIgnoreCase("PNumber");
      }
    }
    catch (Exception ex) {}

//    for (int i=0; i < theData.size(); i++) {
    for (int i=rowStart; i < rowStop; i++) {
      ImageLibRecord theRecord = (ImageLibRecord) theData.get(sorter.modelIndex(i));
      byte[] theThumbBytes = null;
      if (jbtnThumbSmall.isSelected())
        theThumbBytes = theRecord.getThumbSmall(mCamsDB);
      else if (jbtnThumbMedium.isSelected())
        theThumbBytes = theRecord.getThumbMedium(mCamsDB);
      else if (jbtnThumbLarge.isSelected())
        theThumbBytes = theRecord.getThumbLarge(mCamsDB);

      ImageIcon theIcon = null;
      if (theThumbBytes != null)
        theIcon = new ImageIcon(theThumbBytes);

      // Create Thumbnail Image (as JLabel)
      JLabel theThumbNail = new JLabel();
      theThumbNail.setBorder(null);
      theThumbNail.setHorizontalAlignment(SwingConstants.CENTER);
      theThumbNail.setVerticalAlignment(SwingConstants.CENTER);
      theThumbNail.setIcon(theIcon);
      theThumbNail.setPreferredSize(new Dimension(thumbSize, thumbSize));
      theThumbNail.setName(String.valueOf(i));

      // Top Section (3 lines of Data)
      JPanel theTopInfo = null;
      Font theFont = new JLabel("xxx").getFont().deriveFont(Font.PLAIN, (float)9.0);

      if (!jbtnThumbSmall.isSelected() && !isPNumber) {
        // Medium or Large, Not P#
        theTopInfo = new JPanel(new XYLayout());
        theTopInfo.setBorder(null);
        theTopInfo.setPreferredSize(new Dimension(thumbSize, 45));
        theTopInfo.add(makeJLabel(theRecord.getRecordName(), theFont),
                       new XYConstraints(2, 0, -1, -1));
        theTopInfo.add(makeJLabel(theRecord.getScanNumber(), theFont),
                       new XYConstraints(2, 14, -1, -1));
        theTopInfo.add(makeJLabel(theRecord.getKeyline(), theFont),
                       new XYConstraints(2, 29, -1, -1));
      }
      else if (!jbtnThumbSmall.isSelected()) {
        // Medium or Large, P# - Don't Show Record Name
        theTopInfo = new JPanel(new XYLayout());
        theTopInfo.setBorder(null);
        theTopInfo.setPreferredSize(new Dimension(thumbSize, 30));
        theTopInfo.add(makeJLabel(theRecord.getScanNumber(), theFont),
                       new XYConstraints(2, 0, -1, -1));
        theTopInfo.add(makeJLabel(theRecord.getKeyline(), theFont),
                       new XYConstraints(2, 14, -1, -1));
      }
      else if (jbtnThumbSmall.isSelected() && !isPNumber) {
        // Small Thumbnail, Not P#
        theTopInfo = new JPanel(new XYLayout());
        theTopInfo.setBorder(null);
        theTopInfo.setPreferredSize(new Dimension(thumbSize, 15));
        theTopInfo.add(makeJLabel(theRecord.getRecordName(), theFont),
                       new XYConstraints(2, 0, -1, -1));
      }

      // Create Panel to hold all 3 elements
      JPanel thePanel = new JPanel(new BorderLayout());
      thePanel.setName(String.valueOf(i));
      thePanel.setBorder(BorderFactory.createEtchedBorder());
      thePanel.add(theThumbNail, BorderLayout.CENTER);

      if (!jbtnThumbSmall.isSelected()) {
        if (isPNumber)
          thePanel.setPreferredSize(new Dimension(thumbSize, thumbSize + 45));
        else
          thePanel.setPreferredSize(new Dimension(thumbSize, thumbSize + 60));
        thePanel.add(makeJLabel(theRecord.getStatus() + " ", theFont), BorderLayout.SOUTH);
        thePanel.add(theTopInfo, BorderLayout.NORTH);
      }
      else if (isPNumber) { // Small Thumbnail P#
        thePanel.setPreferredSize(new Dimension(thumbSize, thumbSize + 15));
        thePanel.add(makeJLabel(theRecord.getScanNumber() + " ", theFont), BorderLayout.SOUTH);
      }
      else { // Small Thumbnail - Not P#
        thePanel.setPreferredSize(new Dimension(thumbSize, thumbSize + 30));
        thePanel.add(makeJLabel(theRecord.getScanNumber() + " ", theFont), BorderLayout.SOUTH);
        thePanel.add(theTopInfo, BorderLayout.NORTH);
      }

      thePanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseReleased (MouseEvent e) {
          galleryItem_mouseClicked(e);
        }
      });

      thePanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          thumbGallery_keyPressed(e);
        }
      });
      jPanelGallery.add(thePanel);
    }
    jPanelGallery.doLayout();
    jPanelGallery.updateUI();
    jSplitMain.setDividerLocation(dividerLoc);

    jbtnFirst.setVisible(rowStart > 0);
    jbtnPrevious.setVisible(false);
    jbtnNext.setVisible(false);
    if (theData.size() == 0) {
      jlblStatus.setText("");
    }
    else {
      jlblStatus.setText("Displaying Assets " + (rowStart + 1) + " to " +
                         rowStop + "  (" +
                         theModel.getTotalRecords() + " Total)");
      if (rowStart > 0) jbtnPrevious.setVisible(true);
      if (rowStop < theData.size()) jbtnNext.setVisible(true);
    }
    jbtnLast.setVisible(jbtnNext.isVisible());

    new CheckGalleryPanelHeight().start();

    refreshGallerySelected();
  }

  private JLabel makeJLabel(String theText, Font theFont) {
    JLabel theLabel = new JLabel(theText);
    theLabel.setFont(theFont);
    return theLabel;
  }

  public void galleryItem_mouseClicked (MouseEvent e) {
    JPanel thePanel = (JPanel) e.getSource();

    int x = e.getX(); // X and Y are relative to the source (i.e. JPanel)
    int y = e.getY();

    // Convert X,Y to position within jPanelGallery
    Point galleryPoint = thePanel.getLocation();
    galleryPoint.translate(x, y);

    thePanel = (JPanel) jPanelGallery.getComponentAt(galleryPoint);

    int rowSelected = Integer.parseInt(thePanel.getName());

    // Is Right-Mouse Button Down?
    boolean isRightClick = false;
    if (mCamsDB.windowsOS())
      isRightClick = SwingUtilities.isRightMouseButton(e);
    else { // Mac
      // Mac Right Click -and- NOT CMD Button
      if (SwingUtilities.isRightMouseButton(e) && (e.getModifiersEx() == e.META_DOWN_MASK))
        isRightClick = true;
      // Mac Cntrl Click
      else if ((e.getModifiersEx() & e.CTRL_DOWN_MASK) == e.CTRL_DOWN_MASK)
        isRightClick = true;
    }

    if ( (!mCamsDB.windowsOS()) && (SwingUtilities.isRightMouseButton(e))
        && (e.getModifiersEx() == 4352)) {
      // Mac CMD-Click
      cams.console.Console.println("Mac CMD-Click on row=" + rowSelected);
      if (jtableDetails.isRowSelected(rowSelected))
        jtableDetails.removeRowSelectionInterval(rowSelected, rowSelected);
      else
        jtableDetails.addRowSelectionInterval(rowSelected, rowSelected);
    }
    else if (e.isControlDown()) {
      if (jtableDetails.isRowSelected(rowSelected) && !isRightClick)
        jtableDetails.removeRowSelectionInterval(rowSelected, rowSelected);
      else
        jtableDetails.addRowSelectionInterval(rowSelected, rowSelected);
    }
    else if (e.isShiftDown()) {
      jtableDetails.addRowSelectionInterval(jtableDetails.getSelectedRow(),
                                            rowSelected);
    }
    else {
      jtableDetails.clearSelection();
      jtableDetails.setRowSelectionInterval(rowSelected, rowSelected);
    }

    thePanel.setBorder(BorderFactory.createLoweredBevelBorder());
    thePanel.setBackground(SystemColor.controlShadow);
    thePanel.getComponentAt(5, 5).setBackground(SystemColor.controlShadow);

//      cams.console.Console.println("isRightMouseButton = " + SwingUtilities.isRightMouseButton(e) +
//                      ", getModifiers = " + e.getModifiersEx() +
//                      ", META_DOWN = " + e.META_DOWN_MASK +
//                      ", CTRL_DOWN = " + e.CTRL_DOWN_MASK +
//                      ", SHIFT_DOWN = " + e.SHIFT_DOWN_MASK);

      if (isRightClick)
         assetPopup.show(thePanel, x, y);
      else if (e.getClickCount() == 2) {
        // Double-Clicked on Thumbnail
        jMenuEditProperties_actionPerformed(new ActionEvent(this, 0, null));
//      jMenuPreviewAsset_actionPerformed(new ActionEvent(this, 0, null));
      }

      refreshGallerySelected();
  }

  // Loop through each Gallery panel and set Styles appropriately to indicate
  // selected status
  private void refreshGallerySelected() {
    for (int i = 0; i < jPanelGallery.getComponentCount(); i++) {
      Component theComponent = jPanelGallery.getComponent(i);
      if (theComponent.getClass().equals(JPanel.class)) {
        JPanel thePanel = (JPanel) theComponent;
        thePanel.requestFocus();
        try {
          int rowSelected = Integer.parseInt(thePanel.getName());
          if (jtableDetails.isRowSelected(rowSelected)) {
            thePanel.setBorder(BorderFactory.createLoweredBevelBorder());
            thePanel.setBackground(SystemColor.controlShadow);
            thePanel.getComponentAt(5, 5).setBackground(SystemColor.controlShadow);
          }
          else {
            thePanel.setBorder(BorderFactory.createEtchedBorder());
            thePanel.setBackground(SystemColor.control);
            thePanel.getComponentAt(5, 5).setBackground(SystemColor.control);
          }
        }
        catch (Exception ex) {}
      }
    }
  }

  //File | Exit action performed
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    try {
      // Clear my records from this session from ImageLibSearch
      String sql = "DELETE FROM ImageLibSearch WHERE " +
          "search_user like '" + mCamsDB.getUserInfo().getSearchID() + "%'";
      mCamsDB.execute(sql);
    }
    catch (Exception ex) {}
    System.exit(0);
  }

  //Help | About action performed
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    ImageLibFrame_AboutBox dlg = new ImageLibFrame_AboutBox(this, mVersion);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.show();
  }

  // Open External selected image(s)
  public void jMenuViewAsset_actionPerformed(ActionEvent e) {

    if (jtableDetails.getSelectedRowCount() == 0) {
      JOptionPane.showMessageDialog(this, "No Records Selected.  One or more " +
                                    "records must be selected before selecting " +
                                    "this command.", "No Records Selected",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
//      if (mAcrobatPath == null) {
//        // Load properties
//        Properties props = new Properties();
//        FileInputStream instream = new FileInputStream(new File(
//            "cams-image.properties"));
//        props.load(instream);
//        mAcrobatPath = props.getProperty("AcrobatReader", "");
//        instream.close();
//      }

      int selectedRows[] = jtableDetails.getSelectedRows();
      int selectedRow = jtableDetails.getSelectedRow();

      TableSorter sorter = (TableSorter) jtableDetails.getModel();
      ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();

      Runtime r = Runtime.getRuntime();

      for (int i=0; i < selectedRows.length; i++) {
        ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[i]));

        if (mCamsDB.windowsOS()) {
          r.exec("cmd /c \"" + theRecord.getFullPathWin() + "\"");
        }
        else {
          String[] theCommands = new String[] {"sh", "-c",
              "open \"" + theRecord.getFullPathMac() + "\""};
          r.exec(theCommands);
        }
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("viewAsset: " + ex.getMessage());
    }
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  /**
   * Import PDF Files
   * @param e ActionEvent
   */
  void jMenuImportRecords_actionPerformed(ActionEvent e) {
    // Get selected Catalog
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
        jTreeFolders.getLastSelectedPathComponent();

    if (treeNode == null) return;

    if (treeNode.equals(batchesNode) || treeNode.getParent().equals(batchesNode)) {
      JOptionPane.showMessageDialog(this, "Can not add categories to an import batch.");
      return;
    }

    while (((CamsTreeNode) treeNode.getUserObject()).id != -1)
      treeNode = (DefaultMutableTreeNode) treeNode.getParent();

    CamsTreeNode theNode = (CamsTreeNode) treeNode.getUserObject();

    ImportRecordFrame importFrame = new ImportRecordFrame(this, mCamsDB,
        mCategoryPathHash, theNode);

    if (importFrame.getNumCatalogs() == 0) {
      importFrame.dispose();
      return;
    }

    setVisible(false);
    boolean packFrame = true;
    if (packFrame) {
      importFrame.pack();
    }
    else {
      importFrame.setSize(800, 600);
      importFrame.validate();
    }

    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = importFrame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    importFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    importFrame.show();
  }

  // Mouse Clicked on Detail Table
  public void jtableDetails_mouseClicked (MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    int rowAtPoint = jtableDetails.rowAtPoint(new Point(x, y));

    boolean isRightClick = false;

    if (mCamsDB.windowsOS()) { // Windows
      isRightClick = SwingUtilities.isRightMouseButton(e);
    }
    else { // Mac
      // Mac Right Click -and- NOT CMD Button
      if (SwingUtilities.isRightMouseButton(e) && (e.getModifiersEx() == e.META_DOWN_MASK))
        isRightClick = true;
      // Mac Cntrl Click
      else if ((e.getModifiersEx() & e.CTRL_DOWN_MASK) == e.CTRL_DOWN_MASK)
        isRightClick = true;
    }


    // Make sure current place where mouse clicked is selected
    if ((e.getModifiersEx() == 0) || isRightClick)
      jtableDetails.addRowSelectionInterval(rowAtPoint, rowAtPoint);

    // Set to one row only
    // jtableDetails.setRowSelectionInterval(rowAtPoint, rowAtPoint);

    if (jtableDetails.getSelectedRow() < 0) return;

    if (e.getClickCount() == 2) {
      // Double-Clicked on Detail Item
      jMenuEditProperties_actionPerformed(new ActionEvent(this, 0, null));
    }
    else if (isRightClick) {
      assetPopup.show(jtableDetails, x, y);
    }
  }

  public void jTreeFolders_mouseClicked(MouseEvent e) {
    boolean isRightClick = false;

    if (mCamsDB.windowsOS())
      isRightClick = SwingUtilities.isRightMouseButton(e);
    else { // Mac
//      cams.console.Console.println("isRightMouseButton = " + SwingUtilities.isRightMouseButton(e) +
//                      ", getModifiers = " + e.getModifiersEx() +
//                      ", META_DOWN = " + e.META_DOWN_MASK +
//                      ", CTRL_DOWN = " + e.CTRL_DOWN_MASK);

      // Mac Right Click -and- NOT CMD Button
      if (SwingUtilities.isRightMouseButton(e) && (e.getModifiersEx() == e.META_DOWN_MASK))
        isRightClick = true;
      // Mac Cntrl Click
      else if ((e.getModifiersEx() & e.CTRL_DOWN_MASK) == e.CTRL_DOWN_MASK)
        isRightClick = true;
    }

    if (!isRightClick) { // Not Right Click
      if (e.getClickCount() == 2) {
        // Double-Click
        if (!jTreeFolders.isEnabled()) return;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                               jTreeFolders.getLastSelectedPathComponent();
        if (node == null) return;

        // Import Batches Node
        if (!node.equals(batchesNode)) {
          refreshDetailTable(node);
        }
      } // If Double-Click
    }
    else {
      // Handle Right Click on Tree

      if (!jTreeFolders.isEnabled()) {
        return;
      }

      int x = e.getX();
      int y = e.getY();

      // Set the Selection to where the user right-clicked
      TreePath clickedPath = jTreeFolders.getPathForLocation(x, y);
      jTreeFolders.setSelectionPath(clickedPath);

      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
          jTreeFolders.getLastSelectedPathComponent();

      if (treeNode == null) return;

//      if (node == null) return;
//      CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();

      // Ignore click on "Image Batches" folder
      if (treeNode.equals(batchesNode)) return;

      CamsTreeNode theNode = (CamsTreeNode) treeNode.getUserObject();
      if ((theNode.id < 0) && (theNode.catalog_id == -1)) {
        // Batch Folder
        jMenuAddCategory.setEnabled(false);
        jMenuDeleteCategory.setText("Delete Batch");
        jMenuDeleteCategory.setEnabled(true);
        jMenuRenameCategory.setEnabled(false);
        jMenuPasteAsset.setEnabled(false);
      } else if ((theNode.id == -1) && (theNode.catalog_id > 0)) {
        // Catalog Folder
        jMenuAddCategory.setEnabled(true);
        jMenuDeleteCategory.setText("Delete Catalog");
        jMenuDeleteCategory.setEnabled(false);
        jMenuRenameCategory.setEnabled(false);
        jMenuPasteAsset.setEnabled(false);
      } else {
        jMenuAddCategory.setEnabled(true);
        jMenuDeleteCategory.setText("Delete Category");
        jMenuDeleteCategory.setEnabled(true);
        jMenuRenameCategory.setEnabled(true);
        jMenuPasteAsset.setEnabled(mAssetsToCopy.size() > 0);
      }

      if (treeNode != null)
        catalogPopup.show(jTreeFolders, x, y);
    }
  }

  public class DateCellRenderer  extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean
        isSelected, boolean hasFocus, int row, int column) {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                          row, column);
      if (value instanceof java.util.Date) {
        // Use SimpleDateFormat class to get a formatted String from Date object.
        String strDate = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").format( (
            java.util.Date) value);
        // Sorting algorithm will work with model value. So you dont need to worry
        // about the renderer's display value.
        this.setText(strDate);
      }
      return this;
    }
  }

  void jbtnDetails_actionPerformed(ActionEvent e) {
    int dividerLoc = jSplitMain.getDividerLocation();
    jScrollGallery.setVisible(false);
    jScrollDetails.setVisible(true);
    jSplitMain.setRightComponent(jScrollDetails);
    jSplitMain.setDividerLocation(dividerLoc);

    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    jbtnPrevious.setVisible(false);
    jbtnNext.setVisible(false);
    if (theModel.getData().size() == 0)
      jlblStatus.setText("");
    else
      jlblStatus.setText("Displaying Assets 1 to " + theModel.getData().size() +
                         "  (" + theModel.getTotalRecords() + " Total)");
  }

  public void jbtnShowThumbnails_actionPerformed(ActionEvent e) {
    int dividerLoc = jSplitMain.getDividerLocation();
    jScrollDetails.setVisible(false);
    jScrollGallery.setVisible(true);
    jSplitMain.setRightComponent(jScrollGallery);
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    doEnableDisableFrame(false);
    new RefreshGalleryThread().start();
    jSplitMain.setDividerLocation(dividerLoc);
  }

  class RefreshGalleryThread extends Thread {
    public RefreshGalleryThread() {}

    public void run() {
      refreshGallery();
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      doEnableDisableFrame(true);
    }
  }

  /**
   * Add Category to selected Catalog/Category from Tree
   * @param e ActionEvent
   */
  void jMenuAddCategory_actionPerformed(ActionEvent e) {
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
        jTreeFolders.getLastSelectedPathComponent();

    if (treeNode == null) return;

    if (treeNode.equals(batchesNode) || treeNode.getParent().equals(batchesNode)) {
      JOptionPane.showMessageDialog(this, "Can not add categories to an import batch.");
      return;
    }

    CamsTreeNode theNode = (CamsTreeNode) treeNode.getUserObject();
    String newCategory =
    JOptionPane.showInputDialog(this, "Enter new Category to create under '" +
                                theNode.name + "'", "Create New Category",
                                JOptionPane.QUESTION_MESSAGE);

    if ((newCategory == null) || (newCategory.length() == 0)) return;

    String sql = "INSERT INTO ImageLibCategory (catalog_id, category_name, category_parent) " +
        "VALUES (" + theNode.catalog_id + ", '" + newCategory.replaceAll("'", "''") + "', " + theNode.id + ")";
    mCamsDB.execute(sql);

    // Temporarily Create this new Node so we can show it on the tree
    CamsTreeNode theNewNode = new CamsTreeNode();
    theNewNode.name = newCategory;
    treeNode.add(new DefaultMutableTreeNode(theNewNode));
    jTreeFolders.expandRow(jTreeFolders.getSelectionRows()[0]);

    // Remember which rows are expanded
    ArrayList expandedRows = new ArrayList();
    for (int i=0; i < jTreeFolders.getRowCount(); i++)
      if (jTreeFolders.isExpanded(i)) expandedRows.add(new Integer(i));

    int selectedRow = jTreeFolders.getSelectionRows()[0];

    refreshCatalogTree();

    // Now Re-Expand Rows
    for (int i=0; i < expandedRows.size(); i++) {
      int theRow = ((Integer) expandedRows.get(i)).intValue();
      jTreeFolders.expandRow(theRow);
    }

    // Reselect New Row
    jTreeFolders.setSelectionRow(selectedRow);
  }

  void jMenuDeleteCategory_actionPerformed(ActionEvent e) {
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
        jTreeFolders.getLastSelectedPathComponent();

    if (treeNode == null) return;

    if (treeNode.equals(batchesNode)) {
      JOptionPane.showMessageDialog(this, "You Can Not Delete Top Level Catalogs",
                                    "Can Not Delete Catalogs", JOptionPane.ERROR_MESSAGE);
      return;
    }


    CamsTreeNode theNode = (CamsTreeNode) treeNode.getUserObject();

    if ((theNode.id == -1) && (theNode.catalog_id > 0)) {
      JOptionPane.showMessageDialog(this, "You Can Not Delete Top Level Catalogs",
                                    "Can Not Delete Catalogs", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (treeNode.getParent().equals(batchesNode)) {
      deleteImportBatch(treeNode);
      return;
    }

    ArrayList numAssetsResult = getNumAssetsInFolder(treeNode);
    long numAssets = ((Long) numAssetsResult.get(0)).longValue();
    String inClause = "(SELECT record_id FROM ImageLibSearch WHERE " +
        "search_user = " + (String) numAssetsResult.get(1) + ")";

    int selectedRow = jTreeFolders.getSelectionRows()[0];

    String sql = "";
    // Just delete it if it doesn't contain any assets
    if (numAssets == 0) {
//      sql = "DELETE FROM ImageLibCategory WHERE category_id = " + theNode.id;
      sql = "DELETE FROM ImageLibCategory WHERE category_id in " + inClause;
      mCamsDB.execute(sql);
    }
    else {
      if (JOptionPane.showConfirmDialog(this,
                                        "Remove ALL " + numAssets +
                                        " assets from this category" +
                                        " and then delete the category '" +
                                        theNode.name + "'?",
                                        "Delete Category",
                                        JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE) !=
          JOptionPane.YES_OPTION)
        return;
    }

    // Create a temporary user for the search results table. i.e. 'jeff_delete'
    String searchUser = "'" + mCamsDB.getUserInfo().getSearchID() + "_delete'";

    // For Image Type, actually delete the ImageLibRecord records
    if (theNode.catalog_type.equalsIgnoreCase("Image")) {
      // Create list of records to delete
      sql = "DELETE FROM ImageLibSearch WHERE search_user = " + searchUser;
      mCamsDB.execute(sql);
      sql = "INSERT INTO ImageLibSearch (search_user, search_date, record_id) " +
          "SELECT " + searchUser + ", getDate(), record_id FROM ImageLibCategoryRecord " +
          "WHERE category_id in " + inClause;
      mCamsDB.execute(sql);
    }

    sql = "DELETE FROM ImageLibCategoryRecord WHERE category_id in " + inClause;
    mCamsDB.execute(sql);

    sql = "DELETE FROM ImageLibCategory WHERE category_id in " + inClause;
    mCamsDB.execute(sql);

    // For Image Type, actually delete the ImageLibRecord records
    if (theNode.catalog_type.equalsIgnoreCase("Image")) {
      sql = "DELETE FROM ImageLibRecord WHERE record_id IN (SELECT " +
          "record_id FROM ImageLibSearch WHERE search_user = " + searchUser + ")";
      mCamsDB.execute(sql);
      sql = "DELETE FROM ImageLibSearch WHERE search_user = " + searchUser;
      mCamsDB.execute(sql);
    }

    treeNode.removeFromParent();
    jTreeFolders.setSelectionRow(selectedRow - 1);

    // Remember which rows are expanded
    ArrayList expandedRows = new ArrayList();
    for (int i=0; i < jTreeFolders.getRowCount(); i++)
      if (jTreeFolders.isExpanded(i)) expandedRows.add(new Integer(i));

    refreshCatalogTree();

    // Now Re-Expand Rows
    for (int i=0; i < expandedRows.size(); i++) {
      int theRow = ((Integer) expandedRows.get(i)).intValue();
      jTreeFolders.expandRow(theRow);
    }

    // Reselect Row
    jTreeFolders.setSelectionRow(selectedRow - 1);
  }

  /**
   * getNumAssetsInFolder - Determines the matching subcategories and
   * inserts those category_ids into the ImageLibSearch table for later
   * joining into a query.
   *
   * returns the total # of records in matching categories
   *
   * @param theNode CamsTreeNode
   * @return ArrayList
   */
  private ArrayList getNumAssetsInFolder(DefaultMutableTreeNode treeNode) {
    cams.console.Console.println("getNumAssetsInFolder - Start");

    CamsTreeNode theNode = (CamsTreeNode) treeNode.getUserObject();
    ArrayList result = new ArrayList();

//    cams.console.Console.println("getNumAssetsInFolder - Locating subcategories");
    // Create list of all sub-categories to this folder
    String fullPath = theNode.fullPath;

    // Create a temporary user for the search results table. i.e. 'jeff_subcats'
    String searchUser = "'" + mCamsDB.getUserInfo().getSearchID() + "_subcats'";

//    String sql = "DELETE FROM ImageLibSearch WHERE search_user = " + searchUser;
//    mCamsDB.execute(sql);

    cams.console.Console.println("Generating list of SubCategories - START");
    insertSubCategories(theNode.id);
    cams.console.Console.println("Generating list of SubCategories - END");

//    sql = "INSERT INTO ImageLibSearch (search_user, search_date, record_id) " +
//        "VALUES (" + searchUser + ", getDate(), ";
//    for (int i=0; i < mCategoryList.size(); i++) {
//      CamsTreeNode theTreeNode = (CamsTreeNode) mCategoryList.get(i);
//      String matchPath = theTreeNode.fullPath;
//      if ((matchPath != null) && ((matchPath + ",").startsWith(fullPath + ",")))
//        mCamsDB.execute(sql + theTreeNode.id + ")");
//    }

    String sql = "";
    // Check how many assets exist in this category
    if (!mShowSearchResults)
      sql = "SELECT COUNT(DISTINCT record_id) FROM ImageLibCategoryRecord WHERE " +
            "category_id IN (SELECT record_id FROM ImageLibSearch WHERE " +
            "search_user = " + searchUser + ")";
    else
      sql = "SELECT COUNT(*) FROM ImageLibCategoryRecord INNER JOIN " +
          "ImageLibSearch ON ImageLibCategoryRecord.record_id = ImageLibSearch.record_id " +
          "WHERE category_id IN (SELECT record_id FROM ImageLibSearch WHERE " +
          "search_user = " + searchUser + ") " +
          "GROUP BY search_user HAVING search_user = '" + mCamsDB.getUserInfo().getSearchID() + "'";

//    cams.console.Console.println("getNumAssetsInFolder - Counting matching records.");
    String numAssetsStr = mCamsDB.querySingleString(sql);
    long numAssets = 0;

    if (numAssetsStr != null)
      numAssets = Long.parseLong(numAssetsStr);

    result.add(new Long(numAssets));
    result.add(searchUser);

    cams.console.Console.println("getNumAssetsInFolder - Finished (" + numAssets + " in selected categories)");
    return result;
  }

  public void insertSubCategories(int top_category) {
    Connection dbConn = mCamsDB.getConnection();
    try {
      CallableStatement stmt = dbConn.prepareCall(
          "{call sp_ImageLibGenerateSubCats(?, ?)}");
      stmt.setString(1, mCamsDB.getUserInfo().getSearchID());
      stmt.setInt(2, top_category);
      stmt.executeUpdate();
      stmt.close();
      stmt = null;
    }
    catch (Exception ex) {
      cams.console.Console.println("insertSubCategories: " + ex.getMessage());
    }
  }

  /**
   * DeleteImportBatch: Deletes an entire import batch
   * @param theNode DefaultMutableTreeNode
   */
  void deleteImportBatch(DefaultMutableTreeNode theNode) {
    JOptionPane.showMessageDialog(this, "Can not delete import batches at this time.");
  }

  /**
   * Adjust # of image columns in the gallery view if the screen
   * is resized
   * @param e ComponentEvent
   */
  void this_componentResized(ComponentEvent e) {
    if (jScrollGallery.isVisible()) {
      // Configure Layout
      new CheckGalleryPanelHeight().start();
    }
  }

  class CheckGalleryPanelHeight extends Thread {
    public void run() {
      jPanelGallery.doLayout();

      int theMax = 0;
      int numComponents = 0;
      int theHeight = 0;
      for (int i = 0; i < jPanelGallery.getComponentCount(); i++) {
        Component theComponent = jPanelGallery.getComponent(i);
        if (theComponent.getClass().equals(JPanel.class)) {
          JPanel thePanel = (JPanel) theComponent;
          int bottom = thePanel.getY() + thePanel.getHeight();
          if (bottom > theMax) theMax = bottom;
          if (theHeight == 0) theHeight = thePanel.getHeight();
          numComponents++;
        }
      }
      jPanelGallery.setPreferredSize(new Dimension(10, theMax + 5));
      jPanelGallery.updateUI();

      jScrollGallery.getVerticalScrollBar().setUnitIncrement(theHeight + 5);
//      System.out.println("Found " + numComponents + " components.  Max(Bottom) = " + theMax);
    }
  }

  void jMenuViewDetail_actionPerformed(ActionEvent e) {
    jbtnDetails.setSelected(true);
    jbtnDetails_actionPerformed(e);
  }

  void jMenuViewSmall_actionPerformed(ActionEvent e) {
    jbtnThumbSmall.setSelected(true);
    jbtnShowThumbnails_actionPerformed(e);
  }

  void jMenuViewMedium_actionPerformed(ActionEvent e) {
    jbtnThumbMedium.setSelected(true);
    jbtnShowThumbnails_actionPerformed(e);
  }

  void jMenuViewLarge_actionPerformed(ActionEvent e) {
    jbtnThumbLarge.setSelected(true);
    jbtnShowThumbnails_actionPerformed(e);
  }

  public ImageLibRecord getImageRecordByRow(int tableRow) {
    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    return (ImageLibRecord) theModel.getData().get(sorter.modelIndex(tableRow));
  }

  /**
   * Edit Properties for an asset
   * @param e ActionEvent
   */
  void jMenuEditProperties_actionPerformed(ActionEvent e) {

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeFolders.getLastSelectedPathComponent();
    CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();

    int selectedRows[] = jtableDetails.getSelectedRows();
    if (selectedRows.length == 0) return;
    boolean dataChanged = false;

    String cat_type = getCatalogTypeFromID(getImageRecordByRow(selectedRows[0]).getCatalogId());

    for (int i=0; i < selectedRows.length; i++) {
      if (cat_type.equalsIgnoreCase("Stock")) {
        StockDetailsDialog detailsDialog = new StockDetailsDialog(this, mCamsDB,
            jtableDetails, mCategoryPathHash, selectedRows[i], i,
            selectedRows.length == 1);
        detailsDialog.refreshScreen();

        detailsDialog.show();  // Show as Modal if only one row selected
        dataChanged = detailsDialog.dataSaved;
        if (selectedRows.length == 1)
          detailsDialog.dispose();
        else
          mNumDetailDialogsOpen++;    // Track # of Non-Modal Detail Dialogs
      }
      else if (cat_type.equalsIgnoreCase("Exclusive")) {
        ExclusiveDetailsDialog detailsDialog = new ExclusiveDetailsDialog(this,
            mCamsDB,
            jtableDetails, mCategoryPathHash, selectedRows[i], i,
            selectedRows.length == 1, false);
        detailsDialog.refreshScreen();

        detailsDialog.show();  // Show as Modal if only one row selected
        dataChanged = detailsDialog.dataSaved;
        if (selectedRows.length == 1)
          detailsDialog.dispose();
        else
          mNumDetailDialogsOpen++;    // Track # of Non-Modal Detail Dialogs
      }
      else if (cat_type.startsWith("P")) {
        ExclusiveDetailsDialog detailsDialog = new ExclusiveDetailsDialog(this,
            mCamsDB,
            jtableDetails, mCategoryPathHash, selectedRows[i], i,
            selectedRows.length == 1, true);
        detailsDialog.refreshScreen();

        detailsDialog.show();  // Show as Modal if only one row selected
        dataChanged = detailsDialog.dataSaved;
        if (selectedRows.length == 1)
          detailsDialog.dispose();
        else
          mNumDetailDialogsOpen++;    // Track # of Non-Modal Detail Dialogs
      }
      else if (cat_type.equalsIgnoreCase("Image")) {
        ImageDetailsDialog detailsDialog = new ImageDetailsDialog(this, mCamsDB,
            jtableDetails, mCategoryPathHash, selectedRows[i], i,
            selectedRows.length == 1);
        detailsDialog.refreshScreen();

        // Show as Modal if only one row selected
        detailsDialog.show();
        dataChanged = detailsDialog.dataSaved;
        if (selectedRows.length == 1)
          detailsDialog.dispose();
      }

    }

    // Refresh Current Folder in case items were moved out (Categories changed)
    // Only if detail dialog was shown modal and at least one record was saved
    if ((selectedRows.length == 1) && dataChanged)
      refreshDetailTable(node, true);
  }

  /**
   * Track # of Open Detail Dialogs so we can refresh the
   * screen when the last one is closed
   *
   * @param dataSaved boolean
   */
  public void doDetailWindowClosing(boolean dataSaved) {
    mNumDetailDialogsOpen--;
    if (dataSaved) mAnyDetailDialogsSaved = dataSaved;

    if (mNumDetailDialogsOpen < 1) {
      cams.console.Console.println("Last non-modal detail window closed.");
      if (mAnyDetailDialogsSaved) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                               jTreeFolders.getLastSelectedPathComponent();
        refreshDetailTable(node, true);
      }
      mNumDetailDialogsOpen = 0;
      mAnyDetailDialogsSaved = false;
    }
//    else {
//      theTableSorter.fireTableDataChanged();
//    }
  }

  // Remove Asset from Category
  void jMenuRemoveAsset_actionPerformed(ActionEvent e) {
    int selectedRows[] = jtableDetails.getSelectedRows();
//  int selectedRow = jtableDetails.getSelectedRow();
    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();

    if (selectedRows.length < 1) return;

    ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(sorter.modelIndex(selectedRows[0]));

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeFolders.getLastSelectedPathComponent();

    CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();

    if ((theNode.id < 0) && (theNode.catalog_id == -1)) {
      JOptionPane.showMessageDialog(this, "You can not remove records from a Batch.",
                                    "Invalid Operation", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

//      if (selectedRows.length == 1) {
//        if (JOptionPane.showConfirmDialog(this,
//                                          "Remove Record '" +
//                                          theRecord.getRecordName() +
//                                          "' from current list?  " +
//                                          "The original file " +
//                                          "will not be removed.",
//                                          "Remove Record",
//                                          JOptionPane.YES_NO_CANCEL_OPTION,
//                                          JOptionPane.QUESTION_MESSAGE) !=
//            JOptionPane.YES_OPTION)
//          return;
//      }
//      else {
//        if (JOptionPane.showConfirmDialog(this,
//                                          "Remove " + selectedRows.length +
//                                          " records from current list?  " +
//                                          "The original files " +
//                                          "will not be removed.",
//                                          "Remove Records",
//                                          JOptionPane.YES_NO_CANCEL_OPTION,
//                                          JOptionPane.QUESTION_MESSAGE) !=
//            JOptionPane.YES_OPTION)
//          return;
//      }

    String sql = "";

    // First Make ArrayList of Records to remove
    ArrayList recordsToRemove = new ArrayList();
    for (int i=0; i < selectedRows.length; i++)
      recordsToRemove.add((ImageLibRecord) theModel.getData().get(sorter.modelIndex(selectedRows[i])));

    // Now Remove them
    for (int i=0; i < recordsToRemove.size(); i++) {
      theRecord = (ImageLibRecord) recordsToRemove.get(i);

      if (!mShowSearchResults)
        ;
//        sql = "DELETE FROM ImageLibCategoryRecord WHERE " +
//            "record_id = " + theRecord.getRecordId() + " " +
//            "AND category_id = " + theNode.id;
      else {
        // Actually remove from search results
        sql = "DELETE FROM ImageLibSearch WHERE " +
            "record_id = " + theRecord.getRecordId() + " " +
            "AND search_user = '" + mCamsDB.getUserInfo().getSearchID() + "'";

        mCamsDB.execute(sql);
      }

      theModel.getData().remove(theRecord);

      if (theModel.getData().size() == 0)
        jlblStatus.setText("");
      else
        jlblStatus.setText("Displaying Assets 1 to " + theModel.getData().size() +
                           "  (" + theModel.getTotalRecords() + " Total)");

    }

    theTableSorter.fireTableDataChanged();
    if (jScrollGallery.isVisible())
      refreshGallery();
  }

  void jMenuRenameCategory_actionPerformed(ActionEvent e) {
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
        jTreeFolders.getLastSelectedPathComponent();

    if (treeNode == null) return;

    CamsTreeNode theNode = (CamsTreeNode) treeNode.getUserObject();

    String newName = null;
    String sql = "";

    // Catalog
    if ((theNode.id == -1) && (theNode.catalog_id > 0)) {
      newName =
          JOptionPane.showInputDialog(this, "Enter new name for Catalog '" +
                                      theNode.name + "'", "Rename Catalog",
                                      JOptionPane.QUESTION_MESSAGE);
      sql = "UPDATE ImageLibCatalog SET catalog_name = '" + newName.replaceAll("'", "''") +
          "' WHERE catalog_id = " + theNode.catalog_id;
    } else {
    // Category
      newName =
          JOptionPane.showInputDialog(this, "Enter new name for Category '" +
                              theNode.name + "'", "Rename Category",
                              JOptionPane.QUESTION_MESSAGE);
      sql = "UPDATE ImageLibCategory SET category_name = '" + newName.replaceAll("'", "''") +
          "' WHERE category_id = " + theNode.id;
    }

    if ((newName == null) || (newName.length() == 0)) return;

    mCamsDB.execute(sql);

    theNode.name = newName;
    jTreeFolders.updateUI();
//    DefaultTreeModel treeModel = (DefaultTreeModel) jTreeFolders.getModel();
//    treeModel.reload();
  }

  void clearSearchResults() {
    mShowSearchResults = false;
    mSearchOrder = "";

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    doEnableDisableFrame(false);

    // Reload Tree
    configCatalogTree();
    refreshCatalogTree();

    // Simulate Clicking on Catalog Node to clear Detail Table
//    CamsTreeNode tempNode = new CamsTreeNode();
//    tempNode.catalog_id = 999;
//    tempNode.id = -1;
    refreshDetailTable((DefaultMutableTreeNode)treeTop.getFirstChild());

    doEnableDisableFrame(true);
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  void jbtnXSearch_actionPerformed(ActionEvent e) {
      clearSearchResults();
      if (mSearchDialog != null)
        mSearchDialog.dispose();
      mSearchDialog = null;
  }

  void jbtnSearch_actionPerformed(ActionEvent e) {
    // Get selected Catalog
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        jTreeFolders.getLastSelectedPathComponent();
    if (node == null) {
      jTreeFolders.setSelectionRow(0);
      node = (DefaultMutableTreeNode) jTreeFolders.getLastSelectedPathComponent();
    }
    CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();

    if (mSearchDialog == null)
      mSearchDialog = new SearchDialog(this, mCamsDB, theNode);
    else
      mSearchDialog.reDisplay(theNode);

    mSearchDialog.setSize(mSearchDialog.getSize().width + 10, mSearchDialog.getSize().height + 10);

    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = mSearchDialog.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    mSearchDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    mSearchDialog.setVisible(true);

    if (mSearchDialog.getSearchResultCount() > 0)
      showSearchResults();
//    else
//      clearSearchResults();
  }

  private void showSearchResults() {
    mShowSearchResults = true;
    if (mSearchDialog != null)
      mSearchOrder = mSearchDialog.getOrderBy();

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    doEnableDisableFrame(false);

    // Reload Tree
    configCatalogTree();
    refreshCatalogTree();

//    removeEmptyFolders(treeTop);

    DefaultTreeModel treeModel = (DefaultTreeModel) jTreeFolders.getModel();
    treeModel.reload();

    // Expand All Remaining Folders
// 2/28/05 - Commented to speed up app
//    for (int i=0; i < jTreeFolders.getRowCount(); i++) {
//      jTreeFolders.expandRow(i);
//      jTreeFolders.updateUI();
//    }

    jTreeFolders.expandRow(0);
    jTreeFolders.setSelectionRow(0);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeFolders.getLastSelectedPathComponent();
    if ((node != null) && (!node.equals(batchesNode))) {
//      CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();
      refreshDetailTable(node);
    }
    else {
      // Simulate Clicking on Catalog Node to clear Detail Table
//    CamsTreeNode tempNode = new CamsTreeNode();
//    tempNode.catalog_id = 999;
//    tempNode.id = -1;
      refreshDetailTable((DefaultMutableTreeNode)treeTop.getFirstChild());
    }

    doEnableDisableFrame(true);

    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  public void doEnableDisableFrame(boolean enabled) {
    jToolBar.setEnabled(enabled);
    jTreeFolders.setEnabled(enabled);

    Component[] theButtons = jToolBar.getComponents();

    for (int i=0; i<theButtons.length; i++) {
      theButtons[i].setEnabled(enabled);
    }

    jbtnXSearch.setEnabled(enabled && mShowSearchResults);

    jMenuView.setEnabled(enabled);
  }

  void jMenuPreviewAsset_actionPerformed(ActionEvent e) {
    int[] selectedRows = jtableDetails.getSelectedRows();
    int selectedRow = jtableDetails.getSelectedRow();

    if (jtableDetails.getSelectedRowCount() == 0) {
      JOptionPane.showMessageDialog(this, "No Records Selected.  One or more " +
                                    "records must be selected before selecting " +
                                    "this command.", "No Records Selected",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel) sorter.getTableModel();

    for (int i=0; i < selectedRows.length; i++) {
      ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(sorter.
          modelIndex(selectedRows[i]));

      theRecord.getThumbXLarge(mCamsDB); // Make sure XLarge image is loaded
      ViewImageDialog preview = new ViewImageDialog(new JFrame(), "Preview Image",
          mThumbXLarge, theRecord, mCamsDB);
      preview.setProperties(mCamsDB.getProperties());
      preview.show();
      if (selectedRows.length > 0) // Cascade Windows
        preview.setLocation(50 + i * 50, 50 + i * 50);
    }
    // preview.dispose();
  }

  /**
   * Traverse the Tree and remove folders that contain no records
   * - Start with higher level folders and move down
   */
  void removeEmptyFolders(DefaultMutableTreeNode startLevel) {
    if (startLevel.getChildCount() == 0) return;

    cams.console.Console.println("removeEmptyFolders - Start");
    DefaultMutableTreeNode theTreeNode = (DefaultMutableTreeNode) startLevel.getFirstChild();
    while (theTreeNode != null) {
      // Get Top Level Folders under 'startLevel'
      if (theTreeNode.equals(batchesNode)) {
        theTreeNode = theTreeNode.getNextSibling();
        continue; // Do nothing with this branch
      }

      CamsTreeNode theCamsNode = (CamsTreeNode) theTreeNode.getUserObject();

      if ((theCamsNode.id == -1) && (theCamsNode.catalog_id > 0)) {
        // Top level catalog, can't count or delete this folder
        removeEmptyFolders(theTreeNode);
        theTreeNode = theTreeNode.getNextSibling();
      }
      else {
        // Found category node, Not Catalog, Not Batches
        ArrayList numAssetsResult = getNumAssetsInFolder(theTreeNode);
        long numAssets = ((Long) numAssetsResult.get(0)).longValue();
        if (numAssets > 0) {
          // Found records, try the folder(s) below
          cams.console.Console.println("Found " + numAssets + " assets in and below folder " + theCamsNode.fullPath);
          removeEmptyFolders(theTreeNode);
          theTreeNode = theTreeNode.getNextSibling();
        }
        else {
          // This folder (and those below) are empty
          cams.console.Console.println("Found no assets in and below folder " + theCamsNode.fullPath);
          DefaultMutableTreeNode nodeToDelete = theTreeNode;
          theTreeNode = theTreeNode.getNextSibling();
          nodeToDelete.removeFromParent();
        }
      }
    }
    cams.console.Console.println("removeEmptyFolders - Finish");
  }

  void jbtnPrevious_actionPerformed(ActionEvent e) {
    int mImagesPerPage = 100;
    if (jbtnThumbSmall.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeSmall", "100"));
    else if (jbtnThumbMedium.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeMedium", "50"));
    else if (jbtnThumbLarge.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeLarge", "25"));

    mGalleryStartIndex -= mImagesPerPage;
    refreshGallery();
  }

  void jbtnNext_actionPerformed(ActionEvent e) {
    int mImagesPerPage = 100;
    if (jbtnThumbSmall.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeSmall", "100"));
    else if (jbtnThumbMedium.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeMedium", "50"));
    else if (jbtnThumbLarge.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeLarge", "25"));

    mGalleryStartIndex += mImagesPerPage;
    refreshGallery();
  }

  void jbtnFirst_actionPerformed(ActionEvent e) {
    mGalleryStartIndex = 0;
    refreshGallery();
  }

  void jbtnLast_actionPerformed(ActionEvent e) {
    int mImagesPerPage = 100;
    if (jbtnThumbSmall.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeSmall", "100"));
    else if (jbtnThumbMedium.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeMedium", "50"));
    else if (jbtnThumbLarge.isSelected())
      mImagesPerPage = Integer.parseInt(mCamsDB.getProperties().getProperty("PageSizeLarge", "25"));

    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    ArrayList theData = theModel.getData();

    mGalleryStartIndex = 0;
    while (mGalleryStartIndex < theData.size())
      mGalleryStartIndex += mImagesPerPage;

    mGalleryStartIndex -= mImagesPerPage;
    refreshGallery();
  }

  void jmnuShowBatches_actionPerformed(ActionEvent e) {
    if (jmnuShowBatches.getText().startsWith("Show"))
      // Show Batches
      jmnuShowBatches.setText("Hide Import Batches");
    else
      // Hide Batches
      jmnuShowBatches.setText("Show Import Batches");

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    doEnableDisableFrame(false);

    // Reload Tree
    configCatalogTree();
    refreshCatalogTree();

    // Simulate Clicking on Catalog Node to clear Detail Table
//    CamsTreeNode tempNode = new CamsTreeNode();
//    tempNode.catalog_id = 999;
//    tempNode.id = -1;
    refreshDetailTable((DefaultMutableTreeNode)treeTop.getFirstChild());


    doEnableDisableFrame(true);
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

  }

  ActionListener refreshMemory = new ActionListener() {
    public void actionPerformed(ActionEvent evt) {
      jProgress.setMaximum( (int) MemoryInfo.getMaxHeapSize());
      jProgress.setValue( (int) MemoryInfo.getCurrentHeapSize());
      jProgress.setString(MemoryInfo.getCurrentHeapSize() + "M of " +
                          MemoryInfo.getMaxHeapSize() + "M");
    }
  };

  JMenuItem jMenuPasteAsset = new JMenuItem();
  JMenu jMenuAsset = new JMenu();
  JMenuItem jMenuAssetDelete = new JMenuItem();
  JMenuItem jMenuAssetRemove = new JMenuItem();
  JMenuItem jMenuAssetPreview = new JMenuItem();
  JMenuItem jMenuAssetEdit = new JMenuItem();
  JMenuItem jMenuAssetBatchEdit = new JMenuItem();
  JMenuItem jMenuAssetPrint = new JMenuItem();
  JMenuItem jMenuAssetRedirect = new JMenuItem();
  JMenu jMenuSort = new JMenu();
  JMenuItem jMenuSortRecordName = new JMenuItem();
  JMenuItem jMenuSortScanNum = new JMenuItem();
  JMenuItem jMenuAssetRotateRight = new JMenuItem();
  JMenuItem jMenuAssetRotateLeft = new JMenuItem();
  JMenuItem jMenuCancelSearch = new JMenuItem();
  JMenu jMenuRecord = new JMenu();
  JMenuItem jmnuMaintainKeywords = new JMenuItem();
  JMenuItem jmnuEditProps = new JMenuItem();
  JMenuItem jMenuSelectAll = new JMenuItem();
  JMenuItem jMenuReplaceThumbnail = new JMenuItem();
  JLabel jLabelSpacer = new JLabel();
  JButton jbtnFirst = new JButton();
  JButton jbtnLast = new JButton();
  JMenuItem jMenuSearchPlus = new JMenuItem();

  void jProgress_mouseClicked(MouseEvent e) {
    Runtime.getRuntime().gc();
    doRefreshMemory();
  }

  void doRefreshMemory() {
    refreshMemory.actionPerformed(new ActionEvent(this, -1, null));
  }

  class ImageLibClipboard {
    ImageLibRecord theRecord;
    DefaultMutableTreeNode treeNode;

    public ImageLibClipboard(ImageLibRecord record, DefaultMutableTreeNode node) {
      theRecord = record;
      treeNode = node;
    }
  }

  void jMenuCutAsset_actionPerformed(ActionEvent e) {
    cutCopyAssets(true);
  }

  void jMenuCopyAsset_actionPerformed(ActionEvent e) {
    cutCopyAssets(false);
  }

  private void cutCopyAssets(boolean doCut) {
    mCutAssets = doCut;
    mAssetsToCopy.clear();

    int selectedRows[] = jtableDetails.getSelectedRows();

    if (selectedRows.length == 0) {
      JOptionPane.showMessageDialog(this, "No assets selected.", "No Assets Selected",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeFolders.getLastSelectedPathComponent();

    for (int i=0; i < selectedRows.length; i++) {
      ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
          sorter.modelIndex(selectedRows[i]));
      mAssetsToCopy.add(new ImageLibClipboard(theRecord, node));
    }
  }

  void jMenuPasteAsset_actionPerformed(ActionEvent e) {
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
        jTreeFolders.getLastSelectedPathComponent();

    if (treeNode == null) return;

    if (mAssetsToCopy.size() == 0) {
      JOptionPane.showMessageDialog(this, "No assets have been cut or copied to " +
                                   "the clipboard.", "Nothing to Paste",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    CamsTreeNode thisNode = (CamsTreeNode) treeNode.getUserObject();

    // Are these records being pasted to the same or different catalog?
    ImageLibClipboard firstAsset = (ImageLibClipboard) mAssetsToCopy.get(0);
    if (firstAsset.theRecord.getCatalogId() == thisNode.catalog_id) {
      cams.console.Console.println("Pasting " + mAssetsToCopy.size() + " records within SAME " +
                      "Catalog but Different Category");
      // Same Catalog, Just Add to this Category
      for (int i=0; i < mAssetsToCopy.size(); i++) {
        ImageLibClipboard clipboard = (ImageLibClipboard) mAssetsToCopy.get(i);
        ImageLibRecord theRecord = clipboard.theRecord;
        // Make sure it's not already assigned to this category
        String sql = "DELETE FROM ImageLibCategoryRecord WHERE record_id = " +
            theRecord.getRecordId() + " AND category_id = " + thisNode.id;
        mCamsDB.execute(sql);
        // Now add the category
        sql = "INSERT INTO ImageLibCategoryRecord (category_id, record_id) " +
            "VALUES (" + thisNode.id + "," + theRecord.getRecordId() + ")";
        mCamsDB.execute(sql);

        // If we're searching, add to Search results
        if (mShowSearchResults) {
          sql = "INSERT INTO ImageLibSearch (search_user, search_date, record_id) " +
              "VALUES ('" + mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
              theRecord.getRecordId() + ")";
          mCamsDB.execute(sql);
        }
      }
    }
    else {
      cams.console.Console.println("Pasting " + mAssetsToCopy.size() + " records within DIFFERENT " +
                      "Catalog");

      // New Catalog, Copy Record to new Catalog
      SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

      int batchId = mCamsDB.getNewBatchId("Clipboard");

      for (int i = 0; i < mAssetsToCopy.size(); i++) {
        ImageLibClipboard clipboard = (ImageLibClipboard) mAssetsToCopy.get(i);
        ImageLibRecord theRecord = clipboard.theRecord;

        String uniqueId = df.format(new java.util.Date());

        // Copy Record (replace RecordName with UniqueId so we can find the Record_Id
        String sql =
            "INSERT INTO ImageLibRecord " +
            "(batch_id, catalog_id, RecordName, Status_id, FullPathMac, FullPathWin, " +
            "FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive, " +
            "Area, OrigOnFile, ModelRelease, FileSize, FileFormat, " +
            "RecordCreated, FileDate, LastUpdate, ScanNum, UsageRules, Release_id, " +
            "ImageCompany, Advertiser, OriginalKeywords, ThumbSmall, ThumbMedium, " +
            "ThumbLarge, ThumbXLarge, PNumber, WebDisplay, PriorityDisplay) " +
            "SELECT " + batchId + ", " + thisNode.catalog_id + ", '" + uniqueId + "', " +
            "Status_id, FullPathMac, FullPathWin, " +
            "FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive, " +
            "Area, OrigOnFile, ModelRelease, FileSize, FileFormat, " +
            "RecordCreated, FileDate, LastUpdate, ScanNum, UsageRules, Release_id, " +
            "ImageCompany, Advertiser, OriginalKeywords, ThumbSmall, ThumbMedium, " +
            "ThumbLarge, ThumbXLarge, PNumber, WebDisplay, PriorityDisplay " +
            "FROM ImageLibRecord WHERE record_id = " + theRecord.getRecordId();
        mCamsDB.execute(sql);

        // Now read it back to get record_id
        sql = "SELECT record_id FROM ImageLibRecord WHERE " +
            "RecordName = '" + uniqueId + "'";
        int newRecordId = Integer.parseInt(mCamsDB.querySingleString(sql));

        // Now fix RecordName
        sql = "UPDATE ImageLibRecord SET RecordName = '" +
            theRecord.getRecordName().replaceAll("'", "''") + "' WHERE " +
            "record_id = " + newRecordId;
        mCamsDB.execute(sql);

        // Add the Notes
        sql = "INSERT INTO ImageLibNotes (record_id, notes) " +
              "SELECT " + newRecordId + ", notes FROM ImageLibNotes " +
              "WHERE record_id = " + theRecord.getRecordId();
        mCamsDB.execute(sql);

        // Add the Keywords
        sql = "INSERT INTO ImageLibKeywordRecord (keyword_id, record_id) " +
              "SELECT keyword_id, " + newRecordId + " FROM ImageLibKeywordRecord " +
              "WHERE record_id = " + theRecord.getRecordId();
        mCamsDB.execute(sql);

        // Finally Add to this Category
        sql = "INSERT INTO ImageLibCategoryRecord (category_id, record_id) " +
            "VALUES (" + thisNode.id + "," + newRecordId + ")";
        mCamsDB.execute(sql);

        // If we're searching, add to Search results
        if (mShowSearchResults) {
          sql = "INSERT INTO ImageLibSearch (search_user, search_date, record_id) " +
              "VALUES ('" + mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
              newRecordId + ")";
          mCamsDB.execute(sql);
        }
      }
    }

    CamsTreeNode theNode = (CamsTreeNode) firstAsset.treeNode.getUserObject();
    if ((theNode.id < 0) && (theNode.catalog_id == -1))
      // Can not delete from Batch
      mCutAssets = false;

//  if (mShowSearchResults) mCutAssets = false;

    if (mCutAssets) {
      cams.console.Console.println("Now removing (cut) " + mAssetsToCopy.size() + " records " +
                      "from original category");
      // Delete the Clipboard Assets from their Original Categories
      for (int i = 0; i < mAssetsToCopy.size(); i++) {
        ImageLibClipboard clipboard = (ImageLibClipboard) mAssetsToCopy.get(i);
        ImageLibRecord theRecord = clipboard.theRecord;
        theNode = (CamsTreeNode) clipboard.treeNode.getUserObject();
        String sql = "DELETE FROM ImageLibCategoryRecord WHERE " +
            "record_id = " + theRecord.getRecordId() + " AND " +
            "category_id = " + theNode.id;
        mCamsDB.execute(sql);
      }
    }

    mAssetsToCopy.clear();
    mCutAssets = false;

    refreshDetailTable(treeNode);
  }

  void jMenuSortRecordName_actionPerformed(ActionEvent e) {
    int sort = theTableSorter.getSortingStatus(0);
    if (sort == 1)
      sort = -1;
    else
      sort = 1;

    for (int i = 0; i < theTableSorter.getColumnCount(); i++)
      theTableSorter.setSortingStatus(i, 0);

    theTableSorter.setSortingStatus(0, sort);

    if (jScrollGallery.isVisible()) {
      mGalleryStartIndex = 0;
      refreshGallery();
    }
  }

  void jMenuSortScanNum_actionPerformed(ActionEvent e) {
    int sort = theTableSorter.getSortingStatus(3);
    if (sort == 1)
      sort = -1;
    else
      sort = 1;

    for (int i = 0; i < theTableSorter.getColumnCount(); i++)
      theTableSorter.setSortingStatus(i, 0);

    theTableSorter.setSortingStatus(3, sort);

    if (jScrollGallery.isVisible()) {
      mGalleryStartIndex = 0;
      refreshGallery();
    }
  }


  // Update FullPathMac / FullPathWin for selected Asset
  void jMenuAssetRedirect_actionPerformed(ActionEvent e) {
    if (jtableDetails.getSelectedRowCount() != 1) {
      JOptionPane.showMessageDialog(this,
                                    "Please select a single asset before " +
                                    "selecting this option.",
                                    "Invalid Operation",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    int selectedRow = jtableDetails.getSelectedRow();

    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel) sorter.getTableModel();
    ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
        sorter.modelIndex(selectedRow));

    JFileChooser chooser = new JFileChooser();

    try {
      File f = new File(new File(theRecord.getFullPath(mCamsDB.windowsOS())).
                        getCanonicalPath());

      // Set the selected file
      chooser.setSelectedFile(f);
    }
    catch (Exception exc) {
    }

    // Show the dialog; wait until dialog is closed
    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
      // Get the currently selected file
      File curFile = chooser.getSelectedFile();
      if (mCamsDB.windowsOS()) {
        theRecord.setFullPathMac(null);
        theRecord.setFullPathWin(curFile.getAbsolutePath());
      }
      else { // Mac OS
        theRecord.setFullPathMac(curFile.getAbsolutePath());
        theRecord.setFullPathWin(null);
      }

      theRecord.setFileName(curFile.getName());
      theRecord.setFileSize(curFile.length());
      theRecord.setFileDate(new java.util.Date(curFile.lastModified()));
      // Just pick off file extension for now
      if (curFile.getName().indexOf(".") > -1)
        theRecord.setFileFormat(curFile.getName().substring(curFile.getName().lastIndexOf(".") + 1).toUpperCase());

      theRecord.redirectReference(mCamsDB);
    }

  }

  /**
   * Replace Thumbnail for the selected asset
   */
  void jMenuReplaceThumbnail_actionPerformed(ActionEvent e) {
    if (jtableDetails.getSelectedRowCount() != 1) {
      JOptionPane.showMessageDialog(this,
                                    "Please select a single asset before " +
                                    "selecting this option.",
                                    "Invalid Operation",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    int selectedRow = jtableDetails.getSelectedRow();

    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel) sorter.getTableModel();
    ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
        sorter.modelIndex(selectedRow));

    JFileChooser chooser = new JFileChooser();

    try {
      File f = new File(new File(theRecord.getFullPath(mCamsDB.windowsOS())).
                        getCanonicalPath());

      // Set the selected file
      chooser.setSelectedFile(f);
    }
    catch (Exception exc) {
    }

    // Show the dialog; wait until dialog is closed
    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
      // Get the currently selected file
      File curFile = chooser.getSelectedFile();

      String colorSpace = (String) JOptionPane.showInputDialog(this,
          "Select colorspace of original image:",
          "Colorspace Selection",
          JOptionPane.QUESTION_MESSAGE, null,
          new String[] {"CMYK", "RGB"}
          , "CMYK");

      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      int[] theSizes = {mThumbXLarge, mThumbLarge, mThumbMedium, mThumbSmall};
      String[] theThumbFileNames = mCamsDB.getImaging().createThumbnails(
          curFile.getPath(), theSizes, colorSpace.equals("CMYK"));
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

      if (theThumbFileNames != null) {
        boolean thumbSuccess = ImageLibRecordList.updateThumbnails(mCamsDB, theRecord,
            theThumbFileNames);
        theRecord.setThumbSmall(null);
        theRecord.setThumbMedium(null);
        theRecord.setThumbLarge(null);
        theRecord.setThumbXLarge(null);

        // Update thumbnail on screen
        if (jScrollGallery.isVisible())
          refreshGallery();
      }
      else
        JOptionPane.showMessageDialog(new Frame(),
                                      "Error replacing thumbnails.", "Thumbnail Error",
                                      JOptionPane.ERROR_MESSAGE);
    }
  }

  void jMenuAssetRotateRight_actionPerformed(ActionEvent e) {
    if (!jScrollGallery.isVisible()) {
      JOptionPane.showMessageDialog(new Frame(),
                                    "This option is only available when " +
                                    "the gallery (Thumbnail) mode is active.",
                                    "Invalid Operation",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (jtableDetails.getSelectedRowCount() == 0) {
      JOptionPane.showMessageDialog(new Frame(),
                                    "Please select at least one asset before " +
                                    "selecting this option.",
                                    "Invalid Operation",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    new DoRotateAssets(90).start();
  }

  void jMenuAssetRotateLeft_actionPerformed(ActionEvent e) {
    if (!jScrollGallery.isVisible()) {
      JOptionPane.showMessageDialog(new Frame(),
                                    "This option is only available when " +
                                    "the gallery (Thumbnail) mode is active.",
                                    "Invalid Operation",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (jtableDetails.getSelectedRowCount() == 0) {
      JOptionPane.showMessageDialog(new Frame(),
                                    "Please select at least one asset before " +
                                    "selecting this option.",
                                    "Invalid Operation",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    new DoRotateAssets(-90).start();
  }

  class DoRotateAssets extends Thread {
    private int mRotation = 90;

    public DoRotateAssets(int rotation) {
      mRotation = rotation;
    }

    public void run() {
      int selectedRows[] = jtableDetails.getSelectedRows();

      TableSorter sorter = (TableSorter) jtableDetails.getModel();
      ImageLibTableModel theModel = (ImageLibTableModel) sorter.getTableModel();

      int[] theSizes = {
          mThumbXLarge, mThumbLarge, mThumbMedium, mThumbSmall};
      ImageMagick imaging = new ImageMagick(mCamsDB);

      for (int i = 0; i < selectedRows.length; i++) {
        ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[i]));

        if (theRecord.getThumbXLarge(mCamsDB) == null)
          continue; // No thumbnail loaded, skip this record

        try {
          String tempDir = imaging.getTempDir();
          String appDir = imaging.getImageAppDir();

          // Save current JPG
          String rotate_in = tempDir + "rotate_in.jpg";
          String rotate_out = tempDir + "rotate_out.jpg";
          imaging.deleteFiles(tempDir, "Rotate", "jpg");

          File theFile = new File(rotate_in);
          OutputStream fileOutStream = new FileOutputStream(theFile);
          fileOutStream.write(theRecord.getThumbXLarge());
          fileOutStream.close();
          theFile = null;

          String theCommand = "\"" + appDir + "convert\" \"" + rotate_in +
              "\" -rotate " + mRotation +
              " \"" + rotate_out + "\"";
          String result = imaging.shellCmd(theCommand);

          String[] theThumbFileNames = imaging.createThumbnails(rotate_out,
              theSizes, false);
          if (theThumbFileNames != null) {
            ImageLibRecordList.updateThumbnails(mCamsDB, theRecord,
                                                theThumbFileNames);
            theRecord.setThumbSmall(null);
            theRecord.setThumbMedium(null);
            theRecord.setThumbLarge(null);
            theRecord.setThumbXLarge(null);
          }
        }
        catch (Exception ex) {
          cams.console.Console.println("Error rotating Thumbnails: " + ex.getMessage());
        }
      }

      refreshGallery();
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }

  /**
   * DeleteAsset: Remove Asset from All Categories (Still in Import Batch)
   */
  void jMenuAssetDelete_actionPerformed(ActionEvent e) {
    int selectedRows[] = jtableDetails.getSelectedRows();
    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();

    if (jtableDetails.getSelectedRowCount() == 0) {
      JOptionPane.showMessageDialog(this, "No Records Selected.  One or more " +
                                    "records must be selected before selecting " +
                                    "this command.", "No Records Selected",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(sorter.modelIndex(selectedRows[0]));

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeFolders.getLastSelectedPathComponent();

    CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();

    if ((theNode.id < 0) && (theNode.catalog_id == -1)) {
      JOptionPane.showMessageDialog(this, "You can not delete records from a Batch.",
                                    "Invalid Operation", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

      if (selectedRows.length == 1) {
        if (JOptionPane.showConfirmDialog(this,
                                          "Delete Record '" +
                                          theRecord.getRecordName() +
                                          "' from all categories?  " +
                                          "The original file " +
                                          "will not be deleted.",
                                          "Delete Record",
                                          JOptionPane.YES_NO_CANCEL_OPTION,
                                          JOptionPane.QUESTION_MESSAGE) !=
            JOptionPane.YES_OPTION)
          return;
      }
      else {
        if (JOptionPane.showConfirmDialog(this,
                                          "Remove " + selectedRows.length +
                                          " records from all categories?  " +
                                          "The original files " +
                                          "will not be removed.",
                                          "Delete Records",
                                          JOptionPane.YES_NO_CANCEL_OPTION,
                                          JOptionPane.QUESTION_MESSAGE) !=
            JOptionPane.YES_OPTION)
          return;
      }

    String sql = "";

    // First Make ArrayList of Records to remove
    ArrayList recordsToRemove = new ArrayList();
    for (int i=0; i < selectedRows.length; i++)
      recordsToRemove.add((ImageLibRecord) theModel.getData().get(sorter.modelIndex(selectedRows[i])));

    // Now Remove them
    for (int i=0; i < recordsToRemove.size(); i++) {
      theRecord = (ImageLibRecord) recordsToRemove.get(i);

      if (mShowSearchResults) {
        // Actually remove from search results
        sql = "DELETE FROM ImageLibSearch WHERE " +
            "record_id = " + theRecord.getRecordId() + " " +
            "AND search_user = '" + mCamsDB.getUserInfo().getSearchID() + "'";

        mCamsDB.execute(sql);
      }

      // Remove all categories for this record
      theRecord.setCategories(new ArrayList());
      theRecord.updateRecordCategories(mCamsDB);

      // For Image Type, actually delete from ImageLibRecord
      if (theNode.catalog_type.equalsIgnoreCase("Image")) {
        sql = "DELETE FROM ImageLibRecord WHERE record_id = " +
            theRecord.getRecordId();
        mCamsDB.execute(sql);
      }

      theModel.getData().remove(theRecord);
    }

    theTableSorter.fireTableDataChanged();
    if (jScrollGallery.isVisible())
      refreshGallery();
  }

  /**
   * Batch Edit Multiple Records.  Show a clean screen and batch save new
   * entries to all selected records
   *
   * @param e ActionEvent
   */
  void jMenuAssetBatchEdit_actionPerformed(ActionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeFolders.getLastSelectedPathComponent();
    CamsTreeNode theNode = (CamsTreeNode) node.getUserObject();

    int selectedRows[] = jtableDetails.getSelectedRows();
    if (selectedRows.length < 2) {
      JOptionPane.showMessageDialog(this, "Two or more Assets should be selected " +
                                    "for Batch Edit mode.");
      return;
    }
    boolean dataChanged = false;

    String cat_type = getCatalogTypeFromID(getImageRecordByRow(selectedRows[0]).getCatalogId());

    if (cat_type.equalsIgnoreCase("Stock")) {
      StockDetailsDialog detailsDialog = new StockDetailsDialog(this, mCamsDB,
          jtableDetails, mCategoryPathHash, selectedRows);

      detailsDialog.show();  // Show as Modal if only one row selected
      dataChanged = detailsDialog.dataSaved;
      detailsDialog.dispose();
    }
      else if (cat_type.equalsIgnoreCase("Exclusive")) {
        ExclusiveDetailsDialog detailsDialog = new ExclusiveDetailsDialog(this,
            mCamsDB,
            jtableDetails, mCategoryPathHash, selectedRows, false);

        detailsDialog.show();  // Show as Modal if only one row selected
        dataChanged = detailsDialog.dataSaved;
        detailsDialog.dispose();
      }
      else if (cat_type.startsWith("P")) {
        ExclusiveDetailsDialog detailsDialog = new ExclusiveDetailsDialog(this,
            mCamsDB,
            jtableDetails, mCategoryPathHash, selectedRows, true);

        detailsDialog.show();  // Show as Modal if only one row selected
        dataChanged = detailsDialog.dataSaved;
        detailsDialog.dispose();
      }
      else if (cat_type.equalsIgnoreCase("Image")) {
        JOptionPane.showMessageDialog(this, "Batch Edit not available for Image catalog");
        return;
      }

    // Refresh Current Folder in case items were moved out (Categories changed)
    // Only if detail dialog was shown modal and at least one record was saved
    if (dataChanged)
      refreshDetailTable(node, true);
  }

  private String formatDate(java.util.Date theDate) {
//    return new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").format(theDate);
    return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(theDate);
  }

  // Keep shortening string until it fits within specified maxWidth
  private String formatPrintString(String theText, FontMetrics fm, int maxWidth) {
    if (fm.stringWidth(theText) <= maxWidth)
      return theText;

    String result = theText;
    while (fm.stringWidth(result + "...") > maxWidth)
      result = result.substring(0, result.length() - 1);
    return result + "...";
  }

  void jMenuAssetPrint_actionPerformed(ActionEvent e) {
    if (jtableDetails.getSelectedRowCount() == 0) {
      JOptionPane.showMessageDialog(this, "No Records Selected.  One or more " +
                                    "records must be selected before selecting " +
                                    "this command.", "No Records Selected",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (jbtnThumbSmall.isSelected()) {
      htmlPrintSmallThumbnails();
    }
    else if (jbtnThumbMedium.isSelected()) {
      htmlPrintMediumThumbnails();
    }
    else if (jbtnThumbLarge.isSelected()) {
      htmlPrintLargeThumbnails();
    }

  }

  /**
   * PrintGalleryClass: Prints Image Gallery
   */
  class PrintGalleryClass extends JComponent implements Printable {
    private int mThisPage = 0;
    private int mPages = 0;
    private int mRowIndex = 0;
    private HashMap pageRowHash = new HashMap();

    public int print(Graphics g, PageFormat pf, int pageIndex) {
//    System.out.println("print: pageIndex = " + pageIndex + ", mPages = " + mPages);

      mThisPage = pageIndex;

      // Page 0 starts with SelectedRow=0
      if (pageRowHash.isEmpty())
        pageRowHash.put(new Integer(0), new Integer(0));

      if (pageIndex > mPages) {
        return Printable.NO_SUCH_PAGE;
      }

      mRowIndex = ((Integer) pageRowHash.get(new Integer(pageIndex))).intValue();
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pf.getImageableX(), pf.getImageableY());
      drawGraphics(g2d, pf);
      return Printable.PAGE_EXISTS;
    }

    private void drawGraphics(Graphics2D graphics, PageFormat pf) {
      TableSorter sorter = (TableSorter) jtableDetails.getModel();
      ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();

      int[] selectedRows = jtableDetails.getSelectedRows();
      graphics.setFont(new Font("Arial", Font.PLAIN, 9));

      int thumbSize = -1;
      if (jbtnThumbSmall.isSelected())
        thumbSize = mThumbSmall;
      else if (jbtnThumbMedium.isSelected())
        thumbSize = mThumbMedium;
      else if (jbtnThumbLarge.isSelected())
        thumbSize = mThumbLarge;

      int printX = 1;
      int printY = 1;

      int fontHeight = graphics.getFontMetrics().getHeight();

      for (int i = mRowIndex; i < jtableDetails.getSelectedRowCount(); i++) {
        ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[i]));
        byte[] theThumbBytes = null;
        if (jbtnThumbSmall.isSelected())
          theThumbBytes = theRecord.getThumbSmall(mCamsDB);
        else if (jbtnThumbMedium.isSelected())
          theThumbBytes = theRecord.getThumbMedium(mCamsDB);
        else if (jbtnThumbLarge.isSelected())
          theThumbBytes = theRecord.getThumbLarge(mCamsDB);

        if (theThumbBytes != null) {
          if (jbtnThumbSmall.isSelected()) {
            // Small Thumbnail, just show record name beneath
            Image theImage = new ImageIcon(theThumbBytes).getImage();
            graphics.drawImage(theImage, printX, printY, this);
            graphics.drawString(formatPrintString(theRecord.getRecordName(),
                                                  graphics.getFontMetrics(),
                                thumbSize), printX, printY + thumbSize + fontHeight);
            graphics.drawRect(printX - 1, printY - 1, thumbSize + 2, thumbSize + fontHeight + 3);
            printX += thumbSize + 5;
            if (printX + thumbSize > pf.getImageableWidth()) {
              printX = 1;
              printY = printY + thumbSize + fontHeight + 10;
              if (printY + thumbSize + fontHeight > pf.getImageableHeight()) {
                mRowIndex = i + 1;
                if (mThisPage == mPages) {
                  mPages++;
                  pageRowHash.put(new Integer(mPages), new Integer(mRowIndex));
                }
                return;
              }
            }
          }
          else {
            // Medium or Large Thumb, show RecordName,KeyLine,Scan# on Top
            //                             Status below
            Image theImage = new ImageIcon(theThumbBytes).getImage();
            graphics.drawString(formatPrintString(theRecord.getRecordName(),
                                                  graphics.getFontMetrics(),
                                thumbSize), printX, printY + fontHeight);
            graphics.drawString(formatPrintString(theRecord.getKeyline(),
                                                  graphics.getFontMetrics(),
                                thumbSize), printX, printY + fontHeight * 2);
            graphics.drawString(formatPrintString(theRecord.getScanNumber(),
                                                  graphics.getFontMetrics(),
                                thumbSize), printX, printY + fontHeight * 3);

            graphics.drawImage(theImage, printX, printY + fontHeight * 3 + 3, this);

            graphics.drawString(formatPrintString(theRecord.getRecordName(),
                                                  graphics.getFontMetrics(),
                                thumbSize), printX, printY + thumbSize + fontHeight * 4);

            graphics.drawRect(printX - 1, printY - 1, thumbSize + 2, thumbSize + fontHeight * 4 + 3);
            printX += thumbSize + 5;
            if (printX + thumbSize > pf.getImageableWidth()) {
              printX = 1;
              printY = printY + thumbSize + fontHeight * 4 + 10;
              if (printY + thumbSize + fontHeight * 4 > pf.getImageableHeight()) {
                mRowIndex = i + 1;
                if (mThisPage == mPages) {
                  mPages++;
                  pageRowHash.put(new Integer(mPages), new Integer(mRowIndex));
                }
                return;
              }
            }
          }
        }
      }
    }
  }

  /**
   * PrintDetailTableClass: Prints Detail Table
   */
  class PrintDetailTableClass extends JComponent implements Printable {
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
      int[] columnWidths = new int[theTableModel.getColumnCount()];
      int fontSize = 9;

      TableSorter sorter = (TableSorter) jtableDetails.getModel();
      ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();

//        ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
//            sorter.modelIndex(selectedRows[i]));

      int[] selectedRows = jtableDetails.getSelectedRows();

      // Loop through each column header to initialize column widths
      graphics.setFont(new Font("Arial", Font.BOLD, fontSize));
      for (int i=0; i < theModel.getColumnCount(); i++) {
        columnWidths[i] = graphics.getFontMetrics().stringWidth(theModel.getColumnName(i));
      }
      graphics.setFont(new Font("Arial", Font.PLAIN, fontSize));

      // Loop through each selected item to determine max column widths
      for (int i=0; i < jtableDetails.getSelectedRowCount(); i++)
        for (int j=0; j < theModel.getColumnCount(); j++) {
          String theData = null;
          if (theModel.getColumnClass(j).equals(java.util.Date.class))
            theData = formatDate((java.util.Date) theModel.getValueAt(sorter.modelIndex(selectedRows[i]), j));
          else
            theData = theModel.getValueAt(sorter.modelIndex(selectedRows[i]), j).toString();
          int stringWidth = graphics.getFontMetrics().stringWidth(theData);
          if (stringWidth > columnWidths[j])
            columnWidths[j] = stringWidth;
        }

      // Increase each column by 5
      int fullWidth = 0;
      for (int i=0; i < columnWidths.length; i++) {
        columnWidths[i] += 5;
        fullWidth += columnWidths[i];
      }

      // Make sure auto-fit columns still fits on Page
      if (fullWidth > pf.getImageableWidth()) {
        // Keep date columns and adjust 1st 4 columns to 25% each
        columnWidths[0] = (int) (pf.getImageableWidth() - columnWidths[4] - columnWidths[5]) / 4;
        columnWidths[1] = columnWidths[0];
        columnWidths[2] = columnWidths[0];
        columnWidths[3] = columnWidths[0];
      }

      // Now print Table Header
      int currentX = 0;
      int currentY = graphics.getFontMetrics().getHeight() + 1;

      // Make Bold
      graphics.setFont(new Font("Arial", Font.BOLD, fontSize));
      for (int col=0; col < theModel.getColumnCount(); col++) {
        graphics.drawString(theModel.getColumnName(col), currentX, currentY);
        currentX += columnWidths[col];
      }
      currentY += graphics.getFontMetrics().getHeight();

      // Restore normal style
      graphics.setFont(new Font("Arial", Font.PLAIN, fontSize));

      // Print the Rows
      for (int i=0; i < jtableDetails.getSelectedRowCount(); i++) {
        currentX = 0;
        for (int j = 0; j < theModel.getColumnCount(); j++) {
          String theData = null;
          if (theModel.getColumnClass(j).equals(java.util.Date.class))
            theData = formatDate((java.util.Date) theModel.getValueAt(sorter.modelIndex(selectedRows[i]), j));
          else
            theData = theModel.getValueAt(sorter.modelIndex(selectedRows[i]), j).toString();

          theData = formatPrintString(theData, graphics.getFontMetrics(), columnWidths[j]);
          graphics.drawString(theData, currentX, currentY);
          currentX += columnWidths[j];
        }
        currentY += graphics.getFontMetrics().getHeight();
      }

    }
  }

  void jmnuMaintainKeywords_actionPerformed(ActionEvent e) {
    KeywordMaintainDialog theKeyDialog = new KeywordMaintainDialog(this, mCamsDB, new ArrayList());
    theKeyDialog.show();
  }

  void jmnuEditProps_actionPerformed(ActionEvent e) {
    PropertiesDialog dlg = new PropertiesDialog(this, mCamsDB);
    dlg.setSize(dlg.getSize().width + 10, dlg.getSize().height + 10);

    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = dlg.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    dlg.setLocation( (screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);
    dlg.setVisible(true);
    dlg.dispose();
  }

  private long getMaxRows() {
     return Long.parseLong(mCamsDB.getProperties().getProperty("MaxRows", "1000"));
  }

  private String getCatalogTypeFromID(int catalog_id) {
    for (int i=0; i < mCatalogList.size(); i++) {
      CamsTreeNode theCamsNode = (CamsTreeNode) mCatalogList.get(i);
      if (theCamsNode.catalog_id == catalog_id)
        return theCamsNode.catalog_type;
    }
    return "";
  }

  void jtableDetails_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == e.VK_ENTER) {
      e.setKeyCode(0); // Process Key and remove from buffer
      jMenuEditProperties_actionPerformed(new ActionEvent(this, 0, null));
    }
    else if ((e.getKeyCode() == e.VK_X) && (e.getModifiersEx() != 0))
      cutCopyAssets(true); // Cut
    else if ((e.getKeyCode() == e.VK_C) && (e.getModifiersEx() != 0))
      cutCopyAssets(false); // Copy
    else if ((e.getKeyCode() == e.VK_V) && (e.getModifiersEx() != 0))
      jMenuPasteAsset_actionPerformed(new ActionEvent(this, 0, null)); // Paste
  }

  void thumbGallery_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == e.VK_ENTER) {
      e.setKeyCode(0); // Process Key and remove from buffer
      jMenuEditProperties_actionPerformed(new ActionEvent(this, 0, null));
//      refreshGallerySelected();
    }
  }

  void jMenuSelectAll_actionPerformed(ActionEvent e) {
    jtableDetails.selectAll();
    if (jScrollGallery.isVisible())
      refreshGallerySelected();
  }

  /**
   * Create HTML Printable View for Small Thumbnails
   * Medium thumbnail is 65px so print 8 columns across
   * and 8 down before a page break
   */
  void htmlPrintSmallThumbnails() { // JJS
    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    String tempDir = mCamsDB.getImaging().getTempDir();
    FileWriter fw = null;
    String lf = String.valueOf('\r') + String.valueOf('\n');
    String fileName = tempDir + "/cams_print.html";
    int maxCols = 8;
    int maxRows = 8;

    boolean isPNumber = false;

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
      cams.console.Console.println("Error opening html file for printing: " + ex.getMessage());
      return;
    }

    try {
      int col = 1;
      int row = 1;
      int[] selectedRows = jtableDetails.getSelectedRows();
      String cat_type = getCatalogTypeFromID( ((ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[0]))).getCatalogId());
      isPNumber = cat_type.equalsIgnoreCase("PNumber");

      for (int i = 0; i < selectedRows.length; i++) {
        ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[i]));
        if ((row == 1) && (col == 1)) // New Table / Page
          fw.write("<table border=\"1\" align=\"center\">");
        if (col == 1) // New Row
          fw.write("<tr>");
        fw.write("<td valign=\"top\" width=\"110\">");
        if (!isPNumber)
          fw.write(theRecord.getRecordName() + "<br>");
        String jpgFileName = tempDir + "/thumbSml" + theRecord.getRecordId() + ".jpg";
        writeJPGFile(theRecord.getThumbSmall(mCamsDB), jpgFileName);
        fw.write("<img src=\"" + new File(jpgFileName).getName() + "\"><br>");
        fw.write(theRecord.getScanNumber() + "&nbsp;</td>" + lf);

        col++;
        if (col > maxCols) {
          fw.write("</tr>");
          col = 1;
          row++;
        }

        if (row > maxRows) {
          fw.write("</table>");
          fw.write("<div class=\"page\"></div>");
          row = 1;
        }
      }
      fw.write("</body></html>");
      fw.close();

      viewHTMLFile(fileName);
    }
    catch (Exception ex) {
      cams.console.Console.println("Error writing to html file for printing: " + ex.getMessage());
    }

  }

  /**
   * Create HTML Printable View for Medium Thumbnails
   * Medium thumbnail is 130px so print 4 columns across
   * and 4 down before a page break
   */
  void htmlPrintMediumThumbnails() { // JJS
    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    String tempDir = mCamsDB.getImaging().getTempDir();
    FileWriter fw = null;
    String lf = String.valueOf('\r') + String.valueOf('\n');
    String fileName = tempDir + "/cams_print.html";
    int maxCols = 4;
    int maxRows = 4;

    boolean isPNumber = false;

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
      cams.console.Console.println("Error opening html file for printing: " + ex.getMessage());
      return;
    }

    try {
      int col = 1;
      int row = 1;
      int[] selectedRows = jtableDetails.getSelectedRows();
      String cat_type = getCatalogTypeFromID( ((ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[0]))).getCatalogId());
      isPNumber = cat_type.equalsIgnoreCase("PNumber");

      for (int i = 0; i < selectedRows.length; i++) {
        ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[i]));
        if ((row == 1) && (col == 1)) // New Table / Page
          fw.write("<table border=\"1\" align=\"center\">");
        if (col == 1) // New Row
          fw.write("<tr>");
        fw.write("<td valign=\"top\" width=\"175\">");
        if (!isPNumber)
          fw.write(theRecord.getRecordName() + "<br>");
        fw.write(theRecord.getScanNumber() + "<br>");
        fw.write(theRecord.getKeyline() + "<br>");
        String jpgFileName = tempDir + "/thumbMed" + theRecord.getRecordId() + ".jpg";
        writeJPGFile(theRecord.getThumbMedium(mCamsDB), jpgFileName);
        fw.write("<img src=\"" + new File(jpgFileName).getName() + "\"><br>");
        fw.write(theRecord.getStatus() + "&nbsp;</td>" + lf);

        col++;
        if (col > maxCols) {
          fw.write("</tr>");
          col = 1;
          row++;
        }

        if (row > maxRows) {
          fw.write("</table>");
          fw.write("<div class=\"page\"></div>");
          row = 1;
        }
      }
      fw.write("</body></html>");
      fw.close();

      viewHTMLFile(fileName);
    }
    catch (Exception ex) {
      cams.console.Console.println("Error writing to html file for printing: " + ex.getMessage());
    }

  }

  /**
   * Create HTML Printable View for Large Thumbnails
   * Medium thumbnail is 256px so print 2 columns across
   * and 3 down before a page break
   */
  void htmlPrintLargeThumbnails() {
    TableSorter sorter = (TableSorter) jtableDetails.getModel();
    ImageLibTableModel theModel = (ImageLibTableModel)sorter.getTableModel();
    String tempDir = mCamsDB.getImaging().getTempDir();
    FileWriter fw = null;
    String lf = String.valueOf('\r') + String.valueOf('\n');
    String fileName = tempDir + "/cams_print.html";
    int maxCols = 2;
    int maxRows = 3;

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
      cams.console.Console.println("Error opening html file for printing: " + ex.getMessage());
      return;
    }

    try {
      boolean isPNumber = false;
      int col = 1;
      int row = 1;
      int[] selectedRows = jtableDetails.getSelectedRows();
      String cat_type = getCatalogTypeFromID( ((ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[0]))).getCatalogId());
      isPNumber = cat_type.equalsIgnoreCase("PNumber");

      for (int i = 0; i < selectedRows.length; i++) {
        ImageLibRecord theRecord = (ImageLibRecord) theModel.getData().get(
            sorter.modelIndex(selectedRows[i]));
        if ((row == 1) && (col == 1)) // New Table / Page
          fw.write("<table border=\"1\" align=\"center\">");
        if (col == 1) // New Row
          fw.write("<tr>");
        fw.write("<td valign=\"top\" width=\"300\">");
        if (!isPNumber)
          fw.write(theRecord.getRecordName() + "<br>");
        fw.write(theRecord.getScanNumber() + "<br>");
        fw.write(theRecord.getKeyline() + "<br>");
        String jpgFileName = tempDir + "/thumbLrg" + theRecord.getRecordId() + ".jpg";
        writeJPGFile(theRecord.getThumbLarge(mCamsDB), jpgFileName);
        fw.write("<img src=\"" + new File(jpgFileName).getName() + "\"><br>");
        fw.write(theRecord.getStatus() + "&nbsp;</td>" + lf);

        col++;
        if (col > maxCols) {
          fw.write("</tr>");
          col = 1;
          row++;
        }

        if (row > maxRows) {
          fw.write("</table>");
          fw.write("<div class=\"page\"></div>");
          row = 1;
        }
      }
      fw.write("</body></html>");
      fw.close();

      viewHTMLFile(fileName);
    }
    catch (Exception ex) {
      cams.console.Console.println("Error writing to html file for printing: " + ex.getMessage());
    }

  }

  public void writeJPGFile(byte[] theBytes, String theFileName) {
    try {
      FileOutputStream stream = new FileOutputStream(theFileName, false);
      stream.write(theBytes);
      stream.close();
    }
    catch (Exception ex) {
      cams.console.Console.println("writeJPGFile: " + ex.getMessage());
    }

  }

  public void viewHTMLFile(String theFile) {
    try {
      Runtime r = Runtime.getRuntime();

      if (mCamsDB.windowsOS()) {
        r.exec("cmd /c \"" + theFile + "\"");
      }
      else {
        String[] theCommands = new String[] {
            "sh", "-c",
            "open -a Safari \"" + theFile + "\""};
        r.exec(theCommands);
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("Error in launchHTMLFile: " + ex.getMessage());
    }
  }

  void jbtnSearchPlus_actionPerformed(ActionEvent e) {
    SearchPlusDialog dlgSearch = new SearchPlusDialog(this, mCamsDB);

    dlgSearch.setSize(dlgSearch.getSize().width + 10, dlgSearch.getSize().height + 10);

    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = dlgSearch.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    dlgSearch.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    dlgSearch.setVisible(true);

    if (dlgSearch.getSearchResultCount() > 0)
      showSearchResults();
    else if (dlgSearch.getSearchResultCount() == 0)
      clearSearchResults();

    dlgSearch.dispose();
    dlgSearch = null;
  }


}

/******************************************************************/

class ImageLibFrame_jMenuFileExit_ActionAdapter implements ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuFileExit_ActionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuFileExit_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuHelpAbout_ActionAdapter implements ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuHelpAbout_ActionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuHelpAbout_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuImportRecords_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuImportRecords_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuImportRecords_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuViewAsset_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuViewAsset_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuViewAsset_actionPerformed(e);
  }
}

class ImageLibFrame_jbtnDetails_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jbtnDetails_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jbtnDetails_actionPerformed(e);
  }
}

class ImageLibFrame_jbtnThumbLarge_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jbtnThumbLarge_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jbtnShowThumbnails_actionPerformed(e);
  }
}

class ImageLibFrame_jbtnThumbMedium_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jbtnThumbMedium_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jbtnShowThumbnails_actionPerformed(e);
  }
}

class ImageLibFrame_jbtnThumbSmall_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jbtnThumbSmall_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jbtnShowThumbnails_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuAddCategory_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuAddCategory_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuAddCategory_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuDeleteCategory_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuDeleteCategory_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuDeleteCategory_actionPerformed(e);
  }
}

class ImageLibFrame_this_componentAdapter extends java.awt.event.ComponentAdapter {
  ImageLibFrame adaptee;

  ImageLibFrame_this_componentAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void componentResized(ComponentEvent e) {
    adaptee.this_componentResized(e);
  }
}

class ImageLibFrame_jMenuViewDetail_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuViewDetail_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuViewDetail_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuViewSmall_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuViewSmall_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuViewSmall_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuViewMedium_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuViewMedium_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuViewMedium_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuViewLarge_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuViewLarge_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuViewLarge_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuEditProperties_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuEditProperties_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuEditProperties_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuRemoveAsset_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuRemoveAsset_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuRemoveAsset_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuRenameCategory_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuRenameCategory_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuRenameCategory_actionPerformed(e);
  }
}

class ImageLibFrame_jbtnSearch_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jbtnSearch_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jbtnSearch_actionPerformed(e);
  }
}

class ImageLibFrame_jMenuSearch_actionAdapter implements java.awt.event.ActionListener {
  ImageLibFrame adaptee;

  ImageLibFrame_jMenuSearch_actionAdapter(ImageLibFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jbtnSearch_actionPerformed(e);
  }
}


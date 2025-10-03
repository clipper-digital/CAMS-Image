package cams.imagelib.importrecord;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JTree;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.borland.jbcl.layout.*;

import cams.database.*;
import cams.imagelib.*;
import cams.console.*;
import cams.imagelib.detaildialogs.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ImportRecordFrame extends JFrame {
  private CamsDB mCamsDB = null;
  private JFrame mParent = null;
  private ImageLibRecordList mRecords = null;
  private ImportRecordFrame mTheFrame = this;
  private boolean mUpdateStatus = false;
  private ArrayList mCategoryList = null;
  private HashMap mCategoryFullPathHash = null;
  private CamsTreeNode mCamsNode = null;

  JList jlistCatalog = new JList();
  JScrollPane jScrollCatalogs = new JScrollPane();
  JPanel jpnlDropHere = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JPanel jPanel1 = new JPanel();
  XYLayout xYLayout3 = new XYLayout();
  JLabel jlblStatus = new JLabel();
  XYLayout xYLayout1 = new XYLayout();
  DefaultMutableTreeNode treeTop =
      new DefaultMutableTreeNode("Categories");
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JCheckBox jckThumbnails = new JCheckBox();
  JRadioButton rdoRGB = new JRadioButton();
  JRadioButton rdoCMYK = new JRadioButton();
  ButtonGroup grpColorSpace = new ButtonGroup();

  public ImportRecordFrame(JFrame parent, CamsDB theDB, HashMap categoryHash,
                           CamsTreeNode theNode) {
    try {
      mParent = parent;
      mCamsDB = theDB;
      mCategoryFullPathHash = categoryHash;
      mCamsNode = theNode;
      this.setTitle("CAMS - Import Records");
      jbInit();
      jlistCatalog.setModel(new CustomListModel());
      refreshCatalogList();
      jpnlDropHere.setDropTarget(new DropTarget(jpnlDropHere, new MyDropTargetListener(this)));
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    jlistCatalog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.getContentPane().setLayout(xYLayout1);
    jScrollCatalogs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jpnlDropHere.setLayout(xYLayout2);
    jpnlDropHere.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setText("Drop Files");
    jLabel4.setText("and Folders");
    jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel5.setText("Here");
    jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));
    jPanel1.setLayout(xYLayout3);
    jlblStatus.setBorder(BorderFactory.createLoweredBevelBorder());
    jlblStatus.setText("Status:");
    jPanel1.setBorder(null);
    xYLayout1.setWidth(426);
    xYLayout1.setHeight(245);
    jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel6.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel6.setText("Import Records Into");
    jLabel7.setText("Select a Catalog to");
    jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel7.setFont(new java.awt.Font("Dialog", 1, 12));
    jckThumbnails.setFont(new java.awt.Font("Dialog", 0, 11));
    jckThumbnails.setFocusPainted(true);
    jckThumbnails.setText("Update Thumbnails Only");
    rdoRGB.setText("RGB");
    rdoCMYK.setSelected(true);
    rdoCMYK.setText("CMYK");
    jpnlDropHere.add(jLabel5,  new XYConstraints(18, 82, 106, 21));
    jpnlDropHere.add(jLabel3, new XYConstraints(18, 21, 106, 21));
    jpnlDropHere.add(jLabel4, new XYConstraints(20, 53, 106, 21));
    jPanel1.add(rdoCMYK,    new XYConstraints(243, 62, 68, 17));
    jPanel1.add(rdoRGB,      new XYConstraints(317, 62, 61, 17));
    jPanel1.add(jckThumbnails,             new XYConstraints(243, 37, 175, 18));
    jPanel1.add(jLabel6, new XYConstraints(47, 20, 136, 21));
    jPanel1.add(jLabel7, new XYConstraints(47, 2, 136, 21));
    this.getContentPane().add(jlblStatus,           new XYConstraints(8, 218, 406, -1));
    jPanel1.add(jScrollCatalogs,      new XYConstraints(9, 41, 220, 169));
    jPanel1.add(jpnlDropHere,   new XYConstraints(246, 85, 167, 125));
    jScrollCatalogs.getViewport().add(jlistCatalog, null);
    this.getContentPane().add(jPanel1,      new XYConstraints(0, 0, 622, 218));
    grpColorSpace.add(rdoCMYK);
    grpColorSpace.add(rdoRGB);
  }

  private void refreshCatalogList() {
    CustomListModel theListModel = (CustomListModel) jlistCatalog.getModel();
    theListModel.clear();

    ResultSet rs = null;
    String sql = "";

    boolean isImage = mCamsNode.catalog_type.equalsIgnoreCase("Image");
    if (isImage) {
      // Look at Categories under current Image Catalog
      sql = "SELECT ImageLibCategory.category_id, ImageLibCategory.category_name, " +
          "ImageLibCatalog.catalog_type FROM ImageLibCatalog INNER JOIN " +
          "ImageLibAccess ON ImageLibCatalog.catalog_id = ImageLibAccess.catalog_id " +
          "INNER JOIN ImageLibCategory ON ImageLibCatalog.catalog_id = " +
          "ImageLibCategory.catalog_id WHERE (ImageLibAccess.login = '" +
          mCamsDB.getUserInfo().login + "') AND (ImageLibCatalog.catalog_id = " +
          mCamsNode.catalog_id + ") AND (ImageLibCategory.category_parent = - 1) " +
          "ORDER BY ImageLibCategory.category_name";
    } else {
      // Show All Top Level Catalogs
      sql = "SELECT ImageLibCatalog.catalog_id, catalog_name, catalog_type " +
          "FROM ImageLibCatalog INNER JOIN ImageLibAccess ON " +
          "ImageLibCatalog.catalog_id = ImageLibAccess.catalog_id " +
          "WHERE ImageLibAccess.login = '" + mCamsDB.getUserInfo().login + "' " +
          "AND Upper(Catalog_Type) <> 'IMAGE' " +
          "ORDER BY sort_order";
    }

    try {
      rs = mCamsDB.query(sql);
      CatalogEntry selectedEntry = null;
      while (rs.next()) {
        CatalogEntry theEntry = new CatalogEntry();
        theEntry.catalog_id = rs.getInt(1); // ("catalog_id");
        theEntry.catalog_name = rs.getString(2); // ("catalog_name");
        theEntry.catalog_type = rs.getString(3); // ("catalog_type");
        theListModel.addElement(theEntry);
        if (isImage) {
          if (theEntry.catalog_id == mCamsNode.id)
            selectedEntry = theEntry;
        }
        else {
          if (theEntry.catalog_id == mCamsNode.catalog_id)
            selectedEntry = theEntry;
        }
      }
      if (selectedEntry != null)
        jlistCatalog.setSelectedValue(selectedEntry, true);

      if ((jlistCatalog.getSelectedIndex() == -1) && (theListModel.getSize() > 0))
        jlistCatalog.setSelectedIndex(0);

      if (theListModel.getSize() == 0) {
        JOptionPane.showMessageDialog(this, "No Categories were found.  Please " +
                                      "create at least one category.",
                                      "No Categories", JOptionPane.ERROR_MESSAGE);
      }
    }
    catch (Exception ex) {
      Console.println("refreshCatalogList: " + ex.toString());
    }
    finally {
      if (rs != null) {
        try {
          if (rs.getStatement() != null) rs.getStatement().close();
          rs.close();
        } catch (Exception ex) {}
      }
    }

    jlistCatalog.updateUI();
  }

  public int getNumCatalogs() {
    CustomListModel theListModel = (CustomListModel) jlistCatalog.getModel();
    return theListModel.getSize();
  }

  /**
   * Process Dropped Files
   * @param theFileList ArrayList
   */
  public void processDroppedFiles(ArrayList theFileList) {
    if (theFileList.size() == 0) return;

    if (mCamsNode.catalog_type.equalsIgnoreCase("Image")) {
      processDroppedFiles_Image(theFileList);
      return;
    }

    Console.println(theFileList.size() + " Files/Folders Dragged In.");

    CatalogEntry theCatalogEntry = (CatalogEntry) jlistCatalog.getSelectedValue();
    mRecords = new ImageLibRecordList(mCamsDB, theCatalogEntry.catalog_id,
                                      theCatalogEntry.catalog_type);

    for (int i = 0; i < theFileList.size(); i++) {
      File theFile = (File) theFileList.get(i);
      if (theFile.isDirectory()) {
        Console.println("  " + (i + 1) + ": [Directory] " + theFile.getAbsolutePath());
      }
      else {
        Console.println("  " + (i + 1) + ": [File] " + theFile.getAbsolutePath());      }
    }
    Console.println("");

    // Display Appropriate Details Dialog for common attributes
    if (jckThumbnails.isSelected())
      ; // Don't Show a Detail Page if we're just re-thumbnailing
    else if (theCatalogEntry.catalog_type.equalsIgnoreCase("Stock")) {
      /** Stock **/
      StockDetailsDialog theDetailsDialog = new StockDetailsDialog(mParent, mCamsDB,
                                                null, mCategoryFullPathHash, 0, 0, true);
      theDetailsDialog.setCatalogId(theCatalogEntry.catalog_id);
      theDetailsDialog.setCatalogName(theCatalogEntry.catalog_name);
      theDetailsDialog.setUserName(mCamsDB.getUserInfo().fname + " " + mCamsDB.getUserInfo().lname);
      theDetailsDialog.show();
      if (theDetailsDialog.userCancel) {
        theDetailsDialog.dispose();
        return;
      }

      ImageLibRecord templateRecord = theDetailsDialog.getRecord();
      mRecords.setTemplateRecord(templateRecord);
      theDetailsDialog.dispose();
    }
    else if (theCatalogEntry.catalog_type.equalsIgnoreCase("Exclusive")) {
      /** Exclusive **/
      ExclusiveDetailsDialog theDetailsDialog = new ExclusiveDetailsDialog(mParent, mCamsDB,
          null, mCategoryFullPathHash, 0, 0, true, false);
      theDetailsDialog.setCatalogId(theCatalogEntry.catalog_id);
      theDetailsDialog.setCatalogName(theCatalogEntry.catalog_name);
      theDetailsDialog.setUserName(mCamsDB.getUserInfo().fname + " " + mCamsDB.getUserInfo().lname);
      theDetailsDialog.show();
      if (theDetailsDialog.userCancel) {
        theDetailsDialog.dispose();
        return;
      }

      ImageLibRecord templateRecord = theDetailsDialog.getRecord();
      mRecords.setTemplateRecord(templateRecord);
      theDetailsDialog.dispose();
    }
    else {
      /** P # **/
      ExclusiveDetailsDialog theDetailsDialog = new ExclusiveDetailsDialog(mParent, mCamsDB,
          null, mCategoryFullPathHash, 0, 0, true, true);
      theDetailsDialog.setCatalogId(theCatalogEntry.catalog_id);
      theDetailsDialog.setCatalogName(theCatalogEntry.catalog_name);
      theDetailsDialog.setUserName(mCamsDB.getUserInfo().fname + " " + mCamsDB.getUserInfo().lname);
      theDetailsDialog.show();
      if (theDetailsDialog.userCancel) {
        theDetailsDialog.dispose();
        return;
      }

      ImageLibRecord templateRecord = theDetailsDialog.getRecord();
      mRecords.setTemplateRecord(templateRecord);
      theDetailsDialog.dispose();
    }


    jpnlDropHere.setVisible(false);
    jlistCatalog.setEnabled(false);

    jlblStatus.setText("Scanning Files...");
    mTheFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

    new ScanFoldersThread(theFileList).start();

    mUpdateStatus = true;
    new UpdateStatusThread().start();
  }

  /**
   * Process Dropped Files ** IMAGE Catalog **
   * @param theFileList ArrayList
   */
  public void processDroppedFiles_Image(ArrayList theFileList) {
    if (theFileList.size() == 0) return;

    Console.println(theFileList.size() + " Files/Folders Dragged In.");

    CatalogEntry theCatalogEntry = (CatalogEntry) jlistCatalog.getSelectedValue();

    mRecords = new ImageLibRecordList(mCamsDB, mCamsNode.catalog_id,
                                      mCamsNode.catalog_type,
                                      mCamsNode.name,
                                      theCatalogEntry.catalog_name,
                                      ((ImageLibFrame)mParent).getCategoryList());

    // Do a quick check of the first file/folder to make sure it
    // contains the Catalog name
    File theFile = (File) theFileList.get(0);
    String theFilePath = theFile.getAbsolutePath().toUpperCase();
    if (theFilePath.indexOf(theCatalogEntry.catalog_name.toUpperCase()) == -1) {
      String theMsg = "A file or folder you dragged in (" +
          theFile.getAbsolutePath() + ")\n" +
          "was not located in a folder structure that contained the " +
          "folder '" + theCatalogEntry.catalog_name + "'.";

      JOptionPane.showMessageDialog(this, theMsg, "Can Not Determine Folder Structure",
                                          JOptionPane.ERROR_MESSAGE);
      return;
    }

    for (int i = 0; i < theFileList.size(); i++) {
      theFile = (File) theFileList.get(i);
      if (theFile.isDirectory()) {
        Console.println("  " + (i + 1) + ": [Directory] " + theFile.getAbsolutePath());
      }
      else {
        Console.println("  " + (i + 1) + ": [File] " + theFile.getAbsolutePath());      }
    }
    Console.println("");

    jpnlDropHere.setVisible(false);
    jlistCatalog.setEnabled(false);

    jlblStatus.setText("Scanning Files...");
    mTheFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

    new ScanFoldersThread(theFileList).start();
//    new ScanFoldersThread(theFileList).run();   // Non Threaded for Debug

    mUpdateStatus = true;
    new UpdateStatusThread().start();
  }

  class ScanFoldersThread extends Thread {
    private ArrayList mDroppedFiles;

    public ScanFoldersThread(ArrayList theFileList) {
      mDroppedFiles = theFileList;
    }

    public void run() {
      boolean mCreateNewCategories = false;

      Console.println("ScanFoldersThread Thread Start");
      // Create a List (ImageLibRecordList) of ImageLibRecord of all of the files
      mRecords.clear();
      for (int i=0; i < mDroppedFiles.size(); i++) {
        File theFileOrFolder = (File) mDroppedFiles.get(i);
        if (theFileOrFolder.isDirectory()) {
          addFilesInFolder(theFileOrFolder);
        }
        else {
          mRecords.add(theFileOrFolder);
        }
      }

      mUpdateStatus = false;
      jlblStatus.setText(mRecords.size() + " Total Files Found");
      mTheFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

      if (mCamsNode.catalog_type.equalsIgnoreCase("Image")) {
        // Don't Create New Categories if Only Thumbnailing
        if (jckThumbnails.isSelected())
          mCreateNewCategories = false;
        else {
          // Check to see if there are any new Categories
          if (mRecords.getNewCategories().size() > 0) {
            String theMsg = "The following new categories were found that do\n" +
                "not exist in the " + mRecords.getTopLevelCategory() +
                " catalog:\n\n";
            for (int i = 0; i < mRecords.getNewCategories().size(); i++) {
              String theCategory = (String) mRecords.getNewCategories().get(i);
              theMsg += "   " +
                  theCategory.substring(theCategory.indexOf(mRecords.
                  getTopLevelCategory()) +
                                        mRecords.getTopLevelCategory().length() +
                                        1) + "\n";
            }

            theMsg += "\nContinue with the import and create these\n" +
                "categories as necessary?";

            int rc = JOptionPane.showConfirmDialog(mTheFrame, theMsg,
                "Create new Categories",
                JOptionPane.
                YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (rc == JOptionPane.CANCEL_OPTION) {
              mParent.setVisible(true);
              mTheFrame.dispose();
              return;
            }
            else
              mCreateNewCategories = (rc == JOptionPane.YES_OPTION);
          }
        }
      }

      ProcessImportFrame theProcessImportFrame =
          new ProcessImportFrame(mParent, mCamsDB, mRecords,
                                 jckThumbnails.isSelected(),
                                 mCamsNode.catalog_type.equalsIgnoreCase("Image"),
                                 mCreateNewCategories, rdoCMYK.isSelected());
      boolean packFrame = true;
      if (packFrame) {
        theProcessImportFrame.pack();
      }
      else {
        theProcessImportFrame.setSize(800, 600);
        theProcessImportFrame.validate();
      }

      //Center the window
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = theProcessImportFrame.getSize();
      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      theProcessImportFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      theProcessImportFrame.setVisible(true);
      mTheFrame.dispose();
    }
  }

  class UpdateStatusThread extends Thread {
    public void run() {
      Console.println("UpdateStatus Thread Start");
      while (mUpdateStatus) {
        jlblStatus.setText(mRecords.size() + " Files Scanned.");
        jlblStatus.updateUI();
        try { Thread.sleep(250); } catch (Exception ex) {}
      }
      Console.println("UpdateStatus Thread Finish");
    }
  };

  private void addFilesInFolder(File theFolder) {
    File[] theFileList = theFolder.listFiles();
    for (int i=0; i < theFileList.length; i++) {
      File theFile = theFileList[i];
      if (theFile.isDirectory())
        addFilesInFolder(theFile);
      else
        mRecords.add(theFile);
    }
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      mParent.setVisible(true);
      this.dispose();
    }
  }

  class CatalogEntry {
    public int catalog_id;
    public String catalog_name;
    public String catalog_type;

    public String toString() { return catalog_name; }
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
      CatalogEntry theCatalog = (CatalogEntry) mData.get(index);
      return theCatalog; // .catalog_name;
    }

    public CatalogEntry get(int index) {
      return (CatalogEntry) mData.get(index);
    }

    public void addElement(Object element) {
      mData.add(element);
    }

    public void clear() {
      mData.clear();
    }

  }

}

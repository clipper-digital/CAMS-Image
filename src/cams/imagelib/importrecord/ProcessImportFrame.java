package cams.imagelib.importrecord;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import com.borland.jbcl.layout.*;
import javax.swing.border.*;

import cams.imagelib.*;
import cams.database.*;
import cams.imaging.*;
import cams.console.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author JJS Solutions
 * @version 1.0
 */

public class ProcessImportFrame extends JFrame {
  ImageLibFrame mParent = null;
  ImageLibRecordList mRecords = null;
  CamsDB mCamsDB = null;
  long mCreatedRecordCount = 0;
  long mThumbnailedCount = 0;
  ProcessImportFrame mThisFrame = this;
  ImageMagick mImaging = null;
  boolean mExtractText = false;
  boolean mThumbnailOnly = false;
  boolean mCreateNewCategories = false;
  int mCreatedCategoryCount = 0;
  boolean mIsImageType = false;
  boolean mCancel = false;
  boolean mIsCMYK = false;

  JPanel jPanel1 = new JPanel();
  JProgressBar jprogress = new JProgressBar();
  XYLayout xYLayout1 = new XYLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jlblCurrentFullPath = new JLabel();
  JLabel jlblPreview = new JLabel();
  TitledBorder titledBorder1;
  JLabel jLabel2 = new JLabel();
  JLabel jlblCurrentStatus = new JLabel();
  XYLayout xYLayout2 = new XYLayout();
  JLabel jLabel3 = new JLabel();
  JLabel jlblCurrentFileName = new JLabel();
  JLabel jlblPreviewFile = new JLabel();
  JButton jbtnCancel = new JButton();

  public ProcessImportFrame(JFrame theParent, CamsDB theDB, ImageLibRecordList theRecords,
                            boolean doThumbnails, boolean isImageType,
                            boolean createNewCategories, boolean isCMYK) {
    try {
      mParent = (ImageLibFrame) theParent;
      mCamsDB = theDB;
      mRecords = theRecords;
      mImaging = mCamsDB.getImaging();
      mThumbnailOnly = doThumbnails;
      mCreateNewCategories = createNewCategories;
      mIsImageType = isImageType;
      mIsCMYK = isCMYK;
      this.setTitle("CAMS - Import Records");
      jbInit();
      jprogress.setMinimum(0);
      jprogress.setMaximum(mRecords.size());
      jprogress.setValue(0);
      jprogress.setString("0 of " + mRecords.size());

      // If we're thumbnailing only, don't create categories since
      // we're not adding new records
      if (mThumbnailOnly)
        mCreateNewCategories = false;

      new doProcessRecordsThread().start();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      mParent.setVisible(true);
      if (mCreatedCategoryCount > 0)
        mParent.refreshCatalogTree();
      this.dispose();
    }
  }

  void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    this.getContentPane().setLayout(xYLayout2);
    jprogress.setBorder(BorderFactory.createLoweredBevelBorder());
    jprogress.setOpaque(false);
    jprogress.setString("");
    jprogress.setValue(55);
    jprogress.setBorderPainted(true);
    jprogress.setStringPainted(true);
    jPanel1.setLayout(xYLayout1);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setText("Current Image:");
    jlblCurrentFullPath.setText("<Current Record>");
    jlblCurrentFullPath.setFont(new java.awt.Font("Dialog", 0, 11));
    jlblPreview.setBackground(Color.white);
    jlblPreview.setBorder(BorderFactory.createEtchedBorder());
    jlblPreview.setHorizontalAlignment(SwingConstants.CENTER);
    jlblPreview.setText("");
    jLabel2.setText("Processing:");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));
    jlblCurrentStatus.setFont(new java.awt.Font("Dialog", 0, 11));
    jlblCurrentStatus.setText("<status>");
    xYLayout2.setWidth(400);
    xYLayout2.setHeight(287);
    jLabel3.setText("Current FileName:");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
    jlblCurrentFileName.setFont(new java.awt.Font("Dialog", 0, 11));
    jlblCurrentFileName.setText("<Current Record>");
    jlblPreviewFile.setFont(new java.awt.Font("Dialog", 0, 11));
    jlblPreviewFile.setHorizontalAlignment(SwingConstants.CENTER);
    jlblPreviewFile.setText("");
    jbtnCancel.setText("Cancel");
    jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnCancel_actionPerformed(e);
      }
    });
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.getContentPane().add(jPanel1,   new XYConstraints(0, 0, 400, 227));
    this.getContentPane().add(jprogress,    new XYConstraints(2, 234, 396, -1));
    this.getContentPane().add(jbtnCancel,                                                                   new XYConstraints(118, 259, 164, 26));
    jPanel1.add(jLabel1,  new XYConstraints(12, 10, 98, 18));
    jPanel1.add(jlblCurrentFullPath,    new XYConstraints(117, 10, 278, 18));
    jPanel1.add(jlblCurrentFileName,   new XYConstraints(117, 30, 278, 18));
    jPanel1.add(jlblCurrentStatus,     new XYConstraints(117, 50, 278, 18));
    jPanel1.add(jLabel3,     new XYConstraints(12, 30, 106, 18));
    jPanel1.add(jLabel2,        new XYConstraints(12, 50, 98, 18));
    jPanel1.add(jlblPreview,                         new XYConstraints(135, 80, 130, 130));
    jPanel1.add(jlblPreviewFile, new XYConstraints(89, 211, 223, 18));
  }

  /**
   *
   */
  class doProcessRecordsThread extends Thread {
    public void run() {
      ImageIcon thePreviewIcon = null;
      for (int i = 0; i < mRecords.size(); i++) {
        ImageLibRecord theRecord = (ImageLibRecord) mRecords.get(i);
        mThisFrame.jlblCurrentFullPath.setText(theRecord.getFullPath(mCamsDB.windowsOS()));
        mThisFrame.jlblCurrentFileName.setText(theRecord.getFileName());
        mThisFrame.getContentPane().repaint();

        // *************************************************
        // For Image Type, Check and Create Categories
        // *************************************************
        if (mIsImageType) {
          // First check to see if category exists
          theRecord.setCategoryId(mRecords.getCategoryId(theRecord.
              getCategoryFullPath()));
          if ( (theRecord.getCategoryId() == -1) && (mCreateNewCategories)) {
            // New Category, Create in database
            // Start from top-most level and make sure each level is created
            String[] theCategoryLevels = theRecord.getCategoryFullPath().split(
                ",");
            for (int catLevel = 1; catLevel < theCategoryLevels.length;
                 catLevel++) {
              // Build string of current level and above
              String thisPath = "";
              for (int j = 0; j < catLevel; j++) {
                thisPath += theCategoryLevels[j] + ",";
              }
              thisPath += theCategoryLevels[catLevel];
              if (!mRecords.categoryExists(thisPath)) {
                mThisFrame.jlblCurrentStatus.setText("Creating Category: " +
                    thisPath);
                CamsTreeNode newCategory = new CamsTreeNode();
                newCategory.fullPath = thisPath;
                newCategory.name = theCategoryLevels[catLevel];
                newCategory.parent_id = mRecords.getParentId(thisPath);
                newCategory.catalog_id = mRecords.getCatalogId();
                newCategory.create(mCamsDB);
                Console.println("Creating Category: " + thisPath +
                                ", parent_id = " + newCategory.parent_id +
                                ", new id = " + newCategory.id);
                mRecords.addCategory(newCategory);
                mCreatedCategoryCount++;
              }
            }
            theRecord.setCategoryId(mRecords.getCategoryId(theRecord.
                getCategoryFullPath()));
          }
        }

        if (mIsImageType && (theRecord.getCategoryId() == -1)) {
          mThisFrame.jlblCurrentStatus.setText("Skipping (Invalid Category)...");
        }
        else
        {
          // Add this record to the database
          theRecord.setCatalogId(mRecords.getCatalogId());
          java.util.Date lastModified = mRecords.recordExists(theRecord, mCamsDB);
          if ((lastModified == null) && !mThumbnailOnly) { // Create New Record
            mThisFrame.jlblCurrentStatus.setText("Creating Database Record...");
            // Fill out the rest of the record fields (Categories, etc.)
            mRecords.createRecord(theRecord);
            // Save to Database
            theRecord.save(mCamsDB);
            mCreatedRecordCount++;
          }
          else if ((lastModified != null) && mIsImageType) {
            // Compare current file date to date from database
            long msDiff = theRecord.getFileDate().getTime() - lastModified.getTime();
            if (msDiff > 1000) {
              // File on disk is newer
              theRecord.setFileDate(lastModified);
              String sql = "UPDATE ImageLibRecord SET FileDate = ?, " +
                  "LastUpdate = getDate() WHERE record_id = ?";
              PreparedStatement ps = null;
              try {
                ps = mCamsDB.getConnection().prepareStatement(sql);
                ps.setTimestamp(1, new Timestamp(theRecord.getFileDate().getTime()));
                ps.setInt(2, theRecord.getRecordId());
                ps.executeUpdate();
                ps.close();
                ps = null;

                lastModified = null;  // So it will re-thumbnail
              }
              catch (Exception ex) {
                Console.println("Error updating FileDate on existing record: " + ex.getMessage());
              }
              finally {
                try {
                  if (ps != null) {
                    ps.close();
                    ps = null;
                  }
                } catch (Exception ex) {}
              }
            }
          }

          if ( (theRecord.getRecordId() != -1) && (mImaging.isActive()) &&
              ( (lastModified == null) || mThumbnailOnly)) {
            mThisFrame.jlblCurrentStatus.setText("Creating Thumbnails...");
            int[] theSizes = {
                mParent.mThumbXLarge, mParent.mThumbLarge,
                mParent.mThumbMedium, mParent.mThumbSmall};
            String[] theThumbFileNames = mImaging.createThumbnails(theRecord.
                getFullPath(mCamsDB.windowsOS()), theSizes, mIsCMYK);
            if (thePreviewIcon != null) {
              thePreviewIcon.getImage().flush();
              thePreviewIcon = null;
            }
            if (theThumbFileNames != null)
              thePreviewIcon = new ImageIcon(theThumbFileNames[2]);
            else
              thePreviewIcon = null;

            mThisFrame.jlblPreview.setIcon(thePreviewIcon);
            mThisFrame.jlblPreview.revalidate();

            mThisFrame.jlblPreviewFile.setText(theRecord.getFileName());
            if (theThumbFileNames != null) {
              boolean thumbSuccess = mRecords.updateThumbnails(mCamsDB, theRecord, theThumbFileNames);
              if (!thumbSuccess) {
                // Thumbnailing Failed
                if (new java.util.Date().getTime() - theRecord.getRecordCreated().getTime() < 10000) {
                  // Record just created (< 10 sec ago)
                  Console.println("Invalid Image File (Could not thumbnail).  Deleting/Skipping Record.");
                  mCreatedRecordCount--;
                  mRecords.deleteRecord(theRecord);
                }
              }
              else
                mThumbnailedCount++;
            }
          }
        }

        mThisFrame.jprogress.setValue(i + 1);
        mThisFrame.jprogress.setString( (i + 1) + " of " + mRecords.size());
        mThisFrame.getContentPane().repaint();

        // Check for Cancel Button Clicked
        if (mCancel)
          break;
      }
      mThisFrame.jlblCurrentFullPath.setText("");
      mThisFrame.jlblCurrentFileName.setText("");
      mThisFrame.jlblCurrentStatus.setText("Finished!!");

      Console.println(mCreatedRecordCount + " Records Imported.");

      JOptionPane.showMessageDialog(mThisFrame,
                                    mRecords.size() + " files dragged in, " +
                                    mCreatedRecordCount + " new records created,\n" +
                                    (mRecords.size() - mCreatedRecordCount) +
                                    " were skipped, " +
                                    mThumbnailedCount + " thumbnails created/updated.",
                                    "Import Complete",
                                    JOptionPane.DEFAULT_OPTION);
      mParent.setVisible(true);
      mParent.showLastImport(mRecords.getBatchId());
//    mParent.refreshCatalogTree();
      if (thePreviewIcon != null) {
        thePreviewIcon.getImage().flush();
        thePreviewIcon = null;
      }

      // Try to let everything synch up
      this.yield();
      try { this.sleep(100); } catch (Exception ex) {}

      mThisFrame.dispose();
    }
  }

  void jbtnCancel_actionPerformed(ActionEvent e) {
    jbtnCancel.setText("Canceling...");
    mCancel = true;
  }
}

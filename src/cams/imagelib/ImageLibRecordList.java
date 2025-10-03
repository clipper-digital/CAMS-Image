package cams.imagelib;

import java.io.*;
import java.util.*;
import java.text.*;
import cams.database.*;
import java.sql.*;
import cams.console.*;

/**
 * ImageLibRecordList: Extends ArrayList to hold a list of
 * newly dragged in ImageLibRecords.
 */

public class ImageLibRecordList extends ArrayList {
  private java.util.Date mImportDate = null;
  private int mBatchId = -1;
  private int mCatalogId = -1;
  private String mCatalogType = null;
  private CamsDB mCamsDB = null;
  private ImageLibRecord mTemplateRecord = null;

  /** Just For IMAGE Types **/
  private String mTopLevelCategory = "";
  private CategoryList mExistingCategories; // From Tree
  private String mCatalogName = "";
  private ArrayList mUniqueCategories = new ArrayList(); // All unique categories in this import
  private ArrayList mNewCategories = new ArrayList(); // All NON EXISTING categories in this import

  public ImageLibRecordList(CamsDB theCamsDB, int catalogId, String catalog_type) {
    mCamsDB = theCamsDB;
    mCatalogId = catalogId;
    mCatalogType = catalog_type;
  }

  // For IMAGE Record Types
  public ImageLibRecordList(CamsDB theCamsDB, int catalogId, String catalog_type,
                            String catalog_name, String topLevelCategory,
                            ArrayList existingCategories) {
    mCamsDB = theCamsDB;
    mCatalogId = catalogId;
    mCatalogType = catalog_type;
    mCatalogName = catalog_name;
    mTopLevelCategory = topLevelCategory;
    mExistingCategories = new CategoryList(existingCategories); // ArrayList of CamsTreeNode
  }

  public void clear() {
    super.clear();
  }

  public void setTemplateRecord(ImageLibRecord theRecord) { mTemplateRecord = theRecord; }

  public int getCatalogId() { return mCatalogId; }
  public java.util.Date getImportDate() { return mImportDate; }

  public int getBatchId() {
    if (mBatchId == -1) {
      String sql = "INSERT INTO ImageLibBatch (batch_time, login) VALUES " +
          "(getDate(), '" + mCamsDB.getUserInfo().login + "')";
      mCamsDB.execute(sql);
      sql = "SELECT TOP 1 batch_id, batch_time FROM ImageLibBatch ORDER BY " +
          "batch_time DESC";
      try {
        ResultSet rs = mCamsDB.query(sql);
        if (rs.next()) {
          mBatchId = rs.getInt("batch_id");
          mImportDate = rs.getTimestamp("batch_time");
        }
        if (rs.getStatement() != null) rs.getStatement().close();
        rs.close();
      }
      catch (Exception ex) {
        Console.println("Error Getting Batch ID: " + ex.getMessage());
      }
    }

    return mBatchId;
  }

  public ArrayList getNewCategories() { return mNewCategories; }
  public String getTopLevelCategory() { return mTopLevelCategory; }

  public void add(File theFile) {
    // Check for .DS_STORE Files
    if (theFile.getName().toUpperCase().indexOf(".DS_STORE") > -1) {
      Console.println("Skipping .DS_Store File");
      return;
    }

    ImageLibRecord theRecord = null;
    if (mCatalogType.equalsIgnoreCase("Image"))
      theRecord = new ImageLibRecord(theFile, mCamsDB.windowsOS(), mCatalogName, mTopLevelCategory);
    else
      theRecord = new ImageLibRecord(theFile, mCamsDB.windowsOS());
    super.add(theRecord);

    if (mCatalogType.equalsIgnoreCase("Image")) {
      // Do we know if this path yet?
      // Track for all but only really used for IMAGE type
      if (!mUniqueCategories.contains(theRecord.getCategoryFullPath())) {
        mUniqueCategories.add(theRecord.getCategoryFullPath());
        // See if it's new or already existing
        if ( (!mNewCategories.contains(theRecord.getCategoryFullPath())) &&
            (!mExistingCategories.contains(theRecord.getCategoryFullPath()))) {
          mNewCategories.add(theRecord.getCategoryFullPath());
        }
      }
    }

  }

  public static boolean updateThumbnails(CamsDB theCamsDB, ImageLibRecord theRecord,
                                  String[] theThumbFileNames) {
    boolean result = true;
    PreparedStatement ps = null;
    String[] fields = {"ThumbXLarge", "ThumbLarge", "ThumbMedium", "ThumbSmall"};
    String sql = "";

    try {
      for (int i = 0; i < theThumbFileNames.length; i++) {
        sql = "UPDATE ImageLibRecord SET " + fields[i] + " = ? " +
            "WHERE record_id = " + theRecord.getRecordId();

        String theFileName = theThumbFileNames[i];
        if (theFileName != null) {
          File theFile = new File(theFileName);
          long fileLength = theFile.length();
          InputStream fileInStream = new FileInputStream(theFile);
          ps = theCamsDB.getConnection().prepareStatement(sql);
          ps.setBinaryStream(1, fileInStream, (int)fileLength);
          ps.executeUpdate();
          ps.close();
          ps = null;
        }
      }
    }
    catch (Exception ex) {
      result = false;
      Console.println("MktSupportRecordList:updateThumbnails: " + ex.getMessage());
    }
    finally {
      try {
        if (ps != null) {
          ps.close();
          ps = null;
        }
      } catch (Exception ex) {}
    }
    return result;
  }

  public java.util.Date recordExists(ImageLibRecord theRecord, CamsDB theCamsDB) {
    java.util.Date result = null;
    String sql = "";
    String fullPath = "";
//    SELECT  Distinct r.record_id
//    FROM         ImageLibRecord r, ImageLibCategoryRecord cr
//    WHERE   r.record_id = cr.record_id
//    and r.catalog_id = 1

    if (mCamsDB.windowsOS()) {
      fullPath = theRecord.getFullPathWin().replaceAll("'", "''");
      sql = "SELECT DISTINCT r.record_id, r.filedate FROM " +
          "ImageLibRecord r, ImageLibCategoryRecord cr WHERE " +
          "r.record_id = cr.record_id AND " +
          "Upper(r.FullPathWin) = '" + fullPath.toUpperCase() +
          "' AND catalog_id = " + theRecord.getCatalogId();
    }
    else {
      fullPath = theRecord.getFullPathMac().replaceAll("'", "''");
      sql = "SELECT DISTINCT r.record_id, r.filedate FROM " +
          "ImageLibRecord r, ImageLibCategoryRecord cr WHERE " +
          "r.record_id = cr.record_id AND " +
          "Upper(r.FullPathMac) = '" + fullPath.toUpperCase() +
          "' AND catalog_id = " + theRecord.getCatalogId();
    }

    try {
      ResultSet rs = theCamsDB.query(sql);
      if (rs.next()) {
        theRecord.setRecordId(rs.getInt(1));
        result = rs.getTimestamp(2);
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close();

      if (result != null) {
        Console.println("Matching record found with FullPath = '" + fullPath +
                        "' in Catalog " + theRecord.getCatalogName());
      }
      else { // Check for Filename match only (handle "pending" records)
        String matchFileName = "'" + theRecord.getFileName().replaceAll("'", "''").toUpperCase()
            + "'";
        String matchFileNoExt = matchFileName;
        if (theRecord.getFileName().indexOf(".") > -1)
          matchFileNoExt = "'" +
              theRecord.getFileName().substring(0,
                                                theRecord.getFileName().
                                                lastIndexOf(".")).toUpperCase() + "'";
        sql = "SELECT record_id, filedate FROM ImageLibRecord WHERE " +
            "Upper(FullPathMac) = 'PENDING' AND " +
            "(" +
//            " Upper(FileName) = " + matchFileName + " OR" +
            " Upper(RecordName) = " + matchFileNoExt +
            " OR Upper(RecordName) = " + matchFileName +
            " OR Upper(ScanNum) = " + matchFileNoExt +
            " OR Upper(ScanNum) = " + matchFileName +
            ") " +
            "AND catalog_id = " + theRecord.getCatalogId();
        rs = theCamsDB.query(sql);
//      Console.println("Looking for 'PENDING' Match: " + sql);
        if (rs.next()) {
          theRecord.setRecordId(rs.getInt(1));
          result = rs.getTimestamp(2);
          Console.println("Found match by FileName/RecordName/ScanNum and FullPath='Pending'");
          if (mCamsDB.windowsOS())
            sql = "UPDATE ImageLibRecord SET FullPathWin = '" +
                theRecord.getFullPathWin().replaceAll("'", "''") + "', " +
                "FullPathMac = '', FileName = '" +
                theRecord.getFileName().replaceAll("'", "''") + "' " +
                "WHERE record_id = " + theRecord.getRecordId();
          else
            sql = "UPDATE ImageLibRecord SET FullPathMac = '" +
                theRecord.getFullPathMac().replaceAll("'", "''") + "', " +
                "FullPathWin = '', FileName = '" +
                theRecord.getFileName().replaceAll("'", "''") + "' " +
                "WHERE record_id = " + theRecord.getRecordId();
          theCamsDB.execute(sql);
        }
        if (rs.getStatement() != null) rs.getStatement().close();
        rs.close();
      }
    }
    catch (Exception ex) {
      Console.println("ImageLibRecordList:recordExists: " + ex.getMessage());
    }

    return result;
  }

  /**
   * Create ImageLib Record by:
   *   assigning Batch Id
   *   setting Categories in Object
   *   setting Keywords in Object
   *   setting entries from "Template" (common properties for this import batch)
   *   Creating initial database record in ImageLibRecord
   */
  public void createRecord(ImageLibRecord theRecord) {
    theRecord.setBatchId(getBatchId());
    theRecord.setCatalogId(mCatalogId);

    if (mTemplateRecord != null) {
      theRecord.setRecordName(mTemplateRecord.getRecordName());
      theRecord.setStatusId(mTemplateRecord.getStatusId());
      theRecord.setStatus(mTemplateRecord.getStatus());
      theRecord.setDontDelete(mTemplateRecord.getDontDelete());
      theRecord.setWebDisplay(mTemplateRecord.getWebDisplay());
      theRecord.setPriorityDisplay(mTemplateRecord.getPriorityDisplay());
      theRecord.setCatalogUser(mTemplateRecord.getCatalogUser());
      theRecord.setKeyline(mTemplateRecord.getKeyline());
      theRecord.setCDName(mTemplateRecord.getCDName());
      theRecord.setMFRName(mTemplateRecord.getMFRName());
      theRecord.setExclusive(mTemplateRecord.getExclusive());
      theRecord.setArea(mTemplateRecord.getArea());
      theRecord.setOrigOnFile(mTemplateRecord.getOrigOnFile());
      theRecord.setModelRelease(mTemplateRecord.getModelRelease());
      theRecord.setNotes(mTemplateRecord.getNotes());
      theRecord.setScanNumber(mTemplateRecord.getScanNumber());
      theRecord.setUsageRules(mTemplateRecord.getUsageRules());
      theRecord.setReleases(mTemplateRecord.getReleases());
      theRecord.setReleaseId(mTemplateRecord.getReleaseId());
      theRecord.setImageCompany(mTemplateRecord.getImageCompany());
      theRecord.setAdvertiser(mTemplateRecord.getAdvertiser());
      theRecord.setCategories(mTemplateRecord.getCategories());
      theRecord.setKeywords(mTemplateRecord.getKeywords());
      theRecord.setPNumber(mTemplateRecord.getPNumber());
    }

    String baseFile = theRecord.getFileName();
    if (baseFile.indexOf(".") > -1)
      baseFile = baseFile.substring(0, baseFile.lastIndexOf("."));

    // For Exclusive and P#, Use filename (no extension) for Scan# and RecordName
    if (mCatalogType.equalsIgnoreCase("Exclusive") || mCatalogType.toUpperCase().startsWith("P")) {
      theRecord.setScanNumber(baseFile);
      theRecord.setRecordName(baseFile);
    }
    // For Stock, Use filename (no extension) for Scan Number
    else if (mCatalogType.equalsIgnoreCase("Stock")) {
      theRecord.setRecordName(baseFile);
    }
    // Image Type.  Use single Category Id
    else if (mCatalogType.equalsIgnoreCase("Image")) {
      IDNamePair theCategory = new IDNamePair(theRecord.getCategoryId(),
                                              theRecord.getCategoryName());
      ArrayList theCategories = new ArrayList();
      theCategories.add(theCategory);
      theRecord.setCategories(theCategories);

      theRecord.setScanNumber(theRecord.getFileName()); // File with extension
      theRecord.setRecordName(baseFile); // File with no extension

      theRecord.setCatalogUser(mCamsDB.getUserInfo().fname + " " + mCamsDB.getUserInfo().lname);
    }

    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
    String uniqueId = df.format(new java.util.Date());

    // Create Initial Record
    String sql = "INSERT INTO ImageLibRecord (batch_id, catalog_id, RecordName, RecordCreated) " +
        "values (" + mBatchId + ", " + mCatalogId + ",'" + uniqueId + "', getDate())";
    mCamsDB.execute(sql);

    // Now read it back to get record_id
    sql = "SELECT record_id, RecordCreated FROM ImageLibRecord WHERE " +
        "RecordName = '" + uniqueId + "'";

    try {
      ResultSet rs = mCamsDB.query(sql);
      if (rs.next()) {
        theRecord.setRecordId(rs.getInt("record_id"));
        theRecord.setRecordCreated(rs.getTimestamp("RecordCreated"));
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close();
    }
    catch (Exception ex) {
      Console.println("ImageLibRecordList:createRecord: " + ex.getMessage());
    }

    // Add the Notes
    try {
      PreparedStatement ps = null;
      sql = "INSERT INTO ImageLibNotes (record_id) " +
          "VALUES (" + theRecord.getRecordId() + ")";
      mCamsDB.execute(sql);
    }
    catch (Exception ex) {
      Console.println("ImageLibRecordList:createRecord (notes): " + ex.getMessage());
    }

  }

  public void deleteRecord(ImageLibRecord theRecord) {
    // Remove all categories for this record
    theRecord.setCategories(new ArrayList());
    theRecord.updateRecordCategories(mCamsDB);
    // Remove all keywords for this record
    theRecord.setKeywords(new ArrayList());
    theRecord.updateRecordKeywords(mCamsDB);

    // Delete from ImageLibRecord
    String sql = "DELETE FROM ImageLibRecord WHERE record_id = " +
          theRecord.getRecordId();
    mCamsDB.execute(sql);
  }

  /**
   * Check to see if "theCategory" is already created on the tree
   * @param theCategory String
   * @return boolean
   */
  public boolean categoryExists(String theCategory) {
    return mExistingCategories.contains(theCategory);
  }

  public int getCategoryId(String thePath) {
    int result = -1;

    for (int i=0; i < mExistingCategories.size(); i++) {
      CamsTreeNode theNode = (CamsTreeNode) mExistingCategories.get(i);
      if (theNode.fullPath.equals(thePath)) {
        result = theNode.id;
        break;
      }
    }
    return result;
  }

  public int getParentId(String theChildPath) {
    String theParentPath = theChildPath.substring(0, theChildPath.lastIndexOf(","));
    return getCategoryId(theParentPath);
  }

  public void addCategory(CamsTreeNode theNode) {
    mExistingCategories.add(theNode);
  }

  // Subclass ArrayList so we can do the "Contains" like we need
  class CategoryList extends ArrayList {
    public CategoryList(ArrayList theList) {
      this.addAll(theList);
    }

    public boolean contains(String theFullPath) {
      for (int i=0; i < this.size(); i++) {
        CamsTreeNode theNode = (CamsTreeNode)this.get(i);
        if (theNode.fullPath.equals(theFullPath))
          return true;
      }
      return false;
    }
  }

}

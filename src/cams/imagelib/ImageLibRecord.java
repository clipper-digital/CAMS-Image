package cams.imagelib;

import java.util.*;
import java.io.*;
import java.text.*;

import cams.database.*;
import java.sql.*;
// import cams.console.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ImageLibRecord {
  private int mRecordID = -1;
  private int mBatchID = -1;
  private int mCatalogID = -1;
  private String mCatalogName = "";
  private String mRecordName = "";
  private String mStatus = "";
  private int mStatusID = -1;
  private String mFullPathWin = "";
  private String mFullPathMac = "";
  private String mFileName = "";
  private boolean mDontDelete = false;
  private String mCatalogUser = "";
  private String mKeyline = "";
  private String mCDName = "";
  private String mMFRName = "";
  private boolean mExclusive = false;
  private String mArea = "";
  private boolean mOrigOnFile = false;
  private boolean mModelRelease = false;
  private String mNotes = null;
  private String mFileFormat = "";
  private long mFileSize = 0;
  private java.util.Date mFileDate = null;
  private java.util.Date mRecordCreated = null;
  private java.util.Date mRecordModified = null;
  private String mScanNum = "";
  private String mUsageRules = "";
  private String mReleases = "";
  private int mReleaseId = -1;
  private String mImageCompany = "";
  private String mAdvertiser = "";
  private ArrayList mKeywords = new ArrayList(); // List of IDNamePair objects
  private ArrayList mCategories = new ArrayList(); // List of IDNamePair objects
  private String mOriginalKeywords = "";
  private byte[] mThumbXLarge = null;
  private byte[] mThumbLarge = null;
  private byte[] mThumbMedium = null;
  private byte[] mThumbSmall = null;
  private String mPNumber = "";
  private boolean mWebDisplay = false;
  private boolean mPriorityDisplay = false;

  private String mTopCategory = "";      // Used Only for IMAGE records
  private String mCategoryFullPath = ""; // Used Only for IMAGE records
  private String mCategoryName = "";     // Used Only for IMAGE records
  private int mCategoryId = -1;          // Used Only for IMAGE records

  public ImageLibRecord() {
  }

  public ImageLibRecord(ResultSet rs) {
//    record_id, batch_id, ImageLibRecord.catalog_id, RecordName, " +
//              "ISNULL(ImageLibRecord.Status_id, -1) Status_id, ISNULL(Status, '') Status, " +
//              "FullPathMac, FullPathWin, " +
//              "FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive, " +
//              "Area, OrigOnFile, ModelRelease, Notes, FileFormat, FileSize, FileDate, " +
//              "RecordCreated, LastUpdate, ScanNum, UsageRules, " +
//              "ISNULL(ImageLibRecord.Release_id, -1) Release_id, Release, ImageCompany, " +
//              "Advertiser, OriginalKeywords, catalog_name


    try {
      mRecordID = rs.getInt("record_id");
      mBatchID = rs.getInt("batch_id");
      mCatalogID = rs.getInt("catalog_id");
      mRecordName = isNull(rs.getString("RecordName"), "");
      mStatusID = rs.getInt("Status_id");
      mStatus = isNull(rs.getString("Status"), "");
      mFullPathMac = isNull(rs.getString("FullPathMac"), "");
      mFullPathWin = isNull(rs.getString("FullPathWin"), "");
      mFileName = isNull(rs.getString("FileName"), "");
      mDontDelete = rs.getBoolean("DontDelete");
      mCatalogUser = isNull(rs.getString("CatalogUser"), "");
      mKeyline = isNull(rs.getString("Keyline"), "");
      mCDName = isNull(rs.getString("CDName"), "");
      mMFRName = isNull(rs.getString("MFRName"), "");
      mExclusive = rs.getBoolean("Exclusive");
      mArea = isNull(rs.getString("Area"), "");
      mOrigOnFile = rs.getBoolean("OrigOnFile");
      mModelRelease = rs.getBoolean("ModelRelease");
//    mNotes = isNull(rs.getString("Notes"), "");
      mFileFormat = isNull(rs.getString("FileFormat"), "");
      mFileSize = rs.getInt("FileSize");
      mFileDate = rs.getTimestamp("FileDate");
      mRecordCreated = rs.getTimestamp("RecordCreated");
      mRecordModified = rs.getTimestamp("LastUpdate");
      mScanNum = isNull(rs.getString("ScanNum"), "");
      mUsageRules = isNull(rs.getString("UsageRules"), "");
      mReleaseId = rs.getInt("Release_Id");
      mReleases = isNull(rs.getString("Release"), "");
      mImageCompany = isNull(rs.getString("ImageCompany"), "");
      mAdvertiser = isNull(rs.getString("Advertiser"), "");
      mOriginalKeywords = isNull(rs.getString("OriginalKeywords"), "");
      if (mOriginalKeywords == null) mOriginalKeywords = "";
      mCatalogName = isNull(rs.getString("catalog_name"), "");
      mPNumber = isNull(rs.getString("PNumber"), "");
      mWebDisplay = rs.getBoolean("WebDisplay");
      mPriorityDisplay = rs.getBoolean("PriorityDisplay");
    }
    catch (Exception ex) {
      cams.console.Console.println("Error creating ImageLibRecord: " + ex.getMessage());
    }

  }

  private String isNull(String input, String replace) {
    if (input == null)
      return replace;
    else
      return input;
  }

  public ImageLibRecord(File theFile, boolean isWindows) {
    if (isWindows)
      mFullPathWin = theFile.getAbsolutePath();
    else
      mFullPathMac = theFile.getAbsolutePath();
    mFileName = theFile.getName();
    mFileSize = theFile.length();
    mFileDate = new java.util.Date(theFile.lastModified());
    mRecordModified = new java.util.Date();
    // Just pick off file extension for now
    if (mFileName.indexOf(".") > -1)
      mFileFormat = mFileName.substring(mFileName.lastIndexOf(".") + 1).toUpperCase();
  }

  public ImageLibRecord(File theFile, boolean isWindows, String catalogName, String topCategory) {
    if (isWindows)
      mFullPathWin = theFile.getAbsolutePath();
    else
      mFullPathMac = theFile.getAbsolutePath();
    mFileName = theFile.getName();
    mFileSize = theFile.length();
    mFileDate = new java.util.Date(theFile.lastModified());
    mRecordModified = new java.util.Date();
    // Just pick off file extension for now
    if (mFileName.indexOf(".") > -1)
      mFileFormat = mFileName.substring(mFileName.lastIndexOf(".") + 1).toUpperCase();

    mTopCategory = topCategory;
    mCategoryFullPath = catalogName + "," + parseFullPath(theFile.getAbsolutePath());
    mCategoryName = mCategoryFullPath.split(",")[mCategoryFullPath.split(",").length-1];
  }

  public String parseFullPath(String thePath) {
    String result = thePath;

    // Convert back slashes to forward
    result = result.replace('\\', '/');
    result = result.replace(':', '/');

    // First cut off everything before \SwipeFile\ portion
    result = result.substring(result.indexOf('/' + mTopCategory + '/') + 1);
    // Next drop off FileName
    result = result.substring(0, result.length() - mFileName.length() - 1);
    // Replace Comma with space
    result = result.replace(',', ' ');
    // Now convert slashes to comma
    result = result.replace('/', ',');

    return result;
  }

  public int getRecordId() { return mRecordID; }
  public int getBatchId() { return mBatchID; }
  public int getCatalogId() { return mCatalogID; }
  public String getCatalogName() { return mCatalogName; }
  public String getRecordName() { return mRecordName; }
  public int getStatusId() { return mStatusID; }
  public String getStatus() { return mStatus; }
  public String getFullPathMac() { return mFullPathMac; }
  public String getFullPathWin() { return mFullPathWin; }
  public String getFullPath(boolean isWindows) {
    if (isWindows)
      return getFullPathWin();
    else
      return getFullPathMac();
  }
  public String getFileName() { return mFileName; }
  public boolean getDontDelete() { return mDontDelete; }
  public String getCatalogUser() { return mCatalogUser; }
  public String getKeyline() { return mKeyline; }
  public String getCDName() { return mCDName; }
  public String getMFRName() { return mMFRName; }
  public boolean getExclusive() { return mExclusive; }
  public String getArea() { return mArea; }
  public boolean getOrigOnFile() { return mOrigOnFile; }
  public boolean getModelRelease() { return mModelRelease; }
  public String getNotes() { return (mNotes == null ? "" : mNotes); }
  public String getFileFormat() { return mFileFormat; }
  public long getFileSizeBytes() { return mFileSize; }
  public java.util.Date getFileDate() { return mFileDate; }
  public java.util.Date getRecordCreated() { return mRecordCreated; }
  public java.util.Date getRecordModified() { return mRecordModified; }
  public String getScanNumber() { return mScanNum; }
  public String getUsageRules() { return mUsageRules; }
  public String getReleases() { return mReleases; }
  public int getReleaseId() { return mReleaseId; }
  public String getImageCompany() { return mImageCompany; }
  public String getAdvertiser() { return mAdvertiser; }
  public String getOriginalKeywords() { return mOriginalKeywords; }
  public String getPNumber() { return mPNumber; }
  public boolean getWebDisplay() { return mWebDisplay; }
  public boolean getPriorityDisplay() { return mPriorityDisplay; }

  public ArrayList getCategories() { return mCategories; }
  public String getCategoriesStr() {return ""; }
  public ArrayList getCategories(CamsDB theCamsDB, HashMap theCategoryFullPathHash) {
    if ((mCategories == null) || (mCategories.size() == 0)) {
      // Load Categories from Database
      try {
        ResultSet rs;
        String sql = "SELECT ImageLibCategory.category_id " +
            "FROM ImageLibCategory INNER JOIN ImageLibCategoryRecord ON " +
            "ImageLibCategory.category_id = ImageLibCategoryRecord.category_id " +
            "WHERE ImageLibCategoryRecord.record_id = " + mRecordID;
        rs = theCamsDB.query(sql);
        mCategories = new ArrayList();
        while (rs.next()) {
          int categoryId = rs.getInt("category_id");
          mCategories.add(new IDNamePair(rs.getInt("category_id"),
                         (String) theCategoryFullPathHash.get(new Integer(categoryId))));
        }
        if (rs.getStatement() != null) rs.getStatement().close();
        rs.close();
      }
      catch (Exception ex) {
        cams.console.Console.println("getCategories(camsDB): " + ex.getMessage());
      }
    }
    Collections.sort(mCategories);
    return mCategories;
  }

  public ArrayList getKeywords() { return mKeywords; }
  public ArrayList getKeywords(CamsDB theCamsDB) {
    if ((mKeywords == null) || (mKeywords.size() == 0)) {
      // Load Keywords from Database
      try {
        ResultSet rs;
        String sql = "SELECT ImageLibKeyword.keyword_id, ImageLibKeyword.keyword " +
            "FROM ImageLibKeywordRecord INNER JOIN " +
            "ImageLibKeyword ON ImageLibKeywordRecord.keyword_id = ImageLibKeyword.keyword_id " +
            "WHERE ImageLibKeywordRecord.record_id = " + mRecordID + " " +
            "ORDER BY ImageLibKeyword.keyword";
        rs = theCamsDB.query(sql);
        mKeywords = new ArrayList();
        while (rs.next()) {
          mKeywords.add(new IDNamePair(rs.getInt("keyword_id"), rs.getString("keyword")));
        }
        if (rs.getStatement() != null) rs.getStatement().close();
        rs.close();
      }
      catch (Exception ex) {
        cams.console.Console.println("getKeywords(camsDB): " + ex.getMessage());
      }
    }
    Collections.sort(mKeywords);
    return mKeywords;
  }

  public byte[] getThumbSmall() { return mThumbSmall; }
  public byte[] getThumbMedium() { return mThumbMedium; }
  public byte[] getThumbLarge() { return mThumbLarge; }
  public byte[] getThumbXLarge() { return mThumbXLarge; }

  public String getFileDateStr24() { // 24-Hour
    try {
      SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      return df.format(mFileDate);
    }
    catch (Exception ex) {
      return "";
    }
  }

  public static String getFileDateStr24(java.util.Date theDate) {
    try {
      SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      return df.format(theDate);
    }
    catch (Exception ex) {
      return "";
    }
  }

  public String getFileDateStr12() { // 12-Hour
    try {
      SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
      return df.format(mFileDate);
    }
    catch (Exception ex) {
      return "";
    }
  }

  public void reloadAllThumbnails(CamsDB theCamsDB) {
    mThumbXLarge = null;
    getThumbXLarge(theCamsDB);

    mThumbLarge = null;
    getThumbLarge(theCamsDB);

    mThumbMedium = null;
    getThumbMedium(theCamsDB);

    mThumbSmall = null;
    getThumbSmall(theCamsDB);
  }

  public byte[] getThumbXLarge(CamsDB theCamsDB) {
    if (mThumbXLarge == null) {
      mThumbXLarge = loadThumbnail(theCamsDB, "ThumbXLarge");
    }
    return mThumbXLarge;
  }
  public byte[] getThumbLarge(CamsDB theCamsDB) {
    if (mThumbLarge == null) {
      mThumbLarge = loadThumbnail(theCamsDB, "ThumbLarge");
    }
    return mThumbLarge;
  }
  public byte[] getThumbMedium(CamsDB theCamsDB) {
    if (mThumbMedium == null) {
      mThumbMedium = loadThumbnail(theCamsDB, "ThumbMedium");
    }
    return mThumbMedium;
  }
  public byte[] getThumbSmall(CamsDB theCamsDB) {
    if (mThumbSmall == null) {
      mThumbSmall = loadThumbnail(theCamsDB, "ThumbSmall");
    }
    return mThumbSmall;
  }

  public String getCategoryFullPath() { return mCategoryFullPath; }
  public String getCategoryName() { return mCategoryName; }
  public int getCategoryId() { return mCategoryId; }

  public String getNotes(CamsDB theCamsDB) {
    if (mNotes == null)
      mNotes = theCamsDB.querySingleString("SELECT Notes FROM ImageLibNotes WHERE " +
                                           "record_id = " + mRecordID);

    return (mNotes == null ? "" : mNotes);
  }

  /** SET Functions **/
  public void setRecordId(int theValue) { mRecordID = theValue; }
  public void setBatchId(int theValue) { mBatchID = theValue; }
  public void setCatalogId(int theValue) { mCatalogID = theValue; }
  public void setCatalogName(String theValue) { mCatalogName = theValue; }
  public void setRecordName(String theValue) { mRecordName = theValue; }
  public void setStatusId(int theValue) { mStatusID = theValue; }
  public void setStatus(String theValue) { mStatus = theValue; }
  public void setFullPathMac(String theValue) { mFullPathMac = theValue; }
  public void setFullPathWin(String theValue) { mFullPathWin = theValue; }
  public void setFileName(String theValue) { mFileName = theValue; }
  public void setDontDelete(boolean theValue) { mDontDelete = theValue; }
  public void setCatalogUser(String theValue) { mCatalogUser = theValue; }
  public void setKeyline(String theValue) { mKeyline = theValue; }
  public void setCDName(String theValue) { mCDName = theValue; }
  public void setMFRName(String theValue) { mMFRName = theValue; }
  public void setExclusive(boolean theValue) { mExclusive = theValue; }
  public void setArea(String theValue) { mArea = theValue; }
  public void setOrigOnFile(boolean theValue) { mOrigOnFile = theValue; }
  public void setModelRelease(boolean theValue) { mModelRelease = theValue; }
  public void setNotes(String theValue) { mNotes = theValue; }
  public void setFileFormat(String theValue) { mFileFormat = theValue; }
  public void setFileSize(long theValue) { mFileSize = theValue; }
  public void setFileDate(java.util.Date theValue) { mFileDate = theValue; }
  public void setRecordCreated(java.util.Date theValue) { mRecordCreated = theValue; }
  public void setRecordModified(java.util.Date theValue) { mRecordModified = theValue; }
  public void setScanNumber(String theValue) { mScanNum = theValue; }
  public void setUsageRules(String theValue) { mUsageRules = theValue; }
  public void setReleases(String theValue) { mReleases = theValue; }
  public void setReleaseId(int theValue) { mReleaseId = theValue; }
  public void setImageCompany(String theValue) { mImageCompany = theValue; }
  public void setAdvertiser(String theValue) { mAdvertiser = theValue; }
  public void setOriginalKeywords(String theValue) { mOriginalKeywords = theValue; }
  public void setThumbSmall(byte[] theValue) { mThumbSmall = theValue; }
  public void setThumbMedium(byte[] theValue) { mThumbMedium = theValue; }
  public void setThumbLarge(byte[] theValue) { mThumbLarge = theValue; }
  public void setThumbXLarge(byte[] theValue) { mThumbXLarge = theValue; }
  public void setCategories(ArrayList theValue) { mCategories = theValue; }
  public void setKeywords(ArrayList theValue) { mKeywords = theValue; }
  public void setPNumber(String theValue) { mPNumber = theValue; }
  public void setWebDisplay(boolean theValue) { mWebDisplay = theValue; }
  public void setPriorityDisplay(boolean theValue) { mPriorityDisplay = theValue; }
  public void setCategoryId(int theValue) { mCategoryId = theValue; }

  public boolean save(CamsDB theCamsDB) {
//record_id, batch_id, RecordName, Status, FullPathMac, FullPathWin
//FileName, DontDelete, CatalogUser, Keyline, CDName, MFRName, Exclusive
//Area, OrigOnFile, ModelRelease, Notes, FileFormat, FileSize, FileDate
//RecordCreated, LastUpdate, ScanNum, UsageRules, Releases, ImageCompany
//Advertiser

    PreparedStatement ps = null;
    boolean result = false;
    String sql = "UPDATE ImageLibRecord SET RecordName = ?, Status_id = ?, " +
       "FullPathMac = ?, FullPathWin = ?, FileName = ?, DontDelete = ?, " +
       "CatalogUser = ?, Keyline = ?, CDName = ?, MFRName = ?, Exclusive = ?, " +
       "Area = ?, OrigOnFile = ?, ModelRelease = ?, FileFormat = ?, " +
       "FileSize = ?, FileDate = ?, RecordCreated = ?, LastUpdate = getDate(), " +
       "ScanNum = ?, UsageRules = ?, Release_id = ?, ImageCompany = ?, " +
       "Advertiser = ?, batch_id = ?, catalog_id = ?, OriginalKeywords = ?, " +
       "PNumber = ?, WebDisplay = ?, PriorityDisplay = ? WHERE record_id = ?";

    try {
      ps = theCamsDB.getConnection().prepareStatement(sql);
      ps.setString(1, mRecordName);
      if (mStatusID == -1)
        ps.setNull(2, java.sql.Types.INTEGER);
      else
        ps.setInt(2, mStatusID);
      ps.setString(3, mFullPathMac);
      ps.setString(4, mFullPathWin);
      ps.setString(5, mFileName);
      ps.setBoolean(6, mDontDelete);
      ps.setString(7, mCatalogUser);
      ps.setString(8, mKeyline);
      ps.setString(9, mCDName);
      ps.setString(10, mMFRName);
      ps.setBoolean(11, mExclusive);
      ps.setString(12, mArea);
      ps.setBoolean(13, mOrigOnFile);
      ps.setBoolean(14, mModelRelease);
//      ps.setString(15, mNotes);
      ps.setString(15, mFileFormat);
      ps.setLong(16, mFileSize);
      ps.setTimestamp(17, new Timestamp(mFileDate.getTime()));
      ps.setTimestamp(18, new Timestamp(mRecordCreated.getTime()));
      ps.setString(19, mScanNum);
      ps.setString(20, mUsageRules);
      if (mReleaseId == -1)
        ps.setNull(21, java.sql.Types.INTEGER);
      else
        ps.setInt(21, mReleaseId);
      ps.setString(22, mImageCompany);
      ps.setString(23, mAdvertiser);
      ps.setLong(24, mBatchID);
      ps.setLong(25, mCatalogID);
      ps.setString(26, mOriginalKeywords);
      ps.setString(27, mPNumber);
      ps.setBoolean(28, mWebDisplay);
      ps.setBoolean(29, mPriorityDisplay);

      ps.setLong(30, mRecordID);

      ps.executeUpdate();
      ps.close();
      ps = null;

      sql = "UPDATE ImageLibNotes SET Notes = ? WHERE record_id = ?";
      ps = theCamsDB.getConnection().prepareStatement(sql);
      if ((mNotes != null) && (mNotes.length() > 8000))
        mNotes = mNotes.substring(0, 8000);
      ps.setString(1, mNotes);
      ps.setLong(2, mRecordID);
      ps.executeUpdate();
      ps.close();
      ps = null;

      mRecordModified = new java.util.Date();

      updateRecordCategories(theCamsDB);
      updateRecordKeywords(theCamsDB);

      result = true;
    }
    catch (Exception ex) {
      cams.console.Console.println("ImageLibRecord.save(): " + ex.getMessage());
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

  public boolean redirectReference(CamsDB theCamsDB) {

    PreparedStatement ps = null;
    boolean result = false;
    String sql = "UPDATE ImageLibRecord SET " +
       "FullPathMac = ?, FullPathWin = ?, FileName = ?, FileFormat = ?, " +
       "FileSize = ?, FileDate = ?, LastUpdate = getDate() " +
       "WHERE record_id = ?";

    try {
      ps = theCamsDB.getConnection().prepareStatement(sql);
      ps.setString(1, mFullPathMac);
      ps.setString(2, mFullPathWin);
      ps.setString(3, mFileName);
      ps.setString(4, mFileFormat);
      ps.setLong(5, mFileSize);
      ps.setTimestamp(6, new Timestamp(mFileDate.getTime()));
      ps.setLong(7, mRecordID);

      ps.executeUpdate();
      ps.close();
      ps = null;

      mRecordModified = new java.util.Date();

      result = true;
    }
    catch (Exception ex) {
      cams.console.Console.println("ImageLibRecord.redirectReference(): " + ex.getMessage());
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

  /**
   * Deletes and ReCreates Record to Category linking
   * @return boolean
   */
  public boolean updateRecordCategories(CamsDB theCamsDB) {
    PreparedStatement ps = null;
    boolean result = false;
    try {
      String sql = "DELETE FROM ImageLibCategoryRecord WHERE record_id = " +
          mRecordID;
      theCamsDB.execute(sql);

      for (int i=0; i < mCategories.size(); i++) {
        IDNamePair theCategory = (IDNamePair) mCategories.get(i);
        sql = "INSERT INTO ImageLibCategoryRecord (category_id, record_id) " +
            "VALUES (" + theCategory.getId() + ", " + mRecordID + ")";
        theCamsDB.execute(sql);
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("ImageLibRecord.updateRecordCategories(): " + ex.getMessage());
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

  /**
   * Deletes and ReCreates Record to Keyword linking
   * @return boolean
   */
  public boolean updateRecordKeywords(CamsDB theCamsDB) {
    PreparedStatement ps = null;
    boolean result = false;
    try {
      String sql = "DELETE FROM ImageLibKeywordRecord WHERE record_id = " +
          mRecordID;
      theCamsDB.execute(sql);

      for (int i=0; i < mKeywords.size(); i++) {
        IDNamePair theKeyword = (IDNamePair) mKeywords.get(i);
        sql = "INSERT INTO ImageLibKeywordRecord (keyword_id, record_id) " +
            "VALUES (" + theKeyword.getId() + ", " + mRecordID + ")";
        theCamsDB.execute(sql);
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("ImageLibRecord.updateRecordKeywords(): " + ex.getMessage());
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

  private byte[] loadThumbnail(CamsDB theCamsDB, String colName) {
    String sql = "SELECT " + colName + " FROM ImageLibRecord WHERE " +
        "record_id = " + mRecordID;

    return theCamsDB.queryByteArray(sql);
  }

}

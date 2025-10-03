package cams.database;

import java.text.*;
import java.util.*;
import java.io.*;
import java.sql.*;

// import cams.console.*;
import cams.imaging.ImageMagick;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CamsDB {

  private Connection mConnection = null;
  public String lastError = null;
  public ImageMagick mImaging = null;
  private int mWindowsOS = -1;
  private UserInfo mUserInfo = new UserInfo(); // Logged In User Info
  private Properties mProperties = new Properties(); // Properties from cams-image.properties file

  public CamsDB() {
    initDB();
  }

  public void setImaging(ImageMagick theImagingObject) {
    mImaging = theImagingObject;
  }

  public ImageMagick getImaging() { return mImaging; }

  public UserInfo getUserInfo() { return mUserInfo; }

  private void initDB() {
    try {
      // Register the new Microsoft JDBC driver for SQL Server
      // Updated to support TLS 1.2 and Azure SQL Database
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      //Old driver (deprecated): Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");

      // Load database properties
      FileInputStream instream = new FileInputStream(new File("cams-image.properties"));
      mProperties.load(instream);
      instream.close();

      String dbConnectStr = mProperties.getProperty("dbConnectStr", "");
      String dbUser = mProperties.getProperty("dbUser", "");
      String dbPass = mProperties.getProperty("dbPass", "");
      cams.console.Console.println("dbConnectStr = " + dbConnectStr);

//      mConnection = DriverManager.getConnection
//          ("jdbc:microsoft:sqlserver://xptestj:1433;databasename=CAMS","cams","cams");
      mConnection = DriverManager.getConnection(dbConnectStr, dbUser, dbPass);
    }
    catch (Exception ex) {
      lastError = "initDB: " + ex.getMessage();
      cams.console.Console.println("camsDB:initDB: " + ex.getMessage());
    }
  }

  public Connection getConnection() { return mConnection; }
  public Properties getProperties() { return mProperties; }

  public ResultSet query(String queryString) throws SQLException
  {
      ResultSet myRs = null;

      try
      {
//        Statement myStmt = mConnection.createStatement(ResultSet.
//            TYPE_SCROLL_INSENSITIVE,
//            ResultSet.CONCUR_READ_ONLY);
        Statement myStmt = mConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
        myRs = myStmt.executeQuery(queryString);
      }
      catch (Exception exc)
      {
        lastError = "query: " + exc.getMessage();
        cams.console.Console.println("camsDB:query: " + exc.getMessage());
      }

      return myRs;
  }

  public int getQueryRows(String queryString) throws SQLException
 {
 String sql = queryString;
 int numRows;
 cams.console.Console.println("getQueryRows()");
// Remove all the SELECT elements and replace with Count(*)
 sql = "SELECT Count(*) " + sql.substring(sql.indexOf("FROM"));
// Remove ORDER BY
 if (sql.indexOf("ORDER BY") != -1)
 sql = sql.substring(0, sql.indexOf(" ORDER BY"));
 numRows = Integer.parseInt(querySingleString(sql));
 return numRows;
 }


  public boolean execute(String queryString) {
    boolean result = true;

    try
    {
      Statement myStmt = mConnection.createStatement();
      myStmt.execute(queryString);
      myStmt.close();
    }
    catch (Exception exc)
    {
      lastError = "execute: " + exc.getMessage();
      cams.console.Console.println("camsDB:execute: " + exc.getMessage());
      result = false;
    }

    return result;
  }

  public int executeUpdate(String queryString) {
    int result = 0;

    try
    {
      Statement myStmt = mConnection.createStatement();
      result = myStmt.executeUpdate(queryString);
      myStmt.close();
    }
    catch (Exception exc)
    {
      lastError = "execute: " + exc.getMessage();
      cams.console.Console.println("camsDB:executeUpdate: " + exc.getMessage());
      result = 0;
    }

    return result;
  }

  public String querySingleString(String queryString) {
    String result = null;

    try {
      ResultSet rs = query(queryString);
      if (rs.next())
        result = rs.getString(1);

      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close();
    }
    catch (Exception ex) {
      lastError = "querySingleValue: " + ex.getMessage();
      cams.console.Console.println("camsDB:querySingleValue: " + ex.getMessage());
    }
    return result;
  }

  public byte[] queryByteArray(String queryString) {
    byte[] result = null;

    try {
      ResultSet rs = query(queryString);
      if (rs.next())
        result = rs.getBytes(1);

      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close();
    }
    catch (Exception ex) {
      lastError = "queryByteArray: " + ex.getMessage();
      cams.console.Console.println("camsDB:queryByteArray: " + ex.getMessage());
    }
    return result;
  }

  public boolean test() {
    boolean result = true;
    ResultSet rs = null;

    try {
      rs = query("SELECT COUNT(*) AS Expr1 FROM ImageLibCatalog");
      if (rs.next())
        cams.console.Console.println("Count(*) FROM ImageLibCatalogs = " + rs.getInt(1));
      else
        cams.console.Console.println("Count(*) FROM ImageLibCatalogs = 0 !!");

    }
    catch (Exception ex) {
      lastError = "test: " + ex.getMessage();
      result = false;
      cams.console.Console.println("CamsDB Error (test): " + ex.getMessage());
    }
    finally {
      if (rs != null)
        try { rs.getStatement().close(); rs.close(); } catch (Exception ex) {}
      return result;
    }
  }

  public boolean windowsOS() {
    if (mWindowsOS == -1) {
      Properties props = System.getProperties();
      String osname = props.getProperty("os.name");
      if (osname.toUpperCase().indexOf("WINDOWS") == -1)
        mWindowsOS = 0; // Mac
      else
        mWindowsOS = 1; // Windows
    }

    return (mWindowsOS == 1);
  }

  public int getNewBatchId(String description) {
    int batchId = -1;

      String sql = "INSERT INTO ImageLibBatch (batch_time, login, description) VALUES " +
          "(getDate(), '" + getUserInfo().login + "', '" + description + "')";
      execute(sql);
      sql = "SELECT TOP 1 batch_id, batch_time FROM ImageLibBatch ORDER BY " +
          "batch_time DESC";
      try {
        ResultSet rs = query(sql);
        if (rs.next()) {
          batchId = rs.getInt("batch_id");
        }
        if (rs.getStatement() != null) rs.getStatement().close();
        rs.close();
      }
      catch (Exception ex) {
        cams.console.Console.println("Error Getting Batch ID: " + ex.getMessage());
      }
    return batchId;
  }

  public class UserInfo {
    public String fname = null;
    public String lname = null;
    public String empnum = null;
    public String login = null;
    private String mSearchID = null;
    public String getSearchID() {
      if (mSearchID == null) {
        String timeStr = Long.toString(new java.util.Date().getTime());
        mSearchID = login + "{" + timeStr.substring(timeStr.length() - 3, timeStr.length()) + "}";
        cams.console.Console.println("Unique ID for this session: " + mSearchID);
      }
      return mSearchID;
    }
  }
}

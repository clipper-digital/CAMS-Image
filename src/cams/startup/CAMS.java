package cams.startup;

import javax.swing.*;
import javax.swing.UIManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import cams.database.*;
import cams.imagelib.*;
import cams.imaging.*;
// import cams.console.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CAMS {
  boolean packFrame = true;
  CamsDB mCamsDB = null;

  //Construct the application
  public CAMS() {
    cams.console.Console.println("Starting Up...");
    SplashFrame splashFrame = new SplashFrame();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      splashFrame.pack();
    }
    else {
      splashFrame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = splashFrame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    splashFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    splashFrame.jbtnContinue.setVisible(false);
    splashFrame.setVisible(true);

    Properties p = System.getProperties();
    String osname = p.getProperty("os.name");
    cams.console.Console.println("os.name = " + osname);

    // Initialize Database Connection
    splashFrame.jlblWait.setText("Connecting to Database...");
    boolean dbConnected = false;
    mCamsDB = new CamsDB();
    if (mCamsDB.lastError == null)
      dbConnected = mCamsDB.test();

    if (!dbConnected) {
      splashFrame.jlblWait.setVisible(false);
      JOptionPane.showMessageDialog(splashFrame,
                                    "A connection could not be established with the database.\n" +
                                    "The error message was \"" + mCamsDB.lastError + "\".\n" +
                                    "Click 'OK' to close the application.",
                                    "Can Not Connect to the Database",
                                    JOptionPane.ERROR_MESSAGE);
      System.exit(0);
    }
    splashFrame.setDatabase(mCamsDB);

    // Validate Entries in cams-image.properties
//    dbConnectStr=jdbc:microsoft:sqlserver://xptestj:1433;databasename=CAMS
//    dbUser=cams
//    dbPass=cams
//    ImageAppDir=c:\\Program Files\\ImageMagick-6.0.4-Q16\\
//    TempDir=c:\\temp\\
//    AcrobatReader=C:\\Program Files\\adobe\\Acrobat 6.0\\Reader\\AcroRd32.exe

    // Just check for the existence of the specified Temp folder now,
    // check for the user specific folder after login
    if (!checkTempDir()) {
      JOptionPane.showMessageDialog(splashFrame,
                                    "The location specified for the Temporary Directory could " +
                                    "not be verified.\nPlease check the cams-image.properties file and " +
                                    "make sure the TempDir property specifies a valid directory.",
                                    "Can Not Find Temporary Directory",
                                    JOptionPane.ERROR_MESSAGE);
      System.exit(0);

    }

    splashFrame.jlblWait.setText("Checking Graphics Engine...");
    // Test ImageMagick
    ImageMagick imaging = new ImageMagick(mCamsDB);
    cams.console.Console.println("***************************\n" +
                       "Imaging Test = \n" +
                       imaging.test() +
                       "***************************");

    // Store the ImageMagick object in the CamsDB object
    // since we pass the DB object around (and therefor the imaging object)
    mCamsDB.setImaging(imaging);

    // Check that .ICM and .ICC color profiles exist
    File fileCMYK = new File(imaging.getImageAppDir() + imaging.getProfileCMYK());
    if (!fileCMYK.exists())
      JOptionPane.showMessageDialog(splashFrame,
                              "Could not find CMYK Color Profile '" + fileCMYK.getName() + "'\n" +
                              "in path '" + imaging.getImageAppDir() + "'.\n\n" +
                              "The application will continue but new thumbnails may not be created properly.",
                              "Can Not Find Color Profile",
                              JOptionPane.ERROR_MESSAGE);

    File fileRGB = new File(imaging.getImageAppDir() + imaging.getProfileRGB());
    if (!fileRGB.exists())
      JOptionPane.showMessageDialog(splashFrame,
                              "Could not find RGB Color Profile '" + fileRGB.getName() + "'\n" +
                              "in path '" + imaging.getImageAppDir() + "'.\n\n" +
                              "The application will continue but new thumbnails may not be created properly.",
                              "Can Not Find Color Profile",
                              JOptionPane.ERROR_MESSAGE);

    splashFrame.jlblWait.setVisible(false);

    if (!imaging.isActive())
      JOptionPane.showMessageDialog(splashFrame,
                              "The graphics engine could not be initialized.  Please check the properties file.\n" +
                              "The application will continue but new thumbnails will not be created.",
                              "Can Not Connect Initialize Graphics Converter",
                              JOptionPane.ERROR_MESSAGE);

    splashFrame.jbtnContinue.setVisible(true);
    splashFrame.setVisible(false);

    Logon logonDlg = new Logon(null, "CAMS Image Library Logon", true);
    frameSize = logonDlg.getSize();
    logonDlg.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    logonDlg.setCamsDB(mCamsDB);
    logonDlg.show();
    if (!logonDlg.getSuccess())
      System.exit(0);
    else {
      // Now check temp dir for the actual LoginID
      cams.database.CamsDB.UserInfo theUserInfo = mCamsDB.getUserInfo();
      if (!checkTempDir(theUserInfo.login)) {
        JOptionPane.showMessageDialog(splashFrame,
                                      "The temporary folder '" +
                                      getUserTempDir(theUserInfo.login) + "' could " +
                                      "not be found.\nPlease check that the folder exists.",
                                      "Can Not Find Temporary Directory",
                                      JOptionPane.ERROR_MESSAGE);
        System.exit(0);

      }
      imaging.deleteOldFiles(imaging.getTempDir(), 3);
      showImageLib();
    }

  }

  private boolean directoryExists(String thePath) {
    try {
      return (new File(thePath).isDirectory());
    }
    catch (Exception ex) {}
    return false;
  }

  private boolean checkTempDir() {
    Properties props = mCamsDB.getProperties();
    String tempDir = props.getProperty("TempDir", "");
    return directoryExists(tempDir);
  }

  private boolean checkTempDir(String login) {
    return directoryExists(getUserTempDir(login));
  }

  private String getUserTempDir(String login) {
    Properties props = mCamsDB.getProperties();
    String tempDir = props.getProperty("TempDir", "");

    if (mCamsDB.windowsOS()) {
      if (tempDir.endsWith("\\"))
        tempDir += login;
      else
        tempDir += "\\" + login;
    } else { // Mac
      if (tempDir.endsWith("/"))
        tempDir += login;
      else
        tempDir += "/" + login;
    }

    return tempDir;
  }

  private void showImageLib() {
    ImageLibFrame imgLibFrame = new ImageLibFrame(mCamsDB);
    boolean packFrame = false;
    if (packFrame) {
      imgLibFrame.pack();
    }
    else {
      imgLibFrame.validate();
    }
    imgLibFrame.setSize(800, 600);
    // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = imgLibFrame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    imgLibFrame.setLocation( (screenSize.width - frameSize.width) / 2,
                                (screenSize.height - frameSize.height) / 2);
    imgLibFrame.doEnableDisableFrame(false);
    imgLibFrame.show();
    imgLibFrame.refreshCatalogTree();
    imgLibFrame.jbtnShowThumbnails_actionPerformed(new ActionEvent(this, 0, null));
  }

  //Main method
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new CAMS();
  }
}

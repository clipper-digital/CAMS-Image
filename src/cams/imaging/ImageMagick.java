package cams.imaging;

import java.text.*;
import java.util.*;
import java.io.*;

// import cams.console.*;
import cams.imagelib.*;
import cams.database.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author JJS Solutions
 * @version 1.0
 */

public class ImageMagick {
//  private String mImageAppDir = null;
//  private String mTempDir = null;
  private boolean mActive = false;
//  private long mImageTimeout = 45000;
  private Properties mProperties = null;
  private CamsDB mCamsDB = null;

  public ImageMagick(CamsDB theCamsDB) {
    try {
      mCamsDB = theCamsDB;
      mProperties = theCamsDB.getProperties();

      // Load properties
//      mImageAppDir = mProperties.getProperty("ImageAppDir", "");
//      mTempDir = mProperties.getProperty("TempDir", "");
//      mImageTimeout = Long.parseLong(mProperties.getProperty("ConvertTimeout", "45")) * 1000;
    }
    catch (Exception ex) {
      cams.console.Console.println("ImageMagick(): " + ex.toString());
    }
  }

  public String getTempDir() {
    String tempDir = mProperties.getProperty("TempDir", "");
    if ((mCamsDB.getUserInfo().login != null) && (mCamsDB.getUserInfo().login.length() > 0)) {
      tempDir += mCamsDB.getUserInfo().login;
      if (mCamsDB.windowsOS())
        tempDir += "\\";
      else
        tempDir += "/";
    }

    return tempDir;
  }
  public String getImageAppDir() { return mProperties.getProperty("ImageAppDir", ""); }
  public long getImageTimout() { return Long.parseLong(mProperties.getProperty("ConvertTimeout", "45")) * 1000; }
  public String getProfileCMYK() { return mProperties.getProperty("ProfileCMYK", "clipper.icm"); }
  public String getProfileRGB() { return mProperties.getProperty("ProfileRGB", "AdobeRGB1998.icc"); }

  public String test() {
    String theCommand = "";

    theCommand = "\"" + getImageAppDir() + "convert\" -verbose logo: \"" +
        getTempDir() + "logo.gif\"";
    String result = shellCmd(theCommand);
    if (result.length() > 0) mActive = true;

    deleteFile(getTempDir() + "logo.gif");
    return result;
  }

  public String shellCmd(String theCommand) {
    Process p = null;
    String results = "";

    try {
      cams.console.Console.println("shellCmd(" + theCommand + ")");
      Runtime r = Runtime.getRuntime();
      Properties props = System.getProperties();
      String osname = props.getProperty("os.name");
      if (osname.toUpperCase().indexOf("WINDOWS") == -1) {
        // Mac
        String[] theCommands = new String[] {"sh", "-c", theCommand};
        p = r.exec(theCommands);
      }
      else { // Windows
        p = r.exec(theCommand);
      }
      InputStreamReader inStream = new InputStreamReader(p.getInputStream());
      BufferedReader br = new BufferedReader(inStream);
      String line;
      // Wait up to 45 Seconds for Image to Convert
      long startTime = new Date().getTime();
      while ((!br.ready()) && // BufferedReader has no data
             (getProcessExitValue(p) == -999) && // Task still running
             (new Date().getTime() - startTime < getImageTimout())) { // Been under 45 secs
        Thread.sleep(100);
      }
      if (br.ready()) {
        while ( (line = br.readLine()) != null) {
          results += line + "\n";
        }
      }
      else if (getProcessExitValue(p) == -999) { // Process Still Running
        p.destroy(); // Kill Process
        cams.console.Console.println("Error Waiting for ImageMagick to return. (Waited " + getImageTimout()/1000 + " seconds)");
        results = null;
      }

      try {
        p.getInputStream().close();
        inStream.close();
        br.close();
      }
      catch (Exception ex) {
        cams.console.Console.println("ImageMagick:CloseStreams: " + ex.getMessage());
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("ImageMagick:shellCmd: " + ex.getMessage());
    }
    return results;
  }

  public String shellCmd(String[] theCommands) {
//    String wordEXEPath = "/Applications/Microsoft Office X/Microsoft
//    Word";
//      String docPath = "/users/xxx/test.doc";
//      String[] cmdArr;
//
//      cmdArr = new String[] {"open","-a",wordEXEPath,docPath};
//      Runtime.getRuntime().exec ( cmdArr);

    Process p = null;
    String results = "";

    for (int i=0; i < theCommands.length; i++) {
      cams.console.Console.println("  " + (i+1) + ": " + theCommands[i]);
    }
    try {
      Runtime r = Runtime.getRuntime();
      p = r.exec(theCommands);
      BufferedReader br = new BufferedReader
          (new InputStreamReader(p.getInputStream()));
      String line;
      while ( (line = br.readLine()) != null) {
        results += line + "\n";
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("ImageMagick:shellCmd: " + ex.getMessage());
    }
    return results;
  }

  public boolean isActive() { return mActive; }

  public String[] createThumbnails_(String fileName, int[] theSizes) {
    String theCommand = "";
    String[] theCommands;
    String result = "";
    String[] theThumbFileNames = new String[theSizes.length];

    deleteFiles(getTempDir(), "Image", "jpg");

//    theCommand = "\"" + getImageAppDir() + "convert\" \"" + fileName + "\" -colorspace rgb -thumbnail " +
//        theSizes[0] + "x" + theSizes[0] + " \"" + getTempDir() + "Image%02d.jpg\"";

    theCommand = "\"" + getImageAppDir() + "convert\" \"" + fileName + "\" " +
        "-profile \"" + getImageAppDir() + getProfileCMYK() + "\" " +
        "-profile \"" + getImageAppDir() + getProfileRGB() + "\" " +
        "-colorspace rgb -thumbnail " +
        theSizes[0] + "x" + theSizes[0] + " \"" + getTempDir() + "Image%02d.jpg\"";

    result = shellCmd(theCommand);

    if (result == null) // Error converting PDF
      return null;

//    Console.println("Result=\n" + result + "\n\n");
    theThumbFileNames[0] = getTempDir() + "Image00.jpg";

    // Create smaller thumbnails from 1st
    for (int i=1; i < theSizes.length; i++) {
      theCommand = "\"" + getImageAppDir() + "convert\" \"" + theThumbFileNames[0] + "\" -resize " +
          theSizes[i] + "x" + theSizes[i] + " \"" + getTempDir() + "Image" + i + ".jpg\"";

      result = shellCmd(theCommand);
//      Console.println("Result=\n" + result + "\n\n");
      theThumbFileNames[i] = getTempDir() + "Image" + i + ".jpg";
    }

    return theThumbFileNames;
  }

  public String[] createThumbnails(String fileName, int[] theSizes, boolean isCMYK) {
    String theCommand = "";
    String[] theCommands;
    String result = "";
    String[] theThumbFileNames = new String[theSizes.length];

    deleteFiles(getTempDir(), "Image", "jpg");

    if (isCMYK)
      theCommand = "\"" + getImageAppDir() + "convert\" -density 255 " +
        "-profile \"" + getImageAppDir() + getProfileCMYK() + "\" " +
        "-colorspace cmyk \"" + fileName + "\" " +
        "-profile \"" + getImageAppDir() + getProfileRGB() + "\" " +
        " -thumbnail " + theSizes[0] + "x" + theSizes[0] + " -density 72 -colorspace rgb " +
        "\"" + getTempDir() + "Image%02d.jpg\"";
    else
      theCommand = "\"" + getImageAppDir() + "convert\" -density 255 " +
        "-profile \"" + getImageAppDir() + getProfileRGB() + "\" " +
        "-colorspace rgb \"" + fileName + "\" " +
        "-profile \"" + getImageAppDir() + getProfileRGB() + "\" " +
        " -thumbnail " + theSizes[0] + "x" + theSizes[0] + " -density 72 -colorspace rgb " +
        "\"" + getTempDir() + "Image%02d.jpg\"";

    result = shellCmd(theCommand);

    if (result == null) // Error converting PDF
      return null;

//    Console.println("Result=\n" + result + "\n\n");
    theThumbFileNames[0] = getTempDir() + "Image00.jpg";

    // Create smaller thumbnails from 1st
    for (int i=1; i < theSizes.length; i++) {
      theCommand = "\"" + getImageAppDir() + "convert\" \"" + theThumbFileNames[0] + "\" -thumbnail " +
          theSizes[i] + "x" + theSizes[i] + " \"" + getTempDir() + "Image" + i + ".jpg\"";

      result = shellCmd(theCommand);
//      Console.println("Result=\n" + result + "\n\n");
      theThumbFileNames[i] = getTempDir() + "Image" + i + ".jpg";
    }

    return theThumbFileNames;
  }

  public void deleteFiles(String thePath, String thePatternStart, String thePatternEnd) {
    File theDir = new File(thePath);
    String[] theFiles = theDir.list();

    for (int i=0; i < theFiles.length; i++) {
      if (theFiles[i].startsWith(thePatternStart) &&
        theFiles[i].endsWith(thePatternEnd))
      deleteFile(thePath + theFiles[i]);
    }
  }

  public boolean deleteFile(String theFile) {
    boolean result = false;
    try {
      result = new File(theFile).delete();
    }
    catch (Exception ex) {}

    return result;
  }

  public void deleteOldFiles(String thePath, int daysOld) {
    File theDir = new File(thePath);
    File[] theFiles = theDir.listFiles();

    long msDifference = daysOld * 24 * 60 * 60 * 1000;
    long msNow = new Date().getTime();
    int deleteCount = 0;

    for (int i=0; i < theFiles.length; i++) {
      if (msNow - theFiles[i].lastModified() > msDifference) {
        try {
          theFiles[i].delete();
          deleteCount++;
        }
        catch (Exception ex) {
          cams.console.Console.println("Could not delete file " + theFiles[i].getName());
        }
      }
    }
    cams.console.Console.println("Deleted " + deleteCount + " files older than " + daysOld +
                    " days old from the Temp folder");
  }

  public static int getProcessExitValue(Process proc) {
    int theExitValue = -999;

    try {
      theExitValue = proc.exitValue();
    }
    catch (Exception ex) {}

    return theExitValue;
  }
}

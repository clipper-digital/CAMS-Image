package cams.console;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.lang.*;
import java.text.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author JJS Solutions
 * @version 1.0
 */

public class Console {
  FileOutputStream fd;

  /**
   * Constructor method to instantiate the console object
   */
  public Console() {
  }

  /**
   * Method to output a message to the console.txt file when the DEBUG flag is set to 'true'.
   * The console.txt file path is specified in the file 'application.properties'
   *
   * @param msg Message to be output to console.txt file
   */
  public static void println(String msg) {
    String filePath;
    String lf = String.valueOf('\r') + String.valueOf('\n');
    Console theConsole = new Console();

    try {
      // Check if console.log is too large and copy to backup
      File file = new File("console.log");
      filePath = file.getAbsolutePath();
      long length = file.length();
      if (length > 150000) {
        File file2 = new File("console1.log");
        if (file2.exists())
          file2.delete();
        file.renameTo(file2);
        file.delete();
      }

      FileOutputStream fd = new FileOutputStream("console.log", true);
      SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(
          "dd-MMM-yyyy HH:mm:ss.SSS");
      String timestamp = dateFormat.format(new java.util.Date());
      String msgWithTime = timestamp + " -- " + msg + lf;
      fd.write(msgWithTime.getBytes());
      fd.close();

      // Print to screen if running locally
      System.out.print(msgWithTime);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
  }

}

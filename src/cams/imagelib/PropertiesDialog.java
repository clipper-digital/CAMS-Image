package cams.imagelib;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;

import cams.database.*;
// import cams.console.*;

public class PropertiesDialog extends JDialog {
  private CamsDB mCamsDB = null;
  private Properties mProperties = null;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jbtnOK = new JButton();
  JCheckBox jckSave = new JCheckBox();
  JButton jbtnCancel = new JButton();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JTextField jtxtImageFolder = new JTextField();
  JTextField jtxtTempFolder = new JTextField();
  JTextField jtxtConvertTimeout = new JTextField();
  JTextField jtxtPageSmall = new JTextField();
  JTextField jtxtPageMedium = new JTextField();
  JTextField jtxtPageLarge = new JTextField();
  JTextField jtxtMaxRows = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField jtxtProfileRGB = new JTextField();
  JTextField jtxtProfileCMYK = new JTextField();
  JLabel jLabel9 = new JLabel();

  public PropertiesDialog(Frame frame, CamsDB theDB) {
    super(frame, "Application Properties", true);
    mCamsDB = theDB;
    mProperties = mCamsDB.getProperties();
    try {
      jbInit();
//      jckSave.setVisible(false);
      pack();
      initFields();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jbtnOK.setText("OK");
    jbtnOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnOK_actionPerformed(e);
      }
    });
    jckSave.setActionCommand("jCheckBox1");
    jckSave.setHorizontalAlignment(SwingConstants.CENTER);
    jckSave.setText("Save Changes to Disk");
    jbtnCancel.setText("Cancel");
    jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnCancel_actionPerformed(e);
      }
    });
    jPanel2.setLayout(verticalFlowLayout1);
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(xYLayout1);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setText("Image Magick Folder:");
    jLabel2.setText("Temporary Folder:");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel3.setText("Image Conversion Timeout (sec):");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel5.setText("Page Size Small Thumbnail:");
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel6.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel6.setText("MaxRows for Category Queries:");
    jLabel7.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel7.setText("Page Size Large Thumbnail:");
    jLabel8.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel8.setText("Page Size Medium Thumbnail:");
    jtxtImageFolder.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtImageFolder.setText("");
    jtxtTempFolder.setText("");
    jtxtTempFolder.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtTempFolder.setText("");
    jtxtConvertTimeout.setText("");
    jtxtConvertTimeout.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtConvertTimeout.setText("");
    jtxtPageSmall.setText("");
    jtxtPageSmall.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtPageSmall.setText("");
    jtxtPageMedium.setText("");
    jtxtPageMedium.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtPageMedium.setText("");
    jtxtPageLarge.setText("");
    jtxtPageLarge.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtPageLarge.setText("");
    jtxtMaxRows.setText("");
    jtxtMaxRows.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtMaxRows.setText("");
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel4.setText("RGB Color Profile:");
    jtxtProfileRGB.setText("");
    jtxtProfileRGB.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtProfileRGB.setText("");
    jtxtProfileCMYK.setText("");
    jtxtProfileCMYK.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel9.setText("CMYK Color Profile:");
    jLabel9.setFont(new java.awt.Font("Dialog", 1, 11));
    jPanel1.add(jtxtPageSmall,  new XYConstraints(200, 93, 81, 21));
    jPanel1.add(jtxtPageMedium, new XYConstraints(200, 122, 81, 21));
    jPanel1.add(jtxtPageLarge, new XYConstraints(200, 151, 81, 21));
    jPanel1.add(jtxtMaxRows, new XYConstraints(200, 180, 81, 21));
    jPanel1.add(jLabel1, new XYConstraints(9, 10, -1, -1));
    jPanel1.add(jLabel2, new XYConstraints(9, 38, -1, -1));
    jPanel1.add(jLabel3, new XYConstraints(9, 67, -1, -1));
    jPanel1.add(jLabel5, new XYConstraints(9, 96, -1, -1));
    jPanel1.add(jLabel6, new XYConstraints(9, 183, -1, -1));
    jPanel1.add(jLabel7, new XYConstraints(9, 154, -1, -1));
    jPanel1.add(jLabel8, new XYConstraints(9, 125, -1, -1));
    jPanel1.add(jtxtImageFolder, new XYConstraints(131, 7, 247, 21));
    jPanel1.add(jtxtTempFolder, new XYConstraints(131, 35, 247, 21));
    jPanel1.add(jtxtConvertTimeout, new XYConstraints(200, 64, 81, 21));
    jPanel1.add(jLabel4,   new XYConstraints(9, 241, -1, -1));
    jPanel1.add(jLabel9,   new XYConstraints(9, 212, -1, -1));
    jPanel1.add(jtxtProfileCMYK,      new XYConstraints(131, 209, 247, 21));
    jPanel1.add(jtxtProfileRGB,       new XYConstraints(131, 238, 247, 21));
    jPanel2.add(jckSave, null);
    jPanel2.add(jPanel3, null);
    jPanel3.add(jbtnOK, null);
    jPanel3.add(jbtnCancel, null);
    panel1.add(jPanel1, BorderLayout.CENTER);
    getContentPane().add(panel1);
    panel1.add(jPanel2,  BorderLayout.SOUTH);
  }

  private void initFields() {
    jtxtImageFolder.setText(mProperties.getProperty("ImageAppDir", ""));
    jtxtTempFolder.setText(mProperties.getProperty("TempDir", ""));
    jtxtConvertTimeout.setText(mProperties.getProperty("ConvertTimeout", "45"));
    jtxtPageSmall.setText(mProperties.getProperty("PageSizeSmall", "100"));
    jtxtPageMedium.setText(mProperties.getProperty("PageSizeMedium", "50"));
    jtxtPageLarge.setText(mProperties.getProperty("PageSizeLarge", "25"));
    jtxtMaxRows.setText(mProperties.getProperty("MaxRows", "1000"));
    jtxtProfileCMYK.setText(mProperties.getProperty("ProfileCMYK", "clipper.icm"));
    jtxtProfileRGB.setText(mProperties.getProperty("ProfileRGB", "AdobeRGB1998.icc"));
  }

  void jbtnCancel_actionPerformed(ActionEvent e) {
    hide();
  }

  void jbtnOK_actionPerformed(ActionEvent e) {
    // Check ImageMagick Folder
    if (! new File(jtxtImageFolder.getText()).exists()) {
      JOptionPane.showMessageDialog(this, "Invalid ImageMagick Folder.  " +
                                   "Please double-check the folder specified " +
                                   "for ImageMagick.", "Invalid Folder",
                                   JOptionPane.ERROR_MESSAGE);
      return;
    }
    String imageFolder = jtxtImageFolder.getText();
    if (mCamsDB.windowsOS()) {
      if (!imageFolder.endsWith("\\"))
        imageFolder += "\\";
    } else {
      if (!imageFolder.endsWith("/"))
        imageFolder += "/";
    }

    // Check Temp Folder
    if (! new File(jtxtTempFolder.getText()).exists()) {
      JOptionPane.showMessageDialog(this, "Invalid Temporary Folder.  " +
                                   "Please double-check the folder specified " +
                                   "for Temporary file storage.", "Invalid Folder",
                                   JOptionPane.ERROR_MESSAGE);
      return;
    }
    String tempFolder = jtxtTempFolder.getText();
    if (mCamsDB.windowsOS()) {
      if (!tempFolder.endsWith("\\"))
        tempFolder += "\\";
    } else {
      if (!tempFolder.endsWith("/"))
        tempFolder += "/";
    }

     /*
     ConvertTimeout=15
     PageSizeSmall=20
     PageSizeMedium=15
     PageSizeLarge=10
     MaxRows=100
     */
    int convertTimeout = Integer.parseInt(mProperties.getProperty("ConvertTimeout", "45"));
    int pageSmall = Integer.parseInt(mProperties.getProperty("PageSizeSmall", "100"));
    int pageMedium = Integer.parseInt(mProperties.getProperty("PageSizeMedium", "50"));
    int pageLarge = Integer.parseInt(mProperties.getProperty("PageSizeLarge", "25"));
    int maxRows = Integer.parseInt(mProperties.getProperty("MaxRows", "1000"));

    try { convertTimeout = Integer.parseInt(jtxtConvertTimeout.getText()); }
    catch (Exception ex) {}

    try { pageSmall = Integer.parseInt(jtxtPageSmall.getText()); }
    catch (Exception ex) {}

    try { pageMedium = Integer.parseInt(jtxtPageMedium.getText()); }
    catch (Exception ex) {}

    try { pageLarge = Integer.parseInt(jtxtPageLarge.getText()); }
    catch (Exception ex) {}

    try { maxRows = Integer.parseInt(jtxtMaxRows.getText()); }
    catch (Exception ex) {}

    mProperties.setProperty("ImageAppDir", imageFolder);
    mProperties.setProperty("TempDir", tempFolder);
    mProperties.setProperty("ConvertTimeout", String.valueOf(convertTimeout));
    mProperties.setProperty("PageSizeSmall", String.valueOf(pageSmall));
    mProperties.setProperty("PageSizeMedium", String.valueOf(pageMedium));
    mProperties.setProperty("PageSizeLarge", String.valueOf(pageLarge));
    mProperties.setProperty("MaxRows", String.valueOf(maxRows));
    mProperties.setProperty("ProfileCMYK", jtxtProfileCMYK.getText());
    mProperties.setProperty("ProfileRGB", jtxtProfileRGB.getText());

    if (jckSave.isSelected()) {
      FileOutputStream outstream = null;
      try {
        outstream = new FileOutputStream(new File("cams-image.properties"));
        mProperties.store(outstream, "cams-image.Properties");
        outstream.close();
      }
      catch (Exception ex) {
        cams.console.Console.println("PropertiesDialog: " + ex.getMessage());
        JOptionPane.showMessageDialog(this, "Error Saving Properties File to Disk: " +
                                      ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
      }
      finally {
        if (outstream != null)
          try { outstream.close(); } catch (Exception ex) {}
      }
    }
    hide();
  }

}

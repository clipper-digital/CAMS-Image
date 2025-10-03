package cams.imagelib;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import com.borland.jbcl.layout.*;

import java.io.*;
import java.io.OutputStream;
import java.io.FileOutputStream;

// import cams.console.*;
import cams.imaging.*;
import cams.database.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author JJS Solutions
 * @version 1.0
 */

public class ViewImageDialog extends JDialog {
  private ImageLibRecord mRecord = null;
  private Properties mProperties = null;
  private Date mLaunchTime = null;
  private CamsDB mCamsDB = null;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JLabel jlblPreview = new JLabel();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel3 = new JPanel();
  JLabel jlblStatus = new JLabel();
  JLabel jlblScanNum = new JLabel();
  JLabel jlblRecordName = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  XYLayout xYLayout1 = new XYLayout();
  JLabel jlblKeyline = new JLabel();
  ImageIcon imageRotate;
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton jbtnRotate = new JButton();

  public ViewImageDialog(Frame frame, String title, int imageSize, ImageLibRecord theRecord, CamsDB theCamsDB) {
    super(frame, title, false);
    try {
      jbInit();

      mCamsDB = theCamsDB;
      mRecord = theRecord;

      resetDialog();

      mLaunchTime = new Date();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public ViewImageDialog(Frame frame, String title, int imageSize, ImageLibRecord theRecord, boolean isModal, CamsDB theCamsDB) {
    super(frame, title, isModal);
    try {
      jbInit();

      mCamsDB = theCamsDB;
      mRecord = theRecord;

      resetDialog();

      mLaunchTime = new Date();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setProperties(Properties theProperties) { mProperties = theProperties; }

  private void resetDialog() {
    // Show Thumbnail and size window
    ImageIcon thePreviewIcon = (ImageIcon)jlblPreview.getIcon();
    if (thePreviewIcon != null) {
      thePreviewIcon.getImage().flush();
      thePreviewIcon = null;
    }
    if (mRecord.getThumbXLarge() != null)
      thePreviewIcon = new ImageIcon(mRecord.getThumbXLarge());
    else
      thePreviewIcon = null;

    jlblPreview.setIcon(thePreviewIcon);
    if (thePreviewIcon != null)
      jlblPreview.setPreferredSize(new Dimension(thePreviewIcon.getIconWidth(),  thePreviewIcon.getIconHeight()));
    else
      jlblPreview.setPreferredSize(new Dimension(200, 200));
    jlblPreview.revalidate();

    jlblRecordName.setText(mRecord.getRecordName());
    jlblKeyline.setText(mRecord.getKeyline());
    jlblScanNum.setText(mRecord.getScanNumber());
    jlblStatus.setText(mRecord.getStatus());
    pack();

    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
  }

  private void jbInit() throws Exception {
    imageRotate = new ImageIcon(cams.imagelib.ViewImageDialog.class.getResource("rotate2.png"));

    panel1.setLayout(borderLayout1);
    jlblPreview.setBorder(BorderFactory.createEtchedBorder());
    jlblPreview.setPreferredSize(new Dimension(50, 50));
    jlblPreview.setText("");
    jlblPreview.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jlblPreview_mouseClicked(e);
      }
    });
    jPanel2.setBorder(BorderFactory.createLineBorder(Color.black));
    jPanel2.setDebugGraphicsOptions(0);
    jPanel2.setLayout(borderLayout2);
    this.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        this_keyReleased(e);
      }
    });
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setText("Status :");
    jPanel3.setBorder(BorderFactory.createLineBorder(Color.black));
    jPanel3.setPreferredSize(new Dimension(10, 50));
    jPanel3.setLayout(xYLayout1);
    jlblStatus.setEnabled(true);
    jlblStatus.setFont(new java.awt.Font("Dialog", 0, 11));
    jlblStatus.setText("< Status >");
    jlblScanNum.setText("< Scan # >");
    jlblScanNum.setEnabled(true);
    jlblScanNum.setFont(new java.awt.Font("Dialog", 0, 11));
    jlblRecordName.setText("< Record Name >");
    jlblRecordName.setEnabled(true);
    jlblRecordName.setFont(new java.awt.Font("Dialog", 0, 11));
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel5.setText("Record Name :");
    jLabel6.setText("Scan # :");
    jLabel6.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel7.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel7.setText("Keyline :");
    jlblKeyline.setFont(new java.awt.Font("Dialog", 0, 11));
    jlblKeyline.setToolTipText("");
    jlblKeyline.setEnabled(true);
    jlblKeyline.setText("< Keyline >");
    jPanel4.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    jbtnRotate.setPreferredSize(new Dimension(25, 25));
    jbtnRotate.setFocusPainted(false);
    jbtnRotate.setText("");
    jbtnRotate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnRotate_actionPerformed(e);
      }
    });
    jbtnRotate.setIcon(imageRotate);
    jbtnRotate.setVisible(false);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jlblPreview, null);
    panel1.add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(jPanel4,  BorderLayout.CENTER);
    jPanel4.add(jLabel1, null);
    jPanel4.add(jlblStatus, null);
    jPanel2.add(jbtnRotate,  BorderLayout.EAST);
    jPanel3.add(jlblRecordName, new XYConstraints(90, 1, -1, -1));
    jPanel3.add(jLabel6, new XYConstraints(4, 16, -1, -1));
    jPanel3.add(jLabel7, new XYConstraints(4, 31, -1, -1));
    jPanel3.add(jlblKeyline, new XYConstraints(90, 31, -1, -1));
    jPanel3.add(jlblScanNum, new XYConstraints(90, 16, -1, -1));
    jPanel3.add(jLabel5, new XYConstraints(4, 1, -1, -1));
    panel1.add(jPanel3, BorderLayout.NORTH);
  }

  void this_keyReleased(KeyEvent e) {
    Date theTime = new Date();
    // Make sure the keypress wasn't in the first 2 seconds
    if (theTime.getTime() - mLaunchTime.getTime() > 1000)
      doCloseWindow();
  }

  void jlblPreview_mouseClicked(MouseEvent e) {
    doCloseWindow();
  }

  void jbtnRotate_actionPerformed(ActionEvent e) {
    ImageIcon thePreviewIcon = (ImageIcon)jlblPreview.getIcon();
    if (thePreviewIcon == null) return;

    try {
      ImageMagick imaging = new ImageMagick(mCamsDB);
      String tempDir = imaging.getTempDir();
      String appDir = imaging.getImageAppDir();

      // Save current JPG
      String fileName = tempDir + "rotate_in.jpg";
      String fileName2 = tempDir + "rotate_out.jpg";

      File theFile = new File(fileName);
      OutputStream fileOutStream = new FileOutputStream(theFile);
      fileOutStream.write(mRecord.getThumbXLarge());
      fileOutStream.close();
      theFile = null;


      imaging.deleteFiles(tempDir, "Rotate", "jpg");
      String theCommand = "\"" + appDir + "convert\" \"" + fileName + "\" -rotate 90" +
          " \"" + fileName2 + "\"";
      String result = imaging.shellCmd(theCommand);

      // Read in the rotated JPG
      theFile = new File(fileName2);
      long fileLength = theFile.length();
      InputStream fileInStream = new FileInputStream(theFile);
      byte[] newThumb = new byte[(int)fileLength];
      fileInStream.read(newThumb);
      mRecord.setThumbXLarge(newThumb);

      resetDialog();
    }
    catch (Exception ex) {
      cams.console.Console.println("Rotate JPG: " + ex.getMessage());
    }

  }

  void this_windowClosing(WindowEvent e) {
    doCloseWindow();
  }

  // Close the window and dispose of extra assets
  private void doCloseWindow() {
    mRecord.setThumbXLarge(null);
    dispose();
  }
}

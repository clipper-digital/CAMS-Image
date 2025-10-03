package cams.startup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;

import cams.imagelib.*;
import cams.database.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SplashFrame extends JFrame {
  private CamsDB mCamsDB = null;

  JPanel contentPane;
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  ImageIcon clipperLogo;
  JLabel jlblLogo = new JLabel();
  JButton jbtnContinue = new JButton();
  JPanel jpanelSplash = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  XYLayout xYLayout1 = new XYLayout();
  JLabel jlblWait = new JLabel();

  //Construct the frame
  public SplashFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception  {
    clipperLogo = new ImageIcon(cams.startup.SplashFrame.class.getResource("ClipperLogo.png"));

    contentPane = (JPanel) this.getContentPane();
    jLabel1.setFont(new java.awt.Font("Verdana", 1, 16));
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setHorizontalTextPosition(SwingConstants.TRAILING);
    jLabel1.setText("Clipper Asset Management System");
    contentPane.setLayout(xYLayout1);
    this.setSize(new Dimension(469, 218));
    this.setTitle("CAMS - Clipper Asset Management System");
    jLabel2.setText("CAMS");
    jLabel2.setHorizontalTextPosition(SwingConstants.TRAILING);
    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel2.setFont(new java.awt.Font("Verdana", 1, 16));
    jlblLogo.setText("");
    jlblLogo.setIcon(clipperLogo);
    jbtnContinue.setText("Continue");
    jbtnContinue.addActionListener(new SplashFrame_jbtnContinue_actionAdapter(this));
    jpanelSplash.setLayout(xYLayout2);
    jpanelSplash.setBorder(BorderFactory.createLineBorder(Color.black));
    jlblWait.setFont(new java.awt.Font("Dialog", 0, 12));
    jlblWait.setHorizontalAlignment(SwingConstants.CENTER);
    jlblWait.setText("Connecting to Database...");
    jpanelSplash.add(jlblLogo,     new XYConstraints(127, 8, -1, -1));
    jpanelSplash.add(jLabel2,   new XYConstraints(106, 73, 215, 27));
    jpanelSplash.add(jLabel1,   new XYConstraints(46, 92, 335, 37));
    jpanelSplash.add(jbtnContinue, new XYConstraints(160, 163, 107, 33));
    jpanelSplash.add(jlblWait,          new XYConstraints(111, 169, 208, 20));
    contentPane.add(jpanelSplash,   new XYConstraints(0, 0, 429, 218));
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  void jbtnContinue_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    ImageLibFrame imgLibFrame = new ImageLibFrame(mCamsDB);
    boolean packFrame = false;
    if (packFrame) {
      imgLibFrame.pack();
    }
    else {
      imgLibFrame.validate();
    }
//    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    imgLibFrame.setSize(800, 600);
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = imgLibFrame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    imgLibFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
//    imgLibFrame.setDatabase(mCamsDB);
    imgLibFrame.setVisible(true);
    imgLibFrame.refreshCatalogTree();
  }

  public void setDatabase(CamsDB theCamsDB) {
    mCamsDB = theCamsDB;
  }

}


class SplashFrame_jbtnContinue_actionAdapter implements java.awt.event.ActionListener {
  SplashFrame adaptee;

  SplashFrame_jbtnContinue_actionAdapter(SplashFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jbtnContinue_actionPerformed(e);
  }
}

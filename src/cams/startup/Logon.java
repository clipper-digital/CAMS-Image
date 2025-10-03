package cams.startup;


import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.sql.*;

import cams.database.*;
import cams.database.CamsDB.*;
// import cams.console.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author JJS Solutions
 * @version 1.0
 */

public class Logon extends JDialog {
  JPanel panel1 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel1 = new JLabel();
  JLabel jlblLogo = new JLabel();
  ImageIcon clipperLogo;
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField jtxtUserName = new JTextField();
  JPasswordField jtxtPassword = new JPasswordField();
  JButton jbtnLogon = new JButton();
  CamsDB mCamsDB = null;
  boolean mSuccess = false;

  public Logon(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
      setSize(getSize().width, getSize().height + 10);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public Logon() {
    this(null, "CAMS Image Library", true);
  }

  public void setCamsDB(CamsDB theDB) {
    mCamsDB = theDB;
  }

  public boolean getSuccess() { return mSuccess; }

  private void jbInit() throws Exception {
    clipperLogo = new ImageIcon(cams.startup.SplashFrame.class.getResource("ClipperLogo.png"));
    panel1.setLayout(xYLayout1);
    panel1.setBorder(BorderFactory.createLineBorder(Color.black));
    jLabel2.setText("CAMS");
    jLabel2.setHorizontalTextPosition(SwingConstants.TRAILING);
    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel2.setFont(new java.awt.Font("Verdana", 1, 16));
    jLabel1.setFont(new java.awt.Font("Verdana", 1, 16));
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setHorizontalTextPosition(SwingConstants.TRAILING);
    jLabel1.setText("Clipper Asset Management System");
    jlblLogo.setIcon(clipperLogo);
    jlblLogo.setText("");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel3.setText("User Name:");
    jLabel4.setText("Password:");
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 12));
    jtxtUserName.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtUserName.setText("");
    jtxtUserName.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        jtxtUserName_keyPressed(e);
      }
    });
    jtxtPassword.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtPassword.setText("");
    jtxtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        jtxtPassword_keyPressed(e);
      }
    });
    jbtnLogon.setText("Logon");
    jbtnLogon.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnLogon_actionPerformed(e);
      }
    });
    panel1.add(jlblLogo,      new XYConstraints(88, 16, 172, 40));
    panel1.add(jLabel2,                      new XYConstraints(67, 64, 215, 27));
    panel1.add(jLabel1,        new XYConstraints(0, 94, 346, 37));
    this.getContentPane().add(panel1,  BorderLayout.CENTER);
    panel1.add(jbtnLogon,     new XYConstraints(136, 221, 76, 29));
    panel1.add(jtxtPassword, new XYConstraints(134, 174, 146, -1));
    panel1.add(jLabel3, new XYConstraints(64, 142, 73, 23));
    panel1.add(jtxtUserName, new XYConstraints(134, 142, 146, 22));
    panel1.add(jLabel4, new XYConstraints(64, 174, 73, 23));
  }

  void jbtnLogon_actionPerformed(ActionEvent e) {
    String sql = "SELECT fname, lname, employeenumber, stock FROM UserAccounts " +
                 "WHERE login = '" + jtxtUserName.getText() +"' AND password = '" +
                 String.valueOf(jtxtPassword.getPassword()) + "'";
    ResultSet rs = null;
    try {
      rs = mCamsDB.query(sql);
      if (rs.next()) {
        if (rs.getInt("stock") != 1) {
          JOptionPane.showMessageDialog(this,
                                        "Invalid Logon Permissions.  You do not have access to this application.",
                                        "Invalid Logon",
                                        JOptionPane.ERROR_MESSAGE);
        }
        else {
          UserInfo theUserInfo = mCamsDB.getUserInfo();
          theUserInfo.fname = rs.getString("fname");
          theUserInfo.lname = rs.getString("lname");
          theUserInfo.empnum = rs.getString("employeenumber");
          theUserInfo.login = jtxtUserName.getText().toLowerCase();
          mSuccess = true;
          hide();
        }
      }
      else {
        JOptionPane.showMessageDialog(this,
                                      "Invalid Logon Specified.  Either your User Name and Password\n" +
                                      "are incorrect or you do not have access to this application.",
                                      "Invalid Logon",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
    catch (Exception ex) {
      cams.console.Console.println("Logon:doLogon: " + ex.getMessage());
    }
  }

  void jtxtUserName_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == e.VK_ENTER)
      jtxtPassword.requestFocus();
  }

  void jtxtPassword_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == e.VK_ENTER)
      jbtnLogon_actionPerformed(new ActionEvent(this, -1, null));
  }

}

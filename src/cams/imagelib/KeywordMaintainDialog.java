package cams.imagelib;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

import cams.database.*;
// import cams.console.*;
import cams.imagelib.*;
import javax.swing.event.*;

public class KeywordMaintainDialog extends JDialog {
  public boolean userCancel = true;
  private CamsDB mCamsDB = null;
  private ArrayList mSelectedKeywords = null;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jbtnOK = new JButton();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel jLabel2 = new JLabel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JList jListAllKeywords = new JList(new CustomListModel());
  BorderLayout borderLayout3 = new BorderLayout();
  JTextField jtxtKeyword = new JTextField();
  JButton jbtnAddKeyword = new JButton();
  JButton jbtnDelete = new JButton();
  JButton jbtnRename = new JButton();

  public KeywordMaintainDialog(Frame frame, CamsDB theCamsDB, ArrayList selectedKeywords) {
    super(frame, "Maintain Keywords", true);
    try {
      mCamsDB = theCamsDB;
      mSelectedKeywords = selectedKeywords;
      jbInit();
      pack();
      setSize(getSize().width + 10, getSize().height);

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
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void this_windowOpened(WindowEvent e) {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    new RefreshAllKeywords().start();
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jbtnOK.setText("Close");
    jbtnOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnOK_actionPerformed(e);
      }
    });
    jPanel2.setLayout(xYLayout1);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setHgap(15);
    flowLayout1.setVgap(10);
    jLabel2.setText("All Keywords");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));
    jPanel3.setBorder(null);
    jPanel3.setLayout(borderLayout3);
    jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane2.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtKeyword.setBorder(BorderFactory.createLoweredBevelBorder());
    jtxtKeyword.setText("");
    jtxtKeyword.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        jtxtKeyword_keyReleased(e);
      }
    });
    jbtnAddKeyword.setText("Add");
    jbtnAddKeyword.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnAddKeyword_actionPerformed(e);
      }
    });
    jListAllKeywords.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        jListAllKeywords_valueChanged(e);
      }
    });
    jListAllKeywords.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        jListAllKeywords_keyReleased(e);
      }
    });
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowOpened(WindowEvent e) {
        this_windowOpened(e);
      }
    });
    jbtnDelete.setText("Delete");
    jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnDelete_actionPerformed(e);
      }
    });
    jbtnRename.setText("Rename");
    jbtnRename.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnRename_actionPerformed(e);
      }
    });
    getContentPane().add(panel1);
    panel1.add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(jbtnOK, null);
    panel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jLabel2,  new XYConstraints(52, 10, -1, -1));
    jPanel2.add(jPanel3,  new XYConstraints(5, 28, 179, 391));
    jPanel3.add(jScrollPane2, BorderLayout.CENTER);
    jPanel2.add(jbtnDelete,  new XYConstraints(192, 79, 88, 29));
    jPanel2.add(jbtnRename,  new XYConstraints(192, 127, 88, 29));
    jPanel2.add(jbtnAddKeyword, new XYConstraints(124, 426, 60, -1));
    jPanel2.add(jtxtKeyword, new XYConstraints(5, 428, 115, 18));
    jScrollPane2.getViewport().add(jListAllKeywords, null);
  }

  class RefreshAllKeywords extends Thread {

    public void run() {
      long startTime = new java.util.Date().getTime();
      String sql =
          "SELECT keyword_id, keyword FROM ImageLibKeyword ORDER BY keyword";
      ResultSet rs = null;
      CustomListModel theModel = (CustomListModel) jListAllKeywords.getModel();
      ArrayList theListData = theModel.mData;
      theListData.clear();
      theListData.add(new IDNamePair(-1, "Loading...."));
      theModel.refreshList();
//      jListAllKeywords.updateUI();

      try {
        rs = mCamsDB.query(sql);
        while (rs.next()) {
          theListData.add(new IDNamePair(rs.getInt("keyword_id"),
                                         rs.getString("keyword")));
        }
      }
      catch (Exception ex) {
        cams.console.Console.println("KeywordDialog:refreshAllKeywords: " + ex.getMessage());
      }
      finally {
        try {
          if (rs.getStatement() != null) rs.getStatement().close();
          rs.close();
        }
        catch (Exception ex) {}
      }

      theListData.remove(0); // Remove the "Loading..." entry
      theModel.refreshList();
//      jListAllKeywords.updateUI();

      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      long endTime = new java.util.Date().getTime();
      cams.console.Console.println("refreshKeywords: " + (endTime - startTime) + " ms");

    }
  }

  void jbtnOK_actionPerformed(ActionEvent e) {
    userCancel = false;
    hide();
  }

  void jbtnCancel_actionPerformed(ActionEvent e) {
    hide();
  }

  class CustomListModel extends AbstractListModel {
    private ArrayList mData = new ArrayList();

    public CustomListModel() {}

    // Return the size of the list
    public int getSize() {
      return mData.size();
    }

    // Return an element from the list
    public Object getElementAt(int index) {
      IDNamePair theElement = (IDNamePair) mData.get(index);
      return theElement;
    }

    public IDNamePair get(int index) {
      return (IDNamePair) mData.get(index);
    }

    public void addElement(Object element) {
      mData.add(element);
    }

    public void clear() {
      mData.clear();
    }

    public void refreshList() {
      fireContentsChanged(this, 0, mData.size()-1);
    }
  }


  void jbtnAddKeyword_actionPerformed(ActionEvent e) {
    CustomListModel theKeywordModel = (CustomListModel) jListAllKeywords.getModel();
    ArrayList theKeywordListData = theKeywordModel.mData;

    jtxtKeyword.setText(jtxtKeyword.getText().trim());

    if (jtxtKeyword.getText().equals("")) return;

    // Make sure it doesn't already exist
    for (int i=0; i < theKeywordListData.size(); i++) {
      IDNamePair theKeyword = (IDNamePair) theKeywordListData.get(i);
      if (theKeyword.getName().equalsIgnoreCase(jtxtKeyword.getText())) {
        jtxtKeyword.setText("");
        JOptionPane.showMessageDialog(this, "The keyword you entered already " +
                                      "exists in the list of keywords.",
                                      "Invalid Keyword", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }

    jListAllKeywords.clearSelection();

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      // Insert New Keyword
      String sql = "INSERT INTO ImageLibKeyword (keyword) VALUES (?)";
      ps = mCamsDB.getConnection().prepareStatement(sql);
      ps.setString(1, jtxtKeyword.getText());
      ps.execute();
      ps.close();
      ps = null;

      // Now read it back to get keyword_id
      sql = "SELECT keyword_id, keyword from ImageLibKeyword WHERE keyword = ?";
      ps = mCamsDB.getConnection().prepareStatement(sql);
      ps.setString(1, jtxtKeyword.getText());
      rs = ps.executeQuery();
      if (rs.next()) {
        IDNamePair theKeyword = new IDNamePair(rs.getInt("keyword_id"),
                                               rs.getString("keyword"));
        theKeywordListData.add(theKeyword);
        Collections.sort(theKeywordListData);
      }
      rs.close();
      rs = null;
      ps.close();
      ps = null;

      theKeywordModel.refreshList();

      // Find New Entry and highlight it
      for (int i=0; i < theKeywordListData.size(); i++) {
        IDNamePair theKeyword = (IDNamePair) theKeywordListData.get(i);
        if (theKeyword.getName().equalsIgnoreCase(jtxtKeyword.getText())) {
          jListAllKeywords.setSelectedIndex(i);
          jListAllKeywords.ensureIndexIsVisible(i);
          break;
        }
      }
      jtxtKeyword.setText("");

    }
    catch (Exception ex) {
      cams.console.Console.println("KeywordDialog:AddKeyword: " + ex.getMessage());
    }
  }

  void jtxtKeyword_keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      jbtnAddKeyword_actionPerformed(new ActionEvent(this, 0, null));
    }
    else {
      // Follow along in keyword list as user types
      jListAllKeywords.setSelectedIndex(jListAllKeywords.getNextMatch(jtxtKeyword.getText(), 0,
                                    javax.swing.text.Position.Bias.Forward));
    }
  }

  // If an element is selected, make sure it's in view
  void jListAllKeywords_valueChanged(ListSelectionEvent e) {
    if (jListAllKeywords.getSelectedIndices().length == 1)
      jListAllKeywords.ensureIndexIsVisible(jListAllKeywords.getSelectedIndex());
  }

  void jListAllKeywords_keyReleased(KeyEvent e) {
    if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE) ||
        (e.getKeyCode() == KeyEvent.VK_DELETE))
      jbtnDelete_actionPerformed(new ActionEvent(this, -1, null));
  }

  void jbtnDelete_actionPerformed(ActionEvent e) {
    if (jListAllKeywords.getSelectedValues().length == 0) return;

    CustomListModel theKeywordModel = (CustomListModel) jListAllKeywords.getModel();
    ArrayList theKeywordListData = theKeywordModel.mData;
    Object[] selectedKeywords = jListAllKeywords.getSelectedValues();

    if (JOptionPane.showConfirmDialog(this, "Delete selected " + selectedKeywords.length +
                                      " Keywords?", "Confirm Delete",
                                      JOptionPane.YES_NO_CANCEL_OPTION,
                                      JOptionPane.QUESTION_MESSAGE) !=
      JOptionPane.YES_OPTION) return;

    // First remove these keywords from any records that may link to them
    // and then remove the keyword itself
    for (int i=0; i < selectedKeywords.length; i++) {
      String sql = "DELETE FROM ImageLibKeywordRecord WHERE " +
          "keyword_id = " + ((IDNamePair) selectedKeywords[i]).getId();
      mCamsDB.execute(sql);

      sql = "DELETE FROM ImageLibKeyword WHERE " +
          "keyword_id = " + ((IDNamePair) selectedKeywords[i]).getId();
      mCamsDB.execute(sql);

      // Now remove from list
      theKeywordListData.remove(selectedKeywords[i]);
    }
    theKeywordModel.refreshList();
  }

  void jbtnRename_actionPerformed(ActionEvent e) {
    if (jListAllKeywords.getSelectedValues().length == 0) return;

    if (jListAllKeywords.getSelectedValues().length != 1) {
      JOptionPane.showMessageDialog(this, "Please select only One keyword " +
                                    "to rename.", "Too Many Selected",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    CustomListModel theKeywordModel = (CustomListModel) jListAllKeywords.getModel();
    ArrayList theKeywordListData = theKeywordModel.mData;

    IDNamePair theKeyword = (IDNamePair) jListAllKeywords.getSelectedValue();
    String newName = JOptionPane.showInputDialog(this, "Enter replacement keyword:",
                                                 theKeyword.getName());
    newName = newName.trim();
    if (newName.length() == 0) return;

    for (int i=0; i < theKeywordListData.size(); i++) {
      IDNamePair tmpKeyword = (IDNamePair) theKeywordListData.get(i);
      if (tmpKeyword.getName().equalsIgnoreCase(newName)) {
        JOptionPane.showMessageDialog(this, "A keyword with that name already " +
                                      "exists.", "Invalid Keyword", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }

    String sql = "UPDATE ImageLibKeyword SET keyword = '" +
        newName.replaceAll("'", "''") + "' WHERE keyword_id = " + theKeyword.getId();
    mCamsDB.execute(sql);

    theKeyword.setName(newName);
    theKeywordModel.refreshList();
  }

}

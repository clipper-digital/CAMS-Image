package cams.imagelib.detaildialogs;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

import cams.database.*;
import cams.console.*;
import cams.imagelib.*;
import javax.swing.event.*;

public class KeywordDialog extends JDialog {
  public boolean userCancel = true;
  private CamsDB mCamsDB = null;
  private ArrayList mSelectedKeywords = null;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jbtnOK = new JButton();
  JButton jbtnCancel = new JButton();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList jListSelectedKeywords = new JList(new CustomListModel());
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JList jListAllKeywords = new JList(new CustomListModel());
  BorderLayout borderLayout3 = new BorderLayout();
  JTextField jtxtKeyword = new JTextField();
  JButton jbtnAddKeyword = new JButton();
  JButton jbtnRight = new JButton();
  ImageIcon imageLeft;
  ImageIcon imageRight;
  JButton jbtnLeft = new JButton();
  JPanel jPanel5 = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  JLabel jLabel3 = new JLabel();
  JScrollPane jScrollPane3 = new JScrollPane();
  JTextArea jtxtOriginalKeys = new JTextArea();
  JLabel jLabel4 = new JLabel();

  public KeywordDialog(Frame frame, CamsDB theCamsDB, ArrayList selectedKeywords) {
    super(frame, "Select Keywords", true);
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

  public void setOriginalKeywords(String theKeywords) {
    jtxtOriginalKeys.setText(theKeywords);
    jtxtOriginalKeys.setCaretPosition(0);
  }

  private void jbInit() throws Exception {
    imageLeft = new ImageIcon(cams.imagelib.detaildialogs.KeywordDialog.class.getResource("ArrowLeft.png"));
    imageRight = new ImageIcon(cams.imagelib.detaildialogs.KeywordDialog.class.getResource("ArrowRight.png"));

    panel1.setLayout(borderLayout1);
    jbtnOK.setText("OK");
    jbtnOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnOK_actionPerformed(e);
      }
    });
    jbtnCancel.setText("Cancel");
    jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnCancel_actionPerformed(e);
      }
    });
    jPanel2.setLayout(xYLayout1);
    jPanel4.setLayout(borderLayout2);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanel4.setBorder(null);
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setHgap(15);
    flowLayout1.setVgap(10);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setText("Selected Keywords");
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
    jListAllKeywords.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jListAllKeywords_mouseClicked(e);
      }
    });
    jListSelectedKeywords.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jListSelectedKeywords_mouseClicked(e);
      }
    });
    jbtnRight.setIcon(imageRight);
    jbtnRight.setText("");
    jbtnRight.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnRight_actionPerformed(e);
      }
    });
    jbtnLeft.setText("");
    jbtnLeft.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnLeft_actionPerformed(e);
      }
    });
    jbtnLeft.setIcon(imageLeft);
    jListAllKeywords.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        jListAllKeywords_valueChanged(e);
      }
    });
    jListSelectedKeywords.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        jListSelectedKeywords_valueChanged(e);
      }
    });
    jListAllKeywords.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        jListAllKeywords_keyReleased(e);
      }
    });
    jListSelectedKeywords.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        jListSelectedKeywords_keyReleased(e);
      }
    });
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowOpened(WindowEvent e) {
        this_windowOpened(e);
      }
    });
    jPanel5.setLayout(xYLayout2);
    jPanel5.setBorder(BorderFactory.createEtchedBorder());
    jPanel5.setMinimumSize(new Dimension(0, 50));
    jPanel5.setPreferredSize(new Dimension(0, 80));
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel3.setText("Keywords");
    jScrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jtxtOriginalKeys.setEditable(false);
    jtxtOriginalKeys.setText("");
    jtxtOriginalKeys.setFont(new java.awt.Font("Dialog", 0, 11));
    jtxtOriginalKeys.setWrapStyleWord(true);
    jtxtOriginalKeys.setLineWrap(true);
    jLabel4.setText("Imported");
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 11));
    getContentPane().add(panel1);
    panel1.add(jPanel1,  BorderLayout.CENTER);
    jPanel1.add(jbtnOK, null);
    jPanel1.add(jbtnCancel, null);
    panel1.add(jPanel2,  BorderLayout.NORTH);
    jPanel2.add(jPanel3,       new XYConstraints(5, 38, 179, 198));
    jPanel3.add(jScrollPane2, BorderLayout.CENTER);
    jScrollPane2.getViewport().add(jListAllKeywords, null);
    jPanel2.add(jbtnAddKeyword,   new XYConstraints(124, 239, 60, -1));
    jPanel2.add(jtxtKeyword,  new XYConstraints(5, 241, 115, 18));
    jPanel2.add(jLabel2, new XYConstraints(52, 18, -1, -1));
    jPanel2.add(jLabel1, new XYConstraints(252, 18, -1, -1));
    jPanel2.add(jPanel4, new XYConstraints(233, 38, 166, 199));
    jPanel4.add(jScrollPane1, BorderLayout.CENTER);
    jPanel2.add(jbtnLeft, new XYConstraints(188, 140, 41, 37));
    jPanel2.add(jbtnRight, new XYConstraints(188, 94, 41, 37));
    this.getContentPane().add(jPanel5,  BorderLayout.SOUTH);
    jPanel5.add(jScrollPane3,    new XYConstraints(72, 5, 328, 69));
    jPanel5.add(jLabel4,  new XYConstraints(6, 4, -1, -1));
    jPanel5.add(jLabel3, new XYConstraints(6, 22, -1, -1));
    jScrollPane3.getViewport().add(jtxtOriginalKeys, null);
    jScrollPane1.getViewport().add(jListSelectedKeywords, null);
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
        Console.println("KeywordDialog:refreshAllKeywords: " + ex.getMessage());
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
      Console.println("refreshKeywords: " + (endTime - startTime) + " ms");

      setSelectedKeywords();
    }
  }

  private void setSelectedKeywords() {
    if (mSelectedKeywords == null) return;

    CustomListModel theSelectedModel = (CustomListModel) jListSelectedKeywords.getModel();
    ArrayList theSelectedListData = theSelectedModel.mData;

    CustomListModel theKeywordModel = (CustomListModel) jListAllKeywords.getModel();
    ArrayList theKeywordListData = theKeywordModel.mData;

    theSelectedListData.clear();
    theSelectedListData.addAll(mSelectedKeywords);
    theSelectedModel.refreshList();
//    jListSelectedKeywords.updateUI();

    // Remove selected from All Keywords List
    for (int i=0; i < theSelectedListData.size(); i++)
      if (theKeywordListData.contains(theSelectedListData.get(i)))
        theKeywordListData.remove(theSelectedListData.get(i));

    theKeywordModel.refreshList();
//  jListAllKeywords.updateUI();
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
      if (index >= mData.size()) return null;

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

  /**
   * Select keyword from 'All Keywords', move it to 'Selected Keywords'
   * and remove it from the list of All Keywords
   * @param e MouseEvent
   */
  void jListAllKeywords_mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      selectKeywords();
    }
  }

  private void selectKeywords() {
    CustomListModel theModel = (CustomListModel) jListSelectedKeywords.getModel();
    ArrayList theListData = theModel.mData;
    ArrayList toRemove = new ArrayList();

    // Copy each selected Keyword
    for (int i = 0; i < jListAllKeywords.getSelectedIndices().length; i++) {
      IDNamePair theKeyword = (IDNamePair) jListAllKeywords.getSelectedValues()[
          i];
      if (!theListData.contains(theKeyword)) {
        theListData.add(theKeyword);
        toRemove.add(theKeyword);
      }
    }
    jListAllKeywords.clearSelection();
    Collections.sort(theListData);
    theModel.refreshList();
//    jListSelectedKeywords.updateUI();

    theModel = (CustomListModel) jListAllKeywords.getModel();
    theListData = theModel.mData;
    for (int i = 0; i < toRemove.size(); i++)
      theListData.remove( (IDNamePair) toRemove.get(i));
    theModel.refreshList();
//  jListAllKeywords.updateUI();
  }

  /**
   * Remove keyword from 'Selected Keywords', and add it back to 'All Keywords'
   *
   * @param e MouseEvent
   */
  void jListSelectedKeywords_mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      unselectKeywords();
    }
  }

  private void unselectKeywords() {
    CustomListModel theSelectedModel = (CustomListModel) jListSelectedKeywords.
        getModel();
    ArrayList theSelectedListData = theSelectedModel.mData;

    CustomListModel theKeywordModel = (CustomListModel) jListAllKeywords.
        getModel();
    ArrayList theKeywordListData = theKeywordModel.mData;

    ArrayList toRemove = new ArrayList();

    // Remove each selected Keyword
    for (int i = 0; i < jListSelectedKeywords.getSelectedIndices().length; i++) {
      IDNamePair theKeyword = (IDNamePair) jListSelectedKeywords.
          getSelectedValues()[i];
      toRemove.add(theKeyword);
    }
    jListSelectedKeywords.clearSelection();

    // Remove from right, add to Left
    for (int i = 0; i < toRemove.size(); i++) {
      theSelectedListData.remove( (IDNamePair) toRemove.get(i));
      theKeywordListData.add( (IDNamePair) toRemove.get(i));
    }

    Collections.sort(theSelectedListData);
    Collections.sort(theKeywordListData);

    theSelectedModel.refreshList();
//    jListSelectedKeywords.updateUI();
    theKeywordModel.refreshList();
//    jListAllKeywords.updateUI();
  }

  public ArrayList getSelectedKeywords() {
    CustomListModel theSelectedModel = (CustomListModel) jListSelectedKeywords.getModel();

    return theSelectedModel.mData;
  }

  void jbtnAddKeyword_actionPerformed(ActionEvent e) {
    CustomListModel theSelectedModel = (CustomListModel) jListSelectedKeywords.getModel();
    ArrayList theSelectedListData = theSelectedModel.mData;

    CustomListModel theKeywordModel = (CustomListModel) jListAllKeywords.getModel();
    ArrayList theKeywordListData = theKeywordModel.mData;

    jtxtKeyword.setText(jtxtKeyword.getText().trim());

    if (jtxtKeyword.getText().equals("")) return;

    // Make sure it doesn't already exist
    for (int i=0; i < theKeywordListData.size(); i++) {
      IDNamePair theKeyword = (IDNamePair) theKeywordListData.get(i);
      if (theKeyword.getName().equalsIgnoreCase(jtxtKeyword.getText())) {
        // Move the selected keyword to the right
        jListAllKeywords.setSelectedValue(theKeyword, true);
        jbtnRight_actionPerformed(new ActionEvent(this, -1, null));
        jtxtKeyword.setText("");
//        JOptionPane.showMessageDialog(this, "The keyword you entered already " +
//                                      "exists in the list of keywords.",
//                                      "Invalid Keyword", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }

    for (int i=0; i < theSelectedListData.size(); i++) {
      IDNamePair theKeyword = (IDNamePair) theSelectedListData.get(i);
      if (theKeyword.getName().equalsIgnoreCase(jtxtKeyword.getText())) {
        JOptionPane.showMessageDialog(this, "The keyword you entered already " +
                                      "exists in the list of keywords.",
                                      "Invalid Keyword", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }

    jListAllKeywords.clearSelection();
    jListSelectedKeywords.clearSelection();

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
        theSelectedListData.add(theKeyword);
        Collections.sort(theSelectedListData);
      }
      rs.close();
      rs = null;
      ps.close();
      ps = null;

      theSelectedModel.refreshList();
//      jListSelectedKeywords.updateUI();

      // Find New Entry and highlight it
      for (int i=0; i < theSelectedListData.size(); i++) {
        IDNamePair theKeyword = (IDNamePair) theSelectedListData.get(i);
        if (theKeyword.getName().equalsIgnoreCase(jtxtKeyword.getText())) {
          jListSelectedKeywords.setSelectedIndex(i);
          jListSelectedKeywords.ensureIndexIsVisible(i);
          break;
        }
      }
      jtxtKeyword.setText("");

    }
    catch (Exception ex) {
      Console.println("KeywordDialog:AddKeyword: " + ex.getMessage());
    }
  }

  void jbtnRight_actionPerformed(ActionEvent e) {
    selectKeywords();
  }

  void jbtnLeft_actionPerformed(ActionEvent e) {
    unselectKeywords();
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

  void jListSelectedKeywords_valueChanged(ListSelectionEvent e) {
    if (jListSelectedKeywords.getSelectedIndices().length == 1)
      jListSelectedKeywords.ensureIndexIsVisible(jListSelectedKeywords.getSelectedIndex());
  }

  void jListAllKeywords_keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      jbtnRight_actionPerformed(new ActionEvent(this, 0, null));
      jListAllKeywords.requestFocus();
      try {
        jListAllKeywords.setSelectedIndex(0);
      }
      catch (Exception ex) {}
    }
  }

  void jListSelectedKeywords_keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      jbtnLeft_actionPerformed(new ActionEvent(this, 0, null));
      jListSelectedKeywords.requestFocus();
      try {
        jListSelectedKeywords.setSelectedIndex(0);
      }
      catch (Exception ex) {}
    }
  }

}

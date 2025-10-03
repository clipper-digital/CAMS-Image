package cams.imagelib.search;

import java.sql.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cams.console.*;
import cams.database.*;
import cams.imagelib.*;
import com.borland.jbcl.layout.*;
/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author JJS Solutions
 * @version 1.0
 */

public class SearchDialog extends JDialog {
  private CamsDB mCamsDB = null;
  private ImageLibFrame mParent = null;
  private SearchDialog mTheFrame = this;
  private ArrayList mSearchFields = new ArrayList();
  private CamsTreeNode mTreeNode = null;
  private int mSearchResultCount = 0;
  private ArrayList mStatus = new ArrayList();
  private ArrayList mReleases =  new ArrayList();
//  private ArrayList mKeywords = new ArrayList();
  private int mCatalogId = -999;
  private int mCategoryId = -999;
  private String mOrderBy = "";

  JPanel panel1 = new JPanel();
  JPanel jPanel1 = new JPanel();
  JComboBox cboField1 = new JComboBox();
  XYLayout xYLayout2 = new XYLayout();
  JComboBox cboOperator1 = new JComboBox();
  JTextField txtSearch1 = new JTextField();
  JComboBox cboField2 = new JComboBox();
  JComboBox cboAndOr1 = new JComboBox();
  JComboBox cboOperator2 = new JComboBox();
  JTextField txtSearch2 = new JTextField();
  JTextField txtSearch3 = new JTextField();
  JComboBox cboOperator3 = new JComboBox();
  JComboBox cboField3 = new JComboBox();
  JTextField txtSearch4 = new JTextField();
  JComboBox cboOperator4 = new JComboBox();
  JComboBox cboField5 = new JComboBox();
  JComboBox cboOperator5 = new JComboBox();
  JTextField txtSearch5 = new JTextField();
  JComboBox cboField4 = new JComboBox();
  JPanel jPanel2 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  XYLayout xYLayout3 = new XYLayout();
  JRadioButton rdoSearchEverywhere = new JRadioButton();
  JRadioButton rdoSearchCatalog = new JRadioButton();
  JButton jbtnSearch = new JButton();
  ButtonGroup buttonGroupSearch = new ButtonGroup();
  ButtonGroup buttonGroupSearch2 = new ButtonGroup();
  JComboBox cboSearch1 = new JComboBox();
  JComboBox cboSearch2 = new JComboBox();
  JComboBox cboSearch3 = new JComboBox();
  JComboBox cboSearch4 = new JComboBox();
  JComboBox cboSearch5 = new JComboBox();
  JRadioButton rdoSearchCategory = new JRadioButton();
  JCheckBox chkSearch1 = new JCheckBox();
  JCheckBox chkSearch2 = new JCheckBox();
  JCheckBox chkSearch3 = new JCheckBox();
  JCheckBox chkSearch4 = new JCheckBox();
  JCheckBox chkSearch5 = new JCheckBox();
  JMenuBar jMenuMain = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuClose = new JMenuItem();
  JComboBox cboSort1 = new JComboBox();
  JComboBox cboSort2 = new JComboBox();
  JComboBox cboSort3 = new JComboBox();
  JComboBox cboSort4 = new JComboBox();
  JComboBox cboSort5 = new JComboBox();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout4 = new XYLayout();
  JRadioButton rdoNewSearch = new JRadioButton();
  JRadioButton rdoFilterSearch = new JRadioButton();

  public SearchDialog(Frame frame, CamsDB theDB, CamsTreeNode theNode) {
    super(frame, "Search Records", true);
    mParent = (ImageLibFrame) frame;
    mCamsDB = theDB;
    mTreeNode = theNode;
    try {
      jbInit();
      pack();

      configureFilterButton();
      loadSearchFields();
      initDropDowns();

      mCatalogId = mTreeNode.catalog_id;
      if (mTreeNode.fullPath == null) {
       // Top Level Catalog
       rdoSearchCatalog.setText("Search Catalog '" + mTreeNode.name + "'");
       rdoSearchCategory.setText("Search Category ''");
       rdoSearchCategory.setEnabled(false);
       mCategoryId = -999;
      }
      else {
        String catalog = mTreeNode.fullPath.split(",")[0];
        String category = mTreeNode.fullPath.substring(mTreeNode.fullPath.
            indexOf(",") + 1);
        rdoSearchCatalog.setText("Search Catalog '" + catalog + "'");
        rdoSearchCategory.setText("Search Category '" + category + "'");
        rdoSearchCategory.setEnabled(true);
        rdoSearchCategory.setSelected(true);
        mCategoryId = mTreeNode.id;
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    panel1.setLayout(xYLayout3);
    if (mCamsDB.windowsOS())
      jMenuClose.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.event.InputEvent.CTRL_MASK));
    else // MAC
      jMenuClose.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.event.InputEvent.META_MASK));
    this.setJMenuBar(jMenuMain);
    this.setModal(true);
    this.setResizable(false);
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(xYLayout2);
    txtSearch1.setText("");
    txtSearch2.setText("");
    txtSearch3.setText("");
    txtSearch4.setText("");
    txtSearch5.setText("");
    jPanel2.setLayout(xYLayout1);
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    rdoSearchEverywhere.setActionCommand("");
    rdoSearchEverywhere.setText("Search All Catalogs");
    rdoSearchCatalog.setText("Search Catalog \'xxxxxxxx\'");
    rdoSearchCatalog.setSelected(true);
    jbtnSearch.setText("Search");
    jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnSearch_actionPerformed(e);
      }
    });
    cboField1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cboField1_actionPerformed(e);
      }
    });
    cboField2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cboField2_actionPerformed(e);
      }
    });
    cboField3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cboField3_actionPerformed(e);
      }
    });
    cboField4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cboField4_actionPerformed(e);
      }
    });
    cboField5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cboField5_actionPerformed(e);
      }
    });
    rdoSearchCategory.setSelected(false);
    rdoSearchCategory.setText("Search Category \'xxxxxxxx\'");
    chkSearch1.setText("");
    chkSearch2.setText("");
    chkSearch3.setText("");
    chkSearch4.setText("");
    chkSearch5.setText("");
    jMenuFile.setText("File");
    jMenuClose.setText("Close");
    jMenuClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuClose_actionPerformed(e);
      }
    });
    jPanel3.setBorder(BorderFactory.createEtchedBorder());
    jPanel3.setLayout(xYLayout4);
    rdoNewSearch.setSelected(true);
    rdoNewSearch.setText("New Search");
    rdoFilterSearch.setEnabled(false);
    rdoFilterSearch.setText("Filter Existing Results");
    getContentPane().add(panel1,  BorderLayout.CENTER);
    jPanel1.add(cboField1, new XYConstraints(83, 7, 154, -1));
    jPanel1.add(cboOperator1,   new XYConstraints(243, 7, 119, -1));
    jPanel1.add(cboField2, new XYConstraints(83, 32, 154, -1));
    jPanel1.add(cboOperator2,   new XYConstraints(243, 32, 119, -1));
    jPanel1.add(cboOperator3,   new XYConstraints(243, 57, 119, -1));
    jPanel1.add(cboField3, new XYConstraints(83, 57, 154, -1));
    jPanel1.add(cboOperator4,   new XYConstraints(243, 82, 119, -1));
    jPanel1.add(cboField5, new XYConstraints(83, 107, 154, -1));
    jPanel1.add(cboOperator5,   new XYConstraints(243, 107, 119, -1));
    jPanel1.add(cboField4, new XYConstraints(83, 82, 154, -1));
    jPanel1.add(cboAndOr1, new XYConstraints(8, 32, 69, -1));
    jPanel1.add(cboSearch1,  new XYConstraints(367, 7, 179, -1));
    jPanel1.add(cboSearch2,  new XYConstraints(367, 32, 179, -1));
    jPanel1.add(cboSearch3,  new XYConstraints(367, 57, 179, -1));
    jPanel1.add(cboSearch4,  new XYConstraints(367, 82, 179, -1));
    jPanel1.add(cboSearch5,  new XYConstraints(367, 107, 179, -1));
    jPanel1.add(txtSearch4,  new XYConstraints(367, 82, 179, 19));
    jPanel1.add(txtSearch5, new XYConstraints(367, 107, 179, 19));
    jPanel1.add(txtSearch1, new XYConstraints(367, 7, 179, 19));
    jPanel1.add(txtSearch2, new XYConstraints(367, 32, 179, 19));
    jPanel1.add(txtSearch3, new XYConstraints(367, 57, 179, 19));
    jPanel1.add(chkSearch1, new XYConstraints(364, 6, 37, -1));
    jPanel1.add(chkSearch2, new XYConstraints(364, 31, 37, -1));
    jPanel1.add(chkSearch3, new XYConstraints(364, 56, 37, -1));
    jPanel1.add(chkSearch4, new XYConstraints(364, 81, 37, -1));
    jPanel1.add(chkSearch5, new XYConstraints(364, 106, 37, -1));
    jPanel1.add(cboSort1,         new XYConstraints(551, 7, 62, -1));
    panel1.add(jPanel2,    new XYConstraints(5, 156, 325, 85));
    panel1.add(jPanel1,                  new XYConstraints(5, 6, 627, 141));
    jPanel2.add(rdoSearchCategory,   new XYConstraints(2, 55, 311, 20));
    jPanel2.add(rdoSearchCatalog,   new XYConstraints(2, 30, 270, 20));
    jPanel2.add(rdoSearchEverywhere, new XYConstraints(2, 5, 213, 20));
    buttonGroupSearch.add(rdoSearchEverywhere);
    buttonGroupSearch.add(rdoSearchCatalog);
    buttonGroupSearch.add(rdoSearchCategory);
    jMenuMain.add(jMenuFile);
    jMenuFile.add(jMenuClose);
    jPanel1.add(cboSort2,    new XYConstraints(551, 32, 62, -1));
    jPanel1.add(cboSort3,      new XYConstraints(551, 57, 62, -1));
    jPanel1.add(cboSort4,     new XYConstraints(551, 82, 62, -1));
    jPanel1.add(cboSort5,     new XYConstraints(551, 107, 62, -1));
    panel1.add(jPanel3,      new XYConstraints(346, 156, 286, 85));
    jPanel3.add(jbtnSearch,    new XYConstraints(108, 52, -1, -1));
    jPanel3.add(rdoNewSearch, new XYConstraints(10, 9, 103, 26));
    jPanel3.add(rdoFilterSearch,   new XYConstraints(122, 10, 158, 25));

    this.getRootPane().setDefaultButton(jbtnSearch);
    buttonGroupSearch2.add(rdoNewSearch);
    buttonGroupSearch2.add(rdoFilterSearch);
  }

  public void reDisplay(CamsTreeNode theNode) {
    mTreeNode = theNode;

    mCatalogId = mTreeNode.catalog_id;
    if (mTreeNode.fullPath == null) {
     // Top Level Catalog
     rdoSearchCatalog.setText("Search Catalog '" + mTreeNode.name + "'");
//     rdoSearchCategory.setText("Search Category ''");
//     rdoSearchCategory.setEnabled(false);
//     mCategoryId = -999;
    }
    else {
      String catalog = mTreeNode.fullPath.split(",")[0];
      String category = mTreeNode.fullPath.substring(mTreeNode.fullPath.
          indexOf(",") + 1);
      rdoSearchCatalog.setText("Search Catalog '" + catalog + "'");
      rdoSearchCategory.setText("Search Category '" + category + "'");
      rdoSearchCategory.setEnabled(true);
      rdoSearchCategory.setSelected(true);
      mCategoryId = mTreeNode.id;
    }

    configureFilterButton();
    mSearchResultCount = 0;
  }

  class SearchField implements Comparable {
    String columnName = "";
    String displayName = "";
    Class datatype = String.class;

    public SearchField(String colName, String dispName, Class dtype) {
      columnName = colName;
      displayName = dispName;
      datatype = dtype;
    }

    public String toString() { return displayName; }

    public int compareTo(Object theObject) {
      return toString().compareTo(theObject.toString());
    }

    public boolean equals(Object theObject) {
      return columnName.equals(((SearchField)theObject).columnName);
    }
  }

  void loadSearchFields() {
    mSearchFields.clear();
    mSearchFields.add(new SearchField("RecordName", "Record Name", String.class));
    mSearchFields.add(new SearchField("Status", "Status", String.class));
    mSearchFields.add(new SearchField("FullPathMac", "Full Path Mac", String.class));
    mSearchFields.add(new SearchField("FullPathWin", "Full Path Win", String.class));
    mSearchFields.add(new SearchField("FileName", "File Name", String.class));
    mSearchFields.add(new SearchField("DontDelete", "Don't Delete", Boolean.class));
    mSearchFields.add(new SearchField("CatalogUser", "Catalog User", String.class));
    mSearchFields.add(new SearchField("Keyline", "Keyline", String.class));
    mSearchFields.add(new SearchField("CDName", "CD Name", String.class));
    mSearchFields.add(new SearchField("MFRName", "MFR Name", String.class));
    mSearchFields.add(new SearchField("Exclusive", "Exclusive", Boolean.class));
    mSearchFields.add(new SearchField("Area", "Area", String.class));
    mSearchFields.add(new SearchField("OrigOnFile", "Orig. On File", Boolean.class));
    mSearchFields.add(new SearchField("ModelRelease", "Model Release", Boolean.class));
    mSearchFields.add(new SearchField("Notes", "Notes", String.class));
    mSearchFields.add(new SearchField("FileFormat", "File Format", String.class));
    mSearchFields.add(new SearchField("FileSize", "File Size", Long.class));
    mSearchFields.add(new SearchField("FileDate", "File Date", java.util.Date.class));
    mSearchFields.add(new SearchField("RecordCreated", "Record Created", java.util.Date.class));
    mSearchFields.add(new SearchField("LastUpdate", "Last Update", java.util.Date.class));
    mSearchFields.add(new SearchField("ScanNum", "Scan Number", String.class));
    mSearchFields.add(new SearchField("UsageRules", "Usage Rules", String.class));
    mSearchFields.add(new SearchField("Release_id", "Releases", Integer.class));
    mSearchFields.add(new SearchField("ImageCompany", "Image Company", String.class));
    mSearchFields.add(new SearchField("Advertiser", "Advertiser", String.class));
    mSearchFields.add(new SearchField("OriginalKeywords", "Keywords (Original)", String.class));
    mSearchFields.add(new SearchField("Keyword", "Keywords", String.class));
    mSearchFields.add(new SearchField("PNumber", "P Number", String.class));
    mSearchFields.add(new SearchField("WebDisplay", "Display on Web", Boolean.class));
    mSearchFields.add(new SearchField("PriorityDisplay", "Priority Display", Boolean.class));

    Collections.sort(mSearchFields);
  }

  void initDropDowns() {
    addAndOrs(cboAndOr1);
//    addAndOrs(cboAndOr2);
//    addAndOrs(cboAndOr3);
//    addAndOrs(cboAndOr4);

    addOperators(cboOperator1);
    addOperators(cboOperator2);
    addOperators(cboOperator3);
    addOperators(cboOperator4);
    addOperators(cboOperator5);

    addSearchFields(cboField1);
    cboField1.setSelectedIndex(1);
    addSearchFields(cboField2);
    addSearchFields(cboField3);
    addSearchFields(cboField4);
    addSearchFields(cboField5);

    cboSearch1.setVisible(false);
    cboSearch2.setVisible(false);
    cboSearch3.setVisible(false);
    cboSearch4.setVisible(false);
    cboSearch5.setVisible(false);

    addSorting(cboSort1);
    addSorting(cboSort2);
    addSorting(cboSort3);
    addSorting(cboSort4);
    addSorting(cboSort5);

    loadStatus();
    loadReleases();
  }

  void addAndOrs(JComboBox theCombo) {
    DefaultComboBoxModel theModel = (DefaultComboBoxModel) theCombo.getModel();
    theModel.addElement("and");
    theModel.addElement("or");
    theCombo.setSelectedIndex(0);
  }

  void addOperators(JComboBox theCombo) {
    DefaultComboBoxModel theModel = (DefaultComboBoxModel) theCombo.getModel();
    theModel.removeAllElements();
    theModel.addElement("<");
    theModel.addElement("<=");
    theModel.addElement("=");
    theModel.addElement("<>");
    theModel.addElement(">=");
    theModel.addElement(">");
    theCombo.setSelectedIndex(2);
  }

  void addStringOperators(JComboBox theCombo) {
    DefaultComboBoxModel theModel = (DefaultComboBoxModel) theCombo.getModel();
    theModel.removeAllElements();
    theModel.addElement("Starts With");
    theModel.addElement("Contains");
    theModel.addElement("Does not Contain");
    theModel.addElement("Is");
    theModel.addElement("Is not");
    theModel.addElement("Has Value");
    theModel.addElement("Has No Value");
    theCombo.setSelectedIndex(0);
  }

  void addSorting(JComboBox theCombo) {
    DefaultComboBoxModel theModel = (DefaultComboBoxModel) theCombo.getModel();
    theModel.addElement("");
    theModel.addElement("ASC");
    theModel.addElement("DESC");
    theCombo.setSelectedIndex(0);
  }

  void addSearchFields(JComboBox theCombo) {
    DefaultComboBoxModel theModel = (DefaultComboBoxModel) theCombo.getModel();
    theModel.addElement(new SearchField("", "", String.class));
    for (int i=0; i < mSearchFields.size(); i++) {
      SearchField theField = (SearchField) mSearchFields.get(i);
      theModel.addElement(theField);
    }
    theCombo.setSelectedIndex(0);
  }

  String makeQueryLine(JComboBox cboField, JComboBox cboOperator,
                       JTextField txtSearch, JComboBox cboSearch,
                       JCheckBox chkSearch) {
    String result = null;

    SearchField theSearchField = (SearchField) cboField.getSelectedItem();
    String operator = (String) cboOperator.getSelectedItem();
    String strSearch = "";
    String strSearchPre = "";
    String strSearchPost = "";

    if (cboSearch.isVisible()) { // Drop Down List
      strSearch = String.valueOf(((dropdownItem) cboSearch.getSelectedItem()).getId());
      if (strSearch.equals("-1"))
        strSearch = "";
      else if (strSearch.equals("-999")) // Status (Search by text rather than by ID)
        strSearch = ((dropdownItem) cboSearch.getSelectedItem()).getDesc();
    }
    else if (chkSearch.isVisible()) { // Boolean Yes/No
      if (chkSearch.isSelected())
        strSearch = "1";
      else
        strSearch = "0";
    }
    else { // Text String
      strSearch = txtSearch.getText();
    }

    if (theSearchField.columnName.equals("")) //  || strSearch.trim().equals(""))
      return "";

    if (theSearchField.datatype == String.class) {
      if (operator.equalsIgnoreCase("Starts With")) {
        operator = "like";
        strSearchPost = "%";
      }
      else if (operator.equalsIgnoreCase("Contains")) {
        operator = "like";
        strSearchPre = "%";
        strSearchPost = "%";
      }
      else if (operator.equalsIgnoreCase("Does not Contain")) {
        operator = "not like";
        strSearchPre = "%";
        strSearchPost = "%";
      }
      else if (operator.equalsIgnoreCase("Is")) {
        operator = "like";
      }
      else if (operator.equalsIgnoreCase("Is Not")) {
        operator = "not like";
      }
      else if (operator.equalsIgnoreCase("Has Value")) {
        // { fn LENGTH(ISNULL(FullPathMac, '')) } > 0
        result = "{ fn LENGTH(ISNULL(" + theSearchField.columnName + ", '')) } > 0";
      }
      else if (operator.equalsIgnoreCase("Has No Value")) {
        // { fn LENGTH(ISNULL(FullPathMac, '')) } = 0
        result = "{ fn LENGTH(ISNULL(" + theSearchField.columnName + ", '')) } = 0";
      }

      strSearch = "'" + strSearchPre + strSearch.replaceAll("'", "''") + strSearchPost + "'";
    }
    else if (theSearchField.datatype == Boolean.class) {
      // Make sure the operator is '=' or '<>'
      if (!operator.equals("="))
        operator = "<>";
    }
    else if (theSearchField.datatype == java.util.Date.class) {
      strSearch = "'" + strSearch + "'";
      result = "CAST(CONVERT(varchar, " + theSearchField.columnName + ", 101) AS DateTime) " +
          operator + " " + strSearch;
    }

    // Replace * with %
    if (strSearch.indexOf("*") > -1)
      strSearch = strSearch.replace('*', '%');

    // Handle Status (lookup in ImageLibStatus to get status_ids
    if (theSearchField.columnName.equals("Status")) {
      result = "status_id IN (SELECT status_id FROM ImageLibStatus WHERE " +
          "status " + operator + " " + strSearch + ")";
    }
    else if (theSearchField.columnName.equals("Keyword")) {
      // Check for multiple keyword search (with commas)
      if (txtSearch.getText().indexOf(",") == -1)
        // Single Keyword Search
        result = "ImageLibRecordView.record_id IN (SELECT record_id FROM ImageLibKeywordRecord, " +
            "ImageLibKeyword WHERE ImageLibKeywordRecord.keyword_id = " +
            "ImageLibKeyword.keyword_id AND ImageLibKeyword.keyword " +
            operator + " " + strSearch + ")";
      else {
        String[] keywords = txtSearch.getText().split(",");
        result = "";
        for (int i=0; i < keywords.length; i++) {
          String theKeyword = "'" + strSearchPre + keywords[i].trim().replaceAll("'", "''") + strSearchPost + "'";
          result += "ImageLibRecordView.record_id IN (SELECT record_id FROM ImageLibKeywordRecord, " +
                     "ImageLibKeyword WHERE ImageLibKeywordRecord.keyword_id = " +
                     "ImageLibKeyword.keyword_id AND ImageLibKeyword.keyword " +
                     operator + " " + theKeyword + ") AND ";
        }
        if (result.endsWith(" AND "))
          result = result.substring(0, result.length() - 5);

        result += " ";
//        result = "ImageLibRecordView.record_id IN (SELECT record_id FROM ImageLibKeywordRecord " +
//            "WHERE keyword_id IN (SELECT keyword_id FROM ImageLibKeyword " +
//            "WHERE ";
//        for (int i=0; i < keywords.length; i++) {
//          result += "(keyword " + operator + " '" + strSearchPre +
//              keywords[i].trim() + strSearchPost + "') ";
//          if (i < keywords.length - 1)
//            result += "OR ";
//        }
//        result += ") GROUP BY record_id HAVING COUNT(record_id) = " +
//            keywords.length + ")";
      }
    }

    if (result == null)
      result = theSearchField.columnName + " " + operator + " " + strSearch;

    return "(" + result + ")";
  }

  private String makeOrderBy(JComboBox cboSearchField, JComboBox cboOrderBy) {
    String theOrderBy = (String) cboOrderBy.getSelectedItem();
    SearchField theSearchField = (SearchField) cboSearchField.getSelectedItem();

    if ((theOrderBy.equals("")) ||
        (theSearchField == null) ||
        (theSearchField.columnName.equals("")) )
          return "";

    return theSearchField.columnName + " " + theOrderBy + ",";
  }

  /**********************************************************
   * Perform Search
   *
   **********************************************************/
  void jbtnSearch_actionPerformed(ActionEvent e) {
/*
  3 Cases, Search All, Search Catalog, Search Category

  Also "New Search" or "Filter Search"
*/

    if (doEasterEgg()) return;

    String sql = "";

    if (rdoNewSearch.isSelected()) {
      // Clear Previous Search Results
      sql = "DELETE FROM ImageLibSearch WHERE search_user = '" +
          mCamsDB.getUserInfo().getSearchID() + "'";
      mCamsDB.execute(sql);
    }
    else {
      // Doing a filter Search, Save the prior search results
      sql = "UPDATE ImageLibSearch SET search_user = '" +
          mCamsDB.getUserInfo().getSearchID() + "_prior' WHERE search_user = '" +
          mCamsDB.getUserInfo().getSearchID() + "'";
      mCamsDB.execute(sql);
    }

    // Filter by search scope (all, Catalog, Category)
    if (rdoSearchEverywhere.isSelected()) {
      sql = "INSERT INTO ImageLibSearch SELECT DISTINCT '" +
            mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
            "record_id FROM ImageLibRecordView WHERE ";

      // Search -Everywhere-, Only search in Catalogs they have access to
      sql += "(ImageLibRecordView.catalog_id IN (SELECT catalog_id FROM ImageLibAccess " +
          "WHERE login = '" + mCamsDB.getUserInfo().login + "')) ";
    }
    else if (rdoSearchCatalog.isSelected()) {
      sql = "INSERT INTO ImageLibSearch SELECT DISTINCT '" +
            mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
            "record_id FROM ImageLibRecordView WHERE ";

      // Search just selected Catalog
      sql += "(ImageLibRecordView.catalog_id = " + mCatalogId + ") ";
    }
    else { // Search in the selected Category and Below
      sql = "INSERT INTO ImageLibSearch SELECT DISTINCT '" +
            mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
            "ImageLibRecordView.record_id FROM ImageLibRecordView, ImageLibCategoryRecord WHERE ";

      // Run Stored Procedure to generate list of subcategories
      // in ImageLibSearch table with SearchUser='<login>_subcats'
      mParent.insertSubCategories(mCategoryId);
      String searchUser = "'" + mCamsDB.getUserInfo().getSearchID() + "_subcats'";
      sql += "((category_id IN "
          +  "(SELECT record_id FROM ImageLibSearch WHERE " +
              "search_user = " + searchUser + ")) AND " +
              "(ImageLibRecordView.record_id = ImageLibCategoryRecord.record_id)) ";
    }

    String andOr1 = (String)cboAndOr1.getSelectedItem();
    String andOr2 = (String)cboAndOr1.getSelectedItem();
    String andOr3 = (String)cboAndOr1.getSelectedItem();
    String andOr4 = (String)cboAndOr1.getSelectedItem();

    // Create Where Clause
    String line1 = makeQueryLine(cboField1, cboOperator1, txtSearch1,
                                 cboSearch1, chkSearch1);
    String line2 = makeQueryLine(cboField2, cboOperator2, txtSearch2,
                                 cboSearch2, chkSearch2);
    String line3 = makeQueryLine(cboField3, cboOperator3, txtSearch3,
                                 cboSearch3, chkSearch3);
    String line4 = makeQueryLine(cboField4, cboOperator4, txtSearch4,
                                 cboSearch4, chkSearch4);
    String line5 = makeQueryLine(cboField5, cboOperator5, txtSearch5,
                                 cboSearch5, chkSearch5);

    if (line1.equals("")) return;

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    if (rdoFilterSearch.isSelected()) {
      sql += "AND (ImageLibRecordView.record_id IN (SELECT record_id FROM " +
          "ImageLibSearch WHERE search_user = '" + mCamsDB.getUserInfo().getSearchID() +
          "_prior')) ";
    }

    sql += "AND (" + line1;

    if (!line2.equals("")) {
      sql += " " + andOr1.toUpperCase() + " " + line2;
      if (!line3.equals("")) {
        sql += " " + andOr2.toUpperCase() + " " + line3;
        if (!line4.equals("")) {
          sql += " " + andOr3.toUpperCase() + " " + line4;
          if (!line5.equals("")) {
            sql += " " + andOr4.toUpperCase() + " " + line5;
          }
        }
      }
    }

    // Build ORDER BY
    sql += ") ORDER BY ImageLibRecordView.record_id";

    mOrderBy = makeOrderBy(cboField1, cboSort1);
    mOrderBy += makeOrderBy(cboField2, cboSort2);
    mOrderBy += makeOrderBy(cboField3, cboSort3);
    mOrderBy += makeOrderBy(cboField4, cboSort4);
    mOrderBy += makeOrderBy(cboField5, cboSort5);
    if (mOrderBy.endsWith(","))
      mOrderBy = mOrderBy.substring(0, mOrderBy.length() - 1);

    Console.println("Search sql = \n" + sql);
    Console.println("Order By = " + mOrderBy);

    ResultSet rs = null;

    try {
      mSearchResultCount = mCamsDB.executeUpdate(sql);
      Console.println(mSearchResultCount + " Rows Found.");

      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

      if (mSearchResultCount == 0) {
        JOptionPane.showMessageDialog(this, "No records were found for this query.",
                                      "No Records Found", JOptionPane.INFORMATION_MESSAGE);
        if (rdoFilterSearch.isSelected()) {
          // Restore original search results
          sql = "UPDATE ImageLibSearch SET search_user = '" +
              mCamsDB.getUserInfo().getSearchID() + "' WHERE search_user = '" +
              mCamsDB.getUserInfo().getSearchID() + "_prior'";
          mCamsDB.execute(sql);
        }
        return;
      }
      else {
        if (rdoFilterSearch.isSelected()) {
          // Clear Previous Search Results
          sql = "DELETE FROM ImageLibSearch WHERE search_user = '" +
              mCamsDB.getUserInfo().getSearchID() + "_prior'";
          mCamsDB.execute(sql);
        }
        hide();
      }
    }
    catch (Exception ex) {
      Console.println("Search Error: " + ex.getMessage());
      if (rs != null) {
        try { rs.close(); rs = null; } catch (Exception x) {}
      }
    }

  }

  /**********************************************************
   * Old Search
   **********************************************************/
  void jbtnSearch_OLD_actionPerformed(ActionEvent e) {
/*
  3 Cases, Search All, Search Catalog, Search Category
*/

    if (doEasterEgg()) return;

    String sql = "";

    // Clear Previous Search Results
    sql = "DELETE FROM ImageLibSearch WHERE search_user = '" +
        mCamsDB.getUserInfo().getSearchID() + "'";
    mCamsDB.execute(sql);

    if ( rdoSearchEverywhere.isSelected() || rdoSearchCatalog.isSelected() )
      sql = "INSERT INTO ImageLibSearch " +
          "SELECT DISTINCT '" + mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
          "ImageLibRecordView.record_id FROM ImageLibKeyword INNER JOIN " +
          "ImageLibKeywordRecord ON ImageLibKeyword.keyword_id = " +
          "ImageLibKeywordRecord.keyword_id RIGHT OUTER JOIN " +
          "ImageLibRecordView ON ImageLibKeywordRecord.record_id = " +
          "ImageLibRecordView.record_id LEFT OUTER JOIN " +
          "ImageLibStatus ON ImageLibRecordView.Status_id = ImageLibStatus.status_id " +
          "WHERE (";
    else { // Search Category
      sql = "INSERT INTO ImageLibSearch " +
          "SELECT DISTINCT '" + mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
          "ImageLibRecordView.record_id FROM ImageLibRecordView " +
          "LEFT OUTER JOIN ImageLibKeywordRecord ON " +
          "ImageLibRecordView.record_id = ImageLibKeywordRecord.record_id " +
          "LEFT OUTER JOIN ImageLibStatus ON " +
          "ImageLibRecordView.Status_id = ImageLibStatus.status_id " +
          "INNER JOIN ImageLibCategoryRecord ON " +
          "ImageLibRecordView.record_id = ImageLibCategoryRecord.record_id " +
          "LEFT OUTER JOIN ImageLibKeyword ON " +
          "ImageLibKeywordRecord.keyword_id = ImageLibKeyword.keyword_id " +
          "WHERE (";
    }

    String andOr1 = (String)cboAndOr1.getSelectedItem();
    String andOr2 = (String)cboAndOr1.getSelectedItem();
    String andOr3 = (String)cboAndOr1.getSelectedItem();
    String andOr4 = (String)cboAndOr1.getSelectedItem();

    // Create Where Clause
    String line1 = makeQueryLine(cboField1, cboOperator1, txtSearch1,
                                 cboSearch1, chkSearch1);
    String line2 = makeQueryLine(cboField2, cboOperator2, txtSearch2,
                                 cboSearch2, chkSearch2);
    String line3 = makeQueryLine(cboField3, cboOperator3, txtSearch3,
                                 cboSearch3, chkSearch3);
    String line4 = makeQueryLine(cboField4, cboOperator4, txtSearch4,
                                 cboSearch4, chkSearch4);
    String line5 = makeQueryLine(cboField5, cboOperator5, txtSearch5,
                                 cboSearch5, chkSearch5);

    if (line1.equals("")) return;

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    sql += line1;

    if (!line2.equals("")) {
      sql += " " + andOr1.toUpperCase() + " " + line2;
      if (!line3.equals("")) {
        sql += " " + andOr2.toUpperCase() + " " + line3;
        if (!line4.equals("")) {
          sql += " " + andOr3.toUpperCase() + " " + line4;
          if (!line5.equals("")) {
            sql += " " + andOr4.toUpperCase() + " " + line5;
          }
        }
      }
    }

    sql += ")";

    // Limit to Selected Category (mTreeNode)
    if (rdoSearchCatalog.isSelected())
      sql += " AND catalog_id = " + mCatalogId;
    else if (rdoSearchCategory.isSelected()) {
      // Search in the selected Category and Below

      // Run Stored Procedure to generate list of subcategories
      // in ImageLibSearch table with SearchUser='<login>_subcats'
      mParent.insertSubCategories(mTreeNode.id);

      String searchUser = "'" + mCamsDB.getUserInfo().getSearchID() + "_subcats'";
      sql += " AND category_id IN "
          +  "(SELECT record_id FROM ImageLibSearch WHERE " +
              "search_user = " + searchUser + ")";
    }
    else {
      // Search -Everywhere-, Only search in Catalogs they have access to
      sql += " AND catalog_id IN (SELECT catalog_id FROM ImageLibAccess " +
             "WHERE login = '" + mCamsDB.getUserInfo().login + "')";
    }

    // Build ORDER BY
    sql += " ORDER BY ImageLibRecordView.record_id";

    mOrderBy = makeOrderBy(cboField1, cboSort1);
    mOrderBy += makeOrderBy(cboField2, cboSort2);
    mOrderBy += makeOrderBy(cboField3, cboSort3);
    mOrderBy += makeOrderBy(cboField4, cboSort4);
    mOrderBy += makeOrderBy(cboField5, cboSort5);
    if (mOrderBy.endsWith(","))
      mOrderBy = mOrderBy.substring(0, mOrderBy.length() - 1);

    Console.println("Search sql = " + sql);
    Console.println("Order By = " + mOrderBy);

    ResultSet rs = null;

    try {
      mSearchResultCount = mCamsDB.executeUpdate(sql);
      Console.println(mSearchResultCount + " Rows Found.");

      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

      if (mSearchResultCount == 0) {
        JOptionPane.showMessageDialog(this, "No records were found for this query.",
                                      "No Records Found", JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else
        hide();
    }
    catch (Exception ex) {
      Console.println("Search Error: " + ex.getMessage());
      if (rs != null) {
        try { rs.close(); rs = null; } catch (Exception x) {}
      }
    }

  }

  private boolean doEasterEgg() {
    SearchField theSearchField = (SearchField) cboField1.getSelectedItem();
    if ( (theSearchField.displayName.equals("Record Name")) &&
         (txtSearch1.getText().equalsIgnoreCase("conan schmitt")) ) {
      EasterDialog dlg = new EasterDialog();
      //Center the window
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = dlg.getSize();
      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      dlg.setLocation( (screenSize.width - frameSize.width) / 2,
                      (screenSize.height - frameSize.height) / 2);
      dlg.setVisible(true);
      dlg.dispose();
      return true;
    }
    else
      return false;
  }

  public int getSearchResultCount() { return mSearchResultCount; }

  public String getOrderBy() {
    if (mOrderBy.trim().length() > 0)
      return " ORDER BY " + mOrderBy.trim();
    else
      return "";
  }

  class dropdownItem {
    private int id;
    private String description;

    public String toString() { return description; }
    public String getDesc() { return description; }
    public int getId() { return id; }

    public dropdownItem(int id, String description) {
      this.id = id;
      this.description = description;
    }
  }

  private void loadStatus() {
    ResultSet rs = null;

    mStatus.clear();
    mStatus.add(new dropdownItem(-1, ""));

    try {
      rs = mCamsDB.query("SELECT DISTINCT status FROM ImageLibStatus " +
                         "ORDER BY status");
      while (rs.next()) {
        // adSizes.add(new dropdownItem(rs.getInt("AdvertSizeID"), rs.getString("AdvertDescription")));
        mStatus.add(new dropdownItem(-999, rs.getString("Status")));
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close(); rs = null;
    }
    catch (Exception ex) {
      Console.println("SearchDialog:loadStatus: " + ex.getMessage());
    }
  }

  private void loadReleases() {
    ResultSet rs = null;

    mReleases.clear();
    mReleases.add(new dropdownItem(-1, ""));

    try {
      rs = mCamsDB.query("SELECT release_id, release FROM ImageLibRelease " +
                         "ORDER BY sort_order");
      while (rs.next()) {
        // adSizes.add(new dropdownItem(rs.getInt("AdvertSizeID"), rs.getString("AdvertDescription")));
        mReleases.add(new dropdownItem(rs.getInt("release_id"), rs.getString("release")));
      }
      if (rs.getStatement() != null) rs.getStatement().close();
      rs.close(); rs = null;
    }
    catch (Exception ex) {
      Console.println("SearchDialog:loadReleases: " + ex.getMessage());
    }
  }

  void updateSearchBox(JComboBox cboField, JComboBox cboOperator,
                       JTextField txtSearch, JComboBox cboSearch,
                       JCheckBox chkSearch) {
    SearchField theSearchField = (SearchField)cboField.getSelectedItem();
    String fieldName = cboField.getSelectedItem().toString();
    if (fieldName.equals("Status")) {
      chkSearch.setVisible(false);
      txtSearch.setVisible(false);
      cboSearch.setVisible(true);
      DefaultComboBoxModel theModel = (DefaultComboBoxModel) cboSearch.getModel();
      theModel.removeAllElements();
      for (int i=0; i < mStatus.size(); i++)
        theModel.addElement(mStatus.get(i));
      cboSearch.setSelectedIndex(0);
    }
    else if (fieldName.equals("Releases")) {
      chkSearch.setVisible(false);
      txtSearch.setVisible(false);
      cboSearch.setVisible(true);
      DefaultComboBoxModel theModel = (DefaultComboBoxModel) cboSearch.getModel();
      theModel.removeAllElements();
      for (int i=0; i < mReleases.size(); i++)
        theModel.addElement(mReleases.get(i));
      cboSearch.setSelectedIndex(0);
    }
    else if (theSearchField.datatype == Boolean.class) {
      chkSearch.setVisible(true);
      txtSearch.setVisible(false);
      cboSearch.setVisible(false);
    }
    else {
      chkSearch.setVisible(false);
      txtSearch.setVisible(true);
      cboSearch.setVisible(false);
    }

    // Check for Operator
    if (theSearchField.datatype == String.class)
      addStringOperators(cboOperator);
    else
      addOperators(cboOperator);
  }

  void cboField1_actionPerformed(ActionEvent e) {
    updateSearchBox(cboField1, cboOperator1, txtSearch1, cboSearch1, chkSearch1);
  }

  void cboField2_actionPerformed(ActionEvent e) {
    updateSearchBox(cboField2, cboOperator2, txtSearch2, cboSearch2, chkSearch2);
  }

  void cboField3_actionPerformed(ActionEvent e) {
    updateSearchBox(cboField3, cboOperator3, txtSearch3, cboSearch3, chkSearch3);
  }

  void cboField4_actionPerformed(ActionEvent e) {
    updateSearchBox(cboField4, cboOperator4, txtSearch4, cboSearch4, chkSearch4);
  }

  void cboField5_actionPerformed(ActionEvent e) {
    updateSearchBox(cboField5, cboOperator5, txtSearch5, cboSearch5, chkSearch5);
  }

  void jMenuClose_actionPerformed(ActionEvent e) {
    hide();
  }

  void configureFilterButton() {
    String sql = "SELECT count(*) FROM ImageLibSearch WHERE search_user = '" +
        mCamsDB.getUserInfo().getSearchID() + "'";
    int theCount = Integer.parseInt(mCamsDB.querySingleString(sql));

    if (theCount == 0) {
      rdoFilterSearch.setEnabled(false);
      rdoNewSearch.setSelected(true);
    }
    else {
      rdoFilterSearch.setEnabled(true);
    }

  }


}


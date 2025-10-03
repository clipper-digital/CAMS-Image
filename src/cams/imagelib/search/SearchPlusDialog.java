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

public class SearchPlusDialog  extends JDialog {
  private CamsDB mCamsDB = null;
  private ImageLibFrame mParent = null;
  private int mSearchResultCount = -1;
  private int mStockCategory = 0;
  private int mStockMiscCategory = 0;
  private int mExclCategory = 0;
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JRadioButton jrdoStock = new JRadioButton();
  JRadioButton jrdoExcl = new JRadioButton();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField jTextField3 = new JTextField();
  JTextField jtxtDescription = new JTextField();
  JTextField jtxtScanNum = new JTextField();
  JTextField jTextField4 = new JTextField();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  ButtonGroup buttonGroupSearch = new ButtonGroup();
  ButtonGroup buttonGroupAndOr = new ButtonGroup();
  ButtonGroup buttonGroupFilter = new ButtonGroup();
  JPanel jPanel4 = new JPanel();
  JRadioButton jrdoAND = new JRadioButton();
  JRadioButton jrdoOR = new JRadioButton();
  FlowLayout flowLayout2 = new FlowLayout();
  JPanel jPanel5 = new JPanel();
  VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  JButton jbtnSearch = new JButton();
  JRadioButton jrdoNewSearch = new JRadioButton();
  JRadioButton jrdoFilter = new JRadioButton();

  public SearchPlusDialog(Frame frame, CamsDB theDB) {
    super(frame, "Search Records", true);
    mParent = (ImageLibFrame) frame;
    mCamsDB = theDB;
    try {
      jbInit();
      pack();

      configureFilterButton();
      mStockCategory = Integer.parseInt(mCamsDB.getProperties().getProperty("StockCategory", "16"));
      mStockMiscCategory = Integer.parseInt(mCamsDB.getProperties().getProperty("StockMiscCategory", "17"));
      mExclCategory = Integer.parseInt(mCamsDB.getProperties().getProperty("ExclCategory", "23"));
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    jPanel2.setLayout(flowLayout1);
    flowLayout1.setHgap(10);
    jrdoStock.setSelected(true);
    jrdoStock.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jrdoStock_actionPerformed(e);
      }
    });
    jPanel3.setBorder(BorderFactory.createEtchedBorder());
    jPanel3.setLayout(gridBagLayout1);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setText("Scan # :");
    jLabel2.setText("Description :");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel3.setText("CD Name :");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel4.setText("CD # :");
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 11));
    jTextField3.setText("");
    jtxtDescription.setText("");
    jtxtScanNum.setText("");
    jTextField4.setText("");
    jrdoExcl.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jrdoExcl_actionPerformed(e);
      }
    });
    jPanel4.setBorder(BorderFactory.createEtchedBorder());
    jPanel4.setLayout(flowLayout2);
    jrdoAND.setText("AND");
    jrdoAND.setFont(new java.awt.Font("Dialog", 1, 11));
    jrdoAND.setToolTipText("");
    jrdoAND.setSelected(true);
    jrdoOR.setFont(new java.awt.Font("Dialog", 1, 11));
    jrdoOR.setText("OR");
    flowLayout2.setHgap(15);
    flowLayout2.setVgap(5);
    jPanel5.setBorder(BorderFactory.createEtchedBorder());
    jPanel5.setLayout(verticalFlowLayout2);
    jbtnSearch.setMaximumSize(new Dimension(100, 23));
    jbtnSearch.setMinimumSize(new Dimension(100, 23));
    jbtnSearch.setPreferredSize(new Dimension(100, 23));
    jbtnSearch.setText("Search");
    jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnSearch_actionPerformed(e);
      }
    });
    jrdoNewSearch.setText("New Search");
    jrdoNewSearch.setFont(new java.awt.Font("Dialog", 1, 11));
    jrdoNewSearch.setToolTipText("");
    jrdoFilter.setSelected(true);
    jrdoFilter.setToolTipText("");
    jrdoFilter.setFont(new java.awt.Font("Dialog", 1, 11));
    jrdoFilter.setText("Filter Existing Results");
    jPanel6.setBorder(null);
    jPanel2.add(jrdoStock, null);
    jPanel2.add(jrdoExcl, null);
    jPanel1.add(jPanel2, null);
    jPanel4.add(jrdoAND, null);
    jPanel4.add(jrdoOR, null);
    jPanel1.add(jPanel4, null);
    jPanel1.add(jPanel3, null);
    jPanel3.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(11, 9, 0, 39), 0, 0));
    jPanel3.add(jLabel2,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(16, 9, 0, 11), 0, 0));
    jPanel3.add(jLabel3,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(17, 9, 0, 0), 0, 0));
    jPanel3.add(jLabel4,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(18, 9, 0, 15), 0, 0));
    jPanel3.add(jTextField3,   new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(14, 8, 0, 10), 138, 1));
    jPanel3.add(jtxtScanNum,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(11, 8, 0, 10), 138, 1));
    jPanel3.add(jTextField4,      new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 8, 10, 10), 138, 1));
    jPanel3.add(jtxtDescription,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(13, 8, 0, 10), 138, 1));
    jPanel1.add(jPanel5, null);
    jPanel5.add(jPanel6, null);
    jPanel6.add(jrdoNewSearch, null);
    jPanel6.add(jrdoFilter, null);
    jPanel5.add(jPanel7, null);
    jPanel7.add(jbtnSearch, null);
    jPanel1.setLayout(verticalFlowLayout1);
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jrdoStock.setFont(new java.awt.Font("Dialog", 1, 11));
    jrdoStock.setText("Stock Find +");
    jrdoExcl.setText("Excl Find +");
    jrdoExcl.setFont(new java.awt.Font("Dialog", 1, 11));
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    buttonGroupSearch.add(jrdoExcl);
    buttonGroupSearch.add(jrdoStock);
    buttonGroupAndOr.add(jrdoOR);
    buttonGroupAndOr.add(jrdoAND);
    buttonGroupFilter.add(jrdoNewSearch);
    buttonGroupFilter.add(jrdoFilter);

  }

  public int getSearchResultCount() { return mSearchResultCount; }

  void jrdoStock_actionPerformed(ActionEvent e) {
    if (jrdoStock.isSelected()) {
      jLabel3.setText("CD Name :");
      jLabel4.setText("CD # :");
    } else {
      jLabel3.setText("Manufacturer :");
      jLabel4.setText("Advertiser :");
    }
  }

  void jrdoExcl_actionPerformed(ActionEvent e) {
    if (jrdoStock.isSelected()) {
      jLabel3.setText("CD Name :");
      jLabel4.setText("CD # :");
    } else {
      jLabel3.setText("Manufacturer :");
      jLabel4.setText("Advertiser :");
    }
  }

  void jbtnSearch_actionPerformed(ActionEvent e) {
    String sql = "";
    ResultSet rs = null;

    if ((jtxtScanNum.getText().trim().length() == 0) &&
        (jtxtDescription.getText().trim().length() == 0) &&
        (jTextField3.getText().trim().length() == 0) &&
        (jTextField4.getText().trim().length() == 0)) {
      JOptionPane.showMessageDialog(this, "Nothing entered to search for!",
                                    "Invalid Search", JOptionPane.ERROR_MESSAGE);
      return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    if (jrdoNewSearch.isSelected()) {
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

    // Select AND or OR for 4 entries
    String andOr = " AND ";
    if (jrdoOR.isSelected())
      andOr = " OR ";

    // Stock Find +
    if (jrdoStock.isSelected()) {
      // We really need 2 searches, one to search Stock and one for Stock Misc,
      // then union the results

      // First search in the Stock Category
      sql = "INSERT INTO ImageLibSearch SELECT DISTINCT '" +
            mCamsDB.getUserInfo().getSearchID() + "_1', getDate(), " +
            "ImageLibRecordView.record_id FROM ImageLibRecordView, ImageLibCategoryRecord WHERE ";

      // Run Stored Procedure to generate list of subcategories
      // in ImageLibSearch table with SearchUser='<login>_subcats'
      mParent.insertSubCategories(mStockCategory);
      String searchSubCats = "'" + mCamsDB.getUserInfo().getSearchID() + "_subcats'";
      sql += "((category_id IN "
          +  "(SELECT record_id FROM ImageLibSearch WHERE " +
              "search_user = " + searchSubCats + ")) AND " +
              "(ImageLibRecordView.record_id = ImageLibCategoryRecord.record_id)) ";

      if (jrdoFilter.isSelected()) {
        sql += "AND (ImageLibRecordView.record_id IN (SELECT record_id FROM " +
            "ImageLibSearch WHERE search_user = '" + mCamsDB.getUserInfo().getSearchID() +
            "_prior')) ";
      }

      sql += "AND (";

      // Scan #
      if (jtxtScanNum.getText().trim().length() > 0)
        sql += "(ScanNum like '%" + jtxtScanNum.getText() + "%')" + andOr;

      // Description
      if (jtxtDescription.getText().trim().length() > 0)
        sql += "(ImageLibRecordView.record_id IN (SELECT record_id FROM ImageLibKeywordRecord, " +
            "ImageLibKeyword WHERE ImageLibKeywordRecord.keyword_id = " +
            "ImageLibKeyword.keyword_id AND ImageLibKeyword.keyword like " +
            "'%" + jtxtDescription.getText() + "%'))" + andOr;

      // CD Name
      if (jTextField3.getText().trim().length() > 0)
        sql += "(CDName like '%" + jTextField3.getText() + "%')" + andOr;

      // CD #
      if (jTextField4.getText().trim().length() > 0)
        sql += "(ImageCompany like '%" + jTextField4.getText() + "%')" + andOr;

      if (sql.endsWith(" OR "))
        sql = sql.substring(0, sql.length() - 4);

      if (sql.endsWith(" AND "))
        sql = sql.substring(0, sql.length() - 5);

      sql += ") ORDER BY ImageLibRecordView.record_id";

      Console.println("Search sql (Stock) = \n" + sql);

      try {
        mSearchResultCount = mCamsDB.executeUpdate(sql);
        Console.println(mSearchResultCount + " Rows Found in Stock search (1 of 2).");
      }
      catch (Exception ex) {
        Console.println("Search Error: " + ex.getMessage());
        if (rs != null) {
          try { rs.close(); rs = null; } catch (Exception x) {}
        }
      }

      // Now search in Stock Misc Category
      sql = "INSERT INTO ImageLibSearch SELECT DISTINCT '" +
            mCamsDB.getUserInfo().getSearchID() + "_2', getDate(), " +
            "ImageLibRecordView.record_id FROM ImageLibRecordView, ImageLibCategoryRecord WHERE ";

      // Run Stored Procedure to generate list of subcategories
      // in ImageLibSearch table with SearchUser='<login>_subcats'
      mParent.insertSubCategories(mStockMiscCategory);
      searchSubCats = "'" + mCamsDB.getUserInfo().getSearchID() + "_subcats'";
      sql += "((category_id IN "
          +  "(SELECT record_id FROM ImageLibSearch WHERE " +
              "search_user = " + searchSubCats + ")) AND " +
              "(ImageLibRecordView.record_id = ImageLibCategoryRecord.record_id)) ";

      if (jrdoFilter.isSelected()) {
        sql += "AND (ImageLibRecordView.record_id IN (SELECT record_id FROM " +
            "ImageLibSearch WHERE search_user = '" + mCamsDB.getUserInfo().getSearchID() +
            "_prior')) ";
      }

      // All 4 fields search in Notes
      sql += "AND (";

      if (jtxtScanNum.getText().trim().length() > 0)
        sql += "notes like '%" + jtxtScanNum.getText() + "%'" + andOr;

      if (jtxtDescription.getText().trim().length() > 0)
        sql += "notes like '%" + jtxtDescription.getText() + "%'" + andOr;

      if (jTextField3.getText().trim().length() > 0)
        sql += "notes like '%" + jTextField3.getText() + "%'" + andOr;

      if (jTextField4.getText().trim().length() > 0)
        sql += "notes like '%" + jTextField4.getText() + "%'";

      if (sql.endsWith(" OR "))
        sql = sql.substring(0, sql.length() - 4);

      if (sql.endsWith(" AND "))
        sql = sql.substring(0, sql.length() - 5);

      sql += ") ORDER BY ImageLibRecordView.record_id";

      Console.println("Search sql (Stock Misc) = \n" + sql);

      try {
        int count = mCamsDB.executeUpdate(sql);
        mSearchResultCount += count;
        Console.println(count + " Rows Found in Stock Misc search (2 of 2).");
      }
      catch (Exception ex) {
        Console.println("Search Error: " + ex.getMessage());
        if (rs != null) {
          try { rs.close(); rs = null; } catch (Exception x) {}
        }
      }

      // Now combine both searches (OR)
      sql = "INSERT INTO ImageLibSearch SELECT DISTINCT '" +
            mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
            "record_id FROM ImageLibSearch WHERE search_user = '" +
            mCamsDB.getUserInfo().getSearchID() + "_1' OR search_user = '" +
            mCamsDB.getUserInfo().getSearchID() + "_2'";
      mCamsDB.execute(sql);

      sql = "DELETE FROM ImageLibSearch WHERE search_user = '" +
          mCamsDB.getUserInfo().getSearchID() + "_1'";
      mCamsDB.execute(sql);

      sql = "DELETE FROM ImageLibSearch WHERE search_user = '" +
          mCamsDB.getUserInfo().getSearchID() + "_2'";
      mCamsDB.execute(sql);
    }
    else {
      // Excl Find +

      // Now search in the Exclusive Category
      sql = "INSERT INTO ImageLibSearch SELECT DISTINCT '" +
            mCamsDB.getUserInfo().getSearchID() + "', getDate(), " +
            "ImageLibRecordView.record_id FROM ImageLibRecordView, ImageLibCategoryRecord WHERE ";

      // Run Stored Procedure to generate list of subcategories
      // in ImageLibSearch table with SearchUser='<login>_subcats'
      mParent.insertSubCategories(mExclCategory);
      String searchSubCats = "'" + mCamsDB.getUserInfo().getSearchID() + "_subcats'";
      sql += "((category_id IN "
          +  "(SELECT record_id FROM ImageLibSearch WHERE " +
              "search_user = " + searchSubCats + ")) AND " +
              "(ImageLibRecordView.record_id = ImageLibCategoryRecord.record_id)) ";

      if (jrdoFilter.isSelected()) {
        sql += "AND (ImageLibRecordView.record_id IN (SELECT record_id FROM " +
            "ImageLibSearch WHERE search_user = '" + mCamsDB.getUserInfo().getSearchID() +
            "_prior')) ";
      }

      sql += "AND (";

      // Scan #
      if (jtxtScanNum.getText().trim().length() > 0)
        sql += "((ScanNum like '%" + jtxtScanNum.getText() + "%') OR "
            +  " (RecordName like '%" + jtxtScanNum.getText() + "%'))" + andOr;

      // Description
      if (jtxtDescription.getText().trim().length() > 0)
        sql += "((ImageLibRecordView.record_id IN (SELECT record_id FROM ImageLibKeywordRecord, "
            +  "ImageLibKeyword WHERE ImageLibKeywordRecord.keyword_id = "
            +  "ImageLibKeyword.keyword_id AND ImageLibKeyword.keyword like "
            +  "'" + jtxtDescription.getText() + "')) OR "
            +  "(notes like '%" + jtxtDescription.getText() + "%'))" + andOr;

      // Manufacturer
      if (jTextField3.getText().trim().length() > 0)
        sql += "((MFRName like '%" + jTextField3.getText() + "%') OR "
            +  " (notes like '%" + jTextField3.getText() + "%'))" + andOr;

      // Advertiser
      if (jTextField4.getText().trim().length() > 0)
        sql += "((Advertiser like '%" + jTextField4.getText() + "%') OR "
            +  " (notes like '%" + jTextField4.getText() + "%'))";

      if (sql.endsWith(" OR "))
           sql = sql.substring(0, sql.length() - 4);

      if (sql.endsWith(" AND "))
           sql = sql.substring(0, sql.length() - 5);

      sql += ") ORDER BY ImageLibRecordView.record_id";

      Console.println("Search sql (Exclusive) = \n" + sql);

      try {
        mSearchResultCount = mCamsDB.executeUpdate(sql);
        Console.println(mSearchResultCount + " Rows Found in Exclusive + search.");
      }
      catch (Exception ex) {
        Console.println("Search Error: " + ex.getMessage());
        if (rs != null) {
          try { rs.close(); rs = null; } catch (Exception x) {}
        }
      }
    }

    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    if (mSearchResultCount == 0) {
      JOptionPane.showMessageDialog(this, "No records were found for this query.",
                                    "No Records Found", JOptionPane.INFORMATION_MESSAGE);
      if (jrdoFilter.isSelected()) {
        // Restore original search results
        sql = "UPDATE ImageLibSearch SET search_user = '" +
            mCamsDB.getUserInfo().getSearchID() + "' WHERE search_user = '" +
            mCamsDB.getUserInfo().getSearchID() + "_prior'";
        mCamsDB.execute(sql);
      }
      return;
    }
    else {
      if (jrdoFilter.isSelected()) {
        // Clear Previous Search Results
        sql = "DELETE FROM ImageLibSearch WHERE search_user = '" +
            mCamsDB.getUserInfo().getSearchID() + "_prior'";
        mCamsDB.execute(sql);
      }
      hide();
    }

  }

  void configureFilterButton() {
    String sql = "SELECT count(*) FROM ImageLibSearch WHERE search_user = '" +
        mCamsDB.getUserInfo().getSearchID() + "'";
    int theCount = Integer.parseInt(mCamsDB.querySingleString(sql));

    if (theCount == 0) {
      jrdoFilter.setEnabled(false);
      jrdoNewSearch.setSelected(true);
    }
    else {
      jrdoFilter.setEnabled(true);
    }

  }

}

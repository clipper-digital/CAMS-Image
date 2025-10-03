package cams.imagelib;

import java.util.*;
import javax.swing.table.AbstractTableModel;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ImageLibTableModel extends AbstractTableModel {

  private String[] columnNames = {"Record Name", "Status", "Keyline",
      "Scan #", "Record Modified", "Asset Modified"};

  private Class[] mColumnClasses =  {
      String.class, String.class, String.class, String.class,
      java.util.Date.class, java.util.Date.class };

  private int[] mColumnWidths =  { 100, 80, 100, 100, 130, 130 };

  private ArrayList data = new ArrayList();

  private long mTotalRecords = 0; // This holds the value of the total # of
                                 // matching records.  The actual # of
                                 // records returned from the query could
                                 // be limited by MaxRows
  public ArrayList getData() {
    return data;
  }

  public long getTotalRecords() { return mTotalRecords; }
  public void setTotalRecords(long theTotal) { mTotalRecords = theTotal; }

  public int getColumnCount() {
      return columnNames.length;
  }

  public int getColumnWidth (int col) {
       return  mColumnWidths[col];
   }

  public int getRowCount() {
      return data.size();
  }

  public String getColumnName(int col) {
      return columnNames[col];
  }

  public Object getValueAt(int row, int col) {
    ImageLibRecord theRecord = (ImageLibRecord) data.get(row);
    Object retVal = null;

//    private String[] columnNames = {"Record Name", "Status", "Keyline",
//      "Scan #", "Asset Modified", "Record Modified"};

    switch (col) {
      case 0:
        retVal = theRecord.getRecordName();
        break;
      case 1:
        retVal = theRecord.getStatus();
        break;
      case 2:
        retVal = theRecord.getKeyline();
        break;
      case 3:
        retVal = theRecord.getScanNumber();
        break;
      case 4:
        retVal = theRecord.getRecordModified();
        break;
      case 5:
        retVal = theRecord.getFileDate();
        break;
    }

    return retVal;
  }

  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */
  public Class getColumnClass(int c) {
    try {
//    return getValueAt(0, c).getClass();
      return mColumnClasses[c];
    }
    catch (Exception ex) {
      return new String("").getClass();
    }
  }

  /*
   * Don't need to implement this method unless your table's
   * editable.
   */
  public boolean isCellEditable(int row, int col) {
    return false;
  }

  /*
   * Don't need to implement this method unless your table's
   * data can change.
   */
  public void setValueAt(Object value, int row, int col) {
//      data[row][col] = value;
      fireTableCellUpdated(row, col);
  }

}


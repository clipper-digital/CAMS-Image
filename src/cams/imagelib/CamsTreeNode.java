package cams.imagelib;

import cams.database.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author JJS Solutions
 * @version 1.0
 */

public class CamsTreeNode extends Object implements Comparable {
  public int id = -1;
  public int parent_id = -1;
  public int catalog_id = -1;
  public String name;
  public String fullPath; // SwipeFile,Food,Bagels
  public String catalog_type = null; // Used for Catalog records only

  public CamsTreeNode() {
  }

  public String toString() {
    return name;
  }

  public void create(CamsDB theCamsDB) {

    // Insert Row
    String sql = "INSERT INTO ImageLibCategory (catalog_id, category_name, " +
        "category_parent) VALUES (" + catalog_id + ", '" + name + "', " +
        parent_id + ")";
    theCamsDB.execute(sql);

    // Now query to get the catalog_id
    sql = "SELECT category_id FROM ImageLibCategory WHERE " +
        "catalog_id = " + catalog_id + " AND category_name = '" + name + "' " +
        "AND category_parent = " + parent_id;

    id = new Integer(theCamsDB.querySingleString(sql)).intValue();
  }

  public int compareTo(Object anotherNode) {
    return name.compareTo(anotherNode.toString());
  }
}

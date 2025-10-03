package cams.imagelib.detaildialogs;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import javax.swing.JTree;
import javax.swing.tree.*;
import java.util.*;

import cams.imagelib.*;
// import cams.console.*;

public class CategoryDialog extends JDialog {
  private JFrame mImageLibFrame = null;
  public boolean userCancel = true;
  private ArrayList mOriginalCategories = null;

  JPanel panel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  DefaultMutableTreeNode treeTop =
    new DefaultMutableTreeNode("Categories");
  JTree jTreeCategories = new JTree(treeTop);
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jbtnCancel = new JButton();
  JButton jbtnOK = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanelCats = new JPanel();
  JScrollPane jScrollPane2 = new JScrollPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JTextArea jTextCats = new JTextArea();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();

  public CategoryDialog(Frame frame, JFrame mainParent) {
    super(frame, "Select Categories", true);
    mImageLibFrame = mainParent;

    try {
      jbInit();
      setSize(285, 420);

//       pack();

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

      // Set icon for leaf (folder with no subfolders) to the ClosedFolder icon
      DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();
      cellRenderer.setLeafIcon(cellRenderer.getClosedIcon());
      jTreeCategories.setCellRenderer(cellRenderer);
      // Display + and - next to folders
      jTreeCategories.setShowsRootHandles(true);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jTreeCategories.setShowsRootHandles(false);
    jbtnCancel.setText("Cancel");
    jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnCancel_actionPerformed(e);
      }
    });
    jbtnOK.setText("OK");
    jbtnOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbtnOK_actionPerformed(e);
      }
    });
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setHgap(10);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    borderLayout1.setHgap(0);
    jPanelCats.setBorder(BorderFactory.createEtchedBorder());
    jPanelCats.setLayout(borderLayout2);
    jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jTextCats.setFont(new java.awt.Font("Dialog", 0, 11));
    jTextCats.setEditable(false);
    jTextCats.setText("");
    jPanel2.setLayout(borderLayout3);
    jPanel2.setPreferredSize(new Dimension(94, 250));
    getContentPane().add(panel1);
    panel1.add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(jbtnOK, null);
    jPanel1.add(jbtnCancel, null);
    panel1.add(jPanelCats,  BorderLayout.CENTER);
    jPanelCats.add(jScrollPane2,  BorderLayout.CENTER);
    panel1.add(jPanel2, BorderLayout.NORTH);
    jPanel2.add(jScrollPane1,  BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTreeCategories, null);
    jScrollPane2.getViewport().add(jTextCats, null);
    jTreeCategories.getSelectionModel().setSelectionMode
            (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
  }

  /**
   * Set Top Level of Tree and Generate Tree
   * @param catalogId int Catalog ID
   */
  public void setTreeCatalog(int catalogId, String catalogName) {
    treeTop.removeAllChildren();
    treeTop.setUserObject(catalogName);

    ImageLibFrame imageFrame = (ImageLibFrame) mImageLibFrame;
    DefaultMutableTreeNode theCategoryNode = imageFrame.findTreeCatalogNode(catalogId);

//    cams.console.Console.println("Adding Child Nodes to Tree");
    addChildrenToTree(treeTop, theCategoryNode);

//    cams.console.Console.println("Children Added.. Expanding rows...");
//    for (int i=0; i < jTreeCategories.getRowCount(); i++) {
//      jTreeCategories.expandRow(i);
//      jTreeCategories.updateUI();
//    }
//    cams.console.Console.println("Rows expanded.");
  }

  private void addChildrenToTree(DefaultMutableTreeNode myRoot, DefaultMutableTreeNode otherRoot) {
    for (int i=0; i < otherRoot.getChildCount(); i++) {
      DefaultMutableTreeNode theChild = (DefaultMutableTreeNode) otherRoot.getChildAt(i);
      // Add this node
      myRoot.add((DefaultMutableTreeNode)theChild.clone());
      if (theChild.getChildCount() > 0) {
      // This node has children, call this function recursively to add them
        addChildrenToTree( (DefaultMutableTreeNode) myRoot.getChildAt(myRoot.
            getChildCount() - 1), theChild);
      }
    }
  }

  public void setSelectedCategories(ArrayList theCategories, boolean append) {
    if (append) {
      mOriginalCategories = new ArrayList();
      mOriginalCategories.addAll(theCategories);
    }
    else
      mOriginalCategories = null;

    jTreeCategories.clearSelection();
    jTextCats.setText("");

    for (int i=0; i < theCategories.size(); i++) {
      IDNamePair theCategory = (IDNamePair) theCategories.get(i);
      jTextCats.append(theCategory.getName() + "\n");
//      for (int row=0; row < jTreeCategories.getRowCount(); row++) {
//        String thePathStr = jTreeCategories.getPathForRow(row).toString().replaceAll(", ", ",");
//        if (thePathStr.equals("[" + theCategory.getName() + "]")) {
//          jTreeCategories.addSelectionRow(row);
//          break; // Break out of Row loop
//        }
//      }
    }
    jTreeCategories.updateUI();
    jTextCats.setCaretPosition(0);
  }

  public ArrayList getSelectedCategories() {
    // Make sure Top level Catalog is not selected
    jTreeCategories.removeSelectionRow(0);

    ArrayList theCategories = new ArrayList();
    if (mOriginalCategories != null)
      theCategories.addAll(mOriginalCategories);

    // Create a list of Categories the user selected
    for (int i = 0; i < jTreeCategories.getSelectionCount(); i++) {
      int selectedRow = jTreeCategories.getSelectionRows()[i];
      TreePath thePath = jTreeCategories.getPathForRow(selectedRow);
      DefaultMutableTreeNode theTreeNode = (DefaultMutableTreeNode) thePath.
          getLastPathComponent();
      CamsTreeNode theCamsNode = (CamsTreeNode) theTreeNode.getUserObject();
      IDNamePair thisSelection = new IDNamePair(theCamsNode.id, theCamsNode.fullPath);
      if (!theCategories.contains(thisSelection))
        theCategories.add(thisSelection);
    }
    return theCategories;
  }

  void jbtnOK_actionPerformed(ActionEvent e) {
    userCancel = false;
    hide();
  }

  void jbtnCancel_actionPerformed(ActionEvent e) {
    hide();
  }
}

package cams.imagelib;

import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;

import cams.imagelib.*;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer
  {
      private DefaultMutableTreeNode mBatchesNode = null;

      public CustomTreeCellRenderer()
      {
          super();
      }

      public void setBatchesNode(DefaultMutableTreeNode batchesNode) {
        mBatchesNode = batchesNode;
      }

      /**
      * getTreeCellRendererComponent
      * This method is overridden to set the node specific icons and tooltips
      *
      * @return The Component object used to render the cell value
      * @exception
      */
      public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                    boolean sel, boolean expanded,
                                                    boolean leaf, int row, boolean hasFocus)
      {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
        row, hasFocus);

        Font normalFont = getFont().deriveFont(Font.PLAIN);
        Font boldFont = getFont().deriveFont(Font.BOLD);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object theUserObject = node.getUserObject();
        if (node.equals(mBatchesNode)) {
          setFont(boldFont);
        }
        else if (theUserObject instanceof CamsTreeNode) {
          CamsTreeNode theTreeNode = (CamsTreeNode) theUserObject;
          if ((theTreeNode.id == -1) && (theTreeNode.catalog_id != -1)) {
            // Top Level Catalog
            setFont(boldFont);
          }
          else {
            setFont(normalFont);
          }
        }
        else {
          setFont(normalFont);
        }

        return this;
      }

  }

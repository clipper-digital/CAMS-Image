package cams.imagelib.importrecord;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import cams.imagelib.importrecord.*;
import cams.console.*;

/**
 * <p>Title: Clipper Asset Management System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MyDropTargetListener implements DropTargetListener {
  private JFrame mParent = null;

  public MyDropTargetListener(JFrame theParent) {
    mParent = theParent;
  }

  public void dropActionChanged(DropTargetDragEvent e) {
    return;
//    e.acceptDrag(0);
  }

  public void dragEnter(DropTargetDragEvent e) {
    e.acceptDrag(DnDConstants.ACTION_COPY);
  }

  public void dragOver(DropTargetDragEvent e) {
  }

  public void dragExit(DropTargetEvent e) {
  }

  public void drop(DropTargetDropEvent dtde) {
    dtde.acceptDrop(DnDConstants.ACTION_COPY);

    Transferable transferable = dtde.getTransferable();
    if (!transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      Console.println("Attempting to drop incorrect type of object here (not File List).");
      dtde.dropComplete(true);
      return;
    }

    try {
      final ArrayList theFileList = new ArrayList((java.util.List) transferable.getTransferData(DataFlavor.javaFileListFlavor));

      try {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
                ((ImportRecordFrame)mParent).processDroppedFiles(theFileList);
          }
        });
      }
      catch (Exception ex) {}

//      ((ImportRecordFrame)mParent).processDroppedFiles(theFileList);
    }
    catch (Exception ex) {
      Console.println("MyDropTargetListener Exception:" + ex.getMessage());
    }
    finally {
      dtde.dropComplete(true);
    }

  }

}

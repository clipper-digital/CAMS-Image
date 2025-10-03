package cams.imagelib;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import javax.swing.border.*;

public class GalleryTestDialog extends JDialog {
  JPanel jPanelGallery = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel theThumbNail = new JLabel();
  JPanel jPanelThumbNail = new JPanel();
  JLabel jlblStatus = new JLabel();
  JPanel jpnlTopInfo = new JPanel();
  JLabel jlblRecordName = new JLabel();
  JLabel jlblKeyline = new JLabel();
  JLabel jlblScanNum = new JLabel();
  TitledBorder titledBorder1;

  public GalleryTestDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public GalleryTestDialog() {
    this(null, "", false);
  }

  private void jbInit() throws Exception {
    int thumbSize = 256;

    titledBorder1 = new TitledBorder("");
    jPanelGallery.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);


    ImageIcon theIcon = null;
    theIcon = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("search.png"));

    theThumbNail.setBorder(null);
    theThumbNail.setHorizontalAlignment(SwingConstants.CENTER);
    theThumbNail.setVerticalAlignment(SwingConstants.CENTER);
    theThumbNail.setText("");
    theThumbNail.setIcon(theIcon);
    theThumbNail.setPreferredSize(new Dimension(thumbSize, thumbSize));
    theThumbNail.setMaximumSize(new Dimension(thumbSize, thumbSize));
    theThumbNail.setName("1");

    jpnlTopInfo.setBorder(null);
    jpnlTopInfo.setMinimumSize(new Dimension(78, 50));
    jpnlTopInfo.setPreferredSize(new Dimension(78, 50));
    jpnlTopInfo.setLayout(new XYLayout());

    jlblRecordName.setText("Record Name :");
    jlblKeyline.setText("Keyline :");
    jlblScanNum.setText("Scan #:");
    jPanelThumbNail.setBackground(SystemColor.control);
    jpnlTopInfo.add(jlblRecordName, new XYConstraints(2, 0, -1, -1));
    jpnlTopInfo.add(jlblKeyline,  new XYConstraints(2, 14, -1, -1));
    jpnlTopInfo.add(jlblScanNum, new XYConstraints(2, 29, -1, -1));

    jlblStatus.setText("Status:");

    jPanelThumbNail.setLayout(new BorderLayout());
    jPanelThumbNail.setPreferredSize(new Dimension(100, 100));
    jPanelThumbNail.setBorder(BorderFactory.createEtchedBorder());
    jPanelThumbNail.add(jpnlTopInfo, BorderLayout.NORTH);
    jPanelThumbNail.add(theThumbNail, BorderLayout.CENTER);
    jPanelThumbNail.add(jlblStatus, BorderLayout.SOUTH);

    getContentPane().add(jPanelGallery);
    jPanelGallery.add(jPanelThumbNail, null);
  }
}

package cams.imagelib;

import java.awt.*;
import javax.swing.*;

public class EasterDialog extends JDialog {
  private ImageIcon easterEgg = null;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel jLabel1 = new JLabel();

  public EasterDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      easterEgg = new ImageIcon(cams.imagelib.ImageLibFrame.class.getResource("schmitt.jpg"));
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public EasterDialog() {
    this(null, "", true);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().add(panel1);
    jLabel1.setIcon(easterEgg);
    panel1.add(jLabel1, BorderLayout.CENTER);
  }
}

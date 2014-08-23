import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

// House diagnoses badly conditioned networks and divergent populations
// using a JTabbedPane containing various labs.
//
// TODO: add ascii-art House M.D.

public class House {
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setSize(1024, 768+25);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JTabbedPane tabs = new JTabbedPane();
    frame.add(tabs);

    NetLab display = new NetLab();
    tabs.add("Net Lab", display);
    tabs.addKeyListener(display); // HACK

    JPanel empty = new JPanel();
    tabs.add("Empty", empty);

    frame.setVisible(true);
    display.run();
  }
}

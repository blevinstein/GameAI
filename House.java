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

    // give each lab a tab and a thread
    
    NetLab netLab = new NetLab();
    tabs.add("Net Lab", netLab);
    new Thread(() -> netLab.run()).start();

    PopLab popLab = new PopLab();
    tabs.add("Pop Lab", popLab);
    new Thread(() -> popLab.run()).start();

    OpticsLab opticsLab = new OpticsLab();
    tabs.add("Optics Lab", opticsLab);
    new Thread(() -> opticsLab.run()).start();

    frame.setVisible(true);
  }
}

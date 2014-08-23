import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Driver for an application which helps diagnose neural net behavior.
// In theory, something like a visual inspection of the neural net in action,
// to complement other types of testing, and to provide a more intuitive
// view of the network when diagnosing behavioral issues.

// TODO: add more complex diagnostics for Populations (in another class/panel)
class NetDiag extends JPanel implements KeyListener {
  private NeuralNet net = new NeuralNet(new int[]{2,2,1});
  private boolean[] state = new boolean[]{true, false};
  private Function<double[],double[]> f =
    inputs -> new double[]{(inputs[0] > 0) ^ (inputs[1] > 0) ? 1.0 : -1.0};
  // implicit no-argument constructor

  boolean training = false;
  public void run() {
    Throttle t = new Throttle(100); // 100fps max
    while (true) {
      if (training) trainRandom();
      t.sleep();
    }
  }

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    // TODO: add tab panel, each tab is a different tool
    net.drawState(g, Util.btod(state), 10, 10, 1024-20, 768-20);
  }

  public void trainRandom() {
    // choose an input and calculate correct output
    state = new boolean[]{Math.random() < 0.5,
                          Math.random() < 0.5};
    double target[] = f.apply(Util.btod(state));
    boolean targetBit = Util.dtob(target)[0];
    // train the neural network
    net.backpropagate(Util.btod(state), target);
    // check the network's answer
    boolean answer = net.process(Util.btod(state))[0] > 0;
    // DEBUG
    if (answer == targetBit) {
      correct++;
    } else {
      incorrect++;
    }
    System.out.println(String.format("train %2d ^ %2d = %2d (%2d) Success %.2f%%",
                                      state[0]  ? 1 : 0,
                                      state[1]  ? 1 : 0,
                                      targetBit ? 1 : 0,
                                      answer    ? 1 : 0,
                                      correct * 100.0 / (correct + incorrect)));
    repaint();
  }

  private int correct = 0, incorrect = 0; // DEBUG
  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_L:
        NeuralNet newNet = Json.load("patient.json", NeuralNet.class);
        if (newNet != null) {
          net = newNet;
          repaint();
        }
        break;
      case KeyEvent.VK_S:
        Json.save(net, "patient.json");
        break;
      case KeyEvent.VK_T:
        training = !training;
        break;
      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;
    }
  }
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setSize(1024, 768+25);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    NetDiag display = new NetDiag();
    frame.add(display);
    frame.addKeyListener(display);

    frame.setVisible(true);
    display.run();
  }

  private static final long serialVersionUID = 1;
}

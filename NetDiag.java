import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JPanel;

// NOTE: used for diagnosing NeuralNet behavior
// TODO: add more complex diagnostics for Populations (in another class)
class NetDiag extends JPanel implements KeyListener {
  private NeuralNet net = new NeuralNet(new int[]{2,2,1});
  private double[] state = new double[]{0.0, 1.0};
  private Function<double[],double[]> f = inputs -> new double[]{Math.round(inputs[0]) ^ Math.round(inputs[1])};
  // implicit no-argument constructor

  public void run() {
    while (true) {
    }
  }

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    // TODO: add tab panel, each tab is a different tool
    net.drawState(g, state, 10, 10, 1024-20, 768-20);
  }

  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_T:
        // train the neural network
        state = new double[]{Math.round(Math.random()),
                                      Math.round(Math.random())};
        double[] target = f.apply(state);
        net.backpropagate(state, target);
        repaint();
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

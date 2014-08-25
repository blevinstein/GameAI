import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

// This lab helps diagnose neural net behavior.
// In theory, something like a visual inspection of the neural net in action,
// to complement other types of testing, and to provide a more intuitive
// view of the network when diagnosing behavioral issues.
//
// Controls:
// - S to save net, L to load net
// - T to start/stop training
// - R to recent correct/incorrect count

class NetLab extends JPanel implements KeyListener {
  private NeuralNet net = new NeuralNet(new int[]{2,2,1});
  private boolean[] state = randomBits(2);
  private Map<String,Function<boolean[],boolean[]>> functions = new HashMap<>();
  private JComboBox<String> selectFunction;
  private Function<boolean[],boolean[]> f;

  public NetLab() {
    super(null); // no layout manager
    
    // receive key events
    this.setFocusable(true);
    this.addKeyListener(this);

    // add combo box for selecting functions to learn
    selectFunction = new JComboBox<String>();
    selectFunction.setFocusable(false);
    selectFunction.setBounds(10, 10, 200, 25);
    this.add(selectFunction);

    addFunction("XOR", inputs ->
        new boolean[]{(inputs[0]) ^ (inputs[1])});
    addFunction("A", inputs ->
        new boolean[]{inputs[0]});
    addFunction("B", inputs ->
        new boolean[]{inputs[1]});
    addFunction("AND", inputs ->
        new boolean[]{inputs[0] && inputs[1]});
    addFunction("OR", inputs ->
        new boolean[]{inputs[0] || inputs[1]});

    f = functions.get(selectFunction.getItemAt(0));
    selectFunction.addActionListener(e ->
        f = functions.get(
          selectFunction.getItemAt(
            selectFunction.getSelectedIndex())));
  }

  private void addFunction(String name, Function<boolean[],boolean[]> function) {
    selectFunction.addItem(name);
    functions.put(name, function);
  }
  
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
    net.drawState(g, Util.btod(state), 10, 10, getWidth()-20, getHeight()-20);
  }

  private boolean[] randomBits(int n) {
    boolean b[] = new boolean[n];
    for (int i = 0; i < n; i++) {
      b[i] = Math.random() < 0.5;
    }
    return b;
  }

  public void trainRandom() {
    // choose an input and calculate correct output
    state = randomBits(2);
    boolean target[] = f.apply(state);
    boolean targetBit = target[0];
    // train the neural network
    net.backpropagate(Util.btod(state), Util.btod(target));
    // check the network's answer
    boolean answer = net.process(Util.btod(state))[0] > 0;
    // DEBUG
    if (answer == targetBit) {
      correct++;
    } else {
      incorrect++;
    }
    System.out.println(String.format("train(%d, %d) = %d (%d) Success %.2f%%",
                                      state[0]  ? 1 : 0,
                                      state[1]  ? 1 : 0,
                                      targetBit ? 1 : 0,
                                      answer    ? 1 : 0,
                                      correct * 100.0 / (correct + incorrect)));
    repaint();
  }

  private int correct = 0, incorrect = 0;
  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_L:
        NeuralNet newNet = Json.load("patient.json", NeuralNet.class);
        if (newNet != null) net = newNet;
        repaint();
        break;
      case KeyEvent.VK_R:
        correct = incorrect = 0;
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

  private static final long serialVersionUID = 1;
}

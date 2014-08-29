import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.function.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

// This lab helps diagnose neural net behavior.
// In theory, something like a visual inspection of the neural net in action,
// to complement other types of testing, and to provide a more intuitive
// view of the network when diagnosing behavioral issues.

class NetLab extends JPanel implements KeyListener {

  private String HELP =
    "Choose a function for the network to learn. " +
    "Press S to save, L to load a saved net. " +
    "T to start/stop training of the net. " +
    "R to reset correct % stats. ";


  private NetAdapter<Boolean[],Boolean> adapter = new NetAdapter<>(Converters.array(Boolean.class, 2),
                                                                   new BinaryConverter());
  private Boolean[] state = Util.randomBits(2);
  private Map<String,Function<Boolean[],Boolean>> functions = new HashMap<>();
  private JComboBox<String> selectFunction;
  private Function<Boolean[],Boolean> f;

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
        inputs[0] ^ inputs[1]);
    addFunction("A", inputs ->
        inputs[0]);
    addFunction("B", inputs ->
        inputs[1]);
    addFunction("AND", inputs ->
        inputs[0] && inputs[1]);
    addFunction("OR", inputs ->
        inputs[0] || inputs[1]);

    f = functions.get(selectFunction.getItemAt(0));
    selectFunction.addActionListener(e ->
        f = functions.get(
          selectFunction.getItemAt(
            selectFunction.getSelectedIndex())));
  }

  private void addFunction(String name, Function<Boolean[],Boolean> function) {
    selectFunction.addItem(name);
    functions.put(name, function);
  }
  
  boolean training = false;
  public void run() {
    Throttle t = new Throttle(100); // 100fps max
    while (true) {
      if (training) trainRandom();
      repaint();
      t.sleep();
    }
  }

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    adapter.drawState(g, state, 10, 10, getWidth()-20, getHeight()-20);

    // draw overlay text last
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.PLAIN, 15));
    
    // draw success % since last reset
    Util.placeText(g, Util.NE,
                   String.format("Success: %2.2f%%", correctPercent),
                   getWidth()-20, 20);

    // draw help
    if (displayHelp) {
      Util.placeText(g, Util.SE, HELP, getWidth()-20, getHeight()-20);
    } else {
      Util.placeText(g, Util.SE, "H for help", getWidth()-20, getHeight()-20);
    }
  }

  public void trainRandom() {
    // choose an input and calculate correct output
    state = Util.randomBits(2);
    Boolean target = f.apply(state);
    // train the neural network
    adapter.backpropagate(state, target);
    // check the network's answer
    Boolean answer = adapter.process(state);
    // DEBUG
    if (answer == target) {
      correct++;
    } else {
      incorrect++;
    }
    correctPercent = correct * 100.0 / (correct + incorrect);
    repaint();
  }

  private double correctPercent = 0.0;
  private int correct = 0, incorrect = 0;
  private boolean displayHelp = false;
  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_L:
        NeuralNet newNet = Json.load("patient.json", NeuralNet.class);
        if (newNet != null) adapter.setNet(newNet);
        repaint();
        break;
      case KeyEvent.VK_R:
        correct = incorrect = 0;
        break;
      case KeyEvent.VK_S:
        Json.save(adapter.net(), "patient.json");
        break;
      case KeyEvent.VK_T:
        training = !training;
        break;
      case KeyEvent.VK_H:
        displayHelp = true;
        break;
      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;
    }
  }
  public void keyReleased(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_H:
        displayHelp = false;
        break;
    }
  }
  public void keyTyped(KeyEvent e) {}

  private static final long serialVersionUID = 1;
}

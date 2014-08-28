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

// This lab helps visualize the progress of an evolving population.

class PopLab extends JPanel implements KeyListener {
  private final int POPULATION_SIZE = 100;

  private Population<NeuralNet> pop;

  private Map<String,Function<boolean[],boolean[]>> functions = new HashMap<>();
  private JComboBox<String> selectFunction;

  public PopLab() {
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
        new boolean[]{inputs[0] ^ inputs[1]});
    addFunction("A", inputs ->
        new boolean[]{inputs[0]});
    addFunction("B", inputs ->
        new boolean[]{inputs[1]});
    addFunction("AND", inputs ->
        new boolean[]{inputs[0] && inputs[1]});
    addFunction("OR", inputs ->
        new boolean[]{inputs[0] || inputs[1]});

    setFunction(functions.get(selectFunction.getItemAt(0)));
    selectFunction.addActionListener(e ->
        setFunction(functions.get(
            selectFunction.getItemAt(
              selectFunction.getSelectedIndex()))));

    // init population
    pop = new Population<NeuralNet>(POPULATION_SIZE,
        () -> new NeuralNet(new int[]{2,2,1}));
  }

  private void addFunction(String name, Function<boolean[],boolean[]> function) {
    selectFunction.addItem(name);
    functions.put(name, function);
  }

  private void setFunction(Function<boolean[],boolean[]> f) {
    // setup grading policy
    DefaultGrader.register((net) -> {
      boolean cases[][] = {{false, false},
                           {false, true},
                           {true, false},
                           {false, false}};
      double score = 0.0;
      for (boolean[] kase : cases) {
        boolean expected = f.apply(kase)[0];
        boolean actual = Util.dtob(net.process(Util.btod(kase)))[0];
        if (actual == expected) score += 1;
      }
      return score;
    }, NeuralNet.class);
  }

  boolean evolving = false;
  public void run() {
    Throttle t = new Throttle(1); // limit epochs/second
    while (true) {
      if (evolving) {
        pop = pop.epoch();
        System.out.println(pop.stats());
        repaint();
      }
      t.sleep();
    }
  }

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    // show histogram
    Util.drawHistogram(g, pop.fitness(), 1.0, 10, 10, getWidth()-20, getHeight()-20);

    // show a perfect example
    for (int i = 0; i < pop.fitness().length; i++) {
      if (pop.fitness()[i] == 4.0) {
        NeuralNet net = pop.pop().get(i);
        net.drawState(g, Util.btod(Util.randomBits(2)),
                      10, 10 + 25, (getWidth()-20)/2, (getHeight()-20)/2);
        break;
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_E:
        evolving = !evolving;
        break;
      case KeyEvent.VK_L:
        Population<NeuralNet> newPop = Json.load("patients.json", pop.getClass());
        if (newPop != null) pop = newPop;
        repaint();
        break;
      case KeyEvent.VK_S:
        Json.save(pop, "patients.json");
        break;
      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;
    }
  }
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}

  public static final long serialVersionUID = 1;
}

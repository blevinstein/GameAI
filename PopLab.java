import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JPanel;

// This lab helps visualize the progress of an evolving population.

class PopLab extends JPanel implements KeyListener {
  private final int POPULATION_SIZE = 100;

  private Population<NeuralNet> pop;
  private Function<double[],double[]> f =
    inputs -> new double[]{(inputs[0] > 0) ^ (inputs[1] > 0) ? 1.0 : -1.0};

  public PopLab() {
    this.setFocusable(true);
    this.addKeyListener(this);
    // setup grading policy
    DefaultGrader.registerDefaultGrader((net) -> {
      boolean cases[][] = {{false, false},
                           {false, true},
                           {true, false},
                           {false, false}};
      double score = 0.0;
      for (boolean[] kase : cases) {
        boolean expected = Util.dtob(f.apply(Util.btod(kase)))[0];
        boolean actual = Util.dtob(net.process(Util.btod(kase)))[0];
        if (actual == expected) score += 1;
      }
      return score;
    }, NeuralNet.class);
    // init population
    pop = new Population<NeuralNet>(POPULATION_SIZE,
        () -> new NeuralNet(new int[]{2,2,1}));
  }

  boolean evolving = false;
  public void run() {
    Throttle t = new Throttle(4); // 4 epochs/second max
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

    // show something
    g.setColor(Color.BLUE);
    Util.drawHistogram(g, pop.fitness(), 1.0, 10, 10, getWidth()-20, getHeight()-20);
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

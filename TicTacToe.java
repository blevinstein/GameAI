import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;

class TicTacToe extends JPanel implements KeyListener {
  private final int POPULATION_SIZE = 100;

  private Game game = new Game();
  private MemoryLearner memLearner = new MemoryLearner();
  private NetLearner netLearner = new NetLearner();
  
  private Population<NeuralNet> population;
  private Learner<T3State,T3Move> player1 = netLearner, player2 = null;
  private int wins[] = new int[]{0, 0};
  private T3Move suggested; // the best move, as suggested by the AI

  @SuppressWarnings("unchecked")
  public TicTacToe() {
    // set grading policy, Learner -> double
    DefaultGrader.registerDefaultGrader((network) -> {
      NetLearner student = new NetLearner(network);
      // simulate 100 games, 1 point for ties, 2 points for wins
      double score = 0;
      for (int i = 0; i < 100; i++) {
        Game g = new Game(student, memLearner); // NOTE: student is player 0
        System.out.println("memlearner " + memLearner.map().size());
        g.play();
        switch (g.winner()) {
          case -1: score += 1.0; break; // tie
          case  0: score += 2.0; break; // student wins
          case  1:               break; // student loses
        }
      }
      return score;
    }, NeuralNet.class);
   
    // HACK: creates a NetLearner to get the right size of neural net
    population = new Population<NeuralNet>(POPULATION_SIZE, () -> new NetLearner().net());

    setMode(netLearner, null, "NxP");
    
    // load learners
    try {
      Map<String, Double> map = Json.load("brain.json", memLearner.map().getClass());
      if (map != null) memLearner = new MemoryLearner(map);

      Population<NeuralNet> pop = Json.load("pop.json", population.getClass());
      if (pop != null) population = pop;
    } catch (Exception e) {
      System.err.println("Couldn't load. " + e.getMessage());
    }
  }
  
  public void run() {
    // separate thread
    new Thread(() -> {
      // evolve population
      Throttle t = new Throttle(2);
      while (true) {
        long before = System.currentTimeMillis();
        population = population.epoch();
        long after = System.currentTimeMillis();
        System.out.println(population.stats() + " Duration " + (after - before) + " ms");
        netLearner = new NetLearner(population.bestN(1).get(0));
        t.sleep();
      }
    }).start();

    // main loop and repainting
    Throttle throttle = new Throttle(60); // target frame rate
    while (true) {
      mainLoop();
      repaint();
      throttle.sleep();
      frameRate = ticker.tick();
    }
  }

  private Ticker ticker = new Ticker(10);
  private long frameRate;
  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());
    
    // draw the board
    game.state().draw(g, 10, 10, 300, cursor, suggested);
    
    // draw the neural network's thoughts
    netLearner.drawThoughts(g, game.state(), 330, 10, 300);
    
    // show frameRate and mode
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.PLAIN, 25));
    FontMetrics fm = g.getFontMetrics();
    String str = String.format("%d FPS, Mode %s", (int)frameRate, modeStr);
    g.drawString(str,
                 getWidth() - 10 - fm.stringWidth(str),
                 10 + fm.getAscent());
  }

  // NOTE: Controls:
  // - arrows to move cursor
  // - spacebar to make a move
  // - S to save brain, L to load brain
  private String modeStr = "";
  private int mode = 0;
  private T3Move cursor = new T3Move();
  @SuppressWarnings("unchecked")
  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
    case KeyEvent.VK_SPACE:
      if (game.state().validMove(cursor)) {
        game.moveMade(cursor); 
      } else {
        Toolkit.getDefaultToolkit().beep();
      }
      break;
    case KeyEvent.VK_S:
      Json.save(memLearner.map(), "brain.json");
      Json.save(population, "pop.json");
      break;
    case KeyEvent.VK_L:
      Map<String, Double> map = Json.load("brain.json", memLearner.map().getClass());
      if (map != null) memLearner = new MemoryLearner(map);
      Population<NeuralNet> pop = Json.load("pop.json", population.getClass());
      if (pop != null) population = pop;
      break;
    case KeyEvent.VK_M:
      // different "game modes"
      mode = (mode + 1) % 5;
      switch (mode) {
      case 0: 
        setMode(netLearner, null, "NxP"); 
        break;
      case 1: 
        setMode(memLearner, memLearner, "MxM"); 
        break;
      case 2: 
        setMode(netLearner, netLearner, "NxN"); 
        break;
      case 3: 
        setMode(netLearner, memLearner, "NxM"); 
        break;
      case 4: 
        setMode(memLearner, null, "MxP"); 
        break;
      }
      break;
    case KeyEvent.VK_U:
      System.out.println("X " + wins[T3Square.X] + " O " + wins[T3Square.O]);
      break;
    case KeyEvent.VK_UP:
      cursor = cursor.up();
      break;
    case KeyEvent.VK_DOWN:
      cursor = cursor.down();
      break;
    case KeyEvent.VK_LEFT:
      cursor = cursor.left();
      break;
    case KeyEvent.VK_RIGHT:
      cursor = cursor.right();
      break;
    case KeyEvent.VK_ESCAPE:
      System.exit(0);
      break;
    }
  }
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}
  
  // MISC METHODS
  private void setMode(Learner<T3State,T3Move> a,
                       Learner<T3State,T3Move> b, String str) {
    player1 = a;
    player2 = b;
    game = newGame();
    modeStr = str;
  }
  private Game newGame() {
    return new Game(player1, player2);
  }
  private void mainLoop() {
    if (game.done()) {
      // print winner and count wins
      switch(game.winner()) {
        case T3Square.X: wins[T3Square.X]++; break;
        case T3Square.O: wins[T3Square.O]++; break;
      }
      // start new game
      game = newGame();
    } else if(game.canStep()) {
      game.step();
      suggested = memLearner.query(game.state().normalize(game.toMove()));
    }
    // else, game is waiting on user input
  }

  // DRIVER
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setSize(640, 320 + 25);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    TicTacToe display = new TicTacToe();
    frame.add(display);
    frame.addKeyListener(display);

    frame.setVisible(true);
    display.run();
  }

  private static final long serialVersionUID = 1337;
}


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Display extends JPanel implements KeyListener {
  private Game game = new Game();
  private MemoryLearner memLearner = new MemoryLearner();
  private NetLearner netLearner = new NetLearner();
  private AbstractLearner player1 = netLearner, player2 = null;
  private int wins[] = new int[]{0, 0};
  private T3Move suggested; // the best move, as suggested by the AI
  
  public Display() {
    
    setMode(netLearner, null, "NxP");
    
    // load learners
    try {
      HashMap map = Json.loadMap("brain.json");
      if (map != null) memLearner = new MemoryLearner(map);
      
      NeuralNet net = Json.loadNet("net.json");
      if (net != null) netLearner = new NetLearner(net);
      
    } catch (Exception e) {
      System.err.println("Couldn't load. " + e.getMessage());
    }
    
    lastPaintTime = System.currentTimeMillis();
  }
  
  // sleep timing logic in main loop, inspired by Processing
  // https://code.google.com/p/processing/source/browse/trunk/processing/android/core/src/processing/core/PApplet.java?r=7046
  private long targetFrameRate = 100;
  public void run() {
    long oversleep = 0;
    
    while (true) {
      long beforeTime = System.currentTimeMillis();
      mainLoop();
      repaint();
      long afterTime = System.currentTimeMillis();
      
      // sleep to manage frameRate
      long duration = afterTime - beforeTime;
      long waitTime = 1000 / targetFrameRate - duration - oversleep;
      if (waitTime > 0) {
        try {
          Thread.sleep(waitTime);
        } catch(InterruptedException e) {}
      }
      long lastTime = System.currentTimeMillis();
      oversleep = lastTime - (beforeTime + duration + waitTime);
      frameRate = 0.9 * frameRate + 0.1 * 1000 / (lastTime - beforeTime);
    }
  }

  private double frameRate;
  private long lastPaintTime;
  public void paintComponent(Graphics g) {
    
    // execute game loop
    mainLoop();
    
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());
    
    // draw the board
    game.state().draw(g, 10, 10, 300, cursor, suggested);
    
    // draw the neural network's thoughts
    netLearner.drawThoughts(g, game.state(), 330, 10, 300);
    
    // show frameRate and mode
    //frameRate = (System.currentTimeMillis() - lastPaintTime) / 1000;
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.PLAIN, 25));
    FontMetrics fm = g.getFontMetrics();
    String str = String.format("%.2f FPS, Mode %s", frameRate, modeStr);
    g.drawString(str,
                 getWidth() - 10 - fm.stringWidth(str),
                 10 + fm.getAscent());
  }

  // NOTE: Controls:
  // - arrows to move cursor
  // - spacebar to make a move
  // - S to save brain, L to load brain
  // - M to change mode
  private String modeStr = "";
  private int mode = 0;
  private T3Move cursor = new T3Move();
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
      System.out.println("Saving...");
      Json.saveMap(memLearner.map(), "brain.json");
      Json.saveNet(netLearner.net(), "net.json");
      break;
    case KeyEvent.VK_L:
      System.out.println("Loading...");
      memLearner = new MemoryLearner(Json.loadMap("brain.json"));
      netLearner = new NetLearner(Json.loadNet("net.json"));
      break;
    case KeyEvent.VK_M:
      // different "game modes"
      mode = (mode + 1) % 5;
      System.out.println();
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
      if (cursor.y > 0) cursor.y--; 
      break;
    case KeyEvent.VK_DOWN:
      if (cursor.y < 2) cursor.y++; 
      break;
    case KeyEvent.VK_LEFT:
      if (cursor.x > 0) cursor.x--; 
      break;
    case KeyEvent.VK_RIGHT:
      if (cursor.x < 2) cursor.x++; 
      break;
    case KeyEvent.VK_ESCAPE:
      System.exit(0);
      break;
    }
  }
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}
  
  // MISC METHODS
  private void setMode(AbstractLearner a, AbstractLearner b, String str) {
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
        case T3Square.X: wins[T3Square.X]++; System.out.print("X"); break;
        case T3Square.O: wins[T3Square.O]++; System.out.print("O"); break;
        default: System.out.print("T"); break;
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

    Display display = new Display();
    frame.add(display);
    frame.addKeyListener(display);

    frame.setVisible(true);
    display.run();
  }
}

